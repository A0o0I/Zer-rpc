package org.zzb.consumer;

import org.zzb.common.model.User;
import org.zzb.common.service.UserService;
import org.zzb.rpc.core.bootstrap.ConsumerBootStrap;
import org.zzb.rpc.core.config.RpcConfig;
import org.zzb.rpc.core.proxy.ServiceProxyFactory;
import org.zzb.rpc.core.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        ConsumerBootStrap.init();
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);

        User user = new User();
        user.setName("66666");
        User newUser = userService.getUser(user);
        if(newUser != null){
            System.out.println(newUser.getName());
        }
        else {
            System.out.println("user == null");
        }

        long number = userService.getNumber();
        System.out.println(number);


    }
}
