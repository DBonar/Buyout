package tmw.sept22buyout.GameLogic;

import java.util.List;

import tmw.sept22buyout.Chain;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.Actions.PlayGameAct;
import tmw.sept22buyout.Player;
import tmw.sept22buyout.Players;

public class NextTurn implements GameState {

    private PlayGameAct display;
    private Player player;

    public NextTurn(PlayGameAct theDisplay) {
        display = theDisplay;
    }


    public void enter(Player thePlayer) {
        player = thePlayer;
        Players.instance().updateCallbacks(null);
        Chains.instance().updateCallbacks(null);
        // No need to wait here.  We'll just go directly to ending this turn
        // and starting the next one.
        display.ContinueButton.setOnClickListener(null);
        display.msgSet(player,"");
        display.log("Ending " + player.getPlayerName() + "'s turn.");

        if (   ! player.fillTokens()
            || gameEnded()    ) {
            GameState end = new EndGame(display);
            end.enter(player);
        }

        player = player.nextPlayer();
        saveGameState();
        GameState nextState = new StartTurn(display);
        nextState.enter(player);
    }

    boolean gameEnded() {
        // We'll end the game if all chains on the board are safe
        // or if one chain on the board has 41+ tiles.
        // Note that officially a player has to notice and announce
        // the end of the game and may chose not to. I'm not sure
        // when it would be adventagious to not announce it.  So,
        // this is getting checked automatically at the end of every
        // turn.  (After stock purchase since that cannot hurt a player.)
        List<Chain> chains = Chains.instance().allPlacedChains();
        boolean endGame = (chains.size() > 0);

        for (int i = 0; i < chains.size(); i++) {
            Chain chain = chains.get(i);
            if (chain.isMaximal())
                return true;
            if (!chain.isSafe())
                endGame = false;
        }
        // This doesn't seem to be explicitly mentioned, but we need
        // it or the game ends on the first turn with no chains on the board.
        return endGame;
    }

    void saveGameState() {
        // stub
    }
}
