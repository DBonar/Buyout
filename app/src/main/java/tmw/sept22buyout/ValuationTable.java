package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// ValuationTable.java
//
// Values of stock shares given a BankClass and size of chain.  Also, bonus
// values.
//

public class ValuationTable {

    static private final int[] SizeIndexes = {
            // Where to look in arrays to find info on this size chain.
            // Example:  For a chain of size 12, you look in element 6.

            // 0   1   2   3   4   5   6   7   8   9  10
            -1, -1,  1,  2,  3,  4,  5,  5,  5,  5,  5,
            //  11  12  13  14  15  16  17  18  19  20
            6,  6,  6,  6,  6,  6,  6,  6,  6,  6,
            //  21  22  23  24  25  26  27  28  29  30
            7,  7,  7,  7,  7,  7,  7,  7,  7,  7,
            //  31  32  33  34  35  36  37  38  39  40
            8,  8,  8,  8,  8,  8,  8,  8,  8,  8,
            //  41
            9
    };

    // private final int[][] StockPrice = {
    // 	{ 0, 200, 300, 400, 500, 600, 700, 800, 900, 1000 },
    // 	{ 0, 300, 400, 500, 600, 700, 800, 900, 1000, 1100 },
    // 	{ 0, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200 }
    // };

    static private final int[][] StockPrice = {
            { 0, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000 },
            { 0, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500 },
            { 0, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000 }
    };

    // private final int[][][] MajorityBonus = {
    // 	{ 0, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000 },
    // 	{ 0, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000 },
    // 	{ 0, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 11000, 12000 }
    // };

    static private final int[][] MajorityBonus = {
            // (Minority bonuses are 1/2 the majority bonus.)
            { 0, 10000, 15000, 20000, 25000, 30000, 35000, 40000, 45000, 50000 },
            { 0, 15000, 20000, 25000, 30000, 35000, 40000, 45000, 50000, 55000 },
            { 0, 20000, 25000, 30000, 35000, 40000, 45000, 50000, 55000, 60000 }
    };

    static public int getPricePerShare(Chain.BankClass chainclass, int chainsize) {
        if (chainsize == 0) return 0;
        if (chainsize > 41) chainsize = 41;
        int priceindex = SizeIndexes[chainsize];
        if (priceindex < 1)
            PlayGameAct.inst().msgSet("Error in Valuation.getPricePerShare().");
        return StockPrice[chainclass.ordinal()][priceindex];
    }

    static public int getBonusPrice(Chain.BankClass chainclass,
                                    int chainsize,
                                    boolean isMajority) {
        if (chainsize == 0) {
            // System.out.println("Warning: Call to getBonusPrice() with zero-length chain.");
            return 0; }
        if (chainsize > 41) chainsize = 41;
        int priceindex = SizeIndexes[chainsize];
        if (priceindex < 1)
            PlayGameAct.inst().msgSet("Error in Valuation.getBonusPrice().");
        if (isMajority)
            return MajorityBonus[chainclass.ordinal()][priceindex];
            // (Minority bonuses are 1/2 the majority bonus.)
        else return MajorityBonus[chainclass.ordinal()][priceindex] / 2;
    }

}
