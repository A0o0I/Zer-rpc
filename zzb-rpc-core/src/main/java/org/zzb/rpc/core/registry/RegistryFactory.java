package org.zzb.rpc.core.registry;

import org.zzb.rpc.core.registry.impl.EtcdRegistry;
import org.zzb.rpc.core.spi.SpiLoader;

public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    private static volatile Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getInstance(String key){
        return SpiLoader.getInstance(Registry.class,key);
    }

}
