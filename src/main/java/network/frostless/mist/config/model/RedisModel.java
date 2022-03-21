package network.frostless.mist.config.model;

import lombok.Data;
import lombok.Getter;
import network.frostless.frostcore.messaging.redis.RedisCredentials;
import org.jetbrains.annotations.Nullable;

@Getter
@Data
public class RedisModel {

    private final String host;
    private final int port;

    @Nullable
    private final String password;

    private final Integer database;


    public RedisCredentials get() {
        return new RedisCredentials(host, port, password, database);
    }
}
