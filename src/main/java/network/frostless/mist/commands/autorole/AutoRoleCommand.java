package network.frostless.mist.commands.autorole;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import network.frostless.mist.Application;
import network.frostless.mist.config.MistConfig;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;
import network.frostless.mist.core.command.annotations.SubCommand;
import network.frostless.mist.services.ServiceManager;
import network.frostless.mist.services.autorole.AutoRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Command("autorole")
public class AutoRoleCommand extends CommandBase {

    private final AutoRoleService service;
    private final MistConfig config;

    public AutoRoleCommand() {
        this.config = Application.config;
        this.service = ServiceManager.get().getService(AutoRoleService.class).orElseThrow(() -> new RuntimeException("AutoRoleService not found"));
    }


    @Default
    public void onRefresh(
            SlashCommandEvent evt,
            @Param(name = "channel_id", required = true, type = OptionType.CHANNEL) GuildChannel channel,
            @Param(name = "message_id", required = true) String messageId,
            @Param(name = "id", required = true) String id
            ) throws RateLimitedException {

        Interaction interaction = evt.getInteraction();
        interaction.deferReply(true).queue();

        service.refreshRoles();

        Message message = ((TextChannel) channel).retrieveMessageById(messageId).complete(true);

        if(message == null) {
            interaction.getHook().sendMessage("Message not found").setEphemeral(true).queue();
        } else {
            message.editMessageComponents(service.getAutoRoleComponents(id)).queue();
            interaction.getHook().sendMessage("Message refreshed").setEphemeral(true).queue();
        }
    }

    @Override
    public Supplier<List<CommandPrivilege>> getPermissionMapper() {

        return () -> {
           final List<CommandPrivilege> list = new ArrayList<>();

            for (String allowedAdmin : config.get().getAutoRole().getAllowedAdmins()) {
                list.add(CommandPrivilege.enableUser(allowedAdmin));
            }

           return list;
        };
    }
}
