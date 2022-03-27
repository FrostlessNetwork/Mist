package network.frostless.mist.services.polling;

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import network.frostless.mist.Mist;
import network.frostless.mist.config.model.polling.PollModel;
import network.frostless.mist.config.model.polling.PollingConfigModel;
import network.frostless.mist.core.service.Service;
import network.frostless.mist.core.service.impl.EventableService;

import java.time.Instant;
import java.util.*;

public class PollingService implements EventableService {

    public static final String POLL_PREFIX = "mist-poll-";

    public static final String POLL_RESULTS_PREFIX = POLL_PREFIX + "view-results-";

    private final Map<String, Map<String, List<String>>> pollCache = new HashMap<>();

    @SubscribeEvent
    public void onSelect(SelectionMenuEvent event) {
        if (!event.getComponentId().startsWith(POLL_PREFIX)) return;

        String voteId = event.getComponentId().split(POLL_PREFIX)[1];

        PollModel poll = getConfig().getList().get(voteId);

        if (poll == null) {
            event.reply("This poll doesn't exist!").setEphemeral(true).queue();
            return;
        }

        Map<String, List<String>> pollOptions = pollCache.getOrDefault(voteId, Maps.newHashMap());
        List<String> userSelectedOptions = pollOptions.getOrDefault(event.getUser().getId(), new ArrayList<>());

        /* Update user selections */
        userSelectedOptions.clear();
        userSelectedOptions.addAll(event.getValues());

        /* Add everything all back */
        pollOptions.put(event.getUser().getId(), userSelectedOptions);

        pollCache.put(voteId, pollOptions);
        event.reply("Your vote has been recorded!").setEphemeral(true).queue();
    }

    @SubscribeEvent
    public void onView(ButtonClickEvent event) {
        if (!event.getComponentId().startsWith(POLL_PREFIX + "view-results-")) return;

        String pollId = event.getComponentId().split(POLL_PREFIX + "view-results-")[1];

        Set<String> options = getConfig().getList().get(pollId).getOptions().keySet();
        Map<String, Integer> votesCounted = getVotesFor(pollId);

        EmbedBuilder builder = new EmbedBuilder();

        builder.setAuthor("Poll Results", null, event.getJDA().getSelfUser().getEffectiveAvatarUrl());
        builder.setDescription("Results for poll: " + pollId);

        for (String option : options) {
            builder.addField(option, String.valueOf(votesCounted.getOrDefault(option, 0)), false);
        }

        builder.setTimestamp(Instant.now());
        builder.setFooter("Frostless Polling Service");

        event.replyEmbeds(builder.build()).setEphemeral(true).queue();
    }

    public boolean exists(String pollId) {
        return getConfig().getList().containsKey(pollId);
    }

    public ActionRow createSelectionMenu(String voteId) {
        PollModel poll = getConfig().getList().get(voteId);
        if (poll == null) return null;

        SelectionMenu.Builder builder = SelectionMenu.create(POLL_PREFIX + voteId);

        builder.setMinValues(poll.getMinVote());
        builder.setMaxValues(poll.getMaxVote());
        builder.addOptions(poll.getSelectOptions());

        return ActionRow.of(builder.build());
    }

    /**
     * Gets all the votes for a poll
     *
     * @param pollId The poll id
     * @return A map of user id to list of options
     */
    public Map<String, Integer> getVotesFor(String pollId) {
        Map<String, List<String>> pollOptions = pollCache.getOrDefault(pollId, Maps.newHashMap());
        Map<String, Integer> votes = Maps.newHashMap();

        for (List<String> value : pollOptions.values()) {
            for (String option : value) {
                votes.merge(option, 1, Integer::sum);
            }
        }

        return votes;
    }

    private PollingConfigModel getConfig() {
        return Mist.get().getConfig().get().getVoting();
    }
}
