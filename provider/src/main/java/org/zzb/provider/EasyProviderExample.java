package org.zzb.provider;

/*
    简易例子
 */

import org.zzb.common.service.UserService;
import org.zzb.rpc.core.registry.impl.LocalRegistry;
import org.zzb.rpc.core.server.HttpServer;
import org.zzb.rpc.core.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8080);

    }
}
