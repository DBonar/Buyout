package tmw.sept22buyout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static tmw.sept22buyout.PlacementStatus.StatusType.*;

/**
 * Created by Tim Weinrich on 10/15/2017.

 * Player.java

 * One player of Buyout
 */

public class Player {

    private String PlayerName = "";
    private boolean IsMachine = false;
    private int Money = 30000;
    private List<Token> OwnedTokens = new ArrayList<Token>();
    private List<StockShares> OwnedStock = new ArrayList<StockShares>();

    public Player() { }


    public void setPlayerName(String playername) { PlayerName = playername; }
    public void setMachine() {
        IsMachine = true;
    }
    public List<Token> getTokens() { return OwnedTokens; }
    public void addToken(Token token) {	OwnedTokens.add(token); }

    public List<StockShares> getOwnedStock() { return OwnedStock; }
    public String toString() { return PlayerName; }


    public int getChainNShares(Chain chain) {
        for (int i = 0; i < OwnedStock.size(); i++)
            if (OwnedStock.get(i).getChain() == chain)
                return OwnedStock.get(i).getNShares();
        return 0;
    }


    //
    //  Things I need
    //

    public String getPlayerName() { return PlayerName; }
    public boolean isMachine() { return IsMachine; }

    // Pointless methods for a human player
    // These exist only as an interface for Machine players.
    public Token selectTokenToPlay() { return null; }
    public List<Chain> buyStock() { return new ArrayList<Chain>(); }
    public Chain selectNewChain() { return null; }
    public Chain selectSurvivor(List<Chain> potentials) { return potentials.get(0); }
    public Chain selectVictom(List<Chain> potentials) { return potentials.get(0); }
    public List<Integer> mergeActions(Chain victom, Chain survivor) {
        return new ArrayList<Integer>();
    }

    public void removeToken(Token token) {
        // We can't remove by object identity
        Iterator<Token> it = OwnedTokens.iterator();
        while (it.hasNext()) {
            Token trial = (Token) it.next();
            if (   (trial.getRow() == token.getRow())
                && (trial.getCol() == token.getCol()) ) {
                OwnedTokens.remove(trial);
                return;
            }
        }
    }

    public boolean fillTokens() {
        AllTokens at = AllTokens.instance();
        if (OwnedTokens.size() < at.NTokensPerPlayer) {
            Token tk = at.takeNextToken();
            OwnedTokens.add(tk);
            return (tk != null);
        }
        return true;
    }

    public int getMoney() { return Money; }
    public void incrMoney(int arg) { Money += arg; }


    public boolean canAfford(Chain chain) {
        int price = chain.getPricePerShare();
        return (Money >= price);
    }


    public boolean takeStock(Chain chain, int nshares) {
        // Takes nshares shares of stock from chain.
        if (chain.getAvailableStock() < nshares)
            return false;

        chain.decreaseStock(nshares);

        for (int i = 0; i < OwnedStock.size(); i++) {
            if (OwnedStock.get(i).getChain() == chain) {
                OwnedStock.get(i).addStock(nshares);
                return true;
            }
        }

        // We previously owned no stock in chain.
        OwnedStock.add(new StockShares(chain, nshares));
        return true;
    } // boolean takeStock()

    public boolean purchaseStock(Chain chain, int nshares) {
        int cost = chain.getPricePerShare() * nshares;
        if (   (Money >= cost)
            && takeStock(chain, nshares) ) {
            Money -= chain.getPricePerShare() * nshares;
            return true;
        }
        return false;
    }

    public boolean sellStock(Chain chain, int nshares) {
        int totalprice = chain.getPricePerShare() * nshares;
        boolean isstockgiven = giveStock(chain, nshares);
        if (isstockgiven) {
            Money += totalprice;
            return true; }
        return false;
    }

    public boolean giveStock(Chain chain, int nshares) {
        chain.increaseStock(nshares);

        for (int i = 0; i < OwnedStock.size(); i++) {
            if (OwnedStock.get(i).getChain() == chain) {
                OwnedStock.get(i).subtractStock(nshares);
                return true;
            }
        }

        System.out.println("Failure in Player.giveStock()");
        System.exit(1);
        return false;
    }

} // end chain Player
