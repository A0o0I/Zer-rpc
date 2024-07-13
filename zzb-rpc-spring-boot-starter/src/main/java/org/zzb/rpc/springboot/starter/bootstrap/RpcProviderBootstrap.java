package org.zzb.rpc.springboot.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.zzb.rpc.core.RpcApplication;
import org.zzb.rpc.core.config.RegistryConfig;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.model.ServiceMetaInfo;
import org.zzb.rpc.core.registry.Registry;
import org.zzb.rpc.core.registry.RegistryFactory;
import org.zzb.rpc.core.registry.impl.LocalRegistry;
import org.zzb.rpc.springboot.starter.annotation.RpcReference;
import org.zzb.rpc.springboot.starter.annotation.RpcService;

import java.io.IOException;

@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);

        if(rpcService != null){
            Class<?> interfaceClass = rpcService.interfaceClass();
            if(interfaceClass == void.class){
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            //本地注册
            LocalRegistry.register(serviceName,beanClass);

            //注册中心
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());

            try {
                registry.register(serviceMetaInfo);
            }catch (Exception e){
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }

        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean,beanName);
    }
}
