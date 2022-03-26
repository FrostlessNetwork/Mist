package network.frostless.mist.services.polling;

import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import network.frostless.mist.core.service.Service;

public class PollingService implements Service {

    @SubscribeEvent
    public void onSelect(SelectionMenuEvent event) {
        if(!event.getComponentId().startsWith("mist-poll-")) return;

        for (SelectOption selectedOption : event.getSelectedOptions()) {
        }
    }
}
