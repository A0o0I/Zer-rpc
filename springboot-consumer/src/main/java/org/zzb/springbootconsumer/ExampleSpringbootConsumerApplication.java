package org.zzb.springbootconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.zzb.rpc.springboot.starter.annotation.EnableRpc;

@EnableRpc(needServer = false)
@SpringBootApplication
public class ExampleSpringbootConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
    }

}
