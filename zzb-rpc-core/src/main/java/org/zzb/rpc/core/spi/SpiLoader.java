package org.zzb.rpc.core.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import io.netty.util.internal.ResourcesUtil;
import lombok.extern.slf4j.Slf4j;
import org.zzb.rpc.core.serializer.Serializer;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {

    private static Map<String,Map<String,Class<?>>> loaderMap = new ConcurrentHashMap<>();

    private static Map<String,Object> instanceCache = new ConcurrentHashMap<>();

    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    private static final String[] SCAN_DIR = new String[]{RPC_CUSTOM_SPI_DIR,RPC_SYSTEM_SPI_DIR};

    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    public static void loadAll(){
        log.info("加载所有SPI....");

        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }

    }

    public static <T> T getInstance(Class<?> type,String key){



        String typeName = type.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(typeName);

        if(keyClassMap==null){
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", typeName));
        }

        if(!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", typeName, key));
        }

        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();

        if(!instanceCache.containsKey(implClassName)){

            try {
                instanceCache.put(implClassName,implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }

        }
        return (T) instanceCache.get(implClassName);
    }



    public static Map<String,Class<?>> load(Class<?> loadClass) {

        log.info("加载类型为 {} 的SPI",loadClass.getName());

        Map<String,Class<?>> keyClassMap = new HashMap<>();

        for (String dir : SCAN_DIR) {

            List<URL> resources = ResourceUtil.getResources(dir+loadClass.getName());
            for (URL resource : resources) {

                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null){
                        String[] split = line.split("=");
                        if(split.length>1){
                            String key = split[0];
                            String className = split[1];
                            keyClassMap.put(key,Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error", e);
                    e.printStackTrace();
                }

            }
        }

        loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;

    }


}
