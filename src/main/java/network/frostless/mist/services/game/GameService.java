package network.frostless.mist.services.game;

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import network.frostless.mist.core.service.impl.EventableService;

public class GameService implements EventableService {


    @SubscribeEvent
    public void onButtonClick(ButtonClickEvent event) {
        if(event.getButton() == null || event.getButton().getId() == null) return;

        if(event.getButton().getId().startsWith("game-")) return;


    }
}
