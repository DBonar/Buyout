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
    private LList<StockShares> OwnedStock = new LList<StockShares>();

    public Player() { }


    public void setPlayerName(String playername) { PlayerName = playername; }
    public void setMachine() {
        IsMachine = true;
    }
    public List<Token> getTokens() { return OwnedTokens; }
    public void addToken(Token token) {	OwnedTokens.add(token); }

    public LList<StockShares> getOwnedStock() { return OwnedStock; }
    public String toString() { return PlayerName; }


    public int getChainNShares(Chain chain) {
        StockShares onechain = null;
        ListIterator<StockShares> ownedstockpile =
                new ListIterator<StockShares>(OwnedStock);
        while ((onechain = ownedstockpile.getNext()) != null) {
            if (onechain.getChain() == chain)
                return onechain.getNShares(); }
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


    //
    //
    //


    public void contSellChainLoop(Token tokentoplay, Chain buychain, Chain onesellchain,
                                  LList<Chain> stilltosell) {
        // onesellchain.payShareholderBonuses(this)
        onesellchain.payShareholderBonuses(this);
        beginSellChainPlayerLoop(tokentoplay, buychain, onesellchain, stilltosell);
    } // end contSellChainLoop()

    public void nextSellChainLoop(Token tokentoplay, Chain buychain, Chain sellchain,
                                  LList<Chain> stilltosell) {
        sellchain.removeFromBoard();
        if (stilltosell.length() == 0) {
            // We are done selling stock.
            // Combine the chains and continue with the moves

            tokentoplay.moveToBoard(this);
            buychain.moveToBoard(tokentoplay);
            beginBuyStock();
            return;
        }
        else {
            Chain onesellchain = stilltosell.takeFirst();
            contSellChainLoop(tokentoplay, buychain, onesellchain, stilltosell);
        }
    } // end nextSellChainLoop()

    public void beginSellChainPlayerLoop(Token tokentoplay, Chain buychain, Chain sellchain,
                                         LList<Chain> stilltosell) {
        // for seller = this
        Player seller = this;
        Player endplayer = this;
        contSellChainPlayerLoop(tokentoplay, buychain, sellchain, stilltosell,
                seller, endplayer);
    } // end beginSellChainPlayerLoop()

    public void contSellChainPlayerLoop(Token tokentoplay, Chain buychain, Chain sellchain,
                                        LList<Chain> stilltosell, Player seller,
                                        Player endplayer) {
        // if seller has sellchain stock:
        int sharestounload = seller.getChainNShares(sellchain);
        if (sharestounload == 0)
            nextSellChainPlayerLoop(tokentoplay, buychain, sellchain, stilltosell,
                    seller, endplayer);
        else {
            // unloadStock(seller, sellchain)
            beginUnloadStock(tokentoplay, buychain, sellchain, stilltosell,
                    seller, endplayer, sharestounload);
        }
    } // end contSellChainPlayerLoop()

    public void nextSellChainPlayerLoop(Token tokentoplay, Chain buychain, Chain sellchain,
                                        LList<Chain> stilltosell, Player seller,
                                        Player endplayer) {
        // seller = next player
        seller = AllPlayers.instance().nextPlayer(seller);
        // repeat if seller != this
        if (seller != endplayer)
            contSellChainPlayerLoop(tokentoplay, buychain, sellchain, stilltosell,
                    seller, endplayer);
        else nextSellChainLoop(tokentoplay, buychain, sellchain, stilltosell);
    } // end nextSellChainPlayerLoop()

    public void beginUnloadStock(Token tokentoplay, Chain buychain, Chain sellchain,
                                 LList<Chain> stilltosell, Player seller,
                                 Player endplayer, int sharestounload) {
        // Ask seller what to do with his shares of sellchain
        PlayGameAct.inst().log("beginUnloadStock called with (" + tokentoplay.getName() +
                ", " + sellchain.getName() + ", ..., " + seller.getPlayerName() + ", " +
                endplayer.getPlayerName() + ", " + sharestounload);
        WhereAmI wai = new WhereAmI(WhereAmI.PlayPhase.UnloadStock);
        wai.setToken(tokentoplay);
        wai.setBuyChain(buychain);
        wai.setChain(sellchain);
        wai.setChainList(stilltosell);
        wai.setPlayer(seller);
        wai.setEndPlayer(endplayer);
        wai.setNShares(sharestounload);
        WhereAmIStack stack = WhereAmIStack.inst();
        WhereAmIStack.inst().push(wai);
        seller.inputUnloadStock(tokentoplay, buychain, sellchain, seller, sharestounload);
    } // end beginUnloadStock()

    public void inputUnloadStock(Token tokentoplay, Chain buychain, Chain sellchain,
                                 Player seller, int sharestounload) {
        PlayGameAct.inst().startNewPlayerSell();
    } // end inputUnloadStock()

    public boolean afterUnloadStock() {
        WhereAmI wai = WhereAmIStack.inst().pop();
        Token tokentoplay = wai.getToken();
        Chain buychain = wai.getBuyChain();
        Chain sellchain = wai.getChain();
        LList<Chain> stilltosell = wai.getChainList();
        Player seller = wai.getPlayer();
        Player endplayer = wai.getEndPlayer();
        nextSellChainPlayerLoop(tokentoplay, buychain, sellchain, stilltosell,
                seller, endplayer);
        return true;
    } // end afterUnloadStock()

    public void beginBuyStock() {
        if (existsLegalStockPurchase()) {
            WhereAmI wai = new WhereAmI(WhereAmI.PlayPhase.BuyStock);
            WhereAmIStack.inst().push(wai);
            wai.setNShares(3);
            PlayGameAct.inst().msgSet("You may buy up to 3 shares of stock. Select a chain to buy (or Continue to pass).");
            inputBuyStock();
        } else {
            beginTakeTile();
        }
    }

    public void inputBuyStock() {
        PlayGameAct.inst().refreshScreen(this);
        // Now wait for the user to click
    }

    public boolean afterBuyStock(Chain chain) {
        WhereAmI wai = WhereAmIStack.inst().look();
        if (chain == null) { // Player does not wish to buy any more stock
            WhereAmIStack.inst().pop();
            beginTakeTile();
        }
        else {
            if (!chain.isOnBoard()) {
                PlayGameAct.inst().msgSet("Sorry.  That chain is not on the board.",
                        "Please choose a different chain, or click 'Continue'.");
                return false;
            }
            // See if we can afford it
            int price = chain.getPricePerShare();
            if (Money < price) { // He cannot afford it
                PlayGameAct.inst().msgSet("Sorry.  You cannot afford that issue.",
                        "Please choose a different chain, or click 'Continue'.");
                return false;
            }
            if (takeStock(chain, 1)) {
                Money -= price;
                // We have successfully purchased the share
                wai.setNShares(wai.getNShares() - 1);
                PlayGameAct.inst().msgSet("You may buy up to " + wai.getNShares() + " more shares");
                ActionLog.inst().add(this, "bought a share of " + chain.toString());
                PlayGameAct.inst().refreshScreen(this);
                if (wai.getNShares() > 0 &&
                        existsLegalStockPurchase())
                    return false; // Can purchase more
                // All allowable shares have been purchased.  BuyStock phase is over
                WhereAmIStack.inst().pop();
                beginTakeTile();
            } else {
                PlayGameAct.inst().msgSet("Sorry.  There are no more shares of that stock available.",
                        "Please choose a different chain, or click 'Continue'.");
                return false;
            }
        }
        return true;
    } // end afterBuyStock()

    public void beginTakeTile() {
        WhereAmI wai = new WhereAmI(WhereAmI.PlayPhase.TakeTile);
        WhereAmIStack.inst().push(wai);
        Token nexttoken = AllTokens.instance().takeNextToken();
        if (nexttoken != null) addToken(nexttoken);
        inputTakeTile();
    }

    public void inputTakeTile() {
        PlayGameAct.inst().msgSet("Please click 'Continue' to end your turn");
        PlayGameAct.inst().refreshScreen(this);
    }

    public boolean afterTakeTile() {
        WhereAmIStack.inst().pop();
        PlayGameAct.inst().gameLoop();
        return true;
    }



    public boolean canAfford(Chain chain) {
        int price = chain.getPricePerShare();
        return (Money >= price);
    }

    public boolean existsLegalStockPurchase() {
        Chain onechain;
        ListIterator<Chain> chains =
                new ListIterator<Chain>(AllChains.instance().allPlacedChains());
        while ((onechain = chains.getNext()) != null) {
            if ((onechain.getAvailableStock() > 0) &&
                    onechain.getPricePerShare() <= getMoney())
                return true;
        }
        return false;
    }

    public boolean takeStock(Chain chain, int nshares) {
        // Takes nshares shares of stock from chain.
        if (chain.getAvailableStock() < nshares)
            return false;
        chain.decreaseStock(nshares);
        StockShares onechain = null;
        ListIterator<StockShares> ownedstockpile =
                new ListIterator<StockShares>(OwnedStock);
        while ((onechain = ownedstockpile.getNext()) != null) {
            if (onechain.getChain() == chain) {
                onechain.addStock(nshares);
                return true; }
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
        StockShares onechain = null;
        ListIterator<StockShares> ownedstockpile =
                new ListIterator<StockShares>(OwnedStock);
        while ((onechain = ownedstockpile.getNext()) != null) {
            if (onechain.getChain() == chain) {
                onechain.subtractStock(nshares);
                return true; }
        }
        System.out.println("Failure in Player.giveStock()");
        System.exit(1);
        return false;
    }

} // end chain Player
