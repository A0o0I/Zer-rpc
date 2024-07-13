package org.zzb.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头
     */
    private Header header;

    private T body;



    @Data
    public static class Header{

        /**
         *  魔术，版本号，序列号器，
         *  消息类型（请求，响应），
         *  状态，请求id，消息体长度
         */

        private byte magic;

        private byte version;

        private byte serializer;

        private byte type;

        private byte status;

        private long requestId;

        private int bodyLength;

    }

}
