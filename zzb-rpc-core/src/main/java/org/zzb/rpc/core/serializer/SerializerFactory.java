package org.zzb.rpc.core.serializer;

import org.zzb.rpc.core.serializer.impl.HessianSerializer;
import org.zzb.rpc.core.serializer.impl.JdkSerializer;
import org.zzb.rpc.core.serializer.impl.JsonSerializer;
import org.zzb.rpc.core.serializer.impl.KryoSerializer;
import org.zzb.rpc.core.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {


    static {
        SpiLoader.load(Serializer.class);
    }

    private static volatile Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    public static Serializer getInstance(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }



//    private static volatile Map<String,Serializer> KEY_SERIALIZER_MAP;
//
//    private static volatile Serializer DEFAULT_SERIALIZER;
//
//    private static void init(){
//        if(KEY_SERIALIZER_MAP==null){
//            synchronized (SerializerFactory.class){
//                KEY_SERIALIZER_MAP = new HashMap<String,Serializer>(){{
//                    put(SerializerKeys.JDK,new JdkSerializer());
//                    put(SerializerKeys.HESSIAN,new HessianSerializer());
//                    put(SerializerKeys.JSON,new JsonSerializer());
//                    put(SerializerKeys.KRYO,new KryoSerializer());
//                }};
//                DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);
//            }
//        }
//    }
//
//    public static Serializer getInstance(String key){
//        if(KEY_SERIALIZER_MAP==null){
//            init();
//        }
//        return KEY_SERIALIZER_MAP.getOrDefault(key,DEFAULT_SERIALIZER);
//    }

}
