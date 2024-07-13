package org.zzb.rpc.prototype.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  本地注册中心
 */
public class LocalRegistry {

    private static final Map<String,Class<?>> servicesMap = new ConcurrentHashMap<>();

    /**
     * 获取服务
     * @param serviceName
     * @param implClass
     */
    public static void register(String serviceName,Class<?> implClass){
        servicesMap.put(serviceName,implClass);
    }


    /**
     * 获取服务
     * @param serviceName
     * @return
     */
    public static Class<?> get(String serviceName){
        return servicesMap.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName
     * @return
     */
    public static void remove(String serviceName){
        servicesMap.remove(serviceName);
    }



}
