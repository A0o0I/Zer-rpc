package org.zzb.rpc.core.fault.tolerant;

import org.zzb.rpc.core.model.RpcResponse;

import java.util.Map;

public interface TolerantStrategy {
    RpcResponse doTolerant(Map<String,Object> context,Exception e);
}
