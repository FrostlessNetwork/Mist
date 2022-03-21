package network.frostless.mist.services.welcomer;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import network.frostless.mist.core.service.impl.EventableService;

public class WelcomeService implements EventableService {


    @SubscribeEvent
    public void onJoin(GuildMemberJoinEvent event) {

    }
}
