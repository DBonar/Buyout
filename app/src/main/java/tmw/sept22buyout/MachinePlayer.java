package tmw.sept22buyout;

import android.util.Log;

import static tmw.sept22buyout.PlacementStatus.StatusType.*;

/**
 * Created by Tim Weinrich on 1/4/2018.
 *
 * MachinePlayer.java
 *
 * Makes moves for automatic players.
 */

public class MachinePlayer extends Player {

    protected MachinePlayer() {
    }

    public void inputTokenSelection() {
        if (BOGlobals.EndOfGameOption) {
            // PlayGameAct.inst().startEndGame();
            return;
        }
        PlacementStatus status = null;
        Token onetoken;
        ListIterator<Token> tokenlist = new ListIterator<Token>(getTokens());
        while ((onetoken = tokenlist.getNext()) != null) {
            status = onetoken.evaluateForPlacement();
            if (status.getStatus() != IllegalSafe && status.getStatus() != IllegalNoChain) {
                break;
            }
        }
        afterTokenSelection(onetoken);
    }

    public void inputSelectNewChain(Token tokentoplay, List<Chain> unplacedchains) {
        afterSelectNewChain(unplacedchains.getFirst());
    }

    public void inputSelectBuyingChain(Token tokentoplay, List<Chain> buychains,
                                       List<Chain> sellchains) {
        afterSelectBuyingChain(buychains.getFirst());
    }

    public void inputUnloadStock(Token tokentoplay, Chain buychain, Chain sellchain,
                                 Player seller, int sharestounload) {
        seller.sellStock(sellchain, sharestounload);
        ActionLog.inst().add(BOGlobals.CurrentPlayer, seller,
                "has sold " + sharestounload + " shares of " + sellchain.toString());
        BOGlobals.CurrentPlayer.afterUnloadStock();
    }

    public void inputBuyStock() {
        // We will buy up to two shares of the cheapest stock on the board.
        // It is necessary to make sure that there are shares left, before buying.
        // It is necessary to make sure we can afford it, before buying.
        PlayGameAct.inst().log("Entered: inputBuyStock()");
        Chain bestchain = null;
        Chain onechain;
        ListIterator<Chain> chainlist =
                new ListIterator<Chain>(AllChains.instance().getAllChains());
        while ((onechain = chainlist.getNext()) != null) {
            if (onechain.isOnBoard()) {
                if (onechain.getAvailableStock() >= 1) {
                    int pps = onechain.getPricePerShare();
                    if (pps <= getMoney()) {
                        if (bestchain == null || pps <= bestchain.getPricePerShare())
                            bestchain = onechain;
                    }
                }
            }
        }
        // We typically buy two shares
        afterBuyStock(bestchain);
        if (bestchain != null && bestchain.getPricePerShare() <= getMoney())
            // We can afford a second share.
            afterBuyStock(bestchain);
        afterBuyStock(null);
    } // end inputBuyStock()

    public void inputTakeTile() {
        afterTakeTile();
    }

    public boolean afterTakeTile() {
        WhereAmIStack.inst().pop();
        return true;
    }

} // end Class MachinePlayer