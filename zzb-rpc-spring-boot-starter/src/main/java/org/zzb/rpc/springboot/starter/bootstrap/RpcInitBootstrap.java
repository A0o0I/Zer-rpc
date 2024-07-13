package org.zzb.rpc.springboot.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.zzb.rpc.core.RpcApplication;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.server.tcp.VertxTcpServer;
import org.zzb.rpc.springboot.starter.annotation.EnableRpc;

@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        //获取EnableRpc注解属性值
        boolean needServer =(boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        //初始化rpc框架
        RpcApplication.init();

        //得到全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        //启动服务器
        if(needServer){
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
        }else {
            log.info("不启动 server");
        }

    }
}
