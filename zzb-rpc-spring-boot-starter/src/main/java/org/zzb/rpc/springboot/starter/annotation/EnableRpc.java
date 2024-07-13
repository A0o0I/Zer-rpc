package org.zzb.rpc.springboot.starter.annotation;


import org.springframework.context.annotation.Import;
import org.zzb.rpc.springboot.starter.bootstrap.RpcConsumerBootstrap;
import org.zzb.rpc.springboot.starter.bootstrap.RpcInitBootstrap;
import org.zzb.rpc.springboot.starter.bootstrap.RpcProviderBootstrap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcProviderBootstrap.class, RpcConsumerBootstrap.class})
public @interface EnableRpc {

    boolean needServer() default true;

}
