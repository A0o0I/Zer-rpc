package org.zzb.rpc.core.protocol.coder;

import io.vertx.core.buffer.Buffer;
import org.zzb.rpc.core.protocol.ProtocolMessage;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageSerializerEnum;
import org.zzb.rpc.core.serializer.Serializer;
import org.zzb.rpc.core.serializer.SerializerFactory;

import java.io.IOException;

public class ProtocolMessageEncoder {

    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException{

        if(protocolMessage==null||protocolMessage.getHeader()==null){
            return Buffer.buffer();
        }

        ProtocolMessage.Header header = protocolMessage.getHeader();
        //添加header到buffer
        Buffer buffer = Buffer.buffer();
        appendHeader(buffer,header);

        // 获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum==null){
            throw new RuntimeException("序列化协议不存在");
        }

        //序列号body数据
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());

        //写入body长度和数据
        buffer.appendInt(bodyBytes.length);
        buffer.appendBytes(bodyBytes);
        return buffer;
    }

    private static void appendHeader(Buffer buffer, ProtocolMessage.Header header) {
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getSerializer());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendLong(header.getRequestId());
    }

}
