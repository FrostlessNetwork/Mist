package network.frostless.fragment.utils;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import network.frostless.mist.core.command.CommandBase;

import java.util.List;

public class Permissions {

    public static boolean hasPermission(final CommandBase command, final User user) {
        List<CommandPrivilege> privs = command.getPermissionMapper().apply(null);

        for (CommandPrivilege priv : privs) {
            if(priv.getType().equals(CommandPrivilege.Type.USER) && priv.getId().equals(user.getId())) {
                return priv.isEnabled();
            }
        }

        return false;
    }
}
