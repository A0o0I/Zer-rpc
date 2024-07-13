package org.zzb.springbootprovider;

import org.springframework.stereotype.Service;
import org.zzb.common.model.User;
import org.zzb.common.service.UserService;
import org.zzb.rpc.springboot.starter.annotation.RpcService;

@Service
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
