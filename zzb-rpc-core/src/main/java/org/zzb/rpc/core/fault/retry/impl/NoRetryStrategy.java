package org.zzb.rpc.core.fault.retry.impl;

import lombok.extern.slf4j.Slf4j;
import org.zzb.rpc.core.fault.retry.RetryStrategy;
import org.zzb.rpc.core.model.RpcResponse;

import java.util.concurrent.Callable;

@Slf4j
public class NoRetryStrategy implements RetryStrategy {
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
