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
    TextView LblMessage;


    // This is the real reason to add this class.
    // It is common functionality used by both subclasses
    // PlayGameAct and MergerSellAct
    protected java.util.List<LinearLayout> buildLayout(@Nullable View.OnClickListener tokenCallback,
                                                       @Nullable View.OnClickListener chainCallback ) {

        // We are making a vertical stack made of a number
        // of horizontal rows.  We'll create the rows as
        // more LinearLayouts and partially initialize them.
        // Since they all will have different weights, we'll
        // do the final initializations (setLayoutParams) later
        // 1 for the board
        // 1 for player's tiles and cash
        // 1 for the chains
        // 1 for the message
        // and a final row (details left to the caller)
        int totalnrows = 5;
        ArrayList<LinearLayout> hlayout = new ArrayList<LinearLayout>(totalnrows);
        for (int lln = 0; (lln < totalnrows); lln++) {
            LinearLayout temp = new LinearLayout(this);
            temp.setOrientation((LinearLayout.HORIZONTAL));
            hlayout.add(lln, temp);
        }
        int rownum = 0;  // will keep track of which row we're inserting

        // Create the board, AllTokens need to be initialized after the board.
        Board board = Board.initialize(9, 12, this);
        AllTokens.instance();
        hlayout.set(rownum++, board.buildLayout(this) );
        hlayout.set(rownum++, AllPlayers.instance().buildLayout(this, tokenCallback ) );
        hlayout.set(rownum++, AllChains.instance().buildLayout( this, chainCallback ) );

        // Finally, add the bottom bits
        // message location
        {
            LinearLayout.LayoutParams spacer_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            spacer_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            spacer_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            spacer_params.weight = 2;

            LinearLayout temp = hlayout.get(rownum++);
            temp.setLayoutParams(spacer_params);
            LblMessage = new TextView(this);
            LblMessage.setText("Please click the token you wish to place.");
            temp.addView(LblMessage);
        }

        // And space for the last row
        LinearLayout.LayoutParams bottom_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        bottom_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        bottom_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        bottom_params.weight = 1;

        hlayout.get(rownum++).setLayoutParams(bottom_params);

        return hlayout;
    }
}
