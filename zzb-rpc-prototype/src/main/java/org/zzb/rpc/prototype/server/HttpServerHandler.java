package org.zzb.rpc.prototype.server;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.zzb.rpc.prototype.model.RpcRequest;
import org.zzb.rpc.prototype.model.RpcResponse;
import org.zzb.rpc.prototype.registry.LocalRegistry;
import org.zzb.rpc.prototype.serializer.JdkSerializer;
import org.zzb.rpc.prototype.serializer.Serializer;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {


    @Override
    public void handle(HttpServerRequest request) {
        final Serializer serializer = new JdkSerializer();

        System.out.println("Received request: " + request.method() + " " + request.uri());


        request.bodyHandler(body->{

            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            RpcResponse rpcResponse = new RpcResponse();

            if(rpcRequest == null){
                rpcResponse.setMessage("rpcReques is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            try {
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object res = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                rpcResponse.setData(res);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
            }catch (Exception e){
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(request,rpcResponse,serializer);
        });

    }

    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type","application/json");

        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }

    }


}
