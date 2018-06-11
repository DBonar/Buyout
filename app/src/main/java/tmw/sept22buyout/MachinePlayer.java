package tmw.sept22buyout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static tmw.sept22buyout.PlacementStatus.StatusType.*;

/**
 * Created by Tim Weinrich on 1/4/2018.
 *
 * MachinePlayer.java
 *
 * Makes moves for automatic players.
 */

public class MachinePlayer extends Player {


    static private List<String> machineNames = null;
    static void setupNames( int N, String[] list ) {
        // We got a list of names and we want to pick N of
        // them.  We will assume N < len(list) since we
        // know N <= 6 and we wrote the list.
        // Floyd algorithm with a permutation at the end
        Random rand = new Random();
        machineNames = new ArrayList<String>(N);
        int M = list.length;
        Boolean[] used = new Boolean[M];
        for (int i = 0; i < used.length; i++) used[i] = false;
        int in = 0;
        for (int im = M - N; im < M && in < N; im++) {
            int r = rand.nextInt(im + 1 );
            if (used[r]) r = im;
            machineNames.add(in++, list[r]);
            used[r] = true;
        }
        Collections.shuffle(machineNames);
    }


    // If N is the total number of machine players
    // n is the index number of this player n \in [1,N]
    protected MachinePlayer(int n) {
        setMachine();
        if (   (machineNames == null)
            || (n > machineNames.size())     ) { // not initialized
            setPlayerName( "Machine Player #" + Integer.toString(n));
        } else {
            setPlayerName(machineNames.get(n-1));
        }
    }


    //
    //  These are basic overrides.
    //  It would be better to have better logic.
    //

    @Override
    public Token selectTokenToPlay() {
        PlayGameAct.inst().log(getPlayerName() + " selecting token to play.");
        List<Token> tiles = getTokens();
        for (int i = 0; i < tiles.size(); i++) {
            Token tile = (Token) tiles.get(i);
            PlacementStatus status = tile.evaluateForPlacement();
            if ((status.getStatus() != IllegalSafe)
                    && (status.getStatus() != IllegalNoChain)) {
                return tile;
            }
        }
        return null;
    }

    @Override
    public List<Chain> buyStock() {
        PlayGameAct.inst().log(getPlayerName() + " selecting stocks to buy.");
        List<Chain> ret = new ArrayList<Chain>();

        // Buy 1 share of the first thing we can afford.
        // Buy 2 or 3 shares if we can
        int cash = getMoney();
        Iterator<Chain> it = AllChains.instance().allPlacedChains().iterator();
        while (it.hasNext()) {
            Chain possibleBuy = it.next();

            if (   (possibleBuy.getAvailableStock() >= 1)
                && (possibleBuy.getPricePerShare() <= cash) ) {
                ret.add(possibleBuy);
                if (   (possibleBuy.getAvailableStock() >= 2)
                    && (possibleBuy.getPricePerShare() * 2 <= cash) ) {
                    ret.add(possibleBuy);
                    if (   (possibleBuy.getAvailableStock() >= 3)
                         && (possibleBuy.getPricePerShare() * 3 <= cash) ) {
                        ret.add(possibleBuy);
                    }
                }
                return ret;
            }
        }
        // Nothing we could afford
        return ret;
    }

    @Override
    public Chain selectNewChain() {
        PlayGameAct.inst().log(getPlayerName() + " selecting chain to start.");
        List<Chain> chains = AllChains.instance().allUnplacedChains();
        int n = (int)(Utils.random() * chains.size());
        return chains.get(n);
    }

    @Override
    public Chain selectSurvivor(List<Chain> potentials) {
        return potentials.get(0);
    }

    @Override
    public Chain selectVictim(List<Chain> potentials) {
        return potentials.get(0);
    }

    @Override
    // The 3 integers are how many to (sell, trade, keep)
    public List<Integer> mergeActions(Chain victim, Chain survivor) {
        List<Integer> ret = new ArrayList<Integer>();
        ret.add( getChainNShares(victim) );  // sell everything
        ret.add(0);
        ret.add(0);
        return ret;
    }




} // end Class MachinePlayer