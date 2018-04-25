package tmw.sept22buyout;

import android.content.Intent;
import android.graphics.Color;
import android.renderscript.Byte2;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class PlayGameAct extends AppCompatActivity {

    private static PlayGameAct Instance = null;
    private static final String TAG = PlayGameAct.class.getSimpleName();

    TokenButton BtnScnTokens[];
    TextView LblCash;
    ChainButton BtnScnChains[];
    TextView LblScnChains[];
    TextView LblMessage1;
    TextView LblMessage2;
    TextView LblMessage3;
    Button BtnEndGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlayGameAct.onCreate() has started");
        Log.d(TAG, "Player = " + BOGlobals.CurrentPlayer.getPlayerName());
        setContentView(R.layout.activity_play_game);

        // Initialize the game objects
        // initializeGame();
        Instance = this;
        // Create the display
        LinearLayout.LayoutParams btnparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        btnparams.width = 0;
        btnparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        btnparams.weight = 1;

        //create a layout
        LinearLayout.LayoutParams vlparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        vlparams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        vlparams.height = LinearLayout.LayoutParams.MATCH_PARENT;

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(vlparams);

        LinearLayout.LayoutParams hlparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        hlparams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        hlparams.height = 0;
        hlparams.weight = 1;

        //LinearLayout hlayout[] = new LinearLayout[20];
        int nchains = AllChains.instance().getAllChains().length();
        int totalnrows = Board.BoardYSize + nchains + 6;
        LinearLayout hlayout[] = new LinearLayout[totalnrows];
        for (int lln = 0; (lln < totalnrows); lln++) {
            hlayout[lln] = new LinearLayout(this);
            hlayout[lln].setOrientation(LinearLayout.HORIZONTAL);
            hlayout[lln].setLayoutParams(hlparams);
        }

        // Create the board: a grid of TextView
        Board board = Board.instance();
//        Button allbuttons[][] = new Button[13][10];
        // TextView allbuttons[][] = new TextView[13][10];
        for (int boardrow = 0; (boardrow < Board.BoardYSize); boardrow++) {
            for (int boardcol = 0; (boardcol < Board.BoardXSize); boardcol++) {
//                allbuttons[boardcol][boardrow] = new Button(this);
//                allbuttons[boardcol][boardrow].setText("ABCDEFGHIJKL".substring(boardcol-1, boardcol) + boardrow);
//                allbuttons[boardcol][boardrow].setLayoutParams(btnparams);
                BoardSpace space = board.getSpace(boardcol, boardrow);
                String spacename = space.getName();
                TextView element = new TextView(this);
                element.setText(spacename);
                element.setLayoutParams(btnparams);
                hlayout[boardrow].addView(element);
                space.setDisplay(element);

//                allbuttons[boardcol][boardrow] = new TextView(this);
//                allbuttons[boardcol][boardrow].setText("ABCDEFGHIJKL".substring(boardcol-1, boardcol) + boardrow);
//                allbuttons[boardcol][boardrow].setLayoutParams(btnparams);
//                hlayout[boardrow].addView(allbuttons[boardcol][boardrow]);
            }
        }

        // Create the row for token buttons and cash
        TextView lblTokens = new TextView(this);
        lblTokens.setText("Tokens:");

        LinearLayout.LayoutParams lblparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        lblparams.width = 0;
        lblparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        lblparams.weight = 2;
        lblTokens.setLayoutParams(lblparams);
        int rownum = Board.BoardYSize;
        hlayout[rownum].addView(lblTokens);

        LinearLayout.LayoutParams widelblparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        widelblparams.width = 0;
        widelblparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        widelblparams.weight = 5;

        BtnScnTokens = new TokenButton[AllTokens.instance().NTokensPerPlayer];
        for (int tn = 0; (tn < AllTokens.instance().NTokensPerPlayer); tn++) {
            TokenButton token = new TokenButton(this);
            token.setText("Button");
//            final int ftn = tn;
//            token.setId(ftn);
            token.setLayoutParams(btnparams);
            BtnScnTokens[tn] = token;
            View vtoke = (View) token;
            vtoke.setOnClickListener(new View.OnClickListener() {
                //@Override;
                public void onClick(View btn) {
                    tokenBtnCB(btn);
                }
            });
            hlayout[rownum].addView(token);
        }
        LblCash = new TextView(this);
        LblCash.setText("$" + 30000);
        LblCash.setLayoutParams(lblparams);
        // LblCash.setBackgroundColor(ChainColor(2));
        hlayout[rownum].addView(LblCash);

//        for (int chainn = 1; (chainn <= 7); chainn++) {
//            int rown = 10 + chainn;
//            TextView lblChainName = new TextView(this);
//            lblChainName.setText("Chain #" + chainn);
//            lblChainName.setLayoutParams(lblparams);
//            lblChainName.setBackgroundColor(Color.rgb(0, 255, 128));
//            // lblChainName.setBackgroundColor(ChainColor(chainn));
//            hlayout[rown].addView(lblChainName);
//
//            TextView lblChainStatus = new TextView(this);
//            lblChainStatus.setText("is not on board.");
//            lblChainStatus.setLayoutParams(widelblparams);
//            hlayout[rown].addView(lblChainStatus);
//        }

        BtnScnChains = new ChainButton[AllChains.instance().nChains()];
        LblScnChains = new TextView[AllChains.instance().nChains()];
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllChains.instance().getAllChains());
        int chainn = 0;
        while ((onechain = chains.getNext()) != null) {
            rownum++;
            ChainButton chainbtn = new ChainButton(this, onechain);
            chainbtn.setText(onechain.toString());
            chainbtn.setLayoutParams(btnparams);
            chainbtn.setBackgroundColor(onechain.getChainColor());
            BtnScnChains[chainn] = chainbtn;
            View vchainbtn = (View) chainbtn;
            vchainbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View btn) {
                    chainBtnCB(btn);
                }
            });
            hlayout[rownum].addView(chainbtn);

            TextView lblChainStatus = new TextView(this);
            lblChainStatus.setText("is not on board.");
            lblChainStatus.setLayoutParams(widelblparams);
            LblScnChains[chainn] = lblChainStatus;
            hlayout[rownum].addView(lblChainStatus);
            chainn++;
        }

        LblMessage1 = new TextView(this);
        LblMessage1.setText("Please click the token you wish to place.");
        hlayout[++rownum].addView(LblMessage1);
        LblMessage2 = new TextView(this);
        LblMessage2.setText("");
        hlayout[++rownum].addView(LblMessage2);
        LblMessage3 = new TextView(this);
        LblMessage3.setText("");
        hlayout[++rownum].addView(LblMessage3);

        rownum++;
        Button continuebtn = new Button(this);
        continuebtn.setText("Continue");
        continuebtn.setLayoutParams(btnparams);
        View vcontinue = (View) continuebtn;
        vcontinue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                continueBtnCB(btn);
            }
        });
        hlayout[rownum].addView(continuebtn);

        BtnEndGame = new Button(this);
        BtnEndGame.setText("");
        BtnEndGame.setLayoutParams(btnparams);
        View vendgame = (View) BtnEndGame;
        vendgame.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                endGameBtnCB(btn);
            }
        });
        hlayout[rownum].addView(BtnEndGame);

        for (int lln = 0; (lln < totalnrows); lln++) {
            layout.addView(hlayout[lln]);
        }

        //create the layout param for the layout
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        this.addContentView(layout, layoutParam);

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

//        // Some test code for the next List.copy() fn
//        List<String> one = new List<String>("a", "b", "c", "d");
//        List<String> two = new List<String>();
//        two.copy(one);
//        LblMessage2.setText("Copy() test " + two.takeFirst() + two.takeFirst());

        Log.d(TAG, "PlayGameAct.refreshScreen() has started.");
        Player thisplayer = BOGlobals.CurrentPlayer;
        Board board = Board.instance();
        for (int rown = 0; (rown < Board.BoardYSize); rown++) {
            for (int coln = 0; (coln < Board.BoardXSize); coln++) {
                BoardSpace space = board.getSpace(coln, rown);
                TextView view = space.getDisplay();
                if (space.getChain() != null)
                    view.setBackgroundColor(space.getChain().getChainColor());
                else if (space.isOccupied())
                    view.setBackgroundColor(BOGlobals.ClrFullSpace);
                else view.setBackgroundColor(BOGlobals.ClrEmptySpace);
            }
        }
        Token onetoken;
        ListIterator<Token> ptokens =
                new ListIterator<Token>(thisplayer.getTokens());
        for (int tn = 0; (tn < AllTokens.instance().NTokensPerPlayer); tn++) {
            onetoken = ptokens.getNext();
            if (onetoken == null) break;
            TokenButton tbutton = BtnScnTokens[tn];
            tbutton.setToken(onetoken);
            tbutton.setText(onetoken.getName());
            BoardSpace space = board.getSpace(onetoken.getCol(), onetoken.getRow());
            TextView vspace = space.getDisplay();
            vspace.setBackgroundColor(BOGlobals.ClrTokenSpace);
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
