package tmw.sept22buyout.States;

import tmw.sept22buyout.PlayGameAct;
import tmw.sept22buyout.Player;

public class EndGame implements GameState {

    private PlayGameAct display;

    public EndGame(PlayGameAct theDisplay) {
        display = theDisplay;
    }

    public void enter(Player thePlayer) {
        // Stub
    }
}
