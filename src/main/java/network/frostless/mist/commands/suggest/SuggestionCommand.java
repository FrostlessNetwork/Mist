package network.frostless.mist.commands.suggest;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import network.frostless.fragment.utils.SnowflakeUtils;
import network.frostless.mist.Application;
import network.frostless.mist.Mist;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;
import network.frostless.mist.core.command.annotations.SubCommand;

import java.awt.*;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @SubCommand(value = "delete", description = "Deletes a suggestion")
    public void delete(
            SlashCommandEvent evt,
            @Param(name = "channel-message-key", required = true, description = "The channel message key") String channelMessageKey
    ) {
        if (evt.getMember() == null) return;

        if (!isAdmin(evt.getMember())) {
            evt.getHook().sendMessage("You do not have permission to use this command!").setEphemeral(true).queue();
            return;
        }
        evt.deferReply(true).queue();
        InteractionHook hook = evt.getHook();

        Pair<GuildChannel, Message> cm = SnowflakeUtils.parseChannelMessage(channelMessageKey);
        if (cm == null || cm.getLeft() == null || cm.getRight() == null) {
            hook.sendMessage("Invalid channel message key!").setEphemeral(true).queue();
        } else {
            cm.getRight().delete().queue();
            hook.editOriginal("Suggestion deleted!").queue();
        }
    }

    @SubCommand(value = "accept", description = "Accepts a suggestion")
    public void accept(
            SlashCommandEvent evt,
            @Param(name = "channel-message-key", required = true, description = "The channel message key") String channelMessageKey,
            @Param(name = "reason", description = "The reason for accepting the suggestion") String reason
    ) {
        if (evt.getMember() == null) return;

        if (!isAdmin(evt.getMember())) {
            evt.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            return;
        }
        evt.deferReply(true).queue();
        InteractionHook hook = evt.getHook();

        Pair<GuildChannel, Message> cm = SnowflakeUtils.parseChannelMessage(channelMessageKey);
        if (cm == null || cm.getLeft() == null || cm.getRight() == null) {
            hook.sendMessage("Invalid channel message key! You can get this by hovering over the suggestion while holding shift and pressing `Copy ID`").setEphemeral(true).queue();
        } else {
            List<MessageEmbed> embeds = cm.getRight().getEmbeds();
            if (embeds.size() <= 0) {
                hook.sendMessage("This message doesn't have an embed!").setEphemeral(true).queue();
            } else {
                MessageEmbed embed = embeds.get(0);
                EmbedBuilder embedBuilder = new EmbedBuilder(embed);

                embedBuilder.setColor(Color.GREEN);
                cm.getRight().editMessageEmbeds(embedBuilder.build()).queue();

                MessageEmbed embe = generateConditionalEmbed(evt.getMember(), cm.getRight(), embed, SuggestionState.ACCEPTED, reason);

                String chan = Mist.get().getConfig().get().getSuggestionArchive();
                GuildChannel channel = evt.getJDA().getGuildChannelById(chan);
                if (channel instanceof TextChannel txtChan)
                    txtChan.sendMessageEmbeds(embe).queue();


                cm.getRight().replyEmbeds(embe).queue();

                hook.editOriginal("Suggestion accepted!").queue();
            }
        }
    }

    @SubCommand("reject")
    private void deny(SlashCommandEvent evt,
                      @Param(name = "channel-message-key", required = true, description = "The channel message key") String channelMessageKey,
                      @Param(name = "reason", required = true, description = "The reason to deny") String reason) {
        if (evt.getMember() == null) return;

        if (!isAdmin(evt.getMember())) {
            evt.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            return;
        }
        evt.deferReply(true).queue();
        InteractionHook hook = evt.getHook();

        Pair<GuildChannel, Message> cm = SnowflakeUtils.parseChannelMessage(channelMessageKey);
        if (cm == null || cm.getLeft() == null || cm.getRight() == null) {
            hook.sendMessage("Invalid channel message key! You can get this by hovering over the suggestion while holding shift and pressing `Copy ID`").setEphemeral(true).queue();
        } else {
            List<MessageEmbed> embeds = cm.getRight().getEmbeds();
            if (embeds.size() <= 0) {
                hook.sendMessage("This message doesn't have an embed!").setEphemeral(true).queue();
            } else {
                MessageEmbed embed = embeds.get(0);
                EmbedBuilder embedBuilder = new EmbedBuilder(embed);

                embedBuilder.setColor(Color.RED);
                cm.getRight().editMessageEmbeds(embedBuilder.build()).queue();

                MessageEmbed e = generateConditionalEmbed(evt.getMember(), cm.getRight(), embed, SuggestionState.DECLINED, reason);
                cm.getRight().replyEmbeds(e).queue();

                String chan = Mist.get().getConfig().get().getSuggestionArchive();
                GuildChannel channel = evt.getJDA().getGuildChannelById(chan);
                if (channel instanceof TextChannel txtChan)
                    txtChan.sendMessageEmbeds(e).queue();
                hook.sendMessage("Suggestion rejected!").setEphemeral(true).queue();
            }
        }
    }

    private MessageEmbed generateConditionalEmbed(Member member, Message original, MessageEmbed embed, SuggestionState state, String reason) {
        EmbedBuilder embedBuilder = new EmbedBuilder(embed);

        if (embed.getDescription() == null) return embed;

        embedBuilder.setColor(state == SuggestionState.ACCEPTED ? Color.GREEN : Color.RED);
        embedBuilder.setTitle(state == SuggestionState.ACCEPTED ? "Suggestion accepted!" : "Suggestion rejected!");

        Pattern suggestPattern = Pattern.compile("Suggestion from \\*\\*(<@\\d{18}>)\\*\\*");

        Matcher matcher = suggestPattern.matcher(embed.getDescription());

        if (matcher.find()) {
            String suggestor = matcher.group(1);
            embedBuilder
                    .setDescription(embed.getDescription()
                            .replaceFirst(suggestPattern.pattern(), String.format("The suggestion from %s has been **%s** by %s:", suggestor, state == SuggestionState.ACCEPTED ? "accepted" : "denied", member.getAsMention()))
                            .replaceAll("Please vote with ✅ or ❌", "")
                    );

        }

        original.getReactions();

        Map<String, Integer> reactionCounts = new HashMap<>();
        original.getReactions().forEach(r -> reactionCounts.put(r.getReactionEmote().getEmoji(), r.getCount()));

        if (reason != null) {
            embedBuilder.addField("Reason", reason, false);
        }

        embedBuilder.addField("Upvotes", String.valueOf(reactionCounts.get("✅") - 1), true);
        embedBuilder.addField("Downvotes", String.valueOf(reactionCounts.get("❌") - 1), true);

        original.clearReactions().queue();


        return embedBuilder.build();
    }


    private boolean isAdmin(Member member) {
        return Mist.get().getConfig().get().getSuggestionAdmins().exact(member);
    }

    enum SuggestionState {
        NEW,
        ACCEPTED,
        DECLINED
    }
}
