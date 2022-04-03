package network.frostless.mist.config.model.common;


import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigSerializable
public class AdminModel {

    private List<String> users = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    public boolean exact(Member member) {
        if (users.contains(member.getUser().getId())) {
            return true;
        }
        for (String roleId : roles) {
            if (member.getRoles().stream().anyMatch(r -> r.getId().equals(roleId))) {
                return true;
            }
        }
        return false;
    }
}
