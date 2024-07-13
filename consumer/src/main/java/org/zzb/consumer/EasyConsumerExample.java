package org.zzb.consumer;

import org.zzb.common.model.User;
import org.zzb.common.service.UserService;
import org.zzb.rpc.core.proxy.ServiceProxyFactory;

public class EasyConsumerExample {
    public static void main(String[] args) {
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

    }
}
