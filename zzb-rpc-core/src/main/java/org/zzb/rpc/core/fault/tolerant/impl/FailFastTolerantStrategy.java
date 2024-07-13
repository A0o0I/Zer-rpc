package org.zzb.rpc.core.fault.tolerant.impl;

import org.zzb.rpc.core.fault.tolerant.TolerantStrategy;
import org.zzb.rpc.core.model.RpcResponse;

import java.util.Map;

public class FailFastTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务错误",e);
    }
}
