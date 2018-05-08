package tmw.sept22buyout;

import java.util.ArrayList;
import java.util.Collections;
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
    private String machineName(int n ) {
        return machineNames.get(n);
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

    public void inputTokenSelection() {
        if (BOGlobals.EndOfGameOption) {
            // PlayGameAct.inst().startEndGame();
            return;
        }
        PlacementStatus status = null;
        Token onetoken;
        ListIterator<Token> tokenlist = new ListIterator<Token>(getTokens());
        while ((onetoken = tokenlist.getNext()) != null) {
            status = onetoken.evaluateForPlacement();
            if (status.getStatus() != IllegalSafe && status.getStatus() != IllegalNoChain) {
                break;
            }
        }
        afterTokenSelection(onetoken);
    }

    public void inputSelectNewChain(Token tokentoplay, LList<Chain> unplacedchains) {
        afterSelectNewChain(unplacedchains.getFirst());
    }

    public void inputSelectBuyingChain(Token tokentoplay, LList<Chain> buychains,
                                       LList<Chain> sellchains) {
        afterSelectBuyingChain(buychains.getFirst());
    }

    public void inputUnloadStock(Token tokentoplay,
                                 Chain buychain,
                                 Chain sellchain,
                                 Player seller, int sharestounload) {
        seller.sellStock(sellchain, sharestounload);
        Player currentPlayer = AllPlayers.instance().firstPlayer();
        ActionLog.inst().add(currentPlayer,
                             seller,
                             "has sold " + sharestounload + " shares of " + sellchain.toString());
        currentPlayer.afterUnloadStock();
    }

    public void inputBuyStock() {
        // We will buy up to two shares of the cheapest stock on the board.
        // It is necessary to make sure that there are shares left, before buying.
        // It is necessary to make sure we can afford it, before buying.
        Chain bestchain = null;
        Chain onechain;
        ListIterator<Chain> chainlist =
                new ListIterator<Chain>(AllChains.instance().getAllChains());
        while ((onechain = chainlist.getNext()) != null) {
            if (onechain.isOnBoard()) {
                if (onechain.getAvailableStock() >= 1) {
                    int pps = onechain.getPricePerShare();
                    if (pps <= getMoney()) {
                        if (bestchain == null || pps <= bestchain.getPricePerShare())
                            bestchain = onechain;
                    }
                }
            }
        }
        // We typically buy two shares
        afterBuyStock(bestchain);
        if (bestchain != null && bestchain.getPricePerShare() <= getMoney())
            // We can afford a second share.
            afterBuyStock(bestchain);
        afterBuyStock(null);
    } // end inputBuyStock()

    public void inputTakeTile() {
        afterTakeTile();
    }

    public boolean afterTakeTile() {
        WhereAmIStack.inst().pop();
        return true;
    }

} // end Class MachinePlayer