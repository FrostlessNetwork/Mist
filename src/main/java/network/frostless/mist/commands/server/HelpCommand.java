package network.frostless.mist.commands.server;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;
import network.frostless.mist.services.ServiceManager;
import network.frostless.mist.services.command.CommandService;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Command(value = "help", description = "Displays all the commands available to you.")
public class HelpCommand extends CommandBase {

    private final CommandService service;
    public HelpCommand() {
        Optional<CommandService> service = ServiceManager.get().getService(CommandService.class);
        this.service = service.orElseThrow(() -> {
            throw new IllegalStateException("CommandService not found!");
        });
    }
    @Default
    public void onDefault(SlashCommandEvent event) {
        EmbedBuilder embed = new EmbedBuilder();

        StringBuilder builder = new StringBuilder();
        builder.append("**List of commands:** 📚").append("\n");

        for (CommandBase command : service.getCommands().values()) {

            builder
                    .append(":white_medium_small_square: ")
                    .append("Command: ")
                    .append(String.format("**/%s", command.getName()))
                    .append(CommandBase.hasParameters(command.getDefaultMethod()) ? String.format(" %s", generateParameterHelp(command.getDefaultMethod())) : "")
                    .append(":** ")
                    .append(String.format("*%s*", command.getDescription()))
                    .append("\n");
        }

        embed.setDescription(builder.toString());
        embed.setTimestamp(Instant.now());
        embed.setColor(0x43c4df);
        embed.setFooter("💙 Frostless Network", "https://imgur.com/TkerXsa.png");

        event.replyEmbeds(embed.build()).queue();
    }

    private String generateParameterHelp(Method method) {
        List<Param> parameters = CommandBase.getParameters(method);

        StringBuilder builder = new StringBuilder();

        for (Param parameter : parameters) {
            if(parameter.required()) {
                builder.append(String.format("<%s>", parameter.name()));
            }
        }

        return builder.toString();
    }
}
