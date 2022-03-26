package network.frostless.mist.services.invitetracking;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import network.frostless.mist.core.service.impl.EventableService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service used to track invites to a guild.
 */
public class InviteTrackingService implements EventableService {

    private final Logger logger = LogManager.getLogger("Invite Tracking");

    private final Cache<Long, Map<String, Invite>> inviteCache = CacheBuilder.newBuilder().build();

    public InviteTrackingService() {

    }

    @SubscribeEvent
    public void onInviteDelete(GuildInviteDeleteEvent event) throws ExecutionException {
        inviteCache.get(event.getGuild().getIdLong(), HashMap::new).remove(event.getCode());
        logger.debug("Invite deleted: {}", event.getCode());
    }

    @SubscribeEvent
    public void onInviteCreate(GuildInviteCreateEvent event) throws ExecutionException {
        inviteCache.get(event.getGuild().getIdLong(), HashMap::new).put(event.getCode(), event.getInvite());
        logger.debug("Invite created: {}", event.getCode());
    }

    @SubscribeEvent
    public void onGuildJoin(GuildMemberJoinEvent event) {
        logger.debug("Member joined: {}", event.getMember().getUser().getAsTag());
        event.getGuild().retrieveInvites().flatMap((currentInvites) -> {

            Optional<Invite> inviteOptional = currentInvites.parallelStream().filter((inv) -> {
                Map<String, Invite> invs = inviteCache.getIfPresent(event.getGuild().getIdLong());
                if (invs == null) return false;
                Invite oldInvite = invs.get(inv.getCode());
                if (oldInvite == null) return false;
                return inv.getUses() > oldInvite.getUses();
            }).findFirst();

            inviteCache.put(event.getGuild().getIdLong(), currentInvites.stream().collect(Collectors.toMap(Invite::getCode, (inv) -> inv)));
            return sendInviteMessage(inviteOptional.orElse(null), event.getMember());
        }).queue();
    }

    private MessageAction sendInviteMessage(@Nullable Invite invite, Member joiner) {
        TextChannel textChannel = getJDA().getTextChannelById("946282365473398836");
        if (textChannel == null) throw new RuntimeException("Invites channel not found");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("New Member!");
        embed.setDescription(String.format("%s (%s) joined with invite code %s", joiner.getUser().getAsMention(), joiner.getUser().getId(), invite == null ? "None" : invite.getCode()));
        embed.setFooter(String.format("This invite now has %s uses", invite == null ? "0" : String.valueOf(invite.getUses())));
        embed.setColor(0x42f5b6);
        embed.setTimestamp(Instant.now());

        if(invite != null && invite.getInviter() != null) {
            embed.addField("Inviter", String.format("%s (%s)", invite.getInviter().getAsTag(), invite.getInviter().getId()), false);
        }

        return textChannel.sendMessageEmbeds(embed.build());
    }

}
