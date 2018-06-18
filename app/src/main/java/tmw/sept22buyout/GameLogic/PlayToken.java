package tmw.sept22buyout.GameLogic;

import android.view.View;

import tmw.sept22buyout.Board;
import tmw.sept22buyout.Chain;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.Actions.PlayGameAct;
import tmw.sept22buyout.Player;
import tmw.sept22buyout.Players;
import tmw.sept22buyout.Token;

import static tmw.sept22buyout.GameLogic.PlacementStatus.StatusType.IllegalNoChain;
import static tmw.sept22buyout.GameLogic.PlacementStatus.StatusType.IllegalSafe;
import static tmw.sept22buyout.GameLogic.PlacementStatus.StatusType.Join;
import static tmw.sept22buyout.GameLogic.PlacementStatus.StatusType.Merger;
import static tmw.sept22buyout.GameLogic.PlacementStatus.StatusType.NewChain;
import static tmw.sept22buyout.GameLogic.PlacementStatus.StatusType.SimplePlacement;

public class PlayToken implements GameState {

    private PlayGameAct display;
    private Player player;

    public PlayToken(PlayGameAct theDisplay) {
        display = theDisplay;
    }


    //
    //  The turn has started, the main choice is which
    //  token to play.
    //  From here we go to one of 4 phases.
    //  * rejecting the selection as not allowed -- oops
    //  * accepting the play and starting to buy stock
    //  * accepting the play and creating a new chain
    //  * accepting the play and causing a merger.
    //
    public void enter(Player thePlayer) {
        player = thePlayer;
        Players.instance().updateCallbacks(this::playTokenClicked);
        Chains.instance().updateCallbacks(null);
        display.ContinueButton.setOnClickListener(null);
        display.msgSet(player,"Please select a token to place on the board.");
        if (player.isMachine()) {
            display.log( "Player " + player.getPlayerName() + " is selecting a token.");
            Token token = player.selectTokenToPlay();  // Machine player routine
            if (token != null) {
                playToken(token);
            } else {
                // oops, the machine did something wrong
                throw new RuntimeException("Machine player returned null when asked to play a token.");
            }
        }
    }

    public void playTokenClicked(View view) {
        Token token = (Token) view;
        playToken(token);
    }

    private void playToken(Token token) {
        Board board = Board.instance();
        PlacementStatus status = token.evaluateForPlacement();
        display.log(token.getName() + ".evaluateForPlacement() returns " + status.getStatus());

        if (status.getStatus() == IllegalSafe) {
            PlayGameAct.inst().msgSet(player, "You may not merge two safe chains.  " +
                    "Please choose another token.");
            token.setText("");
            token.setOnClickListener(null);

        } else if (status.getStatus() == IllegalNoChain) {
            PlayGameAct.inst().msgSet(player, "There are no more chains available to place on the board. " +
                    "Please choose another token.");
            token.setText("");
            token.setOnClickListener(null);

        } else if (status.getStatus() == SimplePlacement) {
            display.log( "Simple placement, add the token and buy stock");
            player.removeToken(token);
            board.playToken(token);
            GameState nextState = new BuyStock(display);
            nextState.enter(player);

        } else if (status.getStatus() == Join) { // i.e. add to a chain
            player.removeToken(token);
            Chain chain = status.getChain();
            board.addToChain(token, chain);
            chain.testEndGame();
            display.refreshScreen(player);
            GameState nextState = new BuyStock(display);
            nextState.enter(player);

        } // end status == Join
        else if (status.getStatus() == NewChain) {
            // We need to choose a chain to create
            player.removeToken(token);
            board.playToken(token);
            GameState nextState = new CreateChain(display, token);
            nextState.enter(player);

        } // end if status == newchain
        else if (status.getStatus() == Merger) {
            player.removeToken(token);
            board.playToken(token);
            GameState nextState = new Merge(display, token);
            nextState.enter(player);

        } // end if status == merger
    }



}
