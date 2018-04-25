package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 11/29/2017.
 *
 * Tells what will happen if a token is placed on the board
 *
 */

public class PlacementStatus {
    static enum StatusType { IllegalSafe, IllegalNoChain, SimplePlacement, NewChain, Join, Merger };

    private StatusType PStatus = null;
    private Chain PChain = null;
    private List<Chain> PBuyChains = null;
    private List<Chain> PSellChains = null;

    public StatusType getStatus() { return PStatus; }
    public void setStatus(StatusType newstatus) { PStatus = newstatus; }
    public Chain getChain() { return PChain; }
    public void setChain(Chain newchain) { PChain = newchain; }
    public List<Chain> getBuyChains() { return PBuyChains; }
    public void setBuyChains(List<Chain> newbuychains) { PBuyChains = newbuychains; }
    public List<Chain> getSellChains() { return PSellChains; }
    public void setSellChains(List<Chain> newsellchains) { PSellChains = newsellchains; }

}
