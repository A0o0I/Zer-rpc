package org.zzb.common.service;

import org.zzb.common.model.User;
/*
    用户服务
 */
public interface UserService {

    User getUser(User user);

    default short getNumber(){
        return 1;
    }

}
