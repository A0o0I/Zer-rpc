package org.zzb.provider;

import org.zzb.common.service.UserService;
import org.zzb.rpc.core.RpcApplication;
import org.zzb.rpc.core.bootstrap.ProviderBootStrap;
import org.zzb.rpc.core.config.RegistryConfig;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.model.ServiceMetaInfo;
import org.zzb.rpc.core.model.ServiceRegisterInfo;
import org.zzb.rpc.core.registry.Registry;
import org.zzb.rpc.core.registry.RegistryFactory;
import org.zzb.rpc.core.registry.impl.LocalRegistry;
import org.zzb.rpc.core.server.HttpServer;
import org.zzb.rpc.core.server.VertxHttpServer;
import org.zzb.rpc.core.server.tcp.VertxTcpClient;
import org.zzb.rpc.core.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

public class ProviderExample {

//    public static void main(String[] args) {
//        //框架初始化
//        RpcApplication.init();
//
//        //注册服务
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
//
//        //注册到注册中心
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//
//        try {
//            registry.register(serviceMetaInfo);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
////        HttpServer httpServer = new VertxHttpServer();
////        httpServer.doStart(RpcApplication.rpcConfig.getServerPort());
////
//
//        //启动web服务
//        VertxTcpServer vertxTcpServer = new VertxTcpServer();
//        vertxTcpServer.doStart(8080);
//
//    }

    public static void main(String[] args) {

        List<ServiceRegisterInfo> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo<>(UserService.class.getName(),UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        ProviderBootStrap.init(serviceRegisterInfoList);
    }

}
