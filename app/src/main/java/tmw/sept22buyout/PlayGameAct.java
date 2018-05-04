package tmw.sept22buyout;

import android.content.Intent;
import android.graphics.Color;
import android.renderscript.Byte2;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class PlayGameAct extends DisplayLogic {

    private static PlayGameAct Instance = null;
    private static final String TAG = PlayGameAct.class.getSimpleName();

    Button BtnEndGame;
    private LinearLayout layout;  // created and built in onCreate()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlayGameAct.onCreate() has started");
        Log.d(TAG, "Player = " + BOGlobals.CurrentPlayer.getPlayerName());
        setContentView(R.layout.activity_play_game);

        Instance = this;

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

        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(vlparams);
        this.addContentView(layout, vlparams);

        // Get the common rows giving the callbacks for buttons.
        java.util.List<LinearLayout> rows = buildLayout(
                    (View btn) -> { tokenBtnCB(btn); },
                    (View btn) -> { chainBtnCB(btn); }   );

        // Add the buttons on the final row.
        // A 'continue button and an 'end game' button
        // This row needs height as well.
        int last = rows.size() -1;
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
            layout.addView(rows.get(lln));
        }

        refreshScreen();
        WhereAmIStack stack = WhereAmIStack.inst();
        WhereAmI wai = stack.look();
        if (BOGlobals.CurrentPlayer.isMachine())
            msgSet("This is the Machine's turn.  Please click 'Continue'");
        if (wai == null) BOGlobals.CurrentPlayer.beginTokenSelection();
        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.PlayToken)
            msgSet("Please select a token");
        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.SelectNewChain)
            msgSet("Please select the chain you wish to create.");
        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.SelectBuyingChain)
            msgSet("Which chain is the buying chain?");
        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.UnloadStock)
            BOGlobals.CurrentPlayer.afterUnloadStock();
        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.BuyStock)
            msgSet("You may buy stock");
        else if (wai.getPlayPhase() == WhereAmI.PlayPhase.TakeTile)
            msgSet("Please click 'Continue' to end your turn");
        else {
            Log.d(TAG, "Unexpected Play Phase!  Attempting to continue.");
            BOGlobals.CurrentPlayer.beginTokenSelection();
        }
    } // end onCreate()

//    protected void initializeGame() {
//        AllPlayers allplayers = AllPlayers.instance(Intro2Act.NPlayers, Intro2Act.NMachines);
//        Board.instance();
//        AllChains.instance();
//        AllTokens.instance();
//        BOGlobals.CurrentPlayer = allplayers.firstPlayer();
//    }

    public static PlayGameAct inst() {
        if (Instance == null) Instance = new PlayGameAct();
        return Instance;
    }

    public void refreshScreen() {

//        // Some test code for the next LList.copy() fn
//        LList<String> one = new LList<String>("a", "b", "c", "d");
//        LList<String> two = new LList<String>();
//        two.copy(one);
//        LblMessage2.setText("Copy() test " + two.takeFirst() + two.takeFirst());

        Log.d(TAG, "PlayGameAct.refreshScreen() has started.");
        Player thisplayer = BOGlobals.CurrentPlayer;
//        Board board = Board.instance();
//        for (int rown = 0; (rown < Board.BoardYSize); rown++) {
//            for (int coln = 0; (coln < Board.BoardXSize); coln++) {
//                BoardSpace space = board.getSpace(coln, rown);
//                TextView view = space.getDisplay();
//                if (space.getChain() != null)
//                    view.setBackgroundColor(space.getChain().getChainColor());
//                else if (space.isOccupied())
//                    view.setBackgroundColor(BOGlobals.ClrFullSpace);
//                else view.setBackgroundColor(BOGlobals.ClrEmptySpace);
//            }
//        }

        BoardComponent board = (BoardComponent) layout.getChildAt(0);
        Token onetoken;
        ListIterator<Token> ptokens =
                new ListIterator<Token>(thisplayer.getTokens());
        for (int tn = 0; (tn < AllTokens.instance().NTokensPerPlayer); tn++) {
            onetoken = ptokens.getNext();
            if (onetoken == null) break;
            TokenButton tbutton = BtnScnTokens[tn];
            tbutton.setToken(onetoken);
            tbutton.setText(onetoken.getName());

            board.highlight( onetoken );
        }
        LblCash.setText("$" + thisplayer.getMoney());

        for (int cn = 0; (cn < BtnScnChains.length); cn++) {
            ChainButton btnonechain = BtnScnChains[cn];
            Chain onechain = btnonechain.getChain();
            TextView lblonechain = LblScnChains[cn];
            lblonechain.setText(onechain.toFullString(thisplayer));
        }
        if (BOGlobals.EndOfGameOption) BtnEndGame.setText("End Game");
        else BtnEndGame.setText("Show Log");
    } // end refreshScreen()

    public void refreshScreen(Token tokentohighlight) {
        refreshScreen();
        BoardSpace space = Board.instance().getSpace(tokentohighlight);
        space.getDisplay().setBackgroundColor(BOGlobals.ClrChoseSpace);
    }

    public void msgSet(String msg) {
        if (LblMessage1 != null) {
            LblMessage1.setText("");
            LblMessage3.setText(BOGlobals.CurrentPlayer.getPlayerName() + ": " + msg);
        }
    }

    public void msgSet(String errmsg, String msg) {
        if (LblMessage1 != null) {
            LblMessage1.setText(errmsg);
            LblMessage3.setText(BOGlobals.CurrentPlayer.getPlayerName() + ": " + msg);
        }
    }

    public void msgSet(Player player, String msg) {
        if (LblMessage1 != null) {
            LblMessage1.setText("");
            LblMessage3.setText(player.getPlayerName() + ": " + msg);
        }
    }

//    public void errMsgSet(String msg) {
//        if (LblMessage3 != null)
//            LblMessage3.setText(BOGlobals.CurrentPlayer.getPlayerName() + ": " + msg);
//    }
//
//    public void errMsgSet(Player player, String msg) {
//        if (LblMessage3 != null)
//            LblMessage3.setText(player.getPlayerName() + ": " + msg);
//    }

    public void log(String msg) { Log.d(TAG, msg); }

    public void tokenBtnCB(View view) {
        // Ignore token click if we are not asking for a token.
        WhereAmI wai = WhereAmIStack.inst().look();
        if ((wai == null) ||
                (wai.getPlayPhase() != WhereAmI.PlayPhase.PlayToken)) return;
        TokenButton btn = (TokenButton) view;
        Token token = btn.getToken();
        boolean success = BOGlobals.CurrentPlayer.afterTokenSelection(token);
        if (! success) {
            btn.setText("");
        }
    } // end tokenBtnCB()

    public void chainBtnCB(View view) {
        WhereAmI.PlayPhase phase = WhereAmIStack.inst().look().getPlayPhase();
        if (phase == WhereAmI.PlayPhase.SelectNewChain) {
            ChainButton btn = (ChainButton) view;
            Chain chain = btn.getChain();
            boolean success = BOGlobals.CurrentPlayer.afterSelectNewChain(chain);
        }
        else if (phase == WhereAmI.PlayPhase.SelectBuyingChain) {
            ChainButton btn = (ChainButton) view;
            Chain chain = btn.getChain();
            boolean success = BOGlobals.CurrentPlayer.afterSelectBuyingChain(chain);
            if (!success)
                msgSet("That chain is not one of the chains being merged.",
                        "Please select the buying chain.");
        }
        else if (phase == WhereAmI.PlayPhase.BuyStock) {
            ChainButton btn = (ChainButton) view;
            Chain chain = btn.getChain();
            boolean success = BOGlobals.CurrentPlayer.afterBuyStock(chain);
        }
    } // end chainBtnCB()

    public void continueBtnCB(View view) {
        WhereAmI wai = WhereAmIStack.inst().look();
        if (wai == null || BOGlobals.CurrentPlayer.isMachine()) {
            Intro2Act.inst().playGame();
            return;
        }
        WhereAmI.PlayPhase phase = wai.getPlayPhase();
        if (phase == WhereAmI.PlayPhase.BuyStock) {
            BOGlobals.CurrentPlayer.afterBuyStock(null);
        }
        else if (phase == WhereAmI.PlayPhase.TakeTile) {
            BOGlobals.CurrentPlayer.afterTakeTile();
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

//    public void startNewPlayer() {
//        // CurrentPlayer will already be set to the new player
//        if (BOGlobals.CurrentPlayer.isMachine()) {
//            BOGlobals.CurrentPlayer.beginTokenSelection();
//        }
//        else {
//            Intent intent = new Intent(this, NewPlayerAct.class);
//            startActivity(intent);
//        }
//    }

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
