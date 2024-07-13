package org.zzb.rpc.core.loadbalancer;

import org.zzb.rpc.core.loadbalancer.impl.RoundRobinLoadBalancer;
import org.zzb.rpc.core.spi.SpiLoader;

public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    private static volatile LoadBalancer DEFAULT_LOADBALANCER = new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key){
        return SpiLoader.getInstance(LoadBalancer.class,key);
    }

}
