package network.frostless.mist.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.fragment.utils.Permissions;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;
import network.frostless.mist.services.ServiceManager;
import network.frostless.mist.services.command.CommandService;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Iterator;
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

            if(command.getPermissionMapper().apply(null).size() != 0 && !Permissions.hasPermission(command, event.getUser())) continue;

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

        Iterator<Param> iterator = parameters.iterator();

        // We use an iterator, so we can check if there is another
        // parameter to append a space after the parameter unless it ends.
        while (iterator.hasNext()) {
            Param next = iterator.next();

            if (next.required()) builder.append(String.format("<%s>", next.name()));

            if (iterator.hasNext()) builder.append(" ");
        }

        return builder.toString();
    }
}
