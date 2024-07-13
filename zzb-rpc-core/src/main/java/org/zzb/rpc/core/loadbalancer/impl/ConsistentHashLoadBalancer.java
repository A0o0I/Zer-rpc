package org.zzb.rpc.core.loadbalancer.impl;

import org.zzb.rpc.core.loadbalancer.LoadBalancer;
import org.zzb.rpc.core.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {

    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();

    private static final int VIRTUAL_NODE_NUM = 100;


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {

        if (serviceMetaInfoList.isEmpty()){
            return null;
        }


        for (int i = 0; i < serviceMetaInfoList.size(); i++) {
            for (int j = 0; j < VIRTUAL_NODE_NUM; j++) {
                int hash = getHash(serviceMetaInfoList.get(i).getServiceAddress()+"#"+j);
                virtualNodes.put(hash,serviceMetaInfoList.get(i));
            }
        }

        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(getHash(requestParams));
        if(entry == null){
            virtualNodes.firstEntry();
        }

        return entry.getValue();
    }

    private int getHash(Object key){
        return key.hashCode();
    }

}
