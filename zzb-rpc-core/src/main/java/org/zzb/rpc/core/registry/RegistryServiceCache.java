package org.zzb.rpc.core.registry;

import org.zzb.rpc.core.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {

    List<ServiceMetaInfo> serviceCache;

    public void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.serviceCache=newServiceCache;
    }

    public void clearCache(){
        this.serviceCache=null;
    }

    public List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }


}
