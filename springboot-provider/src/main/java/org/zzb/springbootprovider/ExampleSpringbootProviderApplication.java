package org.zzb.springbootprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zzb.rpc.springboot.starter.annotation.EnableRpc;

@EnableRpc
@SpringBootApplication
public class ExampleSpringbootProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootProviderApplication.class,args);
    }
}
