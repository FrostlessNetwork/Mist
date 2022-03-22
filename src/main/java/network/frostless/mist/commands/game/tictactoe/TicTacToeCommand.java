package network.frostless.mist.commands.game.tictactoe;

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import network.frostless.mist.commands.game.tictactoe.core.TicTacToeGenerator;
import network.frostless.mist.core.command.CommandBase;
import network.frostless.mist.core.command.annotations.Command;
import network.frostless.mist.core.command.annotations.Default;
import network.frostless.mist.core.command.annotations.Param;

@Command(value = "tictactoe", description = "Play a game of tic tac toe with another player.")
public class TicTacToeCommand extends CommandBase {

    @Default
    public void onDefault(
            SlashCommandEvent event,
            @Param(name = "player", description = "The player to play with.", required = true, type = OptionType.MENTIONABLE) Member opponent
    ) {
        final Member player = event.getMember();

        event.deferReply().queue();
        InteractionHook hook = event.getInteraction().getHook();

        ActionRow accept = ActionRow.of(
                Button.of(ButtonStyle.SUCCESS, TicTacToeGenerator.ACCEPT.generate(player, "l"), "Accept", Emoji.fromUnicode("✔")),
                Button.of(ButtonStyle.DANGER, TicTacToeGenerator.DECLINE.generate(player, "l"), "Decline", Emoji.fromUnicode("❌"))
        );

        String challenge = String.format("%s, %s has challenged you to a game of tic tac toe!\nDo you accept?", opponent.getAsMention(), player.getAsMention());

        hook.sendMessage(challenge).addActionRows(accept).queue();
    }
}
