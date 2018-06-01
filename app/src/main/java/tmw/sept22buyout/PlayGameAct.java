package tmw.sept22buyout;

import android.content.Intent;
import android.graphics.Color;
import android.renderscript.Byte2;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.List;

import static tmw.sept22buyout.PlacementStatus.StatusType.IllegalNoChain;
import static tmw.sept22buyout.PlacementStatus.StatusType.IllegalSafe;
import static tmw.sept22buyout.PlacementStatus.StatusType.Join;
import static tmw.sept22buyout.PlacementStatus.StatusType.Merger;
import static tmw.sept22buyout.PlacementStatus.StatusType.NewChain;
import static tmw.sept22buyout.PlacementStatus.StatusType.SimplePlacement;

public class PlayGameAct extends DisplayLogic {

    private static final String TAG = PlayGameAct.class.getSimpleName();

    // I'd rather this wasn't an explicit singleton, but
    // for now it is.  Instance is set in onCreate()
    private static PlayGameAct Instance;
    public static PlayGameAct inst() { return Instance;  }

    // View items that get manipulated -- mostly different onClick
    // listeners get set -- as we move between different phases of play
    Button ContinueButton;
    Button EndGameButton;
    private LinearLayout     mainDisplay;      // created and built in onCreate()
    private ConstraintLayout playerTurnPanel;  // Hides the screen at start of a player's turn.
    private TextView         playerNameLabel;  // The text on the playerTurnPanel

    // These are used to pass values between different callbacks.
    // For example, between playing a token and then creating a
    // new chain at that location.
    private Token tempToken_newChain;
    private int temp_stockPurchases;


    //
    // Create the layout in the onCreate method.
    // Later, call refreshScreen as necessary to
    // update the state based on current data.
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlayGameAct.onCreate() has started");
        Instance = this;

        setContentView(R.layout.activity_play_game);
        mainDisplay = (LinearLayout) findViewById(R.id.MainDisplay);
        playerTurnPanel = (ConstraintLayout) findViewById(R.id.PlayerTurnPanel);
        playerNameLabel = (TextView) findViewById(R.id.PlayerNameLabel);

        // Create the display
        // A vertical stack of items.
        // Overall they will fill the parent space.
        // Individual items will have different weights to get
        // different amounts of space.
        LinearLayout.LayoutParams vlparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        vlparams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        vlparams.height = LinearLayout.LayoutParams.MATCH_PARENT;

        mainDisplay = new LinearLayout(this);
        mainDisplay.setOrientation(LinearLayout.VERTICAL);
        mainDisplay.setLayoutParams(vlparams);
        this.addContentView(mainDisplay, vlparams);

        // Get the common rows giving the callbacks for buttons.
        java.util.List<LinearLayout> rows = buildLayout(null, null);

        // Add the buttons on the final row.
        // A 'continue button and an 'end game' button
        // This row needs height as well.
        int last = rows.size() - 1;
        LinearLayout.LayoutParams btnparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        btnparams.width = 0;
        btnparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        btnparams.weight = 1;

        ContinueButton = new Button(this);
        ContinueButton.setText("Continue");
        ContinueButton.setLayoutParams(btnparams);
        ContinueButton.setOnClickListener(this::meaninglessClick);
        rows.get(last).addView(ContinueButton);

        EndGameButton = new Button(this);
        EndGameButton.setText("");
        EndGameButton.setLayoutParams(btnparams);
        EndGameButton.setOnClickListener(this::endGameClicked);
        rows.get(last).addView(EndGameButton);

        // Now add all of these horizontal layouts
        // to the overall vertical layout and refresh
        // the screen to show it all
        for (int lln = 0; (lln < rows.size()); lln++) {
            mainDisplay.addView(rows.get(lln));
        }

    }
    public void refreshScreen(Player player) {
        Board.instance().updateHighlights(player);
        AllPlayers.instance().updatePlayerData(player);
        AllChains.instance().updateLabels(player);

        if (BOGlobals.EndOfGameOption) EndGameButton.setText("End Game");
        else EndGameButton.setText("Show Log");
    } // end refreshScreen()

    public void refreshScreen(Token tokentohighlight) {
        Board.instance().chosen(tokentohighlight);
    }

    public void msgSet(String msg) {
        if (LblMessage1 != null) {
            LblMessage1.setText("");
            LblMessage3.setText(AllPlayers.instance().firstPlayer().getPlayerName()
                    + ": " + msg);
        }
    }

    public void msgSet(String errmsg, String msg) {
        if (LblMessage1 != null) {
            LblMessage1.setText(errmsg);
            LblMessage3.setText(AllPlayers.instance().firstPlayer().getPlayerName()
                    + ": " + msg);
        }
    }

    public void msgSet(Player player, String msg) {
        if (LblMessage1 != null) {
            LblMessage1.setText("");
            LblMessage3.setText(player.getPlayerName() + ": " + msg);
        }
    }

    public void log(String msg) { Log.d(TAG, msg); }



    @Override
    public void onResume() {
        super.onResume();
        gameLoop();
    }

    public void gameLoop() {
        // Start the game loop
        while (!BOGlobals.EndOfGameOption) {
            Board board = Board.instance();
            Player player = AllPlayers.instance().firstPlayer();
            log("Starting " + player.getPlayerName() + "'s turn.");

            // put up the privacy screen
            playerNameLabel.setText(player.getPlayerName() + "'s turn.");
            playerTurnPanel.setVisibility(View.VISIBLE);
            mainDisplay.setVisibility(View.INVISIBLE);

            refreshScreen(player);

            if (player.isMachine()) {
                log( "Player is a machine.");
                Token token = player.selectTokenToPlay();  // Machine player routine

                if (token != null) {
                    PlacementStatus status = token.evaluateForPlacement();
                    playToken(token, null);
                    if (   (status.getStatus() == IllegalSafe)
                        || (status.getStatus() == IllegalNoChain) ) {
                        msgSet(player.getPlayerName() + " tried to play an illegal token.");
                        //Oops, now what?
                    } else {
                        // put the token on the board
                        // and do whatever else we need to do
                        log("Playing token " + token.toString());
                        if (status.getStatus() == SimplePlacement) {
                            log( "Simple placement, add the token and buy stock");
                            board.addToken(token);
                            player.removeToken(token);
                            setForBuyStock();
                            List<Chain> buys = player.buyStock();  // Machine player routine
                            for (int i = 0; i < buys.size(); i++) {
                                buyStock((Chain) buys.get(i));
                            }
                            setForAfterBuyingStock();
                            // That's it, fall through to the end of the while

                        } else if (status.getStatus() == Join) {
                            log( "Adding to an existing chain, then buy stock.");
                            board.addToken(token);
                            player.removeToken(token);
                            Chain chain = status.getChain();
                            chain.fillIn();
                            chain.testEndGame();
                            setForBuyStock();
                            List<Chain> buys = player.buyStock(); // Machine player routine
                            for (int i = 0; i < buys.size(); i++) {
                                buyStock((Chain) buys.get(i));
                            }
                            setForAfterBuyingStock();
                            // That's it, fall through to the end of the while

                        } else if (status.getStatus() == NewChain) {
                            log( "Creating a new chain.);");
                            board.addToken(token);
                            player.removeToken(token);
                            tempToken_newChain = token;
                            setForCreateNewChain();
                            Chain chain = player.selectNewChain();  // Machine player routine
                            createNewChain(chain);
                            setForBuyStock();
                            List<Chain> buys = player.buyStock();  // Machine player routine
                            for (int i = 0; i < buys.size(); i++) {
                                buyStock((Chain) buys.get(i));
                            }
                            setForAfterBuyingStock();
                            // That's it, fall through to the end of the while

                        } else if (status.getStatus() == Merger) {
                            log( "Merging two chains.  Not really implemented.");
                            //beginSelectBuyingChain(tokentoplay, status.getBuyChains(),
                            //        status.getSellChains());

                        }
                    }
                }
            } else {
                // Human player
                //
                // Wait for interaction with the buttons to get
                // us done.
                // N.B. Control flow will _not_ come back
                // into this loop.  Instead, buttons will have
                // to be pressed which ultimately call the
                // nextTurn() method and then call this method
                // (gameLoop()) again is the game didn't end.
                return;
            }

            // We get here when a machine player is done with
            // its turn.
            //
            // Game end will break out of the loop.
            // If the game doesn't end, we do the simple housekeeping
            // (advance to the next player) and then we save the
            // game state so that if this app is stopped it can be
            // restored with data intact.  N.B.  state is only saved
            // at the end of the turn, so coming through onRestore
            // again will restart the turn.
            nextTurn();
        }
    }

    public void nextTurn() {
        Player player = AllPlayers.instance().firstPlayer();
        if (! player.fillTokens()) {
            gameEnd();
        }
        log("Ending " + player.getPlayerName() + "'s turn.");
        checkGameEnd();
        AllPlayers.instance().nextPlayer();
        saveGameState();
    }
    public void nextTurnClicked(View view) {
        // This was clicked because it was a human's turn.
        // So we were not in the game loop, but had returned
        // out of it.  We need to enter it again now in case
        // the next player is a machine.
        nextTurn();
        gameLoop();
    }

    public void checkGameEnd() {
        //  Should check something and if it is true, go to a different action
    }

    public void saveGameState() {
        //  Should save the state in case the app is backgrounded and killed
    }

    public void gameEnd() {
        // Now what?
    }


    @Override
    public void onBackPressed() {}

    public void meaninglessClick(View view) {
    }

    
    //
    //   This is a key point in the logic
    //  The turn has started, the main choice is which
    //  token to play.
    //  From here we go to one of 4 phases.
    //  * rejecting the selection as not allowed
    //  * accepting the play and starting to buy stock
    //  * accepting the play and creating a new chain
    //  * accepting the play and causing a merger.
    //
    public void setForPlayToken(View view) {
        playerTurnPanel.setVisibility(View.INVISIBLE);
        mainDisplay.setVisibility(View.VISIBLE);
        AllPlayers.instance().updateCallbacks(this::playTokenClicked);
        AllChains.instance().updateCallbacks(this::meaninglessClick );
        ContinueButton.setOnClickListener(this::meaninglessClick);
        msgSet("Please select a token to place on the board.");
    }

    public void playToken(Token token, @Nullable TokenButton btn) {
        Board board = Board.instance();
        Player player = AllPlayers.instance().firstPlayer();
        PlacementStatus status = token.evaluateForPlacement();
        log(token.getName() + ".evaluateForPlacement() returns " + status.getStatus());

        if (status.getStatus() == IllegalSafe) {
            PlayGameAct.inst().msgSet("You may not merge two safe chains.",
                    "Please choose another token.");
            if (btn != null) {
                btn.setText("");
                btn.setOnClickListener(this::meaninglessClick);
            }
        } else if (status.getStatus() == IllegalNoChain) {
            PlayGameAct.inst().msgSet("There are no more chains available to place on the board.",
                    "Please choose another token.");
            if (btn != null) {
                btn.setText("");
                btn.setOnClickListener(this::meaninglessClick);
            }
        } else if (status.getStatus() == SimplePlacement) {
            board.addToken(token);
            player.removeToken(token);
            setForBuyStock();
        } else if (status.getStatus() == Join) { // i.e. add to a chain
            Chain chain = status.getChain();
            board.setChain(token, chain);
            player.removeToken(token);
            chain.testEndGame();
            setForBuyStock();
        } // end status == Join
        else if (status.getStatus() == NewChain) {
            // We need to choose a chain to create
            board.addToken(token);
            player.removeToken(token);
            // We need to ask the user to choose a chain
            // (We could have a sepecial case where there is only 1
            //  chain, no choice, but simpler for now to always
            //  have the same control flow).
            log("Entering Player.afterTokenSelection()/UserPicksChain");
            tempToken_newChain = token;
            setForCreateNewChain();
        } // end if status == newchain
        else if (status.getStatus() == Merger) {
            //beginSelectBuyingChain(tokentoplay, status.getBuyChains(),
            //        status.getSellChains());
        } // end if status == merger
    }

    public void playTokenClicked(View view) {
        TokenButton btn = (TokenButton) view;
        playToken(btn.getToken(), btn);
    }


    //
    //  Buying Stock
    //  The player token buttons are meaningless
    //  The chain labels are valid buttons for buying
    //  You can buy at most 3 shares.
    //  The phase ends when you click the Continue button.
    //  The next phase is moving on to the next player's turn.
    //
    public void setForBuyStock() {
        temp_stockPurchases = 0;
        AllPlayers.instance().updateCallbacks(this::meaninglessClick);
        AllChains.instance().updateCallbacks(this::buyStockClick );
        ContinueButton.setOnClickListener(this::nextTurnClicked);
        msgSet("Click on a chain to buy stock or 'Continue' to end your turn.");
    }

    public void setForAfterBuyingStock() {
        AllPlayers.instance().updateCallbacks(this::meaninglessClick);
        AllChains.instance().updateCallbacks(this::meaninglessClick);
        ContinueButton.setOnClickListener(this::nextTurnClicked);
        // set the continue button
        msgSet("Click to end your turn.");
    }

    public void buyStock(Chain chain) {
        if (!chain.isOnBoard()) {
            msgSet("Sorry.  That chain is not on the board.",
                    "Please choose a different chain, or click 'Continue'.");
        } else {
            // See if we can afford it
            Player player = AllPlayers.instance().firstPlayer();
            if (!player.canAfford(chain)) { // He cannot afford it
                PlayGameAct.inst().msgSet("Sorry.  You cannot afford that issue.",
                        "Please choose a different chain, or click 'Continue'.");
            }

            if (player.takeStock(chain, 1)) {
                // We have successfully purchased the share
                refreshScreen(player);
            } else {
                PlayGameAct.inst().msgSet("Sorry.  There are no more shares of that stock available.",
                        "Please choose a different chain, or click 'Continue'.");
            }

            temp_stockPurchases += 1;
            if (temp_stockPurchases == 3) {
                // can't buy any more
                setForAfterBuyingStock();
            }
        }
    }

    public void buyStockClick(View view) {
        ChainButton btn = (ChainButton) view;
        Chain chain = btn.getChain();
        buyStock(chain);
    }


    //
    //  Create a new chain
    //  The player token buttons are meaningless
    //  The chain labels are valid buttons for selecting
    //  The continue button is meaningless
    //  The phase ends when you have selected a valid chain
    //  The next phase is buying stock.
    //
    public void setForCreateNewChain() {
        AllPlayers.instance().updateCallbacks(this::meaninglessClick);
        AllChains.instance().updateCallbacks(this::createNewChainClick);
        msgSet("Please select the chain you wish to create.");
    }

    public void createNewChain(Chain chain) {
        if (chain.isOnBoard()) {
            msgSet("Sorry.  That chain is on the board already.",
                    "Please choose a different chain.");
        } else {
            Board board = Board.instance();
            BoardSpace space = board.getSpace(tempToken_newChain);
            chain.moveToBoard(space);
            Player player = AllPlayers.instance().firstPlayer();
            if (chain.getAvailableStock() > 0) {
                player.takeStock(chain, 1);
            }
            refreshScreen(player);
            // Chain has been created, so move to buying stock
            setForBuyStock();
        }
    }

    public void createNewChainClick(View view) {
        ChainButton btn = (ChainButton) view;
        createNewChain(btn.getChain());
    }






    public void endGameClicked(View view) {
        if (BOGlobals.EndOfGameOption) {
            startEndGame();
        }
        else {
            Intent intent = new Intent(this, DisplayLogAct.class);
            startActivity(intent);
        }
    }


    public void startNewPlayerSell() {
        Intent intent = new Intent(this, NewPlayerSellAct.class);
        startActivity(intent);
    }

    public void startEndGame() {
        Intent intent = new Intent(this, EndGameAct.class);
        startActivity(intent);
    }



} // end chain PlayGameAct
