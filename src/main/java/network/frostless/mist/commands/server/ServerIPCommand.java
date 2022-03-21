package network.frostless.mist.commands.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;

import java.time.Instant;

@Command(value = "serverip", description = "Get the IP of Frostless Network")
public class ServerIPCommand extends CommandBase {


    @Default
    public void send(SlashCommandEvent evt) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setAuthor("Frostless Network Server", "https://play.frostless.network", "https://imgur.com/kRfCaPa.png");
        embed.setTitle("Frostless Network | Server IP");

        embed.setDescription("To join Frostless Network, go into your servers tab and click Add Server. Once you do that, enter: \n\n**play.frostless.network** \n\nAnd then click connect! Enjoy playing on Frostless Network!");
        embed.setColor(0x43c4df);
        embed.setThumbnail("https://imgur.com/TkerXsa.png");
        embed.setTimestamp(Instant.now());
        embed.setFooter("💙 Frostless Network", "https://imgur.com/TkerXsa.png");

        evt.replyEmbeds(embed.build()).queue();
    }
}
