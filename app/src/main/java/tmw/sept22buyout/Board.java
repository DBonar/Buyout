package tmw.sept22buyout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
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

    // Support for a singleton usage since most of the code expects it
    static public int BoardXSize = 0;
    static public int BoardYSize = 0;
    private static Board Instance = null;

    private BoardSpace[][] data;  // [row][col]
    private LinearLayout layout;

    public static Board instance() {
        if (Instance == null)
            throw new RuntimeException("Board is not initialized");
        return Instance;
    }

    public static Board initialize(Context context, int nRows, int nCols) {
        if (Instance != null) {
            if ((nRows != Instance.nrows) || (nCols != Instance.ncols)) {
                throw new RuntimeException("Board is already initialized");
            } else {
                return Instance;
            }
        }
        Instance = new Board(context, nRows, nCols);
        BoardXSize = Instance.ncols;
        BoardYSize = Instance.nrows;
        return Instance;
    }

    // The instance members and constructors
    private int nrows;
    private int ncols;

    private Board(Context context, int nRows, int nCols) {
        nrows = nRows;
        ncols = nCols;
        layout = null;
        fillData();
    }

    private void fillData() {
        data = new BoardSpace[nrows][ncols];
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                data[r][c] = new BoardSpace(c, r);  // BoardSpace expects (col,row)
            }
        }
    }


    //
    // The main external functions
    //

    public BoardSpace getSpace(Token token) {
        return data[token.getRow()][token.getCol()];
    }

    public void addToken(Token token) {
        int row_num = token.getRow();
        int col_num = token.getCol();
        data[row_num][col_num].setOccupied();
        if (layout != null) {
            LinearLayout row = (LinearLayout) layout.getChildAt(row_num);
            TextView cell = (TextView) row.getChildAt(col_num);
            cell.setBackgroundColor(BOGlobals.ClrFullSpace);
        }
    }

    public void setChain(Token token, Chain chain) {
        int row_num = token.getRow();
        int col_num = token.getCol();
        data[row_num][col_num].setChain(chain);
        if (layout != null) {
            LinearLayout row = (LinearLayout) layout.getChildAt(row_num);
            TextView cell = (TextView) row.getChildAt(col_num);
            cell.setBackgroundColor(chain.getChainColor());
        }
    }

    private List<BoardSpace> allNeighbors(int row, int col) {
        List<BoardSpace> result = new ArrayList<BoardSpace>();
        col++;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(data[row][col]);
        col -= 2;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(data[row][col]);
        col++;
        row++;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(data[row][col]);
        row -= 2;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(data[row][col]);
        return result;
    } // LList<BoardSpace> allNeighbors
    public List<BoardSpace> allNeighbors(BoardSpace bs) {
        return allNeighbors(bs.getRow(),bs.getCol());
    }
    public List<Token> allNeighbors(Token token) {
        List neighbors = allNeighbors(token.getRow(), token.getCol());
        List<Token> ret = new ArrayList<Token>();
        for (int i = 0; i < neighbors.size(); i++) {
            BoardSpace bs = (BoardSpace) neighbors.get(i);
            ret.add(new Token(bs.getCol(), bs.getRow()));
        }
        return ret;
    }
    public List<Token> unoccupiedNeighbors(Token token) {
        List<Token> neighbors = allNeighbors(token);
        List<Token> ret = new ArrayList<Token>();
        for (int i = 0; i < neighbors.size(); i++) {
            Token tok = (Token) neighbors.get(i);
            if (!data[tok.getRow()][tok.getCol()].isOccupied()) {
                ret.add(tok);
            }
        }
        return ret;
    }

    public Token randomUnoccupiedSpace() {
        List<Token> possabilities = unoccupiedTokens();
        int n = (int)(Utils.random() * possabilities.size());
        return possabilities.get(n);
    }

    public List<Token> unoccupiedTokens() {
        List<Token> rec = new ArrayList<Token>();
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                if (! data[r][c].isOccupied()) {
                    rec.add( new Token(c,r) );  // Token expects (col, row)
                }
            }
        }
        return rec;
    }

    //
    //  Layout related bits
    //
    public LinearLayout buildLayout(Context context) {
        layout = new LinearLayout(context);

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
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(row_params);

        // It will have YSize rows each of which is a
        // horizontal LinearLayout holding XSize 'buttons'
        for (int rln = 0; rln < nrows; rln++) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation((LinearLayout.HORIZONTAL));
            row.setLayoutParams(row_params);

            for (int cln = 0; cln < ncols; cln++) {
                BoardSpace space = data[rln][cln];
                String spacename = space.getName();
                TextView cell = new TextView(context);
                cell.setPadding(8,0,0,10);
                cell.setText(spacename);
                cell.setLayoutParams(cell_params);
                if (space.getChain() != null)
                    cell.setBackgroundColor(space.getChain().getChainColor());
                else if (space.isOccupied())
                    cell.setBackgroundColor(BOGlobals.ClrFullSpace);
                else cell.setBackgroundColor(BOGlobals.ClrEmptySpace);
                row.addView(cell);
            }

            layout.addView(row);
        }
        return layout;
    }

    public void updateView() {
        if (layout != null) {
            for (int r = 0; r < nrows; r++) {
                LinearLayout row = (LinearLayout) layout.getChildAt(r);
                for (int c = 0; c < ncols; c++) {
                    TextView cell = (TextView) row.getChildAt(c);
                    BoardSpace space = data[r][c];
                    if (space.getChain() != null)
                        cell.setBackgroundColor(space.getChain().getChainColor());
                    else if (space.isOccupied())
                        cell.setBackgroundColor(BOGlobals.ClrFullSpace);
                    else cell.setBackgroundColor(BOGlobals.ClrEmptySpace);
                }
            }
        }
    }

    public void highlight(Token token) {
        // We know how this display works, we can go ahead and
        // directly index into its children.
        if (layout != null) {
            LinearLayout row = (LinearLayout) layout.getChildAt(token.getRow());
            TextView cell = (TextView) row.getChildAt(token.getCol());
            cell.setBackgroundColor(BOGlobals.ClrTokenSpace);
        }
    }

    public void chosen(Token token) {
        if (layout != null) {
            LinearLayout row = (LinearLayout) layout.getChildAt(token.getRow());
            TextView cell = (TextView) row.getChildAt(token.getCol());
            cell.setBackgroundColor(BOGlobals.ClrChoseSpace);
        }
    }
}
