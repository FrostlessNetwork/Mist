package network.frostless.fragment.utils;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import network.frostless.mist.Mist;

public class SnowflakeUtils {

    public static Pair<GuildChannel, Message> parseChannelMessage(String channelMessageId) {
        String[] split = channelMessageId.split("-");
        if (split.length != 2) return null;

        TextChannel channel = Mist.getJda().getTextChannelById(split[0]);
        if (channel == null) return null;

        Message message = channel.retrieveMessageById(split[1]).complete();

        return Pair.of(channel, message);
    }
}
