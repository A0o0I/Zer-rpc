package org.zzb.rpc.core.server.tcp;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.zzb.rpc.core.model.RpcRequest;
import org.zzb.rpc.core.model.RpcResponse;
import org.zzb.rpc.core.protocol.ProtocolMessage;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageDecoder;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageEncoder;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageTypeEnum;
import org.zzb.rpc.core.registry.impl.LocalRegistry;

import java.io.IOException;
import java.lang.reflect.Method;

public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket socket) {
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
            //处理链接
            buffer->{

                //接收请求解码
                ProtocolMessage<RpcRequest> protocolMessage;
                try {
                    protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
                }catch (IOException e){
                    throw new RuntimeException("协议消息解码错误");
                }
                RpcRequest rpcRequest = protocolMessage.getBody();

                //处理请求 响应结果
                RpcResponse rpcResponse = new RpcResponse();

                try {
                    Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                    Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                    Object res = method.invoke(implClass.newInstance(),rpcRequest.getArgs());

                    rpcResponse.setData(res);
                    rpcResponse.setDataType(method.getReturnType());
                    rpcResponse.setMessage("ok");
                }catch (Exception e){
                    e.printStackTrace();
                    rpcResponse.setMessage(e.getMessage());
                    rpcResponse.setException(e);
                }

                //编码发送响应
                ProtocolMessage.Header header = protocolMessage.getHeader();

                header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
                ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header,rpcResponse);
                try {
                    Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                    socket.write(encode);
                }catch (IOException e){
                    throw new RuntimeException("协议消息编码错误");
                }

            });
        socket.handler(bufferHandlerWrapper);

    }
}
