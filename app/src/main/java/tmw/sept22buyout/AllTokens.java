package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// AllTokens.java
//
// All Buyout Tokens
//

public class AllTokens {

    final int NTokensPerPlayer = 6;

    private static AllTokens Instance = null;
    private int NTokens;
    private Token AllRemainingTokens[];
    private int NextToken;

    protected AllTokens() {
        // We put one random token on the board for each player.
        // We keep a list of tokens so placed.
        Board board = Board.instance();
        int nrandomtokens = AllPlayers.instance().length();
        int successn = 0;
        List<Token> placedtokens = new List<Token>();
        for (int attemptn = 1; attemptn < 10000 && successn < nrandomtokens; attemptn++) {
            BoardSpace placer;
            // Test code:
            // if (attemptn==1) placer = board.getSpace(1, 11);
            // else if (attemptn==2) placer = board.getSpace(3, 10);
            // else if (attemptn==3) placer = board.getSpace(4, 10);
            // else
            // end of test code

            placer = board.getSpace(
                    (int)(Utils.random() * Board.BoardXSize),
                    (int)(Utils.random() * Board.BoardYSize));
            // Do not place token on occupied board space
            if (placer.isOccupied()) continue;
            // We cannot place new token next to an existing placed tokens
            boolean isplacerok = true;
            List<BoardSpace> neighborlist = board.allNeighbors(placer);
            BoardSpace oneneighbor;
            ListIterator<BoardSpace> neighbors =
                    new ListIterator<BoardSpace>(neighborlist);
            while ((oneneighbor = neighbors.getNext()) != null) {
                // If this place is occupied, then we cannot place token at
                // placer.
                // System.out.println("Testing loc: [" + oneneighbor.getCol() + ", " + oneneighbor.getRow() + "]");
                if (oneneighbor.isOccupied()) isplacerok = false; }
            if (isplacerok) {
                // We can occupy placer
                placer.setOccupied();
                successn++;
                placedtokens.add(new Token(placer.getCol(), placer.getRow()));
                // System.out.println("Initial token placed at [" + placer.getCol() + ", " + placer.getRow() + "]");
            } // if isplacerok
        } // for attemptn
        // Create all remaining Tokens
        NTokens = (Board.BoardXSize * Board.BoardYSize) - placedtokens.length();
        AllRemainingTokens = new Token[NTokens];
        NextToken = 0;
        int tokencount = 0; // Number of tokens on AllRemainingTokens so far
        for (int col = 0; col < Board.BoardXSize; col++)
            for (int row = 0; row < Board.BoardYSize; row++) {
                // Only add tokens which are not on placedtokens
                if (! board.getSpace(col, row).isOccupied()) {
                    Token newtoken = new Token(col, row);
                    AllRemainingTokens[tokencount++] = newtoken;
                }
            }
//        // Sanity check
//        if (tokencount != AllRemainingTokens.length) {
//            System.out.println("Another bozo mistake in AllTokens()");
//            System.exit(1); }
        // Now shuffle them.
        shuffleTokens();
        print();
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

    public Token find(String name) {
        String nameuc = name.toUpperCase();
        for (int tokenn = 0; tokenn < NTokens; tokenn++) {
            if (AllRemainingTokens[tokenn].getName().toUpperCase().
                    equals(nameuc))
                return AllRemainingTokens[tokenn];
        }
        return null;
    }

    private void shuffleTokens() {
        for (int tokenn = 0; tokenn < NTokens; tokenn++) {
            // Exchange [tokenn] with a random one.
            int rand = (int)(Utils.random() * NTokens);
            Token swapvalue = AllRemainingTokens[tokenn];
            AllRemainingTokens[tokenn] = AllRemainingTokens[rand];
            AllRemainingTokens[rand] = swapvalue;
        }
        if (BOGlobals.Cheat) {
            // For testing, we cheat by placing certain tokens at top
            exchangeTokens(new Token(4, 2), 1);
            exchangeTokens(new Token(4, 3), 2);
            exchangeTokens(new Token(4, 4), 3);
            exchangeTokens(new Token(4, 5), 4);
            exchangeTokens(new Token(4, 6), 5);
            exchangeTokens(new Token(2, 4), 6);
            exchangeTokens(new Token(3, 4), 7);
            exchangeTokens(new Token(5, 4), 8);
            exchangeTokens(new Token(6, 4), 9);
        }
    }

    private void exchangeTokens(Token findtoken, int loc) {
        for (int tokenn = 0; tokenn < NTokens; tokenn++) {
            if (AllRemainingTokens[tokenn].getCol() == findtoken.getCol() &&
                    AllRemainingTokens[tokenn].getRow() == findtoken.getRow()) {
                Token swaptoken = AllRemainingTokens[tokenn];
                AllRemainingTokens[tokenn] = AllRemainingTokens[loc];
                AllRemainingTokens[loc] = swaptoken;
            }
        } // end for tokenn
    } // end exchangeTokens()

    public Token takeNextToken() {
        // Removes the next token on AllRemainingTokens and returns it.
        // System.out.println("Taking token #" + NextToken);
        if (NextToken >= NTokens) {
            PlayGameAct.inst().LblMessage1.setText("An error has occured.");
            PlayGameAct.inst().LblMessage2.setText("The bag of tokens has run out.");
            BOGlobals.EndOfGameOption = true;
            return null;
        }
        Token result = AllRemainingTokens[NextToken++];
        // System.out.println("Result = " + result.toString());
        return result;
    }

    public void print() {
        System.out.println("");
        System.out.println("AllTokens: NTokens = " +
                NTokens + ", NextToken = " + NextToken);
        for (int i = 0; i < NTokens; i++)
            System.out.println("  [" + i + "]: " + AllRemainingTokens[i].toString());
    }

} // class AllTokens
