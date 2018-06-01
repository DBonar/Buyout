package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/10/2017.
 */

//
// AllPlayers.java
//
// All players of Buyout, accessible as a cycled list and in other ways.
// This is a Singleton class -
// Make an AllPlayers instance by calling AllPlayers.instance(int, int) and
// refer to it with AllPlayers.instance()
//

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.Math;

public class AllPlayers {

    private static AllPlayers Instance = null;
    private Player Players[];
    private int NPlayers;
    private int n;

    private AllPlayers() {}  // default constructor disallowed.

    protected AllPlayers(int nplayers, int nmachines, String[] machineNames) {
        // System.out.println("Entered: AllPlayers(" + nplayers + ", " + nmachines + ")");
        MachinePlayer.setupNames( nmachines, machineNames );
        Players = new Player[nplayers];
        NPlayers = nplayers;
        for (int playern = 0; playern < nplayers; playern++) {
            Player newplayer = null;
            if (playern < nplayers - nmachines) {
                // Player is human
                newplayer = new Player();
                newplayer.setPlayerName(Intro2Act.HumanNames[playern]);
            } // if playern
            else {
                // Player is machine
                newplayer = new MachinePlayer(1 + playern - nplayers + nmachines);
            } // else
            Players[playern] = newplayer;
        }
        // Shuffle the players and assign sequence
        // For historical reasons, I assume the sequence on the array must
        // must be the same as the sequence of moves.  This may or may not be
        // true.  tmw, 6/2017
        for (int playern = 0; playern < nplayers; playern++) {
            // Exchange [playern] with a random succeeding one.
            int rand = (int)(Utils.random() * (nplayers - playern));
            int randindex = playern + rand;
            if (randindex != playern) {
                // Swap playern with randindex
                Player swapvalue = Players[playern];
                Players[playern] = Players[randindex];
                Players[randindex] = swapvalue;
            } // if randindex
            if (playern != 0)
                Players[playern - 1].setNextPlayer(Players[playern]);
            if (playern == nplayers - 1)
                Players[playern].setNextPlayer(Players[0]);
        } // for playern = 0
        n = 0;  // i.e. the first player is the first player
    } // AllPlayers()

    public static AllPlayers instance(int nplayers, int nmachines, String[] machineNames) {
        if (Instance == null) Instance = new AllPlayers(nplayers, nmachines, machineNames);
        return Instance;
    }

    public static AllPlayers instance() {
        return Instance;
    }

    public Player getPlayerN(int arg) {
        if (arg < 0 || arg >= NPlayers) return null;
        return Players[arg];
    }

    public int length() {
        return NPlayers;
    }

    public Player firstPlayer() { return Players[n]; }
    public void nextPlayer() { n = n + 1; if (n == NPlayers) n = 0;}


    private TextView cash;
    private TokenButton[] tiles;
    public LinearLayout buildLayout(Context context,
                                    @Nullable View.OnClickListener tokenCallback) {
        LinearLayout ret = new LinearLayout(context);

        LinearLayout.LayoutParams row_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        row_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        row_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        row_params.bottomMargin = 2;
        ret.setOrientation((LinearLayout.HORIZONTAL));
        ret.setLayoutParams(row_params);

        TextView lblTokens = new TextView(context);
        lblTokens.setText("Tokens:");
        LinearLayout.LayoutParams label_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        label_params.width = 0;
        label_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        label_params.weight = 1;
        lblTokens.setLayoutParams(label_params);
        ret.addView(lblTokens);

        LinearLayout.LayoutParams token_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        token_params.width = 0;
        token_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        token_params.weight = 1;
        tiles = new TokenButton[AllTokens.instance().NTokensPerPlayer];
        for (int tn = 0; (tn < AllTokens.instance().NTokensPerPlayer); tn++) {
            TokenButton token = new TokenButton(context);
            token.setText("Button");
            token.setLayoutParams(token_params);
            token.setMinHeight(1);
            token.setMinimumHeight(1);
            tiles[tn] = token;
            if (tokenCallback != null) {
                View vtoke = (View) token;
                vtoke.setOnClickListener(tokenCallback);
            }
            ret.addView(token);
        }

        cash = new TextView(context);
        cash.setText("$" + 30000);
        cash.setLayoutParams(label_params);
        ret.addView(cash);

        return ret;
    }

    public void updatePlayerData(Player player) {
        Token onetoken;
        ListIterator<Token> ptokens =
                new ListIterator<Token>(player.getTokens());
        for (int tn = 0; (tn < AllTokens.instance().NTokensPerPlayer); tn++) {
            onetoken = ptokens.getNext();
            if (onetoken == null) break;
            TokenButton tbutton = tiles[tn];
            tbutton.setToken(onetoken);
            tbutton.setText(onetoken.getName());
        }
        cash.setText("$" + player.getMoney());
    }

    public void updateCallbacks(@Nullable View.OnClickListener tokenCallback) {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i].setOnClickListener(tokenCallback);
        }
    }
}
