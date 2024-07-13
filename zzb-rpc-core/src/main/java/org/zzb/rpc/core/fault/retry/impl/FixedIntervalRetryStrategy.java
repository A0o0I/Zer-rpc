package org.zzb.rpc.core.fault.retry.impl;

import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import lombok.extern.slf4j.Slf4j;
import org.zzb.rpc.core.fault.retry.RetryStrategy;
import org.zzb.rpc.core.model.RpcResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {


    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException,RetryException {

        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class)  //重试条件
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) //重试策略
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) //暂停策略
                .withRetryListener(new RetryListener() {  //监听器
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("尝试次数 {}",attempt.getAttemptNumber());
                    }
                })
                .build();

        return retryer.call(callable);
    }
}
