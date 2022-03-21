package network.frostless.mist.commands.server.link;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;

@Command(value = "link", description = "Link your Discord account to your Minecraft account and earn rewards!")
public class LinkCommand extends CommandBase {

    @Default
    public void onLink(SlashCommandEvent event,
                       @Param(name = "code", description = "The code provided from Minecraft!", required = true) String code) {
        System.out.println(code);
        event.reply("This command is not yet implemented! ").setEphemeral(true).queue();
    }
}
