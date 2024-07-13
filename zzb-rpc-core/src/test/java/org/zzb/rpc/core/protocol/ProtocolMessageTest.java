package org.zzb.rpc.core.protocol;

import cn.hutool.core.util.IdUtil;
import io.vertx.core.buffer.Buffer;
import org.junit.Assert;
import org.junit.Test;
import org.zzb.rpc.core.constant.RpcConstant;
import org.zzb.rpc.core.model.RpcRequest;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageDecoder;
import org.zzb.rpc.core.protocol.coder.ProtocolMessageEncoder;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageSerializerEnum;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageStatusEnum;
import org.zzb.rpc.core.protocol.enums.ProtocolMessageTypeEnum;
import org.zzb.rpc.core.serializer.Serializer;
import org.zzb.rpc.core.serializer.SerializerFactory;

import java.io.IOException;

public class ProtocolMessageTest {

    @Test
    public void testEncodeAndDecode() throws IOException {
        // 构造消息
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) ProtocolMessageSerializerEnum.JDK.getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
        header.setRequestId(IdUtil.getSnowflakeNextId());
        header.setBodyLength(0);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName("myService");
        rpcRequest.setMethodName("myMethod");
        rpcRequest.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        rpcRequest.setParameterTypes(new Class[]{String.class});
        rpcRequest.setArgs(new Object[]{"aaa", "bbb"});
        protocolMessage.setHeader(header);

        Serializer serializer = SerializerFactory.getInstance("jdk");
        byte[] bytes = serializer.serialize(rpcRequest);
        header.setBodyLength(bytes.length);
        protocolMessage.setBody(rpcRequest);


        Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
        ProtocolMessage<?> message = ProtocolMessageDecoder.decode(encodeBuffer);
        Assert.assertNotNull(message);
    }

}
