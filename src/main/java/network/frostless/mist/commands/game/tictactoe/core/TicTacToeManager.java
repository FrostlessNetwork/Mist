package network.frostless.mist.commands.game.tictactoe.core;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import network.frostless.mist.core.service.impl.EventableService;

public class TicTacToeManager implements EventableService {


    @SubscribeEvent
    public void onRequestResponse(ButtonClickEvent event) {
        ButtonInteraction interaction = event.getInteraction();
        String componentId = interaction.getComponentId();

        if(TicTacToeGenerator.isValid(componentId, interaction.getMember())) {
            interaction.reply("You can't accept their invite! It's for them!").setEphemeral(true).queue();
            return;
        }
    }


    /**
     * Requests a unique game id
     * @param player The player who requested the game
     * @param opponent The opponent of the player
     * @return The game id
     */
    public String requestUniqueGameId(Member player, Member opponent) {
        return "l";
    }

}
