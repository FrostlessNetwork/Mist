package network.frostless.mist.commands.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import network.frostless.fragment.utils.TimeUtils;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;

import java.time.Instant;

@Command(value = "guildinfo", description = "Gets information about the Discord server.")
public class GuildInfoCommand extends CommandBase {


    @Default
    public void onDefault(SlashCommandEvent evt) {
        final Guild guild = evt.getGuild();

        if (guild == null) {
            evt.reply("This command can only be used in a Discord server.").queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        builder.setThumbnail(guild.getIconUrl());
        builder.setTitle("Discord Server | " + guild.getName());
        builder.setThumbnail(guild.getIconUrl());
        builder.addField("Owner", guild.getOwner().getAsMention(), true);
        builder.addField("Members", String.valueOf(guild.getMemberCount()), true);
        builder.addField("Channels", String.valueOf(guild.getTextChannels().size() + guild.getVoiceChannels().size()), true);
        builder.addField("Roles", String.valueOf(guild.getRoles().size()), true);
        builder.addField("Region", guild.getRegion().getName(), true);
        builder.addField("Verification Level", guild.getVerificationLevel().name(), true);
        builder.addField("Creation Date", TimeUtils.LONG_DT.parse(guild.getTimeCreated()), true);
        builder.addField("Boost Tier", String.valueOf(guild.getBoostTier()), true);
        builder.addField("Boost Count", String.valueOf(guild.getBoostCount()), true);
        builder.addField("Features", String.join(", ", guild.getFeatures()), true);

        builder.setFooter("Requested by " + evt.getUser().getAsTag(), "https://imgur.com/TkerXsa.png");
        builder.setTimestamp(Instant.now());

        evt.replyEmbeds(builder.build()).queue();
    }

}
