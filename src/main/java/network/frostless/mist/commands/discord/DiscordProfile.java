package network.frostless.mist.commands.discord;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;

@Command("discordprofile")
public class DiscordProfile extends CommandBase {

    @Default
    public void onDefault(SlashCommandEvent evt) {
    }
}
