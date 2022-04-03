package network.frostless.mist.config.model;


import lombok.Data;
import network.frostless.mist.config.model.common.AdminModel;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class MistConfigModel {

    private String guildId = "945581170157051914";

    private String suggestionChannel = "956741310189043712";

    private AutoRoleModel autoRole = new AutoRoleModel();

    private RedisModel redis;

    private AdminModel suggestionAdmins = new AdminModel();

    private String suggestionArchive = "945951848085483540";

}
