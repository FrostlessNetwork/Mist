package network.frostless.mist.core.service;

import net.dv8tion.jda.api.JDA;
import network.frostless.mist.core.DiscordBot;

public interface Service {

    default JDA getJDA() {
        return DiscordBot.getJda();
    }
}
