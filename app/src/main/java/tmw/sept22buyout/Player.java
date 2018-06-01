package tmw.sept22buyout;

import java.util.ArrayList;
import java.util.List;

import static tmw.sept22buyout.PlacementStatus.StatusType.*;

/**
 * Created by Tim Weinrich on 10/15/2017.

 * Player.java

 * One player of Buyout
 */

public class Player {

    private String PlayerName = "";
    private Player NextPlayer;
    private boolean IsMachine = false;
    private int Money = 30000;
    private LList<Token> OwnedTokens = new LList<Token>();
    private LList<StockShares> OwnedStock = new LList<StockShares>();

    public Player() { }


    public void setPlayerName(String playername) { PlayerName = playername; }
    public void setNextPlayer(Player nextplayer) { NextPlayer = nextplayer; }
    public Player nextPlayer() { return NextPlayer; }
    public void setMachine() {
        IsMachine = true;
    }
    public LList<Token> getTokens() { return OwnedTokens; }
    public void addToken(Token token) {	OwnedTokens.add(token); }

    public LList<StockShares> getOwnedStock() { return OwnedStock; }
    public String toString() { return PlayerName; }

    public Token findToken(String tokenname) {
        // Find a token owned by this player named tokenname
        Token onetoken;
        ListIterator<Token> alltokens = new ListIterator<Token>(OwnedTokens);
        while ((onetoken = alltokens.getNext()) != null) {
            if (onetoken.getName().toUpperCase().equals(tokenname.toUpperCase()))
                return onetoken;
        }
        return null;
    }

    public int getChainNShares(Chain chain) {
        StockShares onechain = null;
        ListIterator<StockShares> ownedstockpile =
                new ListIterator<StockShares>(OwnedStock);
        while ((onechain = ownedstockpile.getNext()) != null) {
            if (onechain.getChain() == chain)
                return onechain.getNShares(); }
        return 0;
    }

    public Token findToken(int tokennum) {
        // Find the nth token owned by this player.
        // Of course, the first token is considered token number 0.
        Token answer = OwnedTokens.find(tokennum);
        return answer;
    }


    public void beginTokenSelection() {
        WhereAmIStack.inst().push(new WhereAmI(WhereAmI.PlayPhase.PlayToken));
        PlayGameAct.inst().msgSet("Please select a token to place on the board.");
        ActionLog.inst().prune(this); // Remove old items from the Action Log
        makeSureATokenIsPlayable();
        inputTokenSelection();
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

    public void removeToken(Token token) { OwnedTokens.remove(token); }
    public boolean fillTokens() {
        AllTokens at = AllTokens.instance();
        if (OwnedTokens.length() < at.NTokensPerPlayer) {
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

    public void inputTokenSelection() {
        PlayGameAct.inst().refreshScreen(this);
    }

    public PlacementStatus afterTokenSelection(Token tokentoplay) {
        PlacementStatus status = tokentoplay.evaluateForPlacement();
        PlayGameAct.inst().log(tokentoplay.getName() + ".evaluateForPlacement() returns " + status.getStatus());
        return status;
    } // end afterTokenSelection()




    public boolean afterSelectNewChain(Chain chain) {
        // Make sure chain is a valid option
        WhereAmI wai = WhereAmIStack.inst().look();
        Token tokentoplay = wai.getToken();
        LList<Chain> unplacedchains = wai.getChainList();
        if (! unplacedchains.find(chain)) {
            PlayGameAct.inst().msgSet("That chain is unavailable.",
                    "Please select a different chain.");
            return false; }
        // Put tokentoplay on the board and create chain
        BoardSpace space = Board.instance().getSpace(tokentoplay);
        tokentoplay.moveToBoard(this);
        chain.moveToBoard(space);
        takeStock(chain, 1);
        ActionLog.inst().add(this, "has created the " + chain.toString() + " chain");
        chain.testEndGame();
        WhereAmIStack.inst().pop(); // pop the SelectNewChain
        //PlayGameAct.inst().refreshScreen();
        beginBuyStock();
        return true;
    } // end afterSelectNewChain()


    public boolean afterSelectBuyingChain(Chain chain) {
        // User has selected chain as the buyer.
        WhereAmI wai = WhereAmIStack.inst().look();
        Token tokentoplay = wai.getToken();
        LList<Chain> buychains = wai.getChainList();
        LList<Chain> sellchains = wai.getSellChains();
        // Check for a legal selection
        if (! buychains.find(chain)) return false;
        // chain is the buyer.  All other members of buychains are now the first
        // to be sold.
        wai.setChain(chain);
        Chain onebuychain;
        ListIterator<Chain> buyiter = new ListIterator<Chain>(buychains);
        // for onebuychain in buychains
        while ((onebuychain = buyiter.getNext()) != null) {
            if (onebuychain == chain) continue;
            sellchains.add(onebuychain); }
        WhereAmIStack.inst().pop();
        // Now all chains on sellchains must be sold, in sequence.
        beginSellChainLoop(tokentoplay, chain, sellchains);
        return true;
    } // end afterSelectBuyingChain()


    public void beginSellChainLoop(Token tokentoplay, Chain buychain, LList<Chain> stilltosell) {
        // for onesellchain in sellchains
        if (stilltosell.length() == 0) {
            PlayGameAct.inst().msgSet("Error in beginSellChainLoop()");
            return;
        } else {
            Chain onesellchain = stilltosell.takeFirst();
            contSellChainLoop(tokentoplay, buychain, onesellchain, stilltosell);
        }
    } // end beginSellChainLoop()

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
            BoardSpace space = Board.instance().getSpace(tokentoplay);
            // sellchain.removeFromBoard();
            tokentoplay.moveToBoard(this);
            buychain.moveToBoard(space);
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
        seller = seller.nextPlayer();
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

    public void makeSureATokenIsPlayable() {
        // Player must own at least one playable token.  Otherwise, they are
        // all traded in for new tokens.
        if (isATokenPlayable()) return;
        // No token is playable, so we trade them in.
        AllTokens alltokens = AllTokens.instance();
        OwnedTokens = new LList<Token>();
        for (int tokenn = 1; tokenn <= alltokens.NTokensPerPlayer; tokenn++) {
            Token newtoken = alltokens.takeNextToken();
            if (newtoken != null) addToken(newtoken);
        }
        PlayGameAct.inst().msgSet(PlayerName +
                        " has thrown away all his useless tokens and has taken " +
                        alltokens.NTokensPerPlayer + " new ones:",
                "Please select a token to place on the board.");
        // displayPosition(false, false, true, false);
        // PlayGameAct.inst().refreshScreen();
    } // end makeSureATokenIsPlayable()

    public boolean isATokenPlayable() {
        // Returns true iff at least one token from OwnedTokens may be legally
        // placed on the board.
        Board board = Board.instance();
        Token onetoken;
        ListIterator<Token> alltokens =
                new ListIterator<Token>(OwnedTokens);
        while ((onetoken = alltokens.getNext()) != null) {
            PlacementStatus status = onetoken.evaluateForPlacement();
            if (status.getStatus() != IllegalSafe && status.getStatus() != IllegalNoChain)
                return true;
        }
        return false;
    } // end isATokenPlayable()

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
        Money -= chain.getPricePerShare();
        return true;
    } // boolean takeStock()

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
