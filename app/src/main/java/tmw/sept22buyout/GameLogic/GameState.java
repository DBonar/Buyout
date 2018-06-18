package tmw.sept22buyout.GameLogic;

import tmw.sept22buyout.Player;

public interface GameState {
    //
    //  A GameState is a node in the state machine
    //  that the game progresses through.
    //
    //  It is expected to have a constructor, perhaps
    //  some private data and an 'enter' method. The
    //  constructor provides necessary information for
    //  display for setting callbacks and for initializing
    //  the private data.
    //
    //  When enter is called, any necessary set-up is
    //  done (such as callbacks being linked), then either
    //  the method ends waiting for human interaction or the
    //  appropriate machine player routine is called.
    public void enter(Player player);

}
