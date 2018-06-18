package tmw.sept22buyout.States;


import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tmw.sept22buyout.Board;
import tmw.sept22buyout.BoardSpace;
import tmw.sept22buyout.Chain;
import tmw.sept22buyout.ChainButton;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.PlayGameAct;
import tmw.sept22buyout.Player;
import tmw.sept22buyout.Players;
import tmw.sept22buyout.Token;

public class Merge implements GameState {

    private PlayGameAct display;
    private Player player;
    private Token token;                  // Space where the merger occurs
    private List<Chain> potentials;       // All chains left to be merged
    private List<Chain> equalSized;       // list of equal sized potentials for selecting survivor/victim
    private Chain survivor;               // The chain that survives
    private Chain victim;                 // The chain currently being merged
    private Player mergerPlayer;          // The player making sell/trade/keep choice


    public Merge(PlayGameAct theDisplay, Token theToken) {
        display = theDisplay;
        token = theToken;
        List<BoardSpace> neighbors = Board.instance().allNeighbors(token);
        potentials = new ArrayList<Chain>();
        for (int i = 0; i < neighbors.size(); i++) {
            Chain temp = neighbors.get(i).getChain();
            if (   (temp != null)
                    && ! potentials.contains(temp) ) {
                potentials.add(temp);
            }
        }
        equalSized = null;
        survivor = null;
        victim = null;
        mergerPlayer = null;
    }

    //
    // Merge chains
    //
    // Based on the rules I found online, the largest chain
    // before the mergers will always be the survivor.  If
    // there are multiple chains tied for largest, the merging
    // player decides which is the survivor.  Then the chains
    // are merged into that survivor chain one at a time from
    // largest to smallest (seems backwards, but that's what it
    // said).  Merging player breaks ties.
    //
    // So we need a few different states.
    // [  pick surviving chaing  ]
    // loop over chains being merged
    //     [  pick chain to merge    ]
    //     loop over players
    //        sell, trade or keep?
    // Finally, after the merge, we go to buying stock.
    //
    // The privacy shield should be up when it is a machine's
    // turn, lowered when the player need to interact.  The
    // difference for each state is in the displayed message and
    // the callback for the chain buttons.
    //

    public void selectSurvivorClick(View view) {
        ChainButton cbtn = (ChainButton) view;
        Chain chain = cbtn.getChain();
        if (equalSized.contains(chain)) {
            survivor = chain;
            potentials.remove(chain);
            enter(player);
        } else {
            return; // a no-op
        }
    }

    public void selectVictimClick(View view) {
        ChainButton cbtn = (ChainButton) view;
        Chain chain = cbtn.getChain();
        if (equalSized.contains(chain)) {
            survivor = chain;
            potentials.remove(chain);
            enter(player);
        } else {
            return; // a no-op
        }
    }

    public void mergeClick(View view) {
        ChainButton cbtn = (ChainButton) view;
        Chain chain = cbtn.getChain();

        if (chain == victim) {
            // Sell 1 share of the victim for cash
            mergerPlayer.purchaseStock(victim, -1);
            if (   mergerPlayer.getChainNShares(victim) == 0) {
                display.ContinueButton.setOnClickListener(this::endMergeClick);
            }
        } else if (  (chain == survivor)
                || (mergerPlayer.getChainNShares(victim) > 1)
                || (survivor.getAvailableStock() > 0) ) {
            // Change 2 shares of the victim for 1 of the victor
            mergerPlayer.takeStock(survivor, 1 );
            mergerPlayer.takeStock(victim, -2);
            if (   mergerPlayer.getChainNShares(victim) == 0) {
                display.ContinueButton.setOnClickListener(this::endMergeClick);
            }
        } else {
            // no-op, ignore it.
            return;
        }
        display.refreshScreen(mergerPlayer);
    }

    public void endMergeClick(View view) {
        // Go to the next player or on to the next turn.
        mergerPlayer = mergerPlayer.nextPlayer();
        while (  mergerPlayer.getChainNShares(victim) == 0
                && mergerPlayer != player ) {
            mergerPlayer = mergerPlayer.nextPlayer();
        }
        enter(player);
    }

    public void enter(Player thePlayer) {
        player = thePlayer;

        if (survivor == null) {
            // Is the choice obvious?
            int largest = 0;
            for (int i = 0; i < potentials.size(); i++) {
                if (potentials.get(i).getBoardCount() > largest) {
                    largest = potentials.get(i).getBoardCount();
                }
            }
            equalSized = new ArrayList<Chain>();
            for (int i = 0; i < potentials.size(); i++) {
                if (potentials.get(i).getBoardCount() == largest) {
                    equalSized.add( potentials.get(i) );
                }
            }
            if (equalSized.size() == 1) { // Yes, obvious
                survivor = equalSized.get(0);
                potentials.remove(survivor);
            } else {
                if (player.isMachine()) {
                    Chain chain = player.selectSurvivor(equalSized);
                    survivor = chain;
                    potentials.remove(survivor);
                } else {
                    Players.instance().updateCallbacks(null);
                    Chains.instance().updateCallbacks(this::selectSurvivorClick);
                    display.ContinueButton.setOnClickListener(null);
                    String msg = "Please select which chain survives. ";
                    for (int i = 0; i < equalSized.size(); i++) {
                        msg += " " + equalSized.get(i).getName();
                        if (i < equalSized.size() - 2) {
                            msg += ",";
                        } else if (i == equalSized.size() - 2) {
                            msg += " or";
                        }
                    }
                    display.msgSet(msg);
                    return; // exit and wait for callbacks
                }
            }
        } // When we pass here we have a survivor which is no longer in potentials

        if (victim == null) {
            // Is the choice obvious?
            int largest = 0;
            for (int i = 0; i < potentials.size(); i++) {
                if (potentials.get(i).getBoardCount() > largest) {
                    largest = potentials.get(i).getBoardCount();
                }
            }
            equalSized = new ArrayList<Chain>();
            for (int i = 0; i < potentials.size(); i++) {
                if (potentials.get(i).getBoardCount() == largest) {
                    equalSized.add( potentials.get(i) );
                }
            }
            if (equalSized.size() == 1) {  // Yes, its obvious
                victim = equalSized.get(0);
                potentials.remove(victim);
            } else {
                if (player.isMachine()) {
                    Chain chain = player.selectVictim(equalSized);
                    victim = chain;
                    potentials.remove(chain);
                } else {
                    Players.instance().updateCallbacks(null);
                    Chains.instance().updateCallbacks(this::selectVictimClick);
                    display.ContinueButton.setOnClickListener(null);
                    String msg = "Please select which chain to merge. ";
                    for (int i = 0; i < equalSized.size(); i++) {
                        msg += " " + equalSized.get(i).getName();
                        if (i < equalSized.size() - 2) {
                            msg += ",";
                        } else if (i == equalSized.size() - 2) {
                            msg += " or";
                        }
                    }
                    display.msgSet(msg);
                    return; // exit and wait for callbacks
                }
            }
        } // When we pass here we have a victim which is no longer in potentials

        // So, do the loop for this survivor / victim pair
        // Just as with survivor and victim, when we first come
        // into this method, mergerPlayer == null.
        // When we get a human player as the mergerPlayer, they
        // will have to hit some buttons which will eventually
        // bring us back through this method, but
        // If mergerPlayer == player we've done this whole loop.
        //
        while (mergerPlayer != player) {
            if (mergerPlayer == null) {
                mergerPlayer = player;
            }
            if (mergerPlayer.getChainNShares(victim) > 0) {
                display.playerNameLabel.setText(mergerPlayer.getPlayerName() + "'s merge turn.");
                if (mergerPlayer.isMachine()) {
                    display.showCourtesyPanel();

                    // ask the player how many shares to (sell, trade, keep)
                    List<Integer> actions = mergerPlayer.mergeActions(victim, survivor);
                    if ((actions.size() != 3)
                            || (actions.get(0) + actions.get(1) + actions.get(2)
                            != mergerPlayer.getChainNShares(victim))
                            || (actions.get(0) < 0 || actions.get(1) < 0 || actions.get(2) < 0)
                            || (actions.get(1) % 2 != 0)) {
                        throw new RuntimeException("Error in machine player merge actions.");
                    }
                    display.log("    sell: " + actions.get(0) +
                            "  trade: " + actions.get(1) +
                            "  keep: " + actions.get(2));
                    mergerPlayer.purchaseStock(victim, -actions.get(0));
                    mergerPlayer.takeStock(survivor, actions.get(1) / 2);
                    mergerPlayer.takeStock(victim, -actions.get(1));

                } else {
                    display.hideCourtesyPanel();
                    Players.instance().updateCallbacks(null);
                    Chains.instance().updateCallbacks(this::mergeClick);
                    display.ContinueButton.setOnClickListener(null);
                    display.msgSet("Click on " + victim.getName() + " to sell a share.\n" +
                            "Click on " + survivor.getName() + " aquire 1 share.\n" +
                            "Click 'Continue' to keep the rest of your shares.");
                    return; // exit and wait for callbacks
                    // N.B.  The callback has to advance the mergerPlayer
                }
            }
            mergerPlayer = mergerPlayer.nextPlayer();
        }

        // Do we have more to merge?
        if (potentials.size() > 0) {
            victim = null;
            mergerPlayer = null;
            enter(player);
        } else {
            // Check for any extra, non-chain, spaces we need to merge in.
            GameState nextState = new BuyStock(display);
            nextState.enter(player);
        }
    }
}
