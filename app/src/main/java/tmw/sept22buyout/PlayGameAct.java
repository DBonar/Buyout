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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static tmw.sept22buyout.PlacementStatus.StatusType.IllegalNoChain;
import static tmw.sept22buyout.PlacementStatus.StatusType.IllegalSafe;
import static tmw.sept22buyout.PlacementStatus.StatusType.Join;
import static tmw.sept22buyout.PlacementStatus.StatusType.Merger;
import static tmw.sept22buyout.PlacementStatus.StatusType.NewChain;
import static tmw.sept22buyout.PlacementStatus.StatusType.SimplePlacement;

public class PlayGameAct extends AppCompatActivity {

    private static final String TAG = PlayGameAct.class.getSimpleName();

    // I'd rather this wasn't an explicit singleton, but
    // for now it is.  Instance is set in onCreate()
    // This seems to be used for logging messages.
    private static PlayGameAct Instance;
    public static PlayGameAct inst() { return Instance;  }

    // View items that get manipulated -- mostly different onClick
    // listeners get set -- as we move between different phases of play
    private Button           ContinueButton;
    private Button           EndGameButton;
    private LinearLayout     mainDisplay;      // created and built in onCreate()
    private ConstraintLayout playerTurnPanel;  // Hides the screen at start of a player's turn.
    private TextView         playerNameLabel;  // The text on the playerTurnPanel
    private TextView         LblMessage;       // Used for instructional messages

    // These are used to pass values between different callbacks.
    // between playing a token and a new chain at that location
    private Token tempToken_newChain;
    // between the rounds of stock purchases
    private int temp_stockPurchases;
    // between the steps involved in handling mergers
    private List<Chain> temp_Potentials;
    private List<Chain> temp_Survivor;
    private List<Chain> temp_Victim;
    private Player temp_mergePlayer;


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

        // Create the display, a vertical stack of items.
        // 1 for the board
        // 1 for player's tiles and cash
        // 1 for the chains
        // 1 for the message
        // and a final row (details left to the caller)
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

        // Create the board
        Board board = Board.initialize(9, 12, this);

        // The three main areas, each drawn by the associated class.
        mainDisplay.addView( board.buildLayout(this) );
        mainDisplay.addView( AllPlayers.instance().buildLayout(this, null) );
        mainDisplay.addView( AllChains.instance().buildLayout(this, null) );

        // The space for displaying messages / instructions
        {
            LinearLayout.LayoutParams spacer_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            spacer_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            spacer_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            spacer_params.weight = 2;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(spacer_params);
            LblMessage = new TextView(this);
            LblMessage.setText("Please click the token you wish to place.");
            row.addView(LblMessage);

            mainDisplay.addView(row);
        }

        // And space for the last row, a 'continue' button and an 'end game' button
        {
            LinearLayout.LayoutParams bottom_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            bottom_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            bottom_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            bottom_params.weight = 1;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(bottom_params);

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
            ContinueButton.setMinHeight(1);
            ContinueButton.setMinimumHeight(1);
            row.addView(ContinueButton);

            EndGameButton = new Button(this);
            EndGameButton.setText("");
            EndGameButton.setLayoutParams(btnparams);
            EndGameButton.setOnClickListener(this::endGameClicked);
            EndGameButton.setMinHeight(1);
            EndGameButton.setMinimumHeight(1);
            row.addView(EndGameButton);

            mainDisplay.addView(row);
        }

    }

    public void refreshScreen(Player player) {
        Board.instance().updateHighlights(player);
        AllPlayers.instance().updatePlayerData(player);
        AllChains.instance().updateLabels(player);

        if (BOGlobals.EndOfGameOption) EndGameButton.setText("End Game");
        else EndGameButton.setText("Show Log");
    } // end refreshScreen()


    public void msgSet(String msg) {
        LblMessage.setText(AllPlayers.instance().firstPlayer().getPlayerName()
                    + ": " + msg);
    }

    public void msgSet(String errmsg, String msg) {
        LblMessage.setText(AllPlayers.instance().firstPlayer().getPlayerName()
                    + ": " + msg);
    }

    public void msgSet(Player player, String msg) {
        LblMessage.setText(player.getPlayerName() + ": " + msg);
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
                    //playToken(token);
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
                            player.removeToken(token);
                            board.playToken(token);
                            setForBuyStock();
                            List<Chain> buys = player.buyStock();  // Machine player routine
                            for (int i = 0; i < buys.size(); i++) {
                                buyStock((Chain) buys.get(i));
                            }
                            setForAfterBuyingStock();
                            // That's it, fall through to the end of the while

                        } else if (status.getStatus() == Join) {
                            log( "Adding to an existing chain, then buy stock.");
                            player.removeToken(token);
                            Chain chain = status.getChain();
                            board.addToChain(token, chain);
                            chain.testEndGame();
                            setForBuyStock();
                            List<Chain> buys = player.buyStock(); // Machine player routine
                            for (int i = 0; i < buys.size(); i++) {
                                log( "Buying: " + buys.get(i).toString());
                                buyStock((Chain) buys.get(i));
                            }
                            setForAfterBuyingStock();
                            // That's it, fall through to the end of the while

                        } else if (status.getStatus() == NewChain) {
                            log( "Creating a new chain.");
                            player.removeToken(token);
                            board.playToken(token);
                            tempToken_newChain = token;
                            setForCreateNewChain();
                            Chain chain = player.selectNewChain();  // Machine player routine
                            log( "Creating chain " + chain.toString());
                            createNewChain(chain);
                            setForBuyStock();
                            List<Chain> buys = player.buyStock();  // Machine player routine
                            for (int i = 0; i < buys.size(); i++) {
                                log( "Buying: " + buys.get(i).toString());
                                buyStock((Chain) buys.get(i));
                            }
                            setForAfterBuyingStock();
                            // That's it, fall through to the end of the while

                        } else if (status.getStatus() == Merger) {
                            log ("Merging chains.");
                            player.removeToken(token);
                            board.playToken(token);
                            List<BoardSpace> neighbors = board.allNeighbors(token);
                            List<Chain> potentials = new ArrayList<Chain>();
                            for (int i = 0; i < neighbors.size(); i++) {
                                Chain temp = neighbors.get(i).getChain();
                                if (   (temp != null)
                                        && ! potentials.contains(temp) ) {
                                    potentials.add(temp);
                                }
                            }
                            temp_Potentials = potentials;
                            log( "    " + temp_Potentials.size() + " merging chains.");
                            setForSelectMergeSurvivor();
                            log( "    " + temp_Survivor.size() + " possible survivors.");
                            if (temp_Survivor.size() > 1) {
                                Chain chain = player.selectSurvivor(temp_Potentials);
                                List<Chain> temp = new ArrayList<Chain>();
                                temp.add( chain );
                                temp_Potentials.remove( chain );
                                temp_Survivor = temp;
                            }
                            log( "    " + temp_Survivor.get(0).toString() + " is the survivor.");
                            while (temp_Potentials.size() > 0) {
                                // select the next victim
                                int largest = 0;
                                for (int i = 0; i < temp_Potentials.size(); i++) {
                                    if (temp_Potentials.get(i).getBoardCount() > largest)
                                        largest = temp_Potentials.get(i).getBoardCount();
                                }
                                List<Chain> temp = new ArrayList<Chain>();
                                for (int i = 0; i < temp_Potentials.size(); i++) {
                                    if (temp_Potentials.get(i).getBoardCount() == largest)
                                        temp.add( temp_Potentials.get(i) );
                                }
                                if (temp.size() > 1) {
                                    Chain chain = player.selectVictim(temp);
                                    List<Chain> temp2 = new ArrayList<Chain>();
                                    temp2.add( chain );
                                    temp_Potentials.remove( chain );
                                    temp_Victim = temp2;
                                } else {
                                    temp_Potentials.remove( temp.get(0) );
                                    temp_Victim = temp;
                                }
                                log( "    " + temp_Victim.get(0).toString() + " is first victim.");
                                // do the merge of temp_Victim into temp_Survivor
                                temp_mergePlayer = player;
                                merge();
                            }
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
        AllPlayers.instance().advanceToNextPlayer();
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

    private void playToken(Token token) {
        Board board = Board.instance();
        Player player = AllPlayers.instance().firstPlayer();
        PlacementStatus status = token.evaluateForPlacement();
        log(token.getName() + ".evaluateForPlacement() returns " + status.getStatus());

        if (status.getStatus() == IllegalSafe) {
            PlayGameAct.inst().msgSet("You may not merge two safe chains.",
                    "Please choose another token.");
            token.setText("");
            token.setOnClickListener(this::meaninglessClick);

        } else if (status.getStatus() == IllegalNoChain) {
            PlayGameAct.inst().msgSet("There are no more chains available to place on the board.",
                    "Please choose another token.");
            token.setText("");
            token.setOnClickListener(this::meaninglessClick);

        } else if (status.getStatus() == SimplePlacement) {
            player.removeToken(token);
            board.playToken(token);
            setForBuyStock();

        } else if (status.getStatus() == Join) { // i.e. add to a chain
            player.removeToken(token);
            Chain chain = status.getChain();
            board.addToChain(token, chain);
            chain.testEndGame();
            refreshScreen(player);
            setForBuyStock();

        } // end status == Join
        else if (status.getStatus() == NewChain) {
            // We need to choose a chain to create
            player.removeToken(token);
            board.playToken(token);
            // We need to ask the user to choose a chain
            // (We could have a sepecial case where there is only 1
            //  chain, no choice, but simpler for now to always
            //  have the same control flow).
            log("Entering Player.afterTokenSelection()/UserPicksChain");
            tempToken_newChain = token;
            setForCreateNewChain();

        } // end if status == newchain
        else if (status.getStatus() == Merger) {
            log ("Merging chains.");
            player.removeToken(token);
            board.playToken(token);
            List<BoardSpace> neighbors = board.allNeighbors(token);
            List<Chain> potentials = new ArrayList<Chain>();
            for (int i = 0; i < neighbors.size(); i++) {
                Chain temp = neighbors.get(i).getChain();
                if (   (temp != null)
                    && ! potentials.contains(temp) ) {
                    potentials.add(temp);
                }
            }
            temp_Potentials = potentials;
            setForSelectMergeSurvivor();

        } // end if status == merger
    }

    public void playTokenClicked(View view) {
        Token token = (Token) view;
        playToken(token);
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

            if (player.purchaseStock(chain, 1)) {
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
        ContinueButton.setOnClickListener(this::meaninglessClick);
        msgSet("Please select the chain you wish to create.");
    }

    public void createNewChain(Chain chain) {
        if (chain.isOnBoard()) {
            msgSet("Sorry.  That chain is on the board already.",
                    "Please choose a different chain.");
        } else {
            Board board = Board.instance();
            chain.moveToBoard(tempToken_newChain);
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

    //
    // First, optional, state.  End with temp_Survivor having one entry.
    // Could chain immediately into setForSelectVictim or setForMerger
    // if we have no ambiguity.
    public void setForSelectMergeSurvivor() {
        int largest = 0;
        for (int i = 0; i < temp_Potentials.size(); i++) {
            if (temp_Potentials.get(i).getBoardCount() > largest) {
                largest = temp_Potentials.get(i).getBoardCount();
            }
        }
        List<Chain> large = new ArrayList<Chain>();
        for (int i = 0; i < temp_Potentials.size(); i++) {
            if (temp_Potentials.get(i).getBoardCount() == largest) {
                large.add( temp_Potentials.get(i) );
            }
        }
        if (large.size() == 1) {
            temp_Survivor = large;
            temp_Potentials.remove(temp_Survivor.get(0));
            setForSelectMergeVictim();
        } else {
            // set up to select which one survives
            AllPlayers.instance().updateCallbacks(this::meaninglessClick);
            AllChains.instance().updateCallbacks(this::selectSurvivorClick);
            ContinueButton.setOnClickListener(this::meaninglessClick);
            temp_Survivor = temp_Potentials;
            String msg = "Please select which chain survives. ";
            for (int i = 0; i < temp_Potentials.size(); i++) {
                msg += " " + temp_Potentials.get(i).getName();
                if (i < temp_Potentials.size() - 1) {
                    msg += ",";
                } else {
                    msg += " or";
                }
            }
            msgSet(msg);
        }
    }

    public void selectSurvivorClick(View view) {
        ChainButton cbtn = (ChainButton) view;
        Chain chain = cbtn.getChain();
        if (temp_Survivor.contains(chain)) {
            List<Chain> temp = new ArrayList<Chain>();
            temp.add(chain);
            temp_Survivor = temp;
            temp_Potentials.remove(chain);
            setForSelectMergeVictim();
        } else {
            return; // a no-op
        }
    }


    //
    // Second, optional, state.  End with temp_Victom having one entry.
    // Could chain immediately into setForMerger
    public void setForSelectMergeVictim() {
        int largest = 0;
        for (int i = 0; i < temp_Potentials.size(); i++) {
            if (temp_Potentials.get(i).getBoardCount() > largest) {
                largest = temp_Potentials.get(i).getBoardCount();
            }
        }
        List<Chain> large = new ArrayList<Chain>();
        for (int i = 0; i < temp_Potentials.size(); i++) {
            if (temp_Potentials.get(i).getBoardCount() == largest) {
                large.add( temp_Potentials.get(i) );
            }
        }
        if (large.size() == 1) {
            temp_Victim = large;
            temp_Potentials.remove(temp_Victim.get(0));
            temp_mergePlayer = AllPlayers.instance().firstPlayer();
            setForMerge();
            merge();
        } else {
            // set up to select which one survives
            AllPlayers.instance().updateCallbacks(this::meaninglessClick);
            AllChains.instance().updateCallbacks(this::selectVictimClick);
            ContinueButton.setOnClickListener(this::meaninglessClick);
            temp_Victim = temp_Potentials;
            String msg = "Please select which chain merges first. ";
            for (int i = 0; i < temp_Potentials.size(); i++) {
                msg += " " + temp_Potentials.get(i).getName();
                if (i < temp_Potentials.size() - 1) {
                    msg += ",";
                } else {
                    msg += " or";
                }
            }
            msgSet(msg);
        }
    }

    public void selectVictimClick(View view) {
        ChainButton cbtn = (ChainButton) view;
        Chain chain = cbtn.getChain();
        if (temp_Victim.contains(chain)) {
            List<Chain> temp = new ArrayList<Chain>();
            temp.add(chain);
            temp_Victim = temp;
            temp_Potentials.remove(chain);
            temp_mergePlayer = AllPlayers.instance().firstPlayer();
            while (temp_mergePlayer.getChainNShares(chain) == 0) {
                // We know at least one player has a share of the chain.
                temp_mergePlayer = AllPlayers.instance().nextPlayer(temp_mergePlayer);
            }
            setForMerge();
            merge();
        } else {
            return; // a no-op
        }
    }


    //
    // Now do the merge
    // Each player in turn merges temp_Victim into temp_Survivor
    public void setForMerge() {
        AllPlayers.instance().updateCallbacks(this::meaninglessClick);
        AllChains.instance().updateCallbacks(this::mergeClick);
        ContinueButton.setOnClickListener(this::meaninglessClick);
        msgSet("Click on " + temp_Victim.get(0).getName() + " to sell a share.\n" +
              "Click on " + temp_Survivor.get(0).getName() + " aquire 1 share.\n" +
              "Click 'Continue' to keep the rest of your shares.");
    }

    public void mergeClick(View view) {
        ChainButton cbtn = (ChainButton) view;
        Chain chain = cbtn.getChain();
        Chain victim = temp_Victim.get(0);
        Chain survivor = temp_Survivor.get(0);

        if (chain == victim) {
            // Sell 1 share of the victim for cash
            temp_mergePlayer.purchaseStock(victim, -1);
            if (   temp_mergePlayer.getChainNShares(victim) == 0) {
                ContinueButton.setOnClickListener(this::endMergeClick);
            }
        } else if (  (chain == survivor)
                   || (temp_mergePlayer.getChainNShares(victim) > 1)
                   || (survivor.getAvailableStock() > 0) ) {
            // Change 2 shares of the victim for 1 of the victor
            temp_mergePlayer.takeStock(survivor, 1 );
            temp_mergePlayer.takeStock(victim, -2);
            if (   temp_mergePlayer.getChainNShares(victim) == 0) {
                ContinueButton.setOnClickListener(this::endMergeClick);
            }
        } else {
            // no-op, ignore it.

        }
        refreshScreen(temp_mergePlayer);
    }

    public void merge() {
        playerNameLabel.setText(temp_mergePlayer.getPlayerName() + "'s merge turn.");
        if (temp_mergePlayer.isMachine()) {
            playerTurnPanel.setVisibility(View.VISIBLE);
            mainDisplay.setVisibility(View.INVISIBLE);

            // ask the player how many shares to
            // (sell, trade, keep)
            Chain victim = temp_Victim.get(0);
            Chain survivor = temp_Survivor.get(0);
            List<Integer> actions = temp_mergePlayer.mergeActions(victim,
                                                                  survivor);
            if (   (actions.size() != 3)
                || (actions.get(0) + actions.get(1) + actions.get(2)
                        != temp_mergePlayer.getChainNShares(victim) )
                || (actions.get(0) < 0 || actions.get(1) < 0 || actions.get(2) < 0)
                || (actions.get(1) % 2 != 0) ) {
                throw new RuntimeException("Error in machine player merge actions.");
            }
            log( "    sell: " + actions.get(0) +
                         "  trade: " + actions.get(1) +
                         "  keep: " + actions.get(2) );
            temp_mergePlayer.purchaseStock(victim, - actions.get(0));
            temp_mergePlayer.takeStock(survivor, actions.get(1) / 2);
            temp_mergePlayer.takeStock(victim, - actions.get(1));

            endMergeClick(null); // The view doesn't matter

        } else {
            playerNameLabel.setText(temp_mergePlayer.getPlayerName() + "'s turn.");
            playerTurnPanel.setVisibility(View.INVISIBLE);
            mainDisplay.setVisibility(View.VISIBLE);
            // now exit this and wait for the click callbacks
        }
    }

    public void endMergeClick(View view) {
        // Go to the next player or on to the next turn.
        temp_mergePlayer = AllPlayers.instance().nextPlayer(temp_mergePlayer);
        while (  temp_mergePlayer.getChainNShares(temp_Victim.get(0)) == 0
               && temp_mergePlayer != AllPlayers.instance().firstPlayer() ) {
            temp_mergePlayer = AllPlayers.instance().nextPlayer(temp_mergePlayer);
        }
        if (temp_mergePlayer == AllPlayers.instance().firstPlayer()) {
            // Change the settings of the board and we're done
            nextTurnClicked(view);
        } else {
            // Back to the merge logic
            merge();
        }
    }



    //
    //  Left over bits
    //

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
    }

    public void startEndGame() {
        Intent intent = new Intent(this, EndGameAct.class);
        startActivity(intent);
    }



} // end chain PlayGameAct
