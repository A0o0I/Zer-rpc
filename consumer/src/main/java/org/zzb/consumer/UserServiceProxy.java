package org.zzb.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.zzb.common.model.User;
import org.zzb.common.service.UserService;
import org.zzb.rpc.core.model.RpcRequest;
import org.zzb.rpc.core.model.RpcResponse;
import org.zzb.rpc.core.serializer.impl.JdkSerializer;
import org.zzb.rpc.core.serializer.Serializer;

import java.io.IOException;

/**
 * 静态代理
 */

public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {

        Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();

        try {
            byte[] serialized = serializer.serialize(rpcRequest);
            byte[] res;

            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8080")
                    .body(serialized)
                    .execute()
            ){
                res = httpResponse.bodyBytes();
            }

            RpcResponse deserialized = serializer.deserialize(res, RpcResponse.class);

            return (User) deserialized.getData();

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;

    }
}
