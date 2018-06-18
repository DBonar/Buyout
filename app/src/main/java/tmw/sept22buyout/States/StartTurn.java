package tmw.sept22buyout.States;


import android.view.View;

import tmw.sept22buyout.PlayGameAct;
import tmw.sept22buyout.Player;

public class StartTurn implements GameState {

    private PlayGameAct display;
    private Player player;

    public StartTurn(PlayGameAct theDisplay) {
        display = theDisplay;
    }

    public void enter(Player thePlayer) {
        player = thePlayer;
        display.showCourtesyPanel(thePlayer, "turn", this::startTurnClick);
        if (player.isMachine()) {
            GameState nextState = new PlayToken(display);
            nextState.enter(player);
        }
    }

    public void startTurnClick(View view) {
        display.hideCourtesyPanel();
        GameState nextState = new PlayToken(display);
        nextState.enter(player);
    }
}
