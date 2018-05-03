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
        LinearLayout.LayoutParams hlparams_1 =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        hlparams_1.width = LinearLayout.LayoutParams.MATCH_PARENT;
        hlparams_1.height = 0;
        hlparams_1.weight = 1;

        // A layout for the "buttons".  Used for tiles in the board,
        // tiles a user might play, and elsewhere.
        LinearLayout.LayoutParams btnparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        btnparams.width = 0;
        btnparams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        btnparams.weight = 1;

        // A layout for some other rows with need more space.
        LinearLayout.LayoutParams hlparams_2 =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        hlparams_2.width = LinearLayout.LayoutParams.MATCH_PARENT;
        hlparams_2.height = 0;
        hlparams_2.weight = 2;
        hlparams_2.bottomMargin = 2;

        // Create the board: a grid of TextView in nested LinearLayouts
        {  // TODO This could be refactored into a Board-related class.
            // Each hoizontal line needs to be sized.  So, we
            // can't just use the params from above, we need one
            // that is sized based on the Text boxes it contains.
            LinearLayout.LayoutParams row_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            row_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            row_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            row_params.weight = 1;

            // TODO It would be nice to have the text more centered
            LinearLayout.LayoutParams cell_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            cell_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            cell_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            cell_params.weight = 1;
            cell_params.leftMargin = 2;

            // This is the overall element for the board.  Note
            // that it's height is based on its content just as
            // the height of the individual rows in it is based on
            // content.  So the size of this element should be
            // mostly independant of the screen size.
            // TODO this is probably ugly on a landscape screen
            LinearLayout board_layout = new LinearLayout(this);
            board_layout.setOrientation(LinearLayout.VERTICAL);
            board_layout.setLayoutParams(row_params);

            // It will have YSize rows each of which is a
            // horizontal LinearLayout holding XSize 'buttons'
            Board board = Board.instance();
            for (int rln = 0; rln < Board.BoardYSize; rln++) {
                LinearLayout temp = new LinearLayout(this);
                temp.setOrientation((LinearLayout.HORIZONTAL));
                temp.setLayoutParams(row_params);

                for (int cln = 0; cln < Board.BoardXSize; cln++) {
                    BoardSpace space = board.getSpace(cln, rln);
                    String spacename = space.getName();
                    TextView element = new TextView(this);
                    element.setPadding(8,0,0,0);
                    element.setText(spacename);
                    element.setLayoutParams(cell_params);
                    temp.addView(element);
                    space.setDisplay(element);
                }

                board_layout.addView(temp);
            }

            hlayout.set(rownum++, board_layout);  // replacing the orig one.
        }


        // Now put in a row below the Board with player tokens and cash
        // This row will have weight 2 in hopes of being big enough to seen
        { // TODO Again, this could probably be factored out in Player-related code
            LinearLayout player_row = hlayout.get(rownum++);

            player_row.setLayoutParams(hlparams_2);

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
            player_row.addView(lblTokens);

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
                player_row.addView(token);
            }

            LblCash = new TextView(this);
            LblCash.setText("$" + 30000);
            LblCash.setLayoutParams(lblparams);
            player_row.addView(LblCash);
        }

        // Now we add rows for the chains.
        //  These rows are weight 1 in the stack
        {  // TODO usual refrain.  Could be factored into Chain-related code
            BtnScnChains = new ChainButton[AllChains.instance().nChains()];
            LblScnChains = new TextView[AllChains.instance().nChains()];
            Chain onechain;
            ListIterator<Chain> chains = new ListIterator<Chain>(AllChains.instance().getAllChains());

            LinearLayout.LayoutParams label_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            label_params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            label_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            label_params.weight = 1;
            label_params.rightMargin = 5;

            LinearLayout.LayoutParams wide_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            wide_params.width = 0;
            wide_params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            wide_params.weight = 7;
            wide_params.leftMargin = 10;

            int chainn = 0;
            while ((onechain = chains.getNext()) != null) {
                LinearLayout chain_row = hlayout.get(rownum++);
                chain_row.setLayoutParams(hlparams_2);  // also need height

                ChainButton chainbtn = new ChainButton(this, onechain);
                chainbtn.setText(onechain.toString());
                chainbtn.setLayoutParams(label_params);
                chainbtn.setBackgroundColor(onechain.getChainColor());
                BtnScnChains[chainn] = chainbtn;
                if (chainCallback != null) {
                    View vchainbtn = (View) chainbtn;
                    vchainbtn.setOnClickListener(chainCallback);
                }
                chain_row.addView(chainbtn);

                TextView lblChainStatus = new TextView(this);
                lblChainStatus.setText("is not on board.");
                lblChainStatus.setLayoutParams(wide_params);
                LblScnChains[chainn] = lblChainStatus;
                chain_row.addView(lblChainStatus);
                chainn++;
            }
        }

        // Finally, add the bottom bits
        { // message location
            LinearLayout temp = hlayout.get(rownum++);
            temp.setLayoutParams(hlparams_1);
            LblMessage1 = new TextView(this);
            LblMessage1.setText("Please click the token you wish to place.");
            temp.addView(LblMessage1);
        }

        // Two spacer rows
        {
            LinearLayout spacer1 = hlayout.get(rownum++);
            spacer1.setLayoutParams(hlparams_1);
            LblMessage2 = new TextView(this);
            LblMessage2.setText("");
            spacer1.addView(LblMessage2);

            LinearLayout spacer2 = hlayout.get(rownum++);
            spacer2.setLayoutParams(hlparams_1);
            LblMessage3 = new TextView(this);
            LblMessage3.setText("");
            spacer2.addView(LblMessage3);
        }

        // set the params for the last row
        hlayout.get(rownum++).setLayoutParams(hlparams_2);

        return hlayout;
    }
}
