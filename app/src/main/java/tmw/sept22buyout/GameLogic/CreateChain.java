package tmw.sept22buyout.GameLogic;

import android.view.View;

import tmw.sept22buyout.Board;
import tmw.sept22buyout.Chain;
import tmw.sept22buyout.ChainButton;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.Actions.PlayGameAct;
import tmw.sept22buyout.Player;
import tmw.sept22buyout.Players;
import tmw.sept22buyout.Token;

public class CreateChain implements GameState {

    private PlayGameAct display;
    private Player player;
    private Token token;

    public CreateChain(PlayGameAct theDisplay, Token theToken) {
        display = theDisplay;
        token = theToken;
    }

    //
    //  Create a new chain
    //  The player token buttons are meaningless
    //  The chain labels are valid buttons for selecting
    //  The continue button is meaningless
    //  The phase ends when you have selected a valid chain
    //  The next phase is buying stock.
    //
    public void enter(Player thePlayer) {
        player = thePlayer;
        display.log("Entering Player.afterTokenSelection()/UserPicksChain");
        Players.instance().updateCallbacks(null);
        Chains.instance().updateCallbacks(this::createNewChainClick);
        display.ContinueButton.setOnClickListener(null);
        display.msgSet(player, "Please select the chain you wish to create.");
        if (player.isMachine()) {
            Chain chain = player.selectNewChain();  // Machine player routine
            display.log( "Creating chain " + chain.toString());
            createNewChain(chain);
        }
    }


    public void createNewChain(Chain chain) {
        if (chain.isOnBoard()) {
            display.msgSet(player, "Sorry.  That chain is on the board already.  " +
                    "Please choose a different chain.");
        } else {
            Board board = Board.instance();
            board.addToChain(token, chain);
            if (chain.getAvailableStock() > 0) {
                player.takeStock(chain, 1);
            }
            display.refreshScreen(player);
            // Chain has been created, so move to buying stock
            GameState nextState = new BuyStock(display);
            nextState.enter(player);
        }
    }

    public void createNewChainClick(View view) {
        ChainButton btn = (ChainButton) view;
        createNewChain(btn.getChain());
    }
}

