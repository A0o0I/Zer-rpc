package org.zzb.rpc.core.config;

import lombok.Data;
import org.zzb.rpc.core.fault.retry.RetryStrategyKeys;
import org.zzb.rpc.core.fault.tolerant.TolerantStrategyKeys;
import org.zzb.rpc.core.loadbalancer.LoadBalancer;
import org.zzb.rpc.core.loadbalancer.LoadBalancerKeys;
import org.zzb.rpc.core.serializer.Serializer;
import org.zzb.rpc.core.serializer.SerializerKeys;

@Data
public class RpcConfig {

    private String name = "zzb-rpc";

    private String version = "1.0";

    private String serverHost = "localhost";

    private Integer serverPort = 8080;

    private boolean mock = false;

    private String serializer = SerializerKeys.JDK;

    private RegistryConfig registryConfig = new RegistryConfig();

    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;

    private String retryStrategy = RetryStrategyKeys.FIXED_INTERVAL;

    private String tolerantStrategy = TolerantStrategyKeys.FAIL_OVER;

}
