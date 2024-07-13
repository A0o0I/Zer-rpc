package org.zzb.rpc.core.bootstrap;

import io.vertx.core.Vertx;
import org.zzb.rpc.core.RpcApplication;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.model.ServiceMetaInfo;
import org.zzb.rpc.core.model.ServiceRegisterInfo;
import org.zzb.rpc.core.registry.Registry;
import org.zzb.rpc.core.registry.RegistryFactory;
import org.zzb.rpc.core.registry.impl.LocalRegistry;
import org.zzb.rpc.core.server.tcp.VertxTcpServer;


import java.util.List;

public class ProviderBootStrap {

    public static void init(List<ServiceRegisterInfo> serviceRegisterInfoList){

        //初始化
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //注册服务
        for (ServiceRegisterInfo serviceRegisterInfo : serviceRegisterInfoList) {
            //本地注册
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            //注册中心注册
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败",e);
            }
            //启动web服务器
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());

        }


    }

}
