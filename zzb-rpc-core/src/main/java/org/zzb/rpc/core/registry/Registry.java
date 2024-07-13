package org.zzb.rpc.core.registry;

import org.zzb.rpc.core.config.RegistryConfig;
import org.zzb.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Registry {

    /**
     * 初始化
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务发现 获取某个服务的所有节点
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     *  心跳机制
     */
    void heatBeat();

    void watch(String serviceNodeKey);

}
