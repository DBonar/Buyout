package tmw.sept22buyout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MergerSellAct extends DisplayLogic {

    private static MergerSellAct Instance = null;
    private static final String TAG = MergerSellAct.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merger_sell);

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

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(vlparams);
        this.addContentView(layout, vlparams);

        // Get the common rows giving the callbacks for buttons.
        java.util.List<LinearLayout> rows = buildLayout(null,
                                                        null);

        // Add the last row
        int last = rows.size() - 1;
        LinearLayout.LayoutParams btnparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        btnparams.width = 0;
        btnparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        btnparams.weight = 1;

        Button sellbtn = new Button(this);
        sellbtn.setText("Sell");
        sellbtn.setLayoutParams(btnparams);
        View vsell = (View) sellbtn;
        vsell.setOnClickListener((btn) -> { sellBtnCB(btn); } );
        rows.get(last).addView(sellbtn);

        Button tradebtn = new Button(this);
        tradebtn.setText("Trade");
        tradebtn.setLayoutParams(btnparams);
        View vtrade = (View) tradebtn;
        vtrade.setOnClickListener((btn) -> { tradeBtnCB(btn); } );
        rows.get(last).addView(tradebtn);

        Button keepbtn = new Button(this);
        keepbtn.setText("Keep");
        keepbtn.setLayoutParams(btnparams);
        View vkeep = (View) keepbtn;
        vkeep.setOnClickListener((btn) -> { keepBtnCB(btn); } );
        rows.get(last).addView(keepbtn);

        for (int lln = 0; (lln < rows.size()); lln++) {
            layout.addView(rows.get(lln));
        }

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