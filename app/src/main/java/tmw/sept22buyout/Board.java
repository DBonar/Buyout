package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/12/2017.
 */
//
// Board.java
//
// Game board for Buyout game
//

public class Board {

    static final int BoardXSize = 12;
    static final int BoardYSize = 9;

    private static Board Instance = null;

    private BoardSpace[][] GameBoard;

    protected Board() {
        GameBoard = new BoardSpace[BoardXSize][BoardYSize];
        for (int x = 0; x < BoardXSize; x++) {
            for (int y = 0; y < BoardYSize; y++) {
                GameBoard[x][y] = new BoardSpace(x, y);
            }
        }
    }

    public static Board instance() {
        if (Instance == null) Instance = new Board();
        return Instance;
    }

    public BoardSpace getSpace(int col, int row) { return GameBoard[col][row]; }

    public BoardSpace getSpace(Token token) {
        return getSpace(token.getCol(), token.getRow()); }

    public void addToken(Token token) {
        GameBoard[token.getCol()][token.getRow()].setOccupied();
    }

    public List<BoardSpace> allNeighbors(BoardSpace boardspace) {
        List<BoardSpace> result = new List<BoardSpace>();
        int col = boardspace.getCol();
        int row = boardspace.getRow();
        col++;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(GameBoard[col][row]);
        col -= 2;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(GameBoard[col][row]);
        col++;
        row++;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(GameBoard[col][row]);
        row -= 2;
        if (col >= 0 && col < BoardXSize &&
                row >= 0 && row < BoardYSize)
            result.add(GameBoard[col][row]);
        return result;
    } // List<BoardSpace> allNeighbors

}
