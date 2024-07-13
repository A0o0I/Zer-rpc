package org.zzb.common.model;

import java.io.Serializable;
/*
    用户
 */
public class User implements Serializable {

    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
