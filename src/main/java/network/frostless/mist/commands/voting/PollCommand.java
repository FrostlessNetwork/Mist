package network.frostless.mist.commands.voting;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import network.frostless.fragment.embed.EmbedUtils;
import network.frostless.mist.Mist;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Param;
import network.frostless.mist.core.command.annotations.SubCommand;
import network.frostless.mist.services.ServiceManager;
import network.frostless.mist.services.polling.PollingService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static network.frostless.mist.services.polling.PollingService.POLL_RESULTS_PREFIX;

@Command("poll")
public class PollCommand extends CommandBase {

    @SubCommand("create")
    public void create(
            SlashCommandEvent event,
            @Param(name = "poll-id", required = true) String pollId,
            @Param(name = "content", required = true, type = OptionType.STRING, description = "A link to what the message should look like in JSON.") String content,
            @Param(name = "allow-result-viewing", type = OptionType.BOOLEAN, description = "Whether to allow members to view the poll results.") Boolean allowResultViewing
    ) {
        ServiceManager.get(PollingService.class).ifPresent(service -> {
            if (!service.exists(pollId)) return;
            event.deferReply(true).queue();
            EmbedUtils.getMessageContents(content).whenComplete((builder, t) -> {
                if (t != null) {
                    t.printStackTrace();
                    event.getHook().sendMessage("Failed to create poll, make sure you upload the JSON to https://bytebin.lucko.me/").setEphemeral(true).queue();
                    return;
                }

                Button view_results = Button.of(ButtonStyle.PRIMARY, POLL_RESULTS_PREFIX + pollId, "View Results", Emoji.fromUnicode("📊"));
                builder.setActionRows(service.createSelectionMenu(pollId), ActionRow.of(view_results));
                event.getChannel().sendMessage(builder.build()).queue();
            });
        });
    }

    @SubCommand("results")
    public void results(
            SlashCommandEvent event,
            @Param(name = "poll-id", required = true) String pollId
    ) {

        event.reply("Poll results for id: " + pollId).setEphemeral(true).queue();
    }

    @Override
    public Supplier<List<CommandPrivilege>> getPermissionMapper() {
        return () -> ServiceManager.get(PollingService.class).get().getConfig().get().getAdmins().stream().map(CommandPrivilege::enableUser).toList();
    }
}
