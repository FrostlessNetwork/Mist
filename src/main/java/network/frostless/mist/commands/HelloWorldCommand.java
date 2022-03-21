package network.frostless.mist.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;

@Command("helloworld")
public class HelloWorldCommand extends CommandBase {

    @Default
    public void defaultExecutor(SlashCommandEvent evt) {
        evt.reply("Hello World!").queue();
    }
}
