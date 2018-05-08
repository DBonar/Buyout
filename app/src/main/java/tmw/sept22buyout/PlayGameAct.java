package tmw.sept22buyout;

import android.content.Intent;
import android.graphics.Color;
import android.renderscript.Byte2;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class PlayGameAct extends DisplayLogic {

    private static final String TAG = PlayGameAct.class.getSimpleName();
    private static PlayGameAct Instance;

    Button BtnEndGame;
    private LinearLayout     mainDisplay;  // created and built in onCreate()
    private ConstraintLayout playerTurnPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlayGameAct.onCreate() has started");
        Instance = this;

        setContentView(R.layout.activity_play_game);
        mainDisplay = (LinearLayout) findViewById(R.id.MainDisplay);
        playerTurnPanel = (ConstraintLayout) findViewById(R.id.PlayerTurnPanel);

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
        java.util.List<LinearLayout> rows = buildLayout(
                (View btn) -> { tokenBtnCB(btn); },
                (View btn) -> { chainBtnCB(btn); } );

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

        Button continuebtn = new Button(this);
        continuebtn.setText("Continue");
        continuebtn.setLayoutParams(btnparams);
        View vcontinue = (View) continuebtn;
        vcontinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                continueBtnCB(btn);
            }
        });
        rows.get(last).addView(continuebtn);

        BtnEndGame = new Button(this);
        BtnEndGame.setText("");
        BtnEndGame.setLayoutParams(btnparams);
        View vendgame = (View) BtnEndGame;
        vendgame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                endGameBtnCB(btn);
            }
        });
        rows.get(last).addView(BtnEndGame);

        // Now add all of these horizontal layouts
        // to the overall vertical layout and refresh
        // the screen to show it all
        for (int lln = 0; (lln < rows.size()); lln++) {
            mainDisplay.addView(rows.get(lln));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        gameLoop();
    }

    public void gameLoop() {
        // Start the game loop
        while (!BOGlobals.EndOfGameOption) {
            // put up the privacy screen
            playerTurnPanel.setVisibility(View.VISIBLE);
            mainDisplay.setVisibility(View.INVISIBLE);

            Player player = AllPlayers.instance().firstPlayer();
            refreshScreen(player);

            if (player.isMachine()) {
                player.beginTokenSelection();
            } else {
                // Wait for interaction with the buttons to get
                // us done.  N.B. Control flow will not come back
                // into this loop.  Instead, buttons will have
                // to be pressed which ultimately call the
                // checkGameEnd(), nextPlayer(), saveGameState()
                // triple and then call this function again.
                return;
            }

            // Game end will break out of the loop.
            // If the game doesn't end, we do the simple housekeeping
            // (advance to the next player) and then we save the
            // game state so that if this app is stopped it can be
            // restored with data intact.  N.B.  state is only saved
            // at the end of the turn, so coming through onRestore
            // again will restart the turn.
            checkGameEnd();
            AllPlayers.instance().nextPlayer();
            saveGameState();
        }
    }

    public void checkGameEnd() {
        //  Should check something and if it is true, go to a different action
    }

    public void saveGameState() {
        //  Should save the state in case the app is backgrounded and killed
    }

    public void startTurnButtonClicked(View view) {
        playerTurnPanel.setVisibility(View.INVISIBLE);
        mainDisplay.setVisibility(View.VISIBLE);
        msgSet("Please select a token to place on the board.");
    }

//        AllPlayers allplayers = AllPlayers.instance();
//        for (BOGlobals.CurrentPlayer =
//                     ((BOGlobals.CurrentPlayer == null) ?
//                             allplayers.firstPlayer() :
//                             BOGlobals.CurrentPlayer.nextPlayer());
//             (!BOGlobals.EndOfGameOption);
//             BOGlobals.CurrentPlayer = BOGlobals.CurrentPlayer.nextPlayer()) {
//
//            Player player = BOGlobals.CurrentPlayer;
//            refreshScreen();
//
//            if (!player.isMachine()) {
//                // A human player.
//                // They will hit the button to lower the privacy
//                // screen and then interact with the rest of the controls.
//            } else {
//                // the player is a machine.  To avoid indefinite recursion, we make the
//                // move and return here.
//                player.beginTokenSelection();
//                if (WhereAmIStack.inst().look() != null) {
//                    // This player's turn is not over, but control has been handed to
//                    // another player (presumably MergerSellAct.)  So we allow this
//                    // thread to die.
//                    return;
//                }
//                // Otherwise, we continue on with the next player.
//                if (BOGlobals.EndOfGameOption) {
//                    Intent intent = new Intent(this, EndGameAct.class);
//                    startActivity(intent);
//                    return;
//                }
//            }
//        }
//    }

//        WhereAmIStack stack = WhereAmIStack.inst();
//        WhereAmI wai = stack.look();
//        if (BOGlobals.CurrentPlayer.isMachine())
//            msgSet("This is the Machine's turn.  Please click 'Continue'");
//        if (wai == null) BOGlobals.CurrentPlayer.beginTokenSelection();
//        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.PlayToken)
//            msgSet("Please select a token");
//        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.SelectNewChain)
//            msgSet("Please select the chain you wish to create.");
//        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.SelectBuyingChain)
//            msgSet("Which chain is the buying chain?");
//        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.UnloadStock)
//            BOGlobals.CurrentPlayer.afterUnloadStock();
//        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.BuyStock)
//            msgSet("You may buy stock");
//        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.TakeTile)
//            msgSet("Please click 'Continue' to end your turn");
//        else {
//            Log.d(TAG, "Unexpected Play Phase!  Attempting to continue.");
//            BOGlobals.CurrentPlayer.beginTokenSelection();
//        }
//    } // end onCreate()

//    protected void initializeGame() {
//        AllPlayers allplayers = AllPlayers.instance(Intro2Act.NPlayers, Intro2Act.NMachines);
//        Board.instance();
//        AllChains.instance();
//        AllTokens.instance();
//        BOGlobals.CurrentPlayer = allplayers.firstPlayer();
//    }

    public static PlayGameAct inst() {
        return Instance;
    }

    public void refreshScreen(Player player) {
        Log.d(TAG, "PlayGameAct.refreshScreen() has started.");

        board.updateView();

        Token onetoken;
        ListIterator<Token> ptokens =
                new ListIterator<Token>(player.getTokens());
        for (int tn = 0; (tn < AllTokens.instance().NTokensPerPlayer); tn++) {
            onetoken = ptokens.getNext();
            if (onetoken == null) break;
            TokenButton tbutton = BtnScnTokens[tn];
            tbutton.setToken(onetoken);
            tbutton.setText(onetoken.getName());
            board.highlight( onetoken );
        }
        LblCash.setText("$" + player.getMoney());

        for (int cn = 0; (cn < BtnScnChains.length); cn++) {
            ChainButton btnonechain = BtnScnChains[cn];
            Chain onechain = btnonechain.getChain();
            TextView lblonechain = LblScnChains[cn];
            lblonechain.setText(onechain.toFullString(player));
        }
        if (BOGlobals.EndOfGameOption) BtnEndGame.setText("End Game");
        else BtnEndGame.setText("Show Log");
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

    public void tokenBtnCB(View view) {
        // Ignore token click if we are not asking for a token.
        WhereAmI wai = WhereAmIStack.inst().look();
        if ((wai == null) ||
                (wai.getPlayPhase() != WhereAmI.PlayPhase.PlayToken)) return;
        TokenButton btn = (TokenButton) view;
        Token token = btn.getToken();
        boolean success = AllPlayers.instance().firstPlayer().afterTokenSelection(token);
        if (! success) {
            btn.setText("");
        }
    } // end tokenBtnCB()

    public void chainBtnCB(View view) {
        WhereAmI.PlayPhase phase = WhereAmIStack.inst().look().getPlayPhase();
        if (phase == WhereAmI.PlayPhase.SelectNewChain) {
            ChainButton btn = (ChainButton) view;
            Chain chain = btn.getChain();
            boolean success = AllPlayers.instance().firstPlayer().afterSelectNewChain(chain);
        }
        else if (phase == WhereAmI.PlayPhase.SelectBuyingChain) {
            ChainButton btn = (ChainButton) view;
            Chain chain = btn.getChain();
            boolean success = AllPlayers.instance().firstPlayer().afterSelectBuyingChain(chain);
            if (!success)
                msgSet("That chain is not one of the chains being merged.",
                        "Please select the buying chain.");
        }
        else if (phase == WhereAmI.PlayPhase.BuyStock) {
            ChainButton btn = (ChainButton) view;
            Chain chain = btn.getChain();
            boolean success = AllPlayers.instance().firstPlayer().afterBuyStock(chain);
        }
    } // end chainBtnCB()

    public void continueBtnCB(View view) {
        WhereAmI wai = WhereAmIStack.inst().look();
        if (wai == null || AllPlayers.instance().firstPlayer().isMachine()) {
            gameLoop();
            return;
        }
        WhereAmI.PlayPhase phase = wai.getPlayPhase();
        if (phase == WhereAmI.PlayPhase.BuyStock) {
            AllPlayers.instance().firstPlayer().afterBuyStock(null);
        }
        else if (phase == WhereAmI.PlayPhase.TakeTile) {
            AllPlayers.instance().firstPlayer().afterTakeTile();
        }
    }

    public void endGameBtnCB(View view) {
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

    @Override
    public void onBackPressed() {
    }

} // end chain PlayGameAct
