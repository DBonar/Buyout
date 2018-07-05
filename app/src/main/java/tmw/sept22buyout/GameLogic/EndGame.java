package tmw.sept22buyout.GameLogic;

import android.content.Intent;


import tmw.sept22buyout.Actions.EndGameAct;
import tmw.sept22buyout.Actions.PlayGameAct;
import tmw.sept22buyout.Player;

public class EndGame implements GameState {

    private PlayGameAct display;

    public EndGame(PlayGameAct theDisplay) {
        display = theDisplay;
    }

    public void enter(Player thePlayer) {
        // We've come here because the game is done.
        // We want to shift to a new activity
        // Since this class isn't an Activity, just a bit
        // of logic inside an activity, we refer to the
        // activity we are part of to get the context and
        // start the intent.
        Intent intent = new Intent(display.getApplicationContext(), EndGameAct.class);
        display.startActivity(intent);
    }
}
