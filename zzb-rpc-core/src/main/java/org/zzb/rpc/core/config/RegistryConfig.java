package org.zzb.rpc.core.config;

import lombok.Data;

@Data
public class RegistryConfig {

    private String registry = "etcd";

    private String address = "http://47.120.7.112:2379";

    private String username;

    private String password;

    private Long timeout = 10000L;

}
