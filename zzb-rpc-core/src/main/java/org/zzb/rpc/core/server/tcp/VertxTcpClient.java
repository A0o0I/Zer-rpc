package org.zzb.rpc.core.server.tcp;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.zzb.rpc.core.RpcApplication;
import org.zzb.rpc.core.model.RpcRequest;
import org.zzb.rpc.core.model.RpcResponse;
import org.zzb.rpc.core.model.ServiceMetaInfo;
import org.zzb.rpc.core.protocol.ProtocolConstant;
import org.zzb.rpc.core.protocol.ProtocolMessage;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageDecoder;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageEncoder;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageSerializerEnum;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageTypeEnum;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VertxTcpClient {

    public void start(){

        Vertx vertx = Vertx.vertx();
        vertx.createNetClient().connect(8888,"localhost",result->{

            if(result.succeeded()){
                System.out.println("Connected to TCP server");
                io.vertx.core.net.NetSocket socket = result.result();
                for (int i = 0; i < 1000; i++) {
                    // 发送数据
                    Buffer buffer = Buffer.buffer();
                    String str = "Hello, server!Hello, server!Hello, server!Hello, server!";
                    buffer.appendInt(0);
                    buffer.appendInt(str.getBytes().length);
                    buffer.appendBytes(str.getBytes());
                    socket.write(buffer);
                }
                socket.handler(buffer -> {
                    System.out.println("Received response from server: " + buffer.toString());
                });
            }else {
                System.err.println("Failed to connect to TCP server");
            }

        });

    }

    public static void main(String[] args) {
        new VertxTcpClient().start();
    }

    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws InterruptedException, ExecutionException {

        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

        netClient.connect(serviceMetaInfo.getServicePort(),serviceMetaInfo.getServiceHost(),
                result->{
                    if(!result.succeeded()){
                        System.err.println("Failed to connect to TCP server");
                        return;
                    }
                    NetSocket socket = result.result();

                    ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                    ProtocolMessage.Header header = new ProtocolMessage.Header();
                    header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                    header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                    header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                    header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
                    header.setRequestId(IdUtil.getSnowflakeNextId());

                    protocolMessage.setHeader(header);
                    protocolMessage.setBody(rpcRequest);

                    try {
                        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                        socket.write(encodeBuffer);
                    } catch (IOException e) {
                        throw new RuntimeException("协议消息解码错误");
                    }


                    TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(
                            buffer->{
                                try {
                                    ProtocolMessage<RpcResponse> rpcResponseProtocolMessage =
                                            (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    responseFuture.complete(rpcResponseProtocolMessage.getBody());
                                }catch (IOException e){
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            }
                    );
                    socket.handler(bufferHandlerWrapper);
                });
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = responseFuture.get(10L, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new RuntimeException("请求超时未响应");
        }

        netClient.close();
        return rpcResponse;
    }

}