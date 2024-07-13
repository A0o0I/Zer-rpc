package org.zzb.rpc.core.server.tcp;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
import org.zzb.rpc.core.server.HttpServer;

public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        // 在这里编写处理请求的逻辑，根据 requestData 构造响应数据并返回
        // 这里只是一个示例，实际逻辑需要根据具体的业务需求来实现
        return "Hello, client!".getBytes();
    }

    @Override
    public void doStart(int port) {
        // 创建vertx实例
        Vertx vertx = Vertx.vertx();
        //创建tcp服务器
        NetServer server = vertx.createNetServer();

        //添加请求处理
//        server.connectHandler(netSocket -> {
//            netSocket.handler(buffer -> {
//                //处理接收的数组
//                byte[] requestData = buffer.getBytes();
//                byte[] responseData = handleRequest(requestData);
//                //发送响应
//                netSocket.write(Buffer.buffer(responseData));
//            });
//        });

        //添加请求处理器
        server.connectHandler(new TcpServerHandler());

//        server.connectHandler(socket -> {
//            socket.handler(buffer -> {
//                RecordParser parser = RecordParser.newFixed(8);
//                parser.setOutput(new Handler<Buffer>() {
//
//                    int size = -1;
//                    Buffer resultBuffer = Buffer.buffer();
//
//                    @Override
//                    public void handle(Buffer buffer) {
//
//                        if(size == -1){
//                            size = buffer.getInt(4);
//                            parser.fixedSizeMode(size);
//                            resultBuffer.appendBuffer(buffer);
//                        }else {
//                            resultBuffer.appendBuffer(buffer);
//                            System.out.println(resultBuffer.toString());
//                            // 重置一轮
//                            parser.fixedSizeMode(8);
//                            size = -1;
//                            resultBuffer = Buffer.buffer();
//                        }
//                    }
//                });
//                socket.handler(parser);


//                if (buffer.getBytes().length < messageLength) {
//                    System.out.println("半包, length = " + buffer.getBytes().length);
//                    return;
//                }
//                if (buffer.getBytes().length > messageLength) {
//                    System.out.println("粘包, length = " + buffer.getBytes().length);
//                    return;
//                }
//                String str = new String(buffer.getBytes(0, messageLength));
//                System.out.println(str);
//                if (testMessage.equals(str)) {
//                    System.out.println("good");
//                }
//            });
//
//        });

        server.listen(port,result->{
            if (result.succeeded()) {
                System.out.println("TCP server started on port " + port);
            } else {
                System.err.println("Failed to start TCP server: " + result.cause());
            }
        });

    }


    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }

}
