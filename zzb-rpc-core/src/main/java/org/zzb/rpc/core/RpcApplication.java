package org.zzb.rpc.core;

import lombok.extern.slf4j.Slf4j;
import org.zzb.rpc.core.config.RegistryConfig;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.constant.RpcConstant;
import org.zzb.rpc.core.registry.Registry;
import org.zzb.rpc.core.registry.RegistryFactory;
import org.zzb.rpc.core.utils.ConfigUtils;

@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init ,config = {}",newRpcConfig.toString());

        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    public static void init(){

        RpcConfig newRpcConfig;

        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }

        init(newRpcConfig);

    }


    public static RpcConfig getRpcConfig(){
        if(rpcConfig==null){
            synchronized (RpcApplication.class){
                if(rpcConfig==null){
                    init();
                }
            }
        }
        return rpcConfig;


    }

}
