package org.zzb.rpc.core.loadbalancer.impl;

import org.zzb.rpc.core.loadbalancer.LoadBalancer;
import org.zzb.rpc.core.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        int size = serviceMetaInfoList.size();
        if(size == 0){
            return null;
        }
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }

        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }

//    public static void main(String[] args) {
//
//        List<ServiceMetaInfo> serviceMetaInfoList = new ArrayList<>();
//
//        ServiceMetaInfo s1 = new ServiceMetaInfo();
//        s1.setServiceName("1");
//        serviceMetaInfoList.add(s1);
//
//        ServiceMetaInfo s2 = new ServiceMetaInfo();
//        s2.setServiceName("2");
//        serviceMetaInfoList.add(s2);
//
//        ServiceMetaInfo s3= new ServiceMetaInfo();
//        s3.setServiceName("3");
//        serviceMetaInfoList.add(s3);
//
//        ServiceMetaInfo s4 = new ServiceMetaInfo();
//        s4.setServiceName("4");
//        serviceMetaInfoList.add(s4);
//
//        RoundRobinLoadBalancer roundRobinLoadBalancer = new RoundRobinLoadBalancer();
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//        System.out.println(roundRobinLoadBalancer.select(null, serviceMetaInfoList).getServiceName());
//
//    }
}
