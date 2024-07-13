package org.zzb.rpc.core.fault.tolerant.impl;

import lombok.extern.slf4j.Slf4j;
import org.zzb.rpc.core.fault.tolerant.TolerantStrategy;
import org.zzb.rpc.core.model.RpcResponse;

import java.util.Map;

@Slf4j
public class FailSafeTolerantStategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
      log.info("静默处理异常",e);
      return new RpcResponse();
    }
}