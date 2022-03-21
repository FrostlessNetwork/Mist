package network.frostless.mist.commands.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;

import java.time.Instant;

@Command(value = "playercount", description = "Shows the current player count")
public class PlayerCountCommand extends CommandBase {

    @Default
    public void onCommand(SlashCommandEvent event) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Frostless Network | Player Count");
        embed.setThumbnail("https://imgur.com/TkerXsa.png");
        embed.setColor(0x43c4df);
        embed.setDescription("There are currently " + event.getJDA().getUsers().size() + " players online.");
        embed.setTimestamp(Instant.now());
        embed.setFooter("Frostless Network", "https://imgur.com/TkerXsa.png");

        event.replyEmbeds(embed.build()).queue();
    }
}
