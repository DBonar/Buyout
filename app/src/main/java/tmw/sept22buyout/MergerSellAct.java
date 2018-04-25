package tmw.sept22buyout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MergerSellAct extends AppCompatActivity {

    private static MergerSellAct Instance = null;
    private static final String TAG = MergerSellAct.class.getSimpleName();

    TokenButton BtnScnTokens[];
    TextView LblCash;
    ChainButton BtnScnChains[];
    TextView LblScnChains[];
    TextView LblMessage1;
    TextView LblMessage2;
    TextView LblMessage3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merger_sell);

        // Instance = this;
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
//            vtoke.setOnClickListener(new View.OnClickListener() {
//                //@Override;
//                public void onClick(View btn) {
//                    tokenBtnCB(btn);
//                }
//            });
            hlayout[rownum].addView(token);
        }
        LblCash = new TextView(this);
        LblCash.setText("Not Specified");
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
//            vchainbtn.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View btn) {
//                    chainBtnCB(btn);
//                }
//            });
            hlayout[rownum].addView(chainbtn);

            TextView lblChainStatus = new TextView(this);
            lblChainStatus.setText("is not specified.");
            lblChainStatus.setLayoutParams(widelblparams);
            LblScnChains[chainn] = lblChainStatus;
            hlayout[rownum].addView(lblChainStatus);
            chainn++;
        }

        LblMessage1 = new TextView(this);
        LblMessage1.setText("Not specified.");
        hlayout[++rownum].addView(LblMessage1);
        LblMessage2 = new TextView(this);
        LblMessage2.setText("");
        hlayout[++rownum].addView(LblMessage2);
        LblMessage3 = new TextView(this);
        LblMessage3.setText("");
        hlayout[++rownum].addView(LblMessage3);

        rownum++;
        Button sellbtn = new Button(this);
        sellbtn.setText("Sell");
        sellbtn.setLayoutParams(btnparams);
        View vsell = (View) sellbtn;
        vsell.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                sellBtnCB(btn);
            }
        });
        hlayout[rownum].addView(sellbtn);

        Button tradebtn = new Button(this);
        tradebtn.setText("Trade");
        tradebtn.setLayoutParams(btnparams);
        View vtrade = (View) tradebtn;
        vtrade.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                tradeBtnCB(btn);
            }
        });
        hlayout[rownum].addView(tradebtn);

        Button keepbtn = new Button(this);
        keepbtn.setText("Keep");
        keepbtn.setLayoutParams(btnparams);
        View vkeep = (View) keepbtn;
        vkeep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View btn) {
                keepBtnCB(btn);
            }
        });
        hlayout[rownum].addView(keepbtn);

        for (int lln = 0; (lln < totalnrows); lln++) {
            layout.addView(hlayout[lln]);
        }

        //create the layout param for the layout
        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        this.addContentView(layout, layoutParam);

        refreshScreen();
    }

    public void refreshScreen() {

//        // Some test code for the next List.copy() fn
//        List<String> one = new List<String>("a", "b", "c", "d");
//        List<String> two = new List<String>();
//        two.copy(one);
//        LblMessage2.setText("Copy() test " + two.takeFirst() + two.takeFirst());

        Log.d(TAG, "MergerSellAct.refreshScreen() has started.");
        WhereAmI wai = WhereAmIStack.inst().look();
        Token token = wai.getToken();
        BoardSpace highlightspace = Board.instance().getSpace(token);
        Player thisplayer = wai.getPlayer();
        Chain buychain = wai.getBuyChain();
        Chain sellchain = wai.getChain();
        int nsharestounload = wai.getNShares();
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
        TextView view = highlightspace.getDisplay();
        view.setBackgroundColor(BOGlobals.ClrChoseSpace);
        LblCash.setText("$" + thisplayer.getMoney());
        for (int cn = 0; (cn < BtnScnChains.length); cn++) {
            ChainButton btnonechain = BtnScnChains[cn];
            Chain onechain = btnonechain.getChain();
            TextView lblonechain = LblScnChains[cn];
            lblonechain.setText(onechain.toFullString(thisplayer));
        }

        msgSet(thisplayer, sellchain.getName() + " is being bought by " +
                buychain.getName() + ". Please dispose of " + nsharestounload + " shares.");
    } // end refreshScreen()

    public void msgSet(Player player, String msg) {
        LblMessage1.setText(player.getPlayerName() + ": " + msg);
    }

    public void sellBtnCB(View view) {
        WhereAmI wai = WhereAmIStack.inst().look();
        Player seller = wai.getPlayer();
        Chain sellchain = wai.getChain();
        int nshares = wai.getNShares();
        seller.sellStock(sellchain, 1);
        nshares--;
        wai.setNShares(nshares);
        ActionLog.inst().add(BOGlobals.CurrentPlayer, seller, "has sold a share of " +
                sellchain.toString());
        if (nshares == 0) finishPlayerMerger();
        else refreshScreen();
    } // end sellBtnCB()

    public void tradeBtnCB(View view) {
        WhereAmI wai = WhereAmIStack.inst().look();
        Player seller = wai.getPlayer();
        Chain buychain = wai.getBuyChain();
        Chain sellchain = wai.getChain();
        int nsharestounload = wai.getNShares();
        if (nsharestounload >= 2) {
            if (seller.takeStock(buychain, 1)) {
                seller.giveStock(sellchain, 2);
                nsharestounload -= 2;
            }
        }
        wai.setNShares(nsharestounload);
        ActionLog.inst().add(BOGlobals.CurrentPlayer, seller, "has traded 2 shares of " +
                sellchain.toString() + " for 1 share of " + buychain.toString());
        if (nsharestounload == 0) finishPlayerMerger();
        else refreshScreen();
    } // end traceBtnCB()

    public void keepBtnCB(View view) {
        WhereAmI wai = WhereAmIStack.inst().look();
        Player seller = wai.getPlayer();
        Chain sellchain = wai.getChain();
        int nshares = wai.getNShares();
        nshares--;
        wai.setNShares(nshares);
        ActionLog.inst().add(BOGlobals.CurrentPlayer, seller, "has kept 1 share of " +
                sellchain.toString());
        if (nshares == 0) finishPlayerMerger();
        else refreshScreen();
    } // end keepBtnCB()

    public void finishPlayerMerger() {
        // The main player may be a machine...
        if (BOGlobals.CurrentPlayer.isMachine()) {
            // The main player is a machine
            BOGlobals.CurrentPlayer.afterUnloadStock();
            // Control will return here when this machine player is finished.
            // So we kick off the next player.
            if (WhereAmIStack.inst().look() == null) {
                // This player is finished with his turn.
                Intro2Act.inst().playGame();
            } else {
                // The main player is still not finished with his turn.
                // Presumably, control has been handed over to a new activity.
                // So we allow this thread to die.
                return;
            }
        } else {
            // Intent intent = new Intent(this, PlayGameAct.class);
            Intent intent = new Intent(this, NewPlayerAct.class);
            startActivity(intent);
        }
    } // end finishPlayerMerger()

    @Override
    public void onBackPressed() {
    }

} // end class MergerSellAct