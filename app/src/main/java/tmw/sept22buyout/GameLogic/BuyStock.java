package tmw.sept22buyout.GameLogic;

import android.view.View;

import java.util.List;

import tmw.sept22buyout.Chain;
import tmw.sept22buyout.ChainButton;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.Actions.PlayGameAct;
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

        // check whether player can buy stock
        List<Chain> chains = Chains.instance().allPlacedChains();
        boolean canBuy = false;
        for (int i = 0; i < chains.size(); i++) {
            Chain chain = chains.get(i);
            if (player.canAfford(chain))
                canBuy = true;
        }

        Players.instance().updateCallbacks(null);
        display.ContinueButton.setOnClickListener(this::nextTurnClicked);
        if (canBuy) {
            Chains.instance().updateCallbacks(this::buyStockClick);
            display.msgSet(player, "Click on a chain to buy stock or 'Continue' to end your turn.");
            display.refreshScreen(player);
            if (player.isMachine()) {
                List<Chain> buys = player.buyStock();  // Machine player routine
                for (int i = 0; i < buys.size(); i++) {
                    buyStock(buys.get(i));
                }
                afterBuyingStock();
            }
        } else { // can't buy
            Chains.instance().updateCallbacks(this::buyStockClick);
            display.msgSet(player, "There are no chains you can purchase.\n" +
                            "Please click 'Continue' to end your turn.");
            player.fillTokens();
            display.refreshScreen(player);
            if (player.isMachine())
                afterBuyingStock();
        }
    }

    public void buyStockClick(View view) {
        ChainButton btn = (ChainButton) view;
        Chain chain = btn.getChain();
        buyStock(chain);
    }

    public void nextTurnClicked(View view) {
        player.fillTokens();  // If you clicked 'early' (before buying max), sill need to fill.
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
            if (numberPurchased == maxNumberPurchased) {
                Chains.instance().updateCallbacks(null );
                display.msgSet(player, "Please click 'Continue' to end your turn.");
                player.fillTokens();
                display.refreshScreen(player);
                // Don't automatically nove to the next turn.
                //afterBuyingStock();
            }
        }
    }


}
