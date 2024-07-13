package org.zzb.rpc.core.protocol;

public interface ProtocolConstant {

    /**
     * 消息头长度，协议魔数，协议版本号
     */

    int MESSAGE_HEADER_LENGTH = 17;

    byte PROTOCOL_MAGIC = 0x1;

    byte PROTOCOL_VERSION = 0x1;


}
