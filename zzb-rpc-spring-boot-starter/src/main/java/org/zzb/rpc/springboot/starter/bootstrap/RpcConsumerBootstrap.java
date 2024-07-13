package org.zzb.rpc.springboot.starter.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.zzb.rpc.core.proxy.ServiceProxyFactory;
import org.zzb.rpc.springboot.starter.annotation.RpcReference;

import java.lang.reflect.Field;

@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();

        for (Field field : declaredFields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if(rpcReference != null){
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if(interfaceClass == void.class){
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxy = ServiceProxyFactory.getProxy(interfaceClass);

                try {
                    field.set(bean,proxy);
                    field.setAccessible(false);
                }catch (IllegalAccessException e){
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }


        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
