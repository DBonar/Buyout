package tmw.sept22buyout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayLogic extends AppCompatActivity {

    // This is boring, just a passthrough
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Common variables referred to in the
    // common routine below
    TokenButton BtnScnTokens[];
    TextView LblCash;
    ChainButton BtnScnChains[];
    TextView LblScnChains[];
    TextView LblMessage1;
    TextView LblMessage2;
    TextView LblMessage3;

    // This is the real reason to add this class.
    // It is common functionality used by both subclasses
    // PlayGameAct and MergerSellAct
    protected java.util.List<LinearLayout> buildLayout(@Nullable View.OnClickListener tokenCallback,
                                                       @Nullable View.OnClickListener chainCallback ) {

        // That vertical stack will be made of a number
        // of horizontal rows.  We'll create the rows as
        // more LinearLayouts and partially initialize them.
        // Since they all will have different weights, we'll
        // do the final initializations (setLayoutParams) later
        int nchains = AllChains.instance().getAllChains().length();
        int totalnrows = Board.BoardYSize + nchains + 6;
        ArrayList<LinearLayout> hlayout = new ArrayList<LinearLayout>(totalnrows);
        for (int lln = 0; (lln < totalnrows); lln++) {
            LinearLayout temp = new LinearLayout(this);
            temp.setOrientation((LinearLayout.HORIZONTAL));
            hlayout.add(lln, temp);
            //hlayout[lln].setLayoutParams(hlparams);
        }

        // layout for the board rows.  They all get weight 1
        LinearLayout.LayoutParams hlparams_1 =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        hlparams_1.width = LinearLayout.LayoutParams.MATCH_PARENT;
        hlparams_1.height = 0;
        hlparams_1.weight = 1;
        int rownum = 0; // running counter of which row we're working on
        for (; rownum < Board.BoardYSize; rownum++) {
            hlayout.get(rownum).setLayoutParams(hlparams_1);
        }

        // Create the board: a grid of TextView
        // They need LayoutPrams, so set those up now
        // then loop over the board creating and adding
        // the horizontal rows.
        LinearLayout.LayoutParams btnparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        btnparams.width = 0;
        btnparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        btnparams.weight = 1;

        Board board = Board.instance();
        for (int boardrow = 0; (boardrow < Board.BoardYSize); boardrow++) {
            for (int boardcol = 0; (boardcol < Board.BoardXSize); boardcol++) {
                BoardSpace space = board.getSpace(boardcol, boardrow);
                String spacename = space.getName();
                TextView element = new TextView(this);
                element.setText(spacename);
                element.setLayoutParams(btnparams);
                hlayout.get(boardrow).addView(element);
                space.setDisplay(element);
            }
        }

        // Now put in a row below the Board with player tokens and cash
        // This row will have weight 2 in hopes of being big enough to seen
        LinearLayout.LayoutParams hlparams_2 =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        hlparams_2.width = LinearLayout.LayoutParams.MATCH_PARENT;
        hlparams_2.height = 0;
        hlparams_2.weight = 2;
        hlayout.get(rownum).setLayoutParams(hlparams_2);

        TextView lblTokens = new TextView(this);
        lblTokens.setText("Tokens:");
        LinearLayout.LayoutParams lblparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        lblparams.width = 0;
        lblparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        lblparams.weight = 1;
        lblTokens.setLayoutParams(lblparams);
        hlayout.get(rownum).addView(lblTokens);

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
            token.setLayoutParams(btnparams);
            BtnScnTokens[tn] = token;
            if (tokenCallback != null) {
                View vtoke = (View) token;
                vtoke.setOnClickListener(tokenCallback);
            }
            hlayout.get(rownum).addView(token);
        }

        LblCash = new TextView(this);
        LblCash.setText("$" + 30000);
        LblCash.setLayoutParams(lblparams);
        hlayout.get(rownum).addView(LblCash);

        // Now we add rows for the chains.
        //  These rows are weight 1 in the stack
        BtnScnChains = new ChainButton[AllChains.instance().nChains()];
        LblScnChains = new TextView[AllChains.instance().nChains()];
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllChains.instance().getAllChains());
        int chainn = 0;
        while ((onechain = chains.getNext()) != null) {
            rownum++;
            hlayout.get(rownum).setLayoutParams(hlparams_2);  // also need height

            ChainButton chainbtn = new ChainButton(this, onechain);
            chainbtn.setText(onechain.toString());
            chainbtn.setLayoutParams(btnparams);
            chainbtn.setBackgroundColor(onechain.getChainColor());
            BtnScnChains[chainn] = chainbtn;
            if (chainCallback != null) {
                View vchainbtn = (View) chainbtn;
                vchainbtn.setOnClickListener(chainCallback);
            }
            hlayout.get(rownum).addView(chainbtn);

            TextView lblChainStatus = new TextView(this);
            lblChainStatus.setText("is not on board.");
            lblChainStatus.setLayoutParams(widelblparams);
            LblScnChains[chainn] = lblChainStatus;
            hlayout.get(rownum).addView(lblChainStatus);
            chainn++;
        }

        // Finally, add the bottom bits
        rownum++;
        hlayout.get(rownum).setLayoutParams(hlparams_1);
        LblMessage1 = new TextView(this);
        LblMessage1.setText("Please click the token you wish to place.");
        hlayout.get(rownum).addView(LblMessage1);

        // Two spacer rows
        rownum++;
        hlayout.get(rownum).setLayoutParams(hlparams_1);
        LblMessage2 = new TextView(this);
        LblMessage2.setText("");
        hlayout.get(rownum).addView(LblMessage2);
        rownum++;
        hlayout.get(rownum).setLayoutParams(hlparams_1);
        LblMessage3 = new TextView(this);
        LblMessage3.setText("");
        hlayout.get(rownum).addView(LblMessage3);

        // set the params for the last row
        rownum++;
        hlayout.get(rownum).setLayoutParams(hlparams_2);
        return hlayout;
    }
}
