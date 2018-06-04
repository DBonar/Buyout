package tmw.sept22buyout;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        LList<Chain> mergechains = new LList<Chain>(); // A list of every chain
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
                if (! mergechains.find(thischain))
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
        else if (mergechains.length() == 0 && newchain &&
                AllChains.instance().allUnplacedChains().length() == 0)
            result.setStatus(IllegalNoChain);
        else if (mergechains.length() >= 2) {
            result.setStatus(Merger);
            setMergerChains(result, mergechains); }
        else if (mergechains.length() == 1) {
            // This space will simply be added to the chain that is already
            // there.
            result.setStatus(Join);
            result.setChain(mergechains.getFirst()); }
        else if (newchain)
            result.setStatus(NewChain);
        else result.setStatus(SimplePlacement);
        return result;
    }

    private void setMergerChains(PlacementStatus result,
                                 LList<Chain> mergechains) {
        // Identify the largest chain from mergechains and put it on BuyChains.
        // BuyChains is a list because there may be more than one chain tied
        // for largest.
        // Sort all the rest according to size of chain (largest first) and
        // put them on SellChains.
        LList<Chain> largest = new LList<Chain>(); // The largest chain(s)
        int largesize = -1; // The size of the largest chain(s)
        Chain onemerge;
        ListIterator<Chain> mergers = new ListIterator<Chain>(mergechains);
        // for each chain in mergechains
        while ((onemerge = mergers.getNext()) != null) {
            if (onemerge.getBoardCount() == largesize)
                largest.add(onemerge);
            else if (onemerge.getBoardCount() > largesize) {
                largesize = onemerge.getBoardCount();
                largest = new LList<Chain>(onemerge); }
        } // end while onemerge
        // Now collect all chains not on largest and sort them.
        LList<Chain> sellchainsort = new LList<Chain>();
        ListIterator<Chain> sellmergers = new ListIterator<Chain>(mergechains);
        // for each chain in mergechains
        while ((onemerge = sellmergers.getNext()) != null) {
            if (! largest.find(onemerge)) { // only add chains not on largest
                // A hack of a sort which depends on sellchainsort never being
                // longer than 3 elements.
                if (sellchainsort.length() >= 2) {
                    if (onemerge.getBoardCount() >=
                            sellchainsort.getFirst().getBoardCount())
                        // add onemerge to front of list
                        sellchainsort.add(onemerge);
                    else if (onemerge.getBoardCount() >=
                            sellchainsort.find(1).getBoardCount()) {
                        // put onemerge as 2nd element
                        Chain ele1 = sellchainsort.getFirst();
                        Chain ele2 = onemerge;
                        Chain ele3 = sellchainsort.find(1);
                        sellchainsort = new LList<Chain>(ele1, ele2, ele3);
                    }
                    else { // onemerge belongs at end of list
                        sellchainsort.append(onemerge); }
                } // end if length == 2
                else if (sellchainsort.length() == 1) {
                    if (onemerge.getBoardCount() >=
                            sellchainsort.getFirst().getBoardCount())
                        // add onemerge to front of list
                        sellchainsort.add(onemerge);
                    else sellchainsort.append(onemerge);
                } // end if length == 1
                else if (sellchainsort.length() == 0) sellchainsort.add(onemerge);
            } // end if ! largest
        } // end while onemerge
        result.setBuyChains(largest);
        result.setSellChains(sellchainsort);
    } // end setMergerChains()

    public void moveToBoard(Player player) {
        Board board = Board.instance();
        player.removeToken(this);
        board.playToken(this);
        ActionLog.inst().add(player, "has placed the token " + this.toString());
    }

}
