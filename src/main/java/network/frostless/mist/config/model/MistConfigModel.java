package network.frostless.mist.config.model;


import lombok.Data;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Data
@ConfigSerializable
public class MistConfigModel {

    private String guildId = "945581170157051914";

    private AutoRoleModel autoRole = new AutoRoleModel();

}
