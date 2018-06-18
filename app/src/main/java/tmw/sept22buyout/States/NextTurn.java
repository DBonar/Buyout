package tmw.sept22buyout.States;

import android.view.View;

import tmw.sept22buyout.Chains;
import tmw.sept22buyout.PlayGameAct;
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
        endTurn();
    }

    public void endTurn() {
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

    public boolean gameEnded() {
        // stub
        return false;
    }

    public void saveGameState() {
        // stub
    }
}
