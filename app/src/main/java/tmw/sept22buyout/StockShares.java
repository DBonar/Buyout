package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/15/2017.
 */
//
// StockShares.java
//
// The amount of stock that is owned in the specified chain.
//

import java.io.*;

public class StockShares {

    private Chain SSChain;
    private int NShares = 0;

    public StockShares(Chain newchain, int newshares) {
        SSChain = newchain;
        NShares = newshares;
    }

    public Chain getChain() { return SSChain; }
    public int getNShares() { return NShares; }
    public void addStock(int addedstock) { NShares += addedstock; }
    public void subtractStock(int subdstock) {
        NShares -= subdstock;
    }

}


