package org.zzb.rpc.core.protocol.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

@Getter
public enum ProtocolMessageSerializerEnum {

    JDK(0,"jdk"),
    JSON(1,"json"),
    KRYO(2,"kryo"),
    HESSIAN(3,"hessian");

    private final int key;

    private final String value;

    ProtocolMessageSerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }


    public static ProtocolMessageSerializerEnum getEnumByKey(int key){
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if(anEnum.key==key){
                return anEnum;
            }
        }
        return null;
    }

    public static ProtocolMessageSerializerEnum getEnumByValue(String value){
        if (ObjectUtil.isEmpty(value)){
            return null;
        }
        for (ProtocolMessageSerializerEnum anEnum : ProtocolMessageSerializerEnum.values()) {
            if(anEnum.value.equals(value)){
                return anEnum;
            }
        }
        return null;

    }

}
