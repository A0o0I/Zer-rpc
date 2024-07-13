package org.zzb.provider;

import org.zzb.common.model.User;
import org.zzb.common.service.UserService;
/*
    用户服务实现
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名为 "+user.getName());
        return user;
    }
}
