package network.frostless.mist.services.command;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Param;
import network.frostless.mist.core.command.annotations.SubCommand;
import network.frostless.mist.core.service.impl.EventableService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CommandService implements EventableService {


    private final Logger logger = LogManager.getLogger("Command Service");

    @Getter
    private final Map<String, CommandBase> commands = Maps.newConcurrentMap();


    public void addCommand(CommandBase... commands) {
        for (CommandBase command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void registerCommands() {

        List<CommandData> waitToRegister = new ArrayList<>();

        for (CommandBase command : commands.values()) {
            CommandData commandData = new CommandData(command.getName(), command.getDescription());

            if (command.defaultHasParams()) {
                for (Parameter param : command.getDefaultMethod().getParameters()) {
                    if (param.isAnnotationPresent(Param.class)) {
                        Param annotation = param.getAnnotation(Param.class);
                        commandData.addOption(annotation.type(), annotation.name(), annotation.description(), annotation.required());
                    }
                }
            }

            for (Map.Entry<SubCommand, Method> sc : command.getSubCommands().entrySet()) {
                SubcommandData subcommandData = new SubcommandData(sc.getKey().value(), sc.getKey().description());

                for (Parameter parameter : sc.getValue().getParameters()) {
                    if (parameter.isAnnotationPresent(Param.class)) {
                        Param annotation = parameter.getAnnotation(Param.class);
                        System.out.println(annotation);
                        subcommandData.addOption(annotation.type(), annotation.name(), annotation.description(), annotation.required());
                    }
                }

                commandData.addSubcommands(subcommandData);
            }

//            for (SubcommandGroupData subCommandGroup : command.getSubCommandGroups()) {
//                commandData.addSubcommandGroups(subCommandGroup);
//            }

            waitToRegister.add(commandData);
        }

        Guild guild = getJDA().getGuildById(945581170157051914L);
        if (guild == null) throw new RuntimeException("Guild not found");

        CommandListUpdateAction action = guild.updateCommands().addCommands(waitToRegister);

        action.queue((v) -> {
            logger.info("Registered {} commands", v.size());
            Map<String, Collection<? extends CommandPrivilege>> privileges = Maps.newHashMap();
            for (Command command : v) {
                privileges.put(command.getId(), commands.get(command.getName()).getPermissionMapper().get());
            }
            guild.updateCommandPrivileges(privileges).queue((c) -> logger.info("Updated {} privileges", c.size()));

        });
    }

    @SubscribeEvent
    public void onSlash(SlashCommandEvent event) {
        boolean hasSubCommand = event.getCommandPath().contains("/");

        if (!hasSubCommand && commands.containsKey(event.getCommandPath())) {
            commands.get(event.getCommandPath()).executeDefault(event);
        } else {
            // get subcommand
            String next = event.getCommandPath().replaceAll(event.getName() + "/", "");
            commands.get(event.getName()).executeSubCommand(next, event);
        }
    }
}
