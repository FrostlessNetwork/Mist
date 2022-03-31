package network.frostless.mist.commands.suggest;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.mist.Application;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;
import network.frostless.mist.core.command.annotations.SubCommand;

import java.awt.*;
import java.net.URL;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Command(value = "suggest", description = "Suggest a feature for Frostless Network!")
public class SuggestionCommand extends CommandBase {

    private final Cache<String, Long> cooldown = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();

    @SubCommand(value = "create", description = "Create a new suggestion for the community to vote on!")
    public void suggest(
            SlashCommandEvent evt,
            @Param(name = "suggestion", required = true, description = "The suggestion that you have") String suggestion,
            @Param(name = "image", description = "An image that you want to attach to the suggestion") String image
    ) {

        if (evt.getMember() == null) return;
        evt.deferReply(true).queue();

        if (cooldown.getIfPresent(evt.getMember().getId()) != null) {
            evt.getHook().sendMessage("You can only suggest once every 30 minutes! You currently have " + (30 - ((System.currentTimeMillis() - cooldown.getIfPresent(evt.getMember().getId())) / 1000 / 60)) + " minutes left!")
                    .setEphemeral(true).queue();
            return;
        }

        if (suggestion.length() > 1500) {
            evt.getHook().sendMessage("Your suggestion is too long! Please shorten it to 1500 characters or less!").setEphemeral(true).queue();
            return;
        }


        final EmbedBuilder embed = new EmbedBuilder();

        String sb = String.format("Suggestion from **%s**\n", evt.getMember().getAsMention()) +
                "Please vote with ✅ or ❌" +
                "\n\n" +
                suggestion +
                "\n\n";

        if (image != null) {
            embed.setImage(image);
        }

        embed.setTitle("New Suggestion!");
        embed.setColor(Color.ORANGE);
        embed.setDescription(sb);
        embed.setFooter("Suggestion from " + evt.getMember().getEffectiveName(), evt.getMember().getUser().getAvatarUrl());
        embed.setTimestamp(Instant.now());

        final String suggestionChannel = Application.config.get().getSuggestionChannel();

        TextChannel suggestChannel = (TextChannel) evt.getJDA().getGuildChannelById(suggestionChannel);
        if (suggestChannel == null) {
            evt.getHook().sendMessage("The suggestion channel is not set up correctly. Please contact Frostless Network Support.").setEphemeral(true).queue();
            return;
        }

        suggestChannel.sendMessageEmbeds(embed.build()).queue((c) -> {
            c.addReaction("✅").queue();
            c.addReaction("❌").queue();
            evt.getHook().sendMessage("Your suggestion has been created! Check out <#" + suggestionChannel + "> to see it!").setEphemeral(true).queue();
            cooldown.put(evt.getMember().getId(), System.currentTimeMillis());
        });
    }
}
