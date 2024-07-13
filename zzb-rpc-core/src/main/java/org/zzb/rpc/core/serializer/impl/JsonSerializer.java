package org.zzb.rpc.core.serializer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.zzb.rpc.core.model.RpcRequest;
import org.zzb.rpc.core.model.RpcResponse;
import org.zzb.rpc.core.serializer.Serializer;

import java.io.IOException;

public class JsonSerializer implements Serializer {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T object = OBJECT_MAPPER.readValue(bytes,type);

        if(object instanceof RpcResponse){
            return handleResponse((RpcResponse)object,type);
        }
        if(object instanceof RpcRequest){
            return handleRequest((RpcRequest)object,type);
        }

        return object;
    }

    private <T> T handleRequest(RpcRequest object, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = object.getParameterTypes();
        Object[] args = object.getArgs();

        for(int i=0;i<parameterTypes.length;i++){
            Class<?> clazz = parameterTypes[i];
            if(!clazz.isAssignableFrom(args[i].getClass())){
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }
        return type.cast(object);
    }

    private <T> T handleResponse(RpcResponse object, Class<T> type) throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(object.getData());
        object.setData(OBJECT_MAPPER.readValue(dataBytes, object.getDataType()));
        return type.cast(object);
    }
}
