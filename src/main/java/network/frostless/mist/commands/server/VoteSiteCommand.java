package network.frostless.mist.commands.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;

@Command(value = "voting", description = "List all the sites that you can vote on for Frostless Network!")
public class VoteSiteCommand extends CommandBase {

    @Default
    public void execute(
            SlashCommandEvent event
    ) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Frostless Network | Voting Websites");

        embed.setDescription("""
                - [Website 1](https://best-minecraft-servers.co/server-frostless-network.6718)
                - [Website 2](https://topminecraftservers.org/server/26674)
                - [Website 3](https://servers-minecraft.net/server-frostless-network.11673)
                - [Website 4](https://minecraft.buzz/server/4748)
                - [Website 5](https://minecraft-mp.com/server-s302904)
                - [Website 6](https://minecraft-server-list.com/server/487047/)
                """);

        event.replyEmbeds(embed.build()).queue();
    }
}
