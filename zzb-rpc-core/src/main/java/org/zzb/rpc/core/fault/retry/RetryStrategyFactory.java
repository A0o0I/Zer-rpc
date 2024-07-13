package org.zzb.rpc.core.fault.retry;

import org.zzb.rpc.core.fault.retry.impl.NoRetryStrategy;
import org.zzb.rpc.core.spi.SpiLoader;

public class RetryStrategyFactory {

    static {
        SpiLoader.load(RetryStrategy.class);
    }

    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();

    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class,key);
    }

}
