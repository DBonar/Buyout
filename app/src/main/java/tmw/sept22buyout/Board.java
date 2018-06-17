package tmw.sept22buyout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tim Weinrich on 10/12/2017.
 */
//
// Board.java
//
// Game board for Buyout game
//

public class Board {

    final public int NTokensPerPlayer = 6;

    // Support for a singleton usage since most of the code expects it
    //static public int BoardXSize = 0;
    //static public int BoardYSize = 0;
    private static Board Instance = null;

    // The instance members and constructors
    private int nrows;
    private int ncols;
    private BoardSpace[][] data;  // [row][col]
    private List<Token> tokenStock;
    private LinearLayout layout;

    public static Board instance() {
        if (Instance == null)
            throw new RuntimeException("Board is not initialized");
        return Instance;
    }


    public static Board initialize(int nRows, int nCols, Context context) {
        if (Instance != null) {
            if ((nRows != Instance.nrows) || (nCols != Instance.ncols)) {
                throw new RuntimeException("Board is already initialized");
            } else {
                return Instance;
            }
        }
        Instance = new Board(nRows, nCols, context);
        //BoardXSize = Instance.ncols;
        //BoardYSize = Instance.nrows;
        return Instance;
    }



    private Board(int nRows, int nCols, Context context) {
        nrows = nRows;
        ncols = nCols;
        layout = null;
        data = new BoardSpace[nrows][ncols];
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                data[r][c] = new BoardSpace(r, c, context);
            }
        }

        // Play one token for each player, all separate
        Players players = Players.instance();
        Player player = players.firstPlayer();
        for (int i = 0; i < players.length(); i++, player = player.nextPlayer()) {
            // The loop will terminate, we're playing just a
            // small number of tiles from the board.
            while (true) {
                Token trial = randomUnoccupiedSpace();
                if (unoccupiedNeighbors(trial).size() == 4) {
                    playToken(trial);
                    break;
                }
            }
        }

        // Create the remaining stoke and shuffle them
        tokenStock = unoccupiedTokens();
        Collections.shuffle(tokenStock);

        // Now deal some out to the players
        for (int i = 0; i < players.length(); i++, player = player.nextPlayer()) {
            for (int n = 0; n < NTokensPerPlayer; n++) {
                player.addToken(takeNextToken());
            }
        }

    }




    //
    // The main external functions
    //

    public void playToken(Token token) {
        data[token.getRow()][token.getCol()].setOccupied();
    }

    public void addToChain(Token token, Chain chain) {
        BoardSpace space = data[token.getRow()][token.getCol()];
        if (space.getChain() != chain) {
            space.setChain(chain);

            // Now color all newly attached neighbors
            List<BoardSpace> border = allNeighbors(space);
            for (int i = 0; i < border.size(); i++) {
                BoardSpace neighbor = border.get(i);
                if (neighbor.isOccupied()
                        && (neighbor.getChain() == null)) {
                    neighbor.setChain(chain);
                    border.addAll(allNeighbors(neighbor));
                }
            }
        }
    }

    public void removeChain(Token token, Chain chain) {
        BoardSpace space = data[token.getRow()][token.getCol()];
        space.removeChain();

        // Now color all newly attached neighbors
        List<BoardSpace> border = allNeighbors(space);
        for (int i = 0; i < border.size(); i++) {
            BoardSpace neighbor = border.get(i);
            if (   neighbor.isOccupied()
                    && (neighbor.getChain() == chain)) {
                neighbor.removeChain();
                chain.decrBoardCount();
                border.addAll( allNeighbors(neighbor) );
            }
        }
    }

    public List<BoardSpace> allNeighbors(Token token) {
        int row = token.getRow();
        int col = token.getCol();
        List<BoardSpace> result = new ArrayList<BoardSpace>();
        col++;
        if (col >= 0 && col < ncols &&
                row >= 0 && row < nrows)
            result.add(data[row][col]);
        col -= 2;
        if (col >= 0 && col < ncols &&
                row >= 0 && row < nrows)
            result.add(data[row][col]);
        col++;
        row++;
        if (col >= 0 && col < ncols &&
                row >= 0 && row < nrows)
            result.add(data[row][col]);
        row -= 2;
        if (col >= 0 && col < ncols &&
                row >= 0 && row < nrows)
            result.add(data[row][col]);
        return result;
    }

    private List<Token> unoccupiedNeighbors(Token token) {
        List<BoardSpace> neighbors = allNeighbors(token);
        List<Token> ret = new ArrayList<Token>();
        for (int i = 0; i < neighbors.size(); i++) {
            BoardSpace tok = (BoardSpace) neighbors.get(i);
            if (!tok.isOccupied()) {
                ret.add(tok);
            }
        }
        return ret;
    }

    private Token randomUnoccupiedSpace() {
        List<Token> possabilities = unoccupiedTokens();
        int n = (int)(Utils.random() * possabilities.size());
        return possabilities.get(n);
    }

    private List<Token> unoccupiedTokens() {
        List<Token> rec = new ArrayList<Token>();
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                if (! data[r][c].isOccupied()) {
                    rec.add( data[r][c] );
                }
            }
        }
        return rec;
    }

    public Token takeNextToken() {
        if (tokenStock.size() == 0)
            return null;
        Token ret = tokenStock.get(tokenStock.size()-1);
        tokenStock.remove(ret);
        return ret;
    }



    //
    //  Layout related bits
    //
    public LinearLayout buildLayout(Context context) {
        if (layout == null) {
            layout = new LinearLayout(context);

            // The overall layout needs a bit of buffter space
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.weight = 1;
            params.topMargin = 3;
            params.bottomMargin = 10;
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(params);

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
            row_params.bottomMargin = 2;

            // It will have YSize rows each of which is a
            // horizontal LinearLayout holding XSize 'buttons'
            for (int rln = 0; rln < nrows; rln++) {
                LinearLayout row = new LinearLayout(context);
                row.setOrientation((LinearLayout.HORIZONTAL));
                row.setLayoutParams(row_params);

                for (int cln = 0; cln < ncols; cln++) {
                    BoardSpace space = data[rln][cln];
                    row.addView(space);
                }

                layout.addView(row);
            }
        }
        return layout;
    }

    public void updateHighlights(Player player) {
        // TODO set the callbacks so highlighted space are really buttons
        // remove old highlights
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                BoardSpace space = data[r][c];
                if (!space.isOccupied())
                    space.setBackgroundColor(BOGlobals.ClrEmptySpace);
            }
        }
        // Add the ones for this player
        List<Token> player_tiles = player.getTokens();
        for (int i = 0; i < player_tiles.size(); i++) {
            Token tile = (Token) player_tiles.get(i);
            highlight( tile );
        }
    }

    private void highlight(Token token) {
        data[token.getRow()][token.getCol()].setBackgroundColor(BOGlobals.ClrTokenSpace);
    }

    public void chosen(Token token) {
        data[token.getRow()][token.getCol()].setBackgroundColor(BOGlobals.ClrChoseSpace);
    }
}
