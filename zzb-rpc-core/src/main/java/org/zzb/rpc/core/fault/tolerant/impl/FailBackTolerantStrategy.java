package org.zzb.rpc.core.fault.tolerant.impl;

import lombok.extern.slf4j.Slf4j;
import org.zzb.rpc.core.fault.tolerant.TolerantStrategy;
import org.zzb.rpc.core.model.RpcResponse;

import java.util.Map;

@Slf4j
public class FailBackTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // todo 可自行扩展，获取降级的服务并调用

        return null;
    }
}