package tmw.sept22buyout;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static tmw.sept22buyout.PlacementStatus.StatusType.IllegalNoChain;
import static tmw.sept22buyout.PlacementStatus.StatusType.IllegalSafe;
import static tmw.sept22buyout.PlacementStatus.StatusType.Join;
import static tmw.sept22buyout.PlacementStatus.StatusType.Merger;
import static tmw.sept22buyout.PlacementStatus.StatusType.NewChain;
import static tmw.sept22buyout.PlacementStatus.StatusType.SimplePlacement;

/**
 * Created by Tim Weinrich on 10/18/2017.
 */

public class Token extends Button {

    private int row;
    private int col;
    private String name;
    private TextView Display = null;

    public int getCol() { return col; }
    public int getRow() { return row; }
    public String getName() { return name; }
    public String toString() { return name; }

    public void setData(Token token) {
        if (token != null) {
            row = token.row;
            col = token.col;
            name = token.name;
        } else {
            row = 0;
            col = 0;
            name = "";
        }
        setText(name);
    }

    public Token(int r, int c, Context context) {
        super(context);
        row = r;
        col = c;
        name = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(col, col + 1) +
                      Integer.toString(row + 1);

        // TODO It would be nice to have the text more centered
        LinearLayout.LayoutParams cell_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        cell_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        cell_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        cell_params.weight = 1;
        cell_params.leftMargin = 2;

        this.setPadding(8,0,0,10);
        this.setText(name);
        this.setLayoutParams(cell_params);
        this.setMinHeight(1);
        this.setMinimumHeight(1);
        setBackgroundColor(BOGlobals.ClrTokenSpace);
    }



    public PlacementStatus evaluateForPlacement() {
        // This evaluates the consequences of placing this token on the board
        // and returns the result.
        // If placing this token will cause a merger of two or more chains,
        // the status will also indicate which chain is the buyer (if known)
        // and which other chains are being merged.
        PlacementStatus result = new PlacementStatus();
        Board board = Board.instance();

        // See if there are neighboring chains or occupied spaces
        boolean newchain = false; // true iff there is a neighbor which is
        // occupied, but not part of a chain. Note that a value of true does
        // not necessarily imply that a new chain is being formed.
        List<Chain> mergechains = new ArrayList<Chain>(); // A list of every chain
        // which neighbors boardspace.
        Chain asafechain = null; // a neighboring chain that is safe
        boolean istwosafechains = false; // true iff at least two neighboring
        // chains are safe
        List<BoardSpace> neighborlist = board.allNeighbors(this);
        Iterator<BoardSpace> neighbors = neighborlist.iterator();
        // for each neighbor
        while (neighbors.hasNext()) {
            BoardSpace oneneighbor = (BoardSpace) neighbors.next();
            // If its occupied but not chained, set newchain
            if (oneneighbor.isOccupied() && oneneighbor.getChain() == null)
                newchain = true;
            // If neighbor is a chain
            if (oneneighbor.getChain() != null) {
                Chain thischain = oneneighbor.getChain();
                // Keep count of how many neighoring chains are safe.
                if (thischain.getBoardCount() >= Chain.MinSafeChainSize) {
                    if (asafechain == null) asafechain = thischain;
                    else if (asafechain != thischain) istwosafechains = true;
                }
                if (! mergechains.contains(thischain))
                    // thischain is not on mergechains, so add it
                    mergechains.add(thischain);
            } // end if getChain
        } // end while ((oneneighbor...))

        // Never permitted to combine more than one safe chains, even if there
        // are other, unsafe, chains being combined.
        // If exactly one chain is unsafe, it is always okay, since that will
        // necessarily be the buying chain.
        if (istwosafechains)
            result.setStatus(IllegalSafe);
            // At this point, there is only one legality check still to be made.
            // If a new chain is being created, There must be at least one
            // off-board chain available.
        else if (mergechains.size() == 0 && newchain &&
                AllChains.instance().allUnplacedChains().size() == 0)
            result.setStatus(IllegalNoChain);
        else if (mergechains.size() >= 2) {
            result.setStatus(Merger); }
        else if (mergechains.size() == 1) {
            // This space will simply be added to the chain that is already there.
            result.setStatus(Join);
            result.setChain(mergechains.get(0)); }
        else if (newchain)
            result.setStatus(NewChain);
        else result.setStatus(SimplePlacement);
        return result;
    }




}
