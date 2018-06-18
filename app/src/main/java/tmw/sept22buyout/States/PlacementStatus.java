package tmw.sept22buyout.States;

import tmw.sept22buyout.Chain;
import tmw.sept22buyout.LList;

/**
 * Created by Tim Weinrich on 11/29/2017.
 *
 * Tells what will happen if a token is placed on the board
 *
 */

public class PlacementStatus {
    public enum StatusType { IllegalSafe, IllegalNoChain, SimplePlacement, NewChain, Join, Merger };

    private StatusType PStatus = null;
    private Chain PChain = null;
    private LList<Chain> PBuyChains = null;
    private LList<Chain> PSellChains = null;

    public StatusType getStatus() { return PStatus; }
    public void setStatus(StatusType newstatus) { PStatus = newstatus; }

    // for the Join status
    public Chain getChain() { return PChain; }
    public void setChain(Chain newchain) { PChain = newchain; }

}
