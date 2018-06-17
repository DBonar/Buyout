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
import java.util.List;

public class Players {

    private static Players Instance = null;
    private Player players[];
    private int NPlayers;
    private int n;

    private Players() {}  // default constructor disallowed.

    protected Players(int nplayers, int nmachines, String[] machineNames) {
        // System.out.println("Entered: AllPlayers(" + nplayers + ", " + nmachines + ")");
        MachinePlayer.setupNames( nmachines, machineNames );
        players = new Player[nplayers];
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
            players[playern] = newplayer;
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
                Player swapvalue = players[playern];
                players[playern] = players[randindex];
                players[randindex] = swapvalue;
            } // if randindex
        } // for playern = 0
        n = 0;  // i.e. the first player is the first player
    } // AllPlayers()

    public static Players instance(int nplayers, int nmachines, String[] machineNames) {
        if (Instance == null) Instance = new Players(nplayers, nmachines, machineNames);
        return Instance;
    }

    public static Players instance() {
        return Instance;
    }

    public Player getPlayerN(int arg) {
        if (arg < 0 || arg >= NPlayers) return null;
        return players[arg];
    }

    public int length() {
        return NPlayers;
    }

    public Player firstPlayer() { return players[n]; }
    public Player nextPlayer(Player player) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].getPlayerName() == player.getPlayerName()) {
                if (i < players.length - 1) {
                    return players[i + 1];
                } else {
                    return players[0];
                }
            }
        }
        throw new RuntimeException("Player " + player.getPlayerName() + " is not on the player list.");
    }
    public void advanceToNextPlayer() { n = n + 1; if (n == NPlayers) n = 0;}


    private TextView cash;
    private Token[] tiles;
    public LinearLayout buildLayout(Context context,
                                    @Nullable View.OnClickListener tokenCallback) {
        LinearLayout ret = new LinearLayout(context);

        LinearLayout.LayoutParams row_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        row_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        row_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        row_params.topMargin = 2;
        row_params.bottomMargin = 10;
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
        tiles = new Token[Board.instance().NTokensPerPlayer];
        for (int tn = 0; (tn < Board.instance().NTokensPerPlayer); tn++) {
            Token token = new Token(1,1, context); // replaced later
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
        // Update the tiles and cash amount in the layout
        // to reflect the data for the given player
        List<Token> tokens = player.getTokens();
        int i = 0;
        for (; i < tokens.size(); i++) {
            Token player_tile = (Token) tokens.get(i);
            Token display_tile = tiles[i];
            display_tile.setData( player_tile );
        }
        for (; i < tiles.length; i++) {
            Token display_tile = tiles[i];
            display_tile.setData(null);
        }
        cash.setText("$" + player.getMoney());
    }

    public void updateCallbacks(@Nullable View.OnClickListener tokenCallback) {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i].setOnClickListener(tokenCallback);
        }
    }
}