package network.frostless.mist.core.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import network.frostless.mist.core.command.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommandBase {

    private Method defaultMethod;
    private final Map<String, Method> subCommands = Maps.newConcurrentMap();

    public CommandBase() {
        if (!getClass().isAnnotationPresent(Command.class))
            throw new RuntimeException("CommandBase class must be annotated with @Command");

        for (Method declaredMethod : getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(Default.class)) defaultMethod = declaredMethod;
            if (declaredMethod.isAnnotationPresent(SubCommand.class)) {
                subCommands.put(declaredMethod.getAnnotation(SubCommand.class).value(), declaredMethod);
            }
            declaredMethod.setAccessible(true);
        }
    }

    public Function<net.dv8tion.jda.api.interactions.commands.Command, List<CommandPrivilege>> getPermissionMapper() {
        return (command) -> List.of();
    }

    public List<SubcommandGroupData> getSubCommandGroups() {
        List<SubcommandGroupData> scg = Lists.newArrayList();

        Map<String, List<Method>> sortedSCG = subCommands.entrySet().stream().collect(Collectors.groupingBy(scEntry -> Arrays.stream(scEntry.getKey().split("/")).toList().get(0), Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        for (Map.Entry<String, List<Method>> sortedEntries : sortedSCG.entrySet()) {
            SubcommandGroupData subcommandGroupData = new SubcommandGroupData(sortedEntries.getKey(), "No description provided");

            for (Method method : sortedEntries.getValue()) {
                SubcommandData subcommandData = new SubcommandData(method.getAnnotation(SubCommand.class).value(), method.getAnnotation(SubCommand.class).description());

                for (Parameter parameter : method.getParameters()) {
                    if (parameter.isAnnotationPresent(Param.class)) {
                        Param annotation = parameter.getAnnotation(Param.class);
                        OptionData optionData = new OptionData(annotation.type(), annotation.name(), annotation.description());
                        optionData.setRequired(annotation.required());
                        subcommandData.addOptions(optionData);
                    }
                }

                subcommandGroupData.addSubcommands();
            }

            scg.add(subcommandGroupData);
        }

        return scg;
    }

    public Map<SubCommand, Method> getSubCommands() {
        Map<SubCommand, Method> sc = Maps.newHashMap();

        for (Map.Entry<String, Method> scEntry : subCommands.entrySet()) {
            if (scEntry.getKey().contains("/")) continue;

            sc.put(scEntry.getValue().getAnnotation(SubCommand.class), scEntry.getValue());
        }

        return sc;
    }

    public boolean hasSubCommands() {
        return !subCommands.isEmpty();
    }

    public String getName() {
        return getClass().getAnnotation(Command.class).value();
    }

    public String getDescription() {
        return getClass().getAnnotation(Command.class).description();
    }

    public Method getDefaultMethod() {
        return defaultMethod;
    }

    public void executeDefault(SlashCommandEvent event) {
        try {
            runCommand(defaultMethod, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void executeSubCommand(String path, SlashCommandEvent event) {
        try {
            Method command = subCommands.get(path);
            runCommand(command, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private void runCommand(Method method, SlashCommandEvent evt) throws InvocationTargetException, IllegalAccessException {
        Object[] args = new Object[method.getParameterCount()];

        args[0] = evt;
        List<Param> parameters = getParameters(method);

        for (int i = 0; i < parameters.size(); i++) {
            OptionMapping option = evt.getOption(parameters.get(i).name());

            if (option == null) {
                args[i + 1] = null;
                return;
            }

            OptionMap resolvedOption = OptionMap.as(parameters.get(i).type());

            if (resolvedOption == null) {
                args[i + 1] = null;
            } else {
                args[i + 1] = resolvedOption.get(option);
            }
        }

        for (Object arg : args) {
            System.out.println(arg.getClass().getTypeName());
        }
        method.invoke(this, args);
    }


    public static boolean hasParameters(Method method) {
        return !getParameters(method).isEmpty();
    }

    public static List<Param> getParameters(Method method) {
        List<Param> params = Lists.newArrayList();

        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Param.class)) {
                params.add(parameter.getAnnotation(Param.class));
            }
        }
        return params;
    }

    public boolean defaultHasParams() {
        if(defaultMethod == null) return false;
        return defaultMethod.getParameters().length - 1 > 0;
    }
}
