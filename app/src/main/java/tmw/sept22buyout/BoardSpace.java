package tmw.sept22buyout;

import android.widget.TextView;
import android.content.Context;

/**
 * Created by Tim Weinrich on 10/13/2017.
 */
//
// BoardSpace.java
//
// One space on the game board for Buyout game
//

public class BoardSpace extends Token {

    private boolean isOccupied = false;
    private Chain chain = null;

    public BoardSpace(int r, int c, Context context) {
        super(r, c, context);
    }

    public void setOccupied() {
        isOccupied = true;
        this.setBackgroundColor(BOGlobals.ClrFullSpace);
    }
    public boolean isOccupied() { return isOccupied; }

    public void setChain(Chain newchain) {
        if (chain == newchain) return;
        if (chain != null) chain.decrBoardCount();
        newchain.incrBoardCount();
        isOccupied = true;
        chain = newchain;
        this.setBackgroundColor(newchain.getChainColor());
    }
    public void removeChain() {
        if (chain == null) return;
        chain.decrBoardCount();
        isOccupied = false;
        chain = null;
        this.setBackgroundColor(BOGlobals.ClrEmptySpace);
    }
    public Chain getChain() { return chain; }



} // class BoardSpace
