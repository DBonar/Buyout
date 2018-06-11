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

    //
    // Pointless methods for a human player
    // These exist only as an interface for Machine players.
    //

    // Implement this to select a token to play on the machine
    // player's turn.
    public Token selectTokenToPlay() { return null; }

    // Implement this to select which stocks to buy.  You
    // are expected to get information about what chains
    // exist from AllChains.instance()
    public List<Chain> buyStock() { return new ArrayList<Chain>(); }

    // Implement this to select which chain to start when
    // creating a chain.  You are expected to get information
    // about which chains can be created from AllChains.instance()
    public Chain selectNewChain() { return null; }

    // Implement this to pick which chain (from the list of potentials)
    // is the merge survivor.
    public Chain selectSurvivor(List<Chain> potentials) { return potentials.get(0); }
    // Implement this to pick which chain (from the list of potentials)
    // is the next merge victim.
    public Chain selectVictim(List<Chain> potentials) { return potentials.get(0); }
    // Implement this to return the machine player's merge actions.
    // The return value is a tuple of the number of shares to sell,
    // the number of shares to trade and the number of shares to keep.
    // The 3 numbers must add up to the machine player's initial number of
    // shares in the victim chain, all values must be positive and
    // the number of shares to trade must be even.
    public List<Integer> mergeActions(Chain viciom, Chain survivor) {
        return new ArrayList<Integer>(); // error if not overridden
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
