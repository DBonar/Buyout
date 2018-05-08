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
    Board board;

    // This is the real reason to add this class.
    // It is common functionality used by both subclasses
    // PlayGameAct and MergerSellAct
    protected java.util.List<LinearLayout> buildLayout(@Nullable View.OnClickListener tokenCallback,
                                                       @Nullable View.OnClickListener chainCallback ) {

        // TODO This works on a big screen, but not a small one
        
        // We are making a vertical stack made of a number
        // of horizontal rows.  We'll create the rows as
        // more LinearLayouts and partially initialize them.
        // Since they all will have different weights, we'll
        // do the final initializations (setLayoutParams) later
        int nchains = AllChains.instance().getAllChains().length();
        // 1 for the board
        // 1 for player's tiles and cash
        // nchains for the chains
        // 1 for a space
        // 1 for the message
        // 2 more for a spacer
        // and a final row (details left to the caller)
        int totalnrows = nchains + 7;
        ArrayList<LinearLayout> hlayout = new ArrayList<LinearLayout>(totalnrows);
        for (int lln = 0; (lln < totalnrows); lln++) {
            LinearLayout temp = new LinearLayout(this);
            temp.setOrientation((LinearLayout.HORIZONTAL));
            hlayout.add(lln, temp);
            //hlayout[lln].setLayoutParams(hlparams);
        }
        int rownum = 0;  // will keep track of which row we're inserting

        // layout for some board rows.  weight 1
        LinearLayout.LayoutParams spacer_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        spacer_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        spacer_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        spacer_params.weight = 1;

        // Create the board
        board = Board.initialize(this, 9, 12);
        hlayout.set(rownum++, board.buildLayout(this));
        AllTokens.instance();  // We hit this at least twice.
        
        // Now put in a row below the Board with player tokens and cash
        // This row will have weight 2 in hopes of being big enough to seen
        { // TODO Again, this could probably be factored out in Player-related code
            LinearLayout player_row = hlayout.get(rownum++);

            LinearLayout.LayoutParams row_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            row_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            row_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            row_params.bottomMargin = 2;
            player_row.setLayoutParams(row_params);

            TextView lblTokens = new TextView(this);
            lblTokens.setText("Tokens:");
            LinearLayout.LayoutParams label_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            label_params.width = 0;
            label_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            label_params.weight = 1;
            lblTokens.setLayoutParams(label_params);
            player_row.addView(lblTokens);

            LinearLayout.LayoutParams token_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            token_params.width = 0;
            token_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            token_params.weight = 1;
            BtnScnTokens = new TokenButton[AllTokens.instance().NTokensPerPlayer];
            for (int tn = 0; (tn < AllTokens.instance().NTokensPerPlayer); tn++) {
                TokenButton token = new TokenButton(this);
                token.setText("Button");
                token.setLayoutParams(token_params);
                token.setMinHeight(1);
                token.setMinimumHeight(1);
                BtnScnTokens[tn] = token;
                if (tokenCallback != null) {
                    View vtoke = (View) token;
                    vtoke.setOnClickListener(tokenCallback);
                }
                player_row.addView(token);
            }

            LblCash = new TextView(this);
            LblCash.setText("$" + 30000);
            LblCash.setLayoutParams(label_params);
            player_row.addView(LblCash);
        }

        // Now we add rows for the chains.
        //  These rows are weight 1 in the stack
        {  // TODO usual refrain.  Could be factored into Chain-related code

            LinearLayout.LayoutParams row_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            row_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            row_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            LinearLayout.LayoutParams label_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            label_params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            label_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            label_params.weight = 1;

            LinearLayout.LayoutParams wide_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            wide_params.width = 0;
            wide_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            wide_params.weight = 7;
            wide_params.leftMargin = 15;

            BtnScnChains = new ChainButton[AllChains.instance().nChains()];
            LblScnChains = new TextView[AllChains.instance().nChains()];
            Chain onechain;
            ListIterator<Chain> chains = new ListIterator<Chain>(AllChains.instance().getAllChains());
            int chainn = 0;
            while ((onechain = chains.getNext()) != null) {
                LinearLayout chain_row = hlayout.get(rownum++);
                chain_row.setLayoutParams(row_params);
                chain_row.setBaselineAligned(false);

                ChainButton chainbtn = new ChainButton(this, onechain);
                chainbtn.setText(onechain.toString());
                chainbtn.setLayoutParams(label_params);
                chainbtn.setIncludeFontPadding(false);
                chainbtn.setMinHeight(1);
                chainbtn.setMinimumHeight(1);
                chainbtn.setBackgroundColor(onechain.getChainColor());
                BtnScnChains[chainn] = chainbtn;
                if (chainCallback != null) {
                    View vchainbtn = (View) chainbtn;
                    vchainbtn.setOnClickListener(chainCallback);
                }
                chain_row.addView(chainbtn);

                TextView lblChainStatus = new TextView(this);
                lblChainStatus.setText("is not on board.\n second line");
                lblChainStatus.setLayoutParams(wide_params);
                LblScnChains[chainn] = lblChainStatus;
                chain_row.addView(lblChainStatus);

                chainn++;
            }
        }

        // Finally, add the bottom bits
        { // message location
            LinearLayout temp = hlayout.get(rownum++);
            temp.setLayoutParams(spacer_params);
            LblMessage1 = new TextView(this);
            LblMessage1.setText("Please click the token you wish to place.");
            temp.addView(LblMessage1);
        }

        // Two spacer rows
        {
            LinearLayout spacer1 = hlayout.get(rownum++);
            spacer1.setLayoutParams(spacer_params);
            LblMessage2 = new TextView(this);
            LblMessage2.setText("");
            spacer1.addView(LblMessage2);

            LinearLayout spacer2 = hlayout.get(rownum++);
            spacer2.setLayoutParams(spacer_params);
            LblMessage3 = new TextView(this);
            LblMessage3.setText("");
            spacer2.addView(LblMessage3);
        }

        // set the params for the last row
        hlayout.get(rownum++).setLayoutParams(spacer_params);

        return hlayout;
    }
}
