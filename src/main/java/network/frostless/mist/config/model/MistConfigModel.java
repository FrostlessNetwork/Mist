package network.frostless.mist.config.model;


import lombok.Data;
import network.frostless.mist.config.model.polling.PollingConfigModel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class MistConfigModel {

    private String guildId = "945581170157051914";

    private AutoRoleModel autoRole = new AutoRoleModel();

    private PollingConfigModel voting = new PollingConfigModel();

    private RedisModel redis;

}
