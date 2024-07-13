package org.zzb.rpc.core.fault.tolerant;

import org.zzb.rpc.core.fault.tolerant.impl.FailFastTolerantStrategy;
import org.zzb.rpc.core.spi.SpiLoader;

public class TolerantStrategyFactory {

    static {
        SpiLoader.load(TolerantStrategy.class);
    }

    private static final TolerantStrategy DEFAULT_RETRY_STRATEGY = new FailFastTolerantStrategy();

    public static TolerantStrategy getInstance(String key){
        return SpiLoader.getInstance(TolerantStrategy.class,key);
    }

}
