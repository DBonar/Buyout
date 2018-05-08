package tmw.sept22buyout;

import android.support.annotation.ColorInt;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Tim Weinrich on 10/13/2017.
 */
//
// Chain.java
//
// Represents a chain of banks in Buyout
//
//
// Chain.java
//
// Represents a chain of banks in Buyout
//

public class Chain {

    final static int MaxChainSize = 40; // It is permissible to have chains larger
    // than MaxChainSize.  But if any chain is larger than this, the current
    // player may end the game.
    final static int MinSafeChainSize = 11;

    static public enum BankClass { Community, SnL, Investment }

    private String Name;
    private BankClass ChainClass;
    private boolean OnBoard = false;
    private BoardSpace ChainSeed = null; // Position of one tile of this chain
    private int BoardCount = 0;
    private int StockAvailable = 25;
    private @ColorInt int ChainColor;

    public Chain(String chainname, BankClass newvalue, @ColorInt int newcolor) {
        Name = chainname;
        ChainClass = newvalue;
        OnBoard = false;
        ChainSeed = null;
        BoardCount = 0;
        ChainColor = newcolor;
    }

    public String getName() { return Name; }
    public String getChainClass() { return ChainClass.name(); }
    public boolean isOnBoard() { return OnBoard; }
    public boolean isSafe() { return (getBoardCount() >= MinSafeChainSize); };
    public int getBoardCount() { return BoardCount; }
    public void incrBoardCount() { BoardCount++; }
    public void decrBoardCount() { BoardCount--; }
    public int getAvailableStock() { return StockAvailable; }
    public void decreaseStock(int nshares) {
        StockAvailable -= nshares;
    }
    public void increaseStock(int nshares) { StockAvailable += nshares; }
    public int getPricePerShare() {
        return ValuationTable.getPricePerShare(ChainClass, BoardCount); }
    public int getStartingPricePerShare() {
        return ValuationTable.getPricePerShare(ChainClass, 2); }
    public int getBonusPrice(boolean isprimary) {
        return ValuationTable.getBonusPrice(ChainClass, BoardCount, isprimary);
    }
    public @ColorInt int getChainColor() { return ChainColor; }
    public String toString() { return Name; }
    public String toStringWClass() {
        String issafe = (getBoardCount() >= MinSafeChainSize) ?
                "Safe " : "";
        return Name + "(" + issafe + ChainClass + " Bank)"; }
    // We'd like the FullString to look like this:
    // Class=nn, Size=dd, Price=dd, Holdings=dd, Unsold=dd
    public String toFullString(Player player) {
        return "Class=" + ChainClass.name() +
                ((isSafe()) ? ("(" + "Safe" + ")") : "") +
                ", Size=" + BoardCount +
                ((BoardCount == 0) ? (", Starting Price=$" + getStartingPricePerShare()) :
                        ", Price=$" + getPricePerShare()) +
                ", Holdings=" + player.getChainNShares(this) +
                ", Unsold=" + StockAvailable; }
    // public String toFullString() { return Name + " " + ChainClass.name() + " " + ChainSeed + " " + BoardCount + " " + StockAvailable; }

    public void moveToBoard(BoardSpace space) {
        // put this chain on the board
        Board board = Board.instance();
        OnBoard = true;
        ChainSeed = space;
        ChainSeed.setChain(this);
        fillIn();
    }

    public void removeFromBoard() {
        // take this chain off the board.  We assume the chain has been bought
        // by another chain, and the boardspaces will be converted by its call
        // to fillIn().
        OnBoard = false;
        ChainSeed = null;
        // BoardCount will be reset by fillIn()
    }

    public void fillIn() {
        // Find all occupied spaces which can reach the ChainSeed by way of
        // occupied spaces.  Mark them all as part of this Chain.
        LList<BoardSpace> borderlist = new LList<BoardSpace>();
        LList<BoardSpace> markedlist = new LList<BoardSpace>();
        borderlist.add(ChainSeed);
        fillInRecurse(borderlist, markedlist);
    }

    public void fillInRecurse(LList<BoardSpace> borderlist,
                              LList<BoardSpace> markedlist) {
        // borderlist is a list of nodes to be checked.
        // markedlist is a list of nodes which have already been checked.
        // If this is too slow, we can rewrite this using markers.
        // Except when this is first called, we can depend that every member of
        // borderlist is occupied.
        BoardSpace thisborder = borderlist.takeFirst();
        if (thisborder == null) return;
        thisborder.setChain(this);
        // Mark this space as already checked
        markedlist.add(thisborder);
        // Check each neighbor of thisborder.
        List<BoardSpace> neighborlist = Board.instance().allNeighbors(thisborder);
        Iterator<BoardSpace> neighbors = neighborlist.iterator();
        while (neighbors.hasNext()) {
            BoardSpace oneneighbor = (BoardSpace) neighbors.next();
            if (oneneighbor.isOccupied()) {
                // Any unoccupied place is ignored
                // See if this neighbor has already been checked
                if (markedlist.find(oneneighbor) == false) {
                    // It has not been checked, so it needs to be
                    borderlist.add(oneneighbor);
                    fillInRecurse(borderlist, markedlist);
                }
            } // if (oneneighbor.isOccupied)
        } // while
    } // void fillInRecurse()

    public String payShareholderBonuses(Player turnplayer) {
        // Find who owns the most shares of this chain, and who owns the
        // second most, and pay them their bonuses.
        // It is difficult because multiple players can tie for maximum.
        // Alternatively, multiple players can tie for second-to-max.
        // First, we find the maximum and second-to-maximum number of shares
        // owned by any player.
        // Normally, these transactions are written to the ActionLog under
        // turnplayer's turn. If turnplayer is null, this returns the transactions
        // as a String.
        String output = "";
        AllPlayers allplayers = AllPlayers.instance();
        int nprimeshares = 0;
        int nsecondshares = 0;
        int nplayerowners = 0;
        // Compute the largest number of shares anyone owns.
        // At the same time, compute the second largest number of shares.
        // Also, count the number of players who own any shares at all.
        for (int playern = 0; playern < allplayers.length(); playern++) {
            Player oneplayer = allplayers.getPlayerN(playern);
            int nshares = oneplayer.getChainNShares(this);
            if (nshares > 0) nplayerowners++;
            if (nshares > nprimeshares) {
                nsecondshares = nprimeshares;
                nprimeshares = nshares; }
            else if (nshares < nprimeshares && nshares > nsecondshares)
                nsecondshares = nshares;
        }
        // Now collect up all players who own nprimeshares
        if (nprimeshares != 0) { // I doubt if it is possible for this to be 0
            // but just in case.
            int nprimeplayers = 0;
            for (int playern = 0; playern < allplayers.length(); playern++) {
                Player oneplayer = allplayers.getPlayerN(playern);
                if (oneplayer.getChainNShares(this) == nprimeshares)
                    nprimeplayers++;
            }
            // Pay primary bonuses
            int primebonus = getBonusPrice(true);
            if (nprimeplayers > 1 || (nprimeplayers == 1 && nplayerowners == 1))
                primebonus = ((((getBonusPrice(true) +
                        getBonusPrice(false)) / nprimeplayers)
                        + 50) / 100) * 100;
            for (int playern = 0; playern < allplayers.length(); playern++) {
                Player oneplayer = allplayers.getPlayerN(playern);
                if (oneplayer.getChainNShares(this) == nprimeshares) {
                    System.out.println(oneplayer.getPlayerName() + " is paid a primary bonus of " + primebonus + " dollars");
                    oneplayer.incrMoney(primebonus);
                    if (turnplayer == null) {
                        output += oneplayer.toString() + " received a primary bonus of $" +
                                primebonus +
                                " for " + toString() + "\n";
                    }
                    else {
                        ActionLog.inst().add(turnplayer, oneplayer,
                                "received a primary bonus of $" + primebonus +
                                        " for " + toString());
                    }
                } // if oneplayer
            } // for playern
            if (nprimeplayers > 1 || nplayerowners == 1) return output;
        }
        // Now collect up all players who own nsecondshares
        if (nsecondshares != 0) {
            int nsecondplayers = 0;
            for (int playern = 0; playern < allplayers.length(); playern++) {
                Player oneplayer = allplayers.getPlayerN(playern);
                if (oneplayer.getChainNShares(this) == nsecondshares)
                    nsecondplayers++;
            }
            int secondbonus = ((((getBonusPrice(false) / nsecondplayers)
                    + 50) / 100) * 100);
            // Pay secondary bonuses
            for (int playern = 0; playern < allplayers.length(); playern++) {
                Player oneplayer = allplayers.getPlayerN(playern);
                if (oneplayer.getChainNShares(this) == nsecondshares) {
                    System.out.println(oneplayer.getPlayerName() + " is paid a secondary bonus of " + secondbonus + " dollars");
                    oneplayer.incrMoney(secondbonus);
                    if (turnplayer == null) {
                        output += oneplayer.toString() + " received a secondary bonus of $" +
                                secondbonus + " for " + toString() + "\n";
                    }
                    else {
                        ActionLog.inst().add(turnplayer, oneplayer,
                                "received a secondary bonus of $" + secondbonus +
                                        " for " + toString());
                    }
                } // if oneplayer
            } // for playern
        }
        return output;
    } // void payShareholderBonuses()

    public void testEndGame() {
        if (getBoardCount() > Chain.MaxChainSize)
            BOGlobals.EndOfGameOption = true;
        if (getBoardCount() >= Chain.MinSafeChainSize &&
                AllChains.instance().isAllOnBoardChainsSafe())
            BOGlobals.EndOfGameOption = true;
    }

}

