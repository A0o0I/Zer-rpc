package org.zzb.springbootconsumer;


import org.springframework.stereotype.Service;
import org.zzb.common.model.User;
import org.zzb.common.service.UserService;
import org.zzb.rpc.springboot.starter.annotation.RpcReference;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test(){
        User user = new User();
        user.setName("zzzzzb");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}
