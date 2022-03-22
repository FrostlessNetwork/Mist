package network.frostless.mist.commands.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import network.frostless.fragment.utils.EmojiUtils;
import network.frostless.fragment.utils.TimeUtils;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;

import java.awt.*;
import java.time.Instant;

@Command(value = "userinfo", description = "Get Discord information about a user")
public class UserInfoCommand extends CommandBase {

    @Default
    public void execute(
            SlashCommandEvent evt,
            @Param(name = "member", description = "A member to look up information on", type = OptionType.MENTIONABLE) Member member
            ) {
        EmbedBuilder embed = new EmbedBuilder();
        final Member effectiveMember = member == null ? evt.getMember() : member;
        if (effectiveMember == null) return;

        final Color color = effectiveMember.getColor() == null ? Color.WHITE : effectiveMember.getColor();

        embed.setAuthor("User info about " + effectiveMember.getUser().getAsTag(), null, effectiveMember.getEffectiveAvatarUrl())
                .setThumbnail(effectiveMember.getEffectiveAvatarUrl())
                .setColor(color)
                .addField("Joined Guild on", TimeUtils.SHORT_DT.parse(effectiveMember.getTimeJoined()), true)
                .addField("Registered on Discord on", TimeUtils.SHORT_DT.parse(effectiveMember.getTimeCreated()), true)
                .addField(String.format("Roles [%d]", effectiveMember.getRoles().size()), formatRoles(effectiveMember), false)
                .addField("Perks", formatPerks(effectiveMember), true)
                .setFooter("Queried by " + evt.getUser().getAsTag())
                .setTimestamp(Instant.now());

        evt.replyEmbeds(embed.build()).queue();

    }

    private String formatPerks(Member member) {
        final StringBuilder builder = new StringBuilder();

        if(member.getUser().isBot()) builder.append(EmojiUtils.BOT).append(" ");
        if(member.isOwner()) builder.append(EmojiUtils.BLURPLE_CROWN).append(" ");
        if(member.getIdLong() == 201825529333153792L) builder.append(EmojiUtils.GOLDEN_CROWN).append(" ");
        if(member.getTimeBoosted() != null) builder.append(EmojiUtils.NITRO).append(" ");

        for (User.UserFlag flag : member.getUser().getFlags()) {
            String emoji = switch (flag) {
                case HYPESQUAD_BALANCE -> EmojiUtils.HOUSE_BALANCE;
                case HYPESQUAD_BRAVERY -> EmojiUtils.HOUSE_BRAVERY;
                case HYPESQUAD_BRILLIANCE -> EmojiUtils.HOUSE_BRILLIANCE;
                default -> "";
            };

            if(emoji.isBlank()) continue;

            builder.append(emoji).append(" ");
        }

        return builder.toString();
    }

    private String formatRoles(Member member) {
        StringBuilder builder = new StringBuilder();

        for (Role role : member.getRoles()) {
            builder.append("<@&").append(role.getId()).append("> ");
        }

        return builder.toString();
    }

}
