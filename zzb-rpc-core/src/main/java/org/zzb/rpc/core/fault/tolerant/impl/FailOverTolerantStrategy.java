package org.zzb.rpc.core.fault.tolerant.impl;

import lombok.extern.slf4j.Slf4j;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.fault.tolerant.TolerantStrategy;
import org.zzb.rpc.core.loadbalancer.LoadBalancer;
import org.zzb.rpc.core.loadbalancer.LoadBalancerFactory;
import org.zzb.rpc.core.model.RpcRequest;
import org.zzb.rpc.core.model.RpcResponse;
import org.zzb.rpc.core.model.ServiceMetaInfo;
import org.zzb.rpc.core.server.tcp.VertxTcpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class FailOverTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {

        RpcRequest rpcRequest = (RpcRequest) context.get("rpcRequest");

        List<ServiceMetaInfo> serviceMetaInfoList = (List<ServiceMetaInfo>) context.get("serviceMetaInfoList");

        ServiceMetaInfo FailedServiceMetaInfo = (ServiceMetaInfo) context.get("selectedServiceMetaInfo");

        serviceMetaInfoList.remove(FailedServiceMetaInfo);

        RpcConfig rpcConfig = (RpcConfig) context.get("rpcConfig");

        //负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        Map<String,Object> requestParams = new HashMap<>();
        //调用方法名为请求参数
        requestParams.put("methodName",rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

        try {
            return VertxTcpClient.doRequest(rpcRequest,selectedServiceMetaInfo);
        }catch (Exception exception) {
            log.error("FailOver容错处理失败",e);
        }

        return null;
    }
}
