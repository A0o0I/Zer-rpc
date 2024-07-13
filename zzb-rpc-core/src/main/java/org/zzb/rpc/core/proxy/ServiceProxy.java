package org.zzb.rpc.core.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.zzb.rpc.core.RpcApplication;
import org.zzb.rpc.core.config.RegistryConfig;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.constant.RpcConstant;
import org.zzb.rpc.core.fault.retry.RetryStrategy;
import org.zzb.rpc.core.fault.retry.RetryStrategyFactory;
import org.zzb.rpc.core.fault.tolerant.TolerantStrategy;
import org.zzb.rpc.core.fault.tolerant.TolerantStrategyFactory;
import org.zzb.rpc.core.loadbalancer.LoadBalancer;
import org.zzb.rpc.core.loadbalancer.LoadBalancerFactory;
import org.zzb.rpc.core.model.RpcRequest;
import org.zzb.rpc.core.model.RpcResponse;
import org.zzb.rpc.core.model.ServiceMetaInfo;
import org.zzb.rpc.core.protocol.ProtocolConstant;
import org.zzb.rpc.core.protocol.ProtocolMessage;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageDecoder;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageEncoder;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageSerializerEnum;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageTypeEnum;
import org.zzb.rpc.core.registry.Registry;
import org.zzb.rpc.core.registry.RegistryFactory;
import org.zzb.rpc.core.serializer.SerializerFactory;
import org.zzb.rpc.core.serializer.impl.JdkSerializer;
import org.zzb.rpc.core.serializer.Serializer;
import org.zzb.rpc.core.server.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();


        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());

        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);

        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());

        if(CollUtil.isEmpty(serviceMetaInfoList)){
            throw new RuntimeException("暂无服务地址");
        }


        //负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        Map<String,Object> requestParams = new HashMap<>();
        //调用方法名为请求参数
        requestParams.put("methodName",method.getName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);


        /**
         * http服务
         */

//            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
//                    .body(bytes)
//                    .execute() ){
//                byte[] res = httpResponse.bodyBytes();
//                RpcResponse rpcResponse = serializer.deserialize(res, RpcResponse.class);
//                return rpcResponse.getData();
//            }


        /**
         * tcp服务
         */

        //重试机制
        RpcResponse rpcResponse=null;
        try {
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            rpcResponse = retryStrategy.doRetry(()->
                    VertxTcpClient.doRequest(rpcRequest,selectedServiceMetaInfo)
            );
        }catch (Exception e){
            //容错
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            Map<String, Object> context = null;
            if(rpcConfig.getTolerantStrategy().equals("failOver")){
                context = new HashMap<>();
                context.put("rpcConfig",rpcConfig);
                context.put("rpcRequest",rpcRequest);
                context.put("serviceMetaInfoList",serviceMetaInfoList);
                context.put("selectedServiceMetaInfo",selectedServiceMetaInfo);
            }

            rpcResponse = tolerantStrategy.doTolerant(context, e);
        }

        return rpcResponse.getData();
    }
}
