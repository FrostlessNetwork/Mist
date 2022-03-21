package network.frostless.mist.config.model;

import lombok.Data;
import network.frostless.frostcore.messaging.redis.RedisCredentials;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class RedisModel {

    private String host;
    private int port;

    @Nullable
    private String password;

    private Integer database;


    RedisModel() { }

    public RedisCredentials get() {
        return new RedisCredentials(host, port, password, database);
    }
}
