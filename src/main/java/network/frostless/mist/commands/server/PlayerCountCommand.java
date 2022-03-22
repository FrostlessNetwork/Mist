package network.frostless.mist.commands.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import network.frostless.frostcore.messaging.redis.Redis;
import network.frostless.mist.Mist;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.serverapi.RedisKeys;

import java.time.Instant;

@Command(value = "playercount", description = "Shows the current player count")
public class PlayerCountCommand extends CommandBase {

    private final Redis<String, String> redis;


    public PlayerCountCommand(Redis<String, String> redis) {
        this.redis = redis;
    }


    @Default
    public void onCommand(SlashCommandEvent event) {
        Interaction interaction = event.getInteraction();
        interaction.deferReply().queue();

        String totalPlayers = redis.sync().get(RedisKeys.TOTAL_PLAYERS);

        int players = totalPlayers != null ? Integer.parseInt(totalPlayers) : 0;

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Frostless Network | Player Count");
        embed.setThumbnail("https://imgur.com/TkerXsa.png");
        embed.setColor(0x43c4df);
        embed.setDescription(String.format("There are currently **%d** players online.", players));
        embed.setTimestamp(Instant.now());
        embed.setFooter("Frostless Network", "https://imgur.com/TkerXsa.png");

        interaction.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
