package tmw.sept22buyout;

import android.util.Log;

import java.util.Collections;
import java.util.List;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// AllTokens.java
//
// All Buyout Tokens
//

public class AllTokens {

    private static final String TAG = AllTokens.class.getSimpleName();

    final int NTokensPerPlayer = 6;

    private static AllTokens Instance = null;

    private List<Token> remainingTokens;

    protected AllTokens() {
        // We put one random token on the board for each player.
        // We keep a list of tokens so placed.
        Board board = Board.instance();
        int nrandomtokens = AllPlayers.instance().length();
        int successn = 0;

        for (int attemptn = 1; attemptn < 10000 && successn < nrandomtokens; attemptn++) {
            Token placer = board.randomUnoccupiedSpace();
            List<Token> neighborList = board.unoccupiedNeighbors(placer);
            if (neighborList.size() == 4) {  // no occupied neighbors
                // We can occupy placer
                board.playToken(placer);
                successn++;
                Log.d(TAG, "Token setup:  " + placer.toString() + " placed.");
           } // if isplacerok
        } // for attemptn

        // Create all remaining Tokens and shuffle them
        remainingTokens = board.unoccupiedTokens();
        Collections.shuffle(remainingTokens);

        // Now deal some out to the players
        AllPlayers players = AllPlayers.instance();
        for (int tokenn = 1; tokenn <= NTokensPerPlayer; tokenn++) {
            Player oneplayer = players.firstPlayer();
            for (int playern = 0; playern < players.length(); playern++) {
                Token nexttoken = takeNextToken();
                oneplayer.addToken(nexttoken);
                oneplayer = players.nextPlayer(oneplayer);
            }
        }
    }

    static public AllTokens instance() {
        if (Instance == null) Instance = new AllTokens();
        return Instance;
    }

    public Token takeNextToken() {
        if (remainingTokens.size() == 0)
            return null;
        Token ret = remainingTokens.get(remainingTokens.size()-1);
        remainingTokens.remove(ret);
        return ret;
    }

} // class AllTokens
