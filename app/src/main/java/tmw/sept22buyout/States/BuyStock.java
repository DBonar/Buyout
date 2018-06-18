package tmw.sept22buyout.States;

import android.view.View;

import java.util.List;

import tmw.sept22buyout.Chain;
import tmw.sept22buyout.ChainButton;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.PlayGameAct;
import tmw.sept22buyout.Player;
import tmw.sept22buyout.Players;

public class BuyStock implements GameState {

    private PlayGameAct display;
    private Player player;
    private int numberPurchased;
    private final static int maxNumberPurchased = 3;


    //
    //  Buying Stock
    //  The player token buttons are meaningless
    //  The chain labels are valid buttons for buying
    //  You can buy at most 3 shares.
    //  The phase ends when you click the Continue button.
    //  The next phase is moving on to the next player's turn.
    //
    public BuyStock(PlayGameAct theDisplay) {
        display = theDisplay;
        numberPurchased = 0;
    }


    public void enter(Player thePlayer) {
        player = thePlayer;
        Players.instance().updateCallbacks(null);
        Chains.instance().updateCallbacks(this::buyStockClick );
        display.ContinueButton.setOnClickListener(this::nextTurnClicked);
        display.msgSet(player,"Click on a chain to buy stock or 'Continue' to end your turn.");
        if (player.isMachine()) {
            List<Chain> buys = player.buyStock();  // Machine player routine
            for (int i = 0; i < buys.size(); i++) {
                buyStock(buys.get(i));
            }
            if (buys.size() < maxNumberPurchased)
                afterBuyingStock();
        }
    }

    public void buyStockClick(View view) {
        ChainButton btn = (ChainButton) view;
        Chain chain = btn.getChain();
        buyStock(chain);
    }

    public void nextTurnClicked(View view) {
        afterBuyingStock();
    }

    public void afterBuyingStock() {
        GameState nextTurn = new NextTurn(display);
        nextTurn.enter(player);
    }

    public void buyStock(Chain chain) {
        if (!chain.isOnBoard()) {
            display.msgSet(player, "Sorry.  That chain is not on the board.  " +
                    "Please choose a different chain, or click 'Continue'.");
        } else {
            // See if we can afford it
            if (!player.canAfford(chain)) { // He cannot afford it
                display.msgSet(player,"Sorry.  You cannot afford that issue. " +
                        "Please choose a different chain, or click 'Continue'.");
            }

            if (player.purchaseStock(chain, 1)) {
                // We have successfully purchased the share
                display.refreshScreen(player);
            } else {
                display.msgSet(player,"Sorry.  There are no more shares of that stock available. " +
                        "Please choose a different chain, or click 'Continue'.");
            }

            numberPurchased += 1;
            if (numberPurchased == maxNumberPurchased)
               afterBuyingStock();
        }
    }


}
