package org.zzb.rpc.core.fault.retry;

import org.zzb.rpc.core.model.RpcResponse;

import java.util.concurrent.Callable;

public interface RetryStrategy {

    RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception;

}
