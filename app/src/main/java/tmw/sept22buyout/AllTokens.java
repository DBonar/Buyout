package tmw.sept22buyout;

import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
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
    private int NTokens;
    private List<Token> AllRemainingTokens;
    private int NextToken;

    protected AllTokens() {
        // We put one random token on the board for each player.
        // We keep a list of tokens so placed.
        Board board = Board.instance();
        int nrandomtokens = AllPlayers.instance().length();
        int successn = 0;
        LList<Token> placedtokens = new LList<Token>();
        for (int attemptn = 1; attemptn < 10000 && successn < nrandomtokens; attemptn++) {
            Token placer = board.randomUnoccupiedSpace();
            List<Token> neighborList = board.unoccupiedNeighbors(placer);
            if (neighborList.size() == 4) {  // no occupied neighbors
                // We can occupy placer
                board.addToken(placer);
                placedtokens.add(placer);
                successn++;
                Log.d(TAG, "Token setup:  " + placer.toString() + " placed.");
           } // if isplacerok
        } // for attemptn

        // Create all remaining Tokens and shuffle them
        AllRemainingTokens = board.unoccupiedTokens();
        NTokens = AllRemainingTokens.size();
        Collections.shuffle(AllRemainingTokens);

        // Now deal some out to the players
        AllPlayers players = AllPlayers.instance();
        for (int tokenn = 1; tokenn <= NTokensPerPlayer; tokenn++) {
            Player oneplayer = players.firstPlayer();
            for (int playern = 0; playern < players.length(); playern++) {
                Token nexttoken = takeNextToken();
                oneplayer.addToken(nexttoken);
                oneplayer = oneplayer.nextPlayer();
            }
        }
    }

    static public AllTokens instance() {
        if (Instance == null) Instance = new AllTokens();
        return Instance;
    }

    static public AllTokens initialize() {
        if (Instance != null)
            throw new RuntimeException("Tokens already initialized.");
        Instance = new AllTokens();
        return Instance;
    }

    public Token find(String name) {
        String nameuc = name.toUpperCase();
        for (int tokenn = 0; tokenn < NTokens; tokenn++) {
            if (AllRemainingTokens.get(tokenn).getName().toUpperCase().
                    equals(nameuc))
                return AllRemainingTokens.get(tokenn);
        }
        return null;
    }

//    private void shuffleTokens() {
//        for (int tokenn = 0; tokenn < NTokens; tokenn++) {
//            // Exchange [tokenn] with a random one.
//            int rand = (int)(Utils.random() * NTokens);
//            Token swapvalue = AllRemainingTokens[tokenn];
//            AllRemainingTokens[tokenn] = AllRemainingTokens[rand];
//            AllRemainingTokens[rand] = swapvalue;
//        }
//        if (BOGlobals.Cheat) {
//            // For testing, we cheat by placing certain tokens at top
//            exchangeTokens(new Token(4, 2), 1);
//            exchangeTokens(new Token(4, 3), 2);
//            exchangeTokens(new Token(4, 4), 3);
//            exchangeTokens(new Token(4, 5), 4);
//            exchangeTokens(new Token(4, 6), 5);
//            exchangeTokens(new Token(2, 4), 6);
//            exchangeTokens(new Token(3, 4), 7);
//            exchangeTokens(new Token(5, 4), 8);
//            exchangeTokens(new Token(6, 4), 9);
//        }
//    }
//
//    private void exchangeTokens(Token findtoken, int loc) {
//        for (int tokenn = 0; tokenn < NTokens; tokenn++) {
//            if (AllRemainingTokens[tokenn].getCol() == findtoken.getCol() &&
//                    AllRemainingTokens[tokenn].getRow() == findtoken.getRow()) {
//                Token swaptoken = AllRemainingTokens[tokenn];
//                AllRemainingTokens[tokenn] = AllRemainingTokens[loc];
//                AllRemainingTokens[loc] = swaptoken;
//            }
//        } // end for tokenn
//    } // end exchangeTokens()

    public Token takeNextToken() {
        // Removes the next token on AllRemainingTokens and returns it.
        // System.out.println("Taking token #" + NextToken);
        if (NextToken >= NTokens) {
            BOGlobals.EndOfGameOption = true;
            return null;
        }
        Token result = AllRemainingTokens.get(NextToken++);
        // System.out.println("Result = " + result.toString());
        return result;
    }

    public void print() {
        System.out.println("");
        System.out.println("AllTokens: NTokens = " +
                NTokens + ", NextToken = " + NextToken);
        for (int i = 0; i < NTokens; i++)
            System.out.println("  [" + i + "]: " + AllRemainingTokens.get(i).toString());
    }

} // class AllTokens
