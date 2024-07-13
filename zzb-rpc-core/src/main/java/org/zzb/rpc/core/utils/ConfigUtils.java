package org.zzb.rpc.core.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

public class ConfigUtils {

    public static <T> T loadConfig(Class<T> tClass,String prfix){
        return loadConfig(tClass,prfix,"");
    }

    public static <T> T loadConfig(Class<T> tClass,String prfix,String enviironment){
        StringBuilder configFileBuilder = new StringBuilder("application");

        if(StrUtil.isNotBlank(enviironment)){
            configFileBuilder.append("-").append(enviironment);
        }

        configFileBuilder.append(".properties");

        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass,prfix);

    }


}
