package network.frostless.mist.commands.game.tictactoe.core;

import net.dv8tion.jda.api.entities.Member;

public enum TicTacToeGenerator {
    ACCEPT() {
        @Override
        public String generate(Member member, String gameId) {
            return "game-ttt-accept-" + member.getId() + "-" + gameId;
        }
    },
    DECLINE() {
        @Override
        public String generate(Member member, String gameId) {
            return "game-ttt-decline-" + member.getId() + "-" + gameId;
        }
    }
    ;


    public abstract String generate(Member member, String gameId);


    public static boolean isValid(String reply, Member member) {
        return reply.startsWith("game-ttt-accept-" + member.getId()) || reply.startsWith("game-ttt-decline-" + member.getId());
    }
}
