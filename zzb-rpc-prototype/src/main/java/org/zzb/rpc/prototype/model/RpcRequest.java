package org.zzb.rpc.prototype.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zzb.rpc.prototype.serializer.Serializer;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    private String serviceName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] args;


}
