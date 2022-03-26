package network.frostless.mist.commands.suggest;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;

@Command("suggest")
public class SuggestionCommand {

    @Default
    public void suggest(SlashCommandEvent evt, @Param(name = "suggestion", required = true, description = "The suggestion that you have") String suggestion) {

    }
}
