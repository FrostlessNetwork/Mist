package network.frostless.mist.commands.admin;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import network.frostless.mist.Mist;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;

import java.util.List;
import java.util.function.Function;

import static network.frostless.fragment.utils.SnowflakeUtils.parseChannelMessage;

@Command(value = "editmessage", description = "Edits a specified bot message by ID")
public class EditMessageCommand extends CommandBase {

    @Default
    public void onDefault(
            SlashCommandEvent evt,
            @Param(name = "channel-message-id", description = "The channel message Id (channel-messageid)") String channelMessageId,
            @Param(name = "message", description = "The new message") String message) {

        evt.deferReply(true).queue();
        InteractionHook hook = evt.getInteraction().getHook();
        Pair<GuildChannel, Message> pair = parseChannelMessage(channelMessageId);
        if (pair == null || pair.getLeft() == null || pair.getRight() == null) {
            hook.sendMessage("Invalid channel message id").setEphemeral(true).queue();
            return;
        }

        MessageBuilder builder = new MessageBuilder();

        hook.sendMessage("Message edited").setEphemeral(true).queue();
        pair.getRight().editMessage(message).queue();
    }



    @Override
    public Function<net.dv8tion.jda.api.interactions.commands.Command, List<CommandPrivilege>> getPermissionMapper() {
        return (cmd) -> List.of(CommandPrivilege.enableUser("201825529333153792"));
    }
}
