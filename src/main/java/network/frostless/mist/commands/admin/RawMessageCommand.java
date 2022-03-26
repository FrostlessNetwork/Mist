package network.frostless.mist.commands.admin;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import network.frostless.fragment.utils.SnowflakeUtils;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;

@Command("rawmessage")
public class RawMessageCommand extends CommandBase {

    @Default
    public void on(SlashCommandEvent evt, @Param(name = "channel-message-id", required = true, description = "the message you want to view") String message) {
        Pair<GuildChannel, Message> pair = SnowflakeUtils.parseChannelMessage(message);

        evt.deferReply(true).queue();
        InteractionHook hook = evt.getInteraction().getHook();

        if (pair == null || pair.getRight() == null || pair.getLeft() == null) {
            hook.sendMessage("Invalid message id").queue();
            return;
        }

        hook.sendMessage("```\n" + pair.getRight().getContentRaw() +"\n```").setEphemeral(true).queue();
    }
}
