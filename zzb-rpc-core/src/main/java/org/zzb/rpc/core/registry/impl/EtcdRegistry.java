package org.zzb.rpc.core.registry.impl;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.etcd.jetcd.watch.WatchResponse;
import io.vertx.core.impl.ConcurrentHashSet;
import org.zzb.rpc.core.config.RegistryConfig;
import org.zzb.rpc.core.model.ServiceMetaInfo;
import org.zzb.rpc.core.registry.Registry;
import org.zzb.rpc.core.registry.RegistryServiceCache;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     *  根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    private static Set<String> localRegisterNodeKeySet = new HashSet<>();

    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();
    private WatchResponse watchResponse;

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
        heatBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建 lease 客户端
        Lease leaseClient = client.getLeaseClient();

        // 创建租约30s
        long leaseId = leaseClient.grant(30).get().getID();

        // 准备key value
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        // 把租约和键值对 关联
        PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey,StandardCharsets.UTF_8))
                .get();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();
        if(cacheServiceMetaInfoList != null){
            return cacheServiceMetaInfoList;
        }

        //前缀搜搜加 结尾加/
        String searchPrefiix = ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                    ByteSequence.from(searchPrefiix,StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream().map(
                            keyValue -> {
                                //监听key变化
                                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                                watch(key);

                                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                                return JSONUtil.toBean(value, ServiceMetaInfo.class);
                            })
                    .collect(Collectors.toList());
            //写缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");

        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8))
                        .get();
            } catch (Exception e) {
                throw new RuntimeException(key + "下线失败");
            }
        }

        if(kvClient != null){
            kvClient.close();
        }
        if(client != null){
            client.close();
        }
    }

    @Override
    public void heatBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String registerKey : localRegisterNodeKeySet) {

                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get().getKvs();
                        if(keyValues.isEmpty()){
                            continue;
                        }

                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(registerKey + "续签失败", e);
                    }
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {

        Watch watchClient = client.getWatchClient();

        boolean newWatch = watchingKeySet.add(serviceNodeKey);

        if(newWatch){
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8),watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents()) {
                    switch (event.getEventType()){
                        case DELETE:
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }

            });
        }

    }
}
