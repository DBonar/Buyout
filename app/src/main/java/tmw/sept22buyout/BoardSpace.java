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

    public BoardSpace(int r, int c) {
        super(r, c);
    }

    public void setOccupied() {
        isOccupied = true;
        this.setColor(BOGlobals.ClrFullSpace);
    }
    public boolean isOccupied() { return isOccupied; }

    public void setChain(Chain newchain) {
        if (chain == newchain) return;
        if (chain != null) chain.decrBoardCount();
        newchain.incrBoardCount();
        isOccupied = true;
        chain = newchain;
        this.setColor(newchain.getChainColor());
    }
    public void removeChain() {
        if (chain == null) return;
        chain.decrBoardCount();
        isOccupied = false;
        chain = null;
        this.setColor(BOGlobals.ClrEmptySpace);
    }
    public Chain getChain() { return chain; }



} // class BoardSpace
