package tmw.sept22buyout;

import android.widget.TextView;

/**
 * Created by Tim Weinrich on 10/13/2017.
 */
//
// BoardSpace.java
//
// One space on the game board for Buyout game
//

public class BoardSpace extends NamedLoc {

    private TextView Display = null;
    private boolean IsOccupied = false;
    private Chain Chain = null;

    public BoardSpace(int col, int row) {
        super(col, row);
    }

    public TextView getDisplay() { return Display; }
    public void setDisplay(TextView newdisplay) { Display = newdisplay; }
    public void setOccupied() { IsOccupied = true; }
    public boolean isOccupied() { return IsOccupied; }
    public Chain getChain() { return Chain; }

    public void setChain(Chain newchain) {
        if (Chain == newchain) return;
        if (Chain != null) Chain.decrBoardCount();
        newchain.incrBoardCount();
        Chain = newchain;
    }

} // class BoardSpace
