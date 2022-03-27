package network.frostless.mist.commands.admin;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import network.frostless.fragment.embed.EmbedUtils;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;

import java.util.List;
import java.util.function.Supplier;

@Command(value = "sendmessage", description = "Sends a message as the bot")
public class SendMessageCommand extends CommandBase {


    @Default
    public void onDefault(
            SlashCommandEvent evt,
            @Param(name = "url", required = true, description = "The URL of the json of the message") String url
    ) {
        evt.deferReply(true).queue();
        EmbedUtils.getMessageContents(url).thenAccept(message -> {
            evt.getChannel().sendMessage(message.build()).queue();
            evt.getHook().editOriginal("Message sent!").queue();
        });
    }

    @Override
    public Supplier<List<CommandPrivilege>> getPermissionMapper() {
        return () -> List.of(CommandPrivilege.enableUser("201825529333153792"));
    }
}
