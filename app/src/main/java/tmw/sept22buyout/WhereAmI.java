package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/26/2017.
 */
// A Buyout game phase, along with any information to be retained.
// Used for determining what we are going while a user gives input.

public class WhereAmI {

    static enum PlayPhase { PlayToken, SelectNewChain, SelectBuyingChain, UnloadStock, BuyStock, TakeTile };

    private PlayPhase PhaseName;
    private Token PToken = null;
    private Chain PChain = null;
    private Chain PBuyChain = null;
    private List<Chain> PChainList = null;
    private List<Chain> PSellChains = null;
    private Player PPlayer = null;
    private Player PEndPlayer = null;
    private int PNShares = 0;
    //private List<Chain> PChainList = new List<Chain>();
    //private List<Chain> FinishedChains = new List<Chain>();
    //private int NTokensToBuy = 0;

    public WhereAmI(PlayPhase newphasename) {
        PhaseName = newphasename;
    }

    public PlayPhase getPlayPhase() { return PhaseName; }
    public Token getToken() { return PToken; }
    public void setToken(Token newtoken) { PToken = newtoken; }
    public Chain getChain() { return PChain; }
    public void setChain(Chain newchain) { PChain = newchain; }
    public Chain getBuyChain() { return PBuyChain; }
    public void setBuyChain(Chain newchain) { PBuyChain = newchain; }
    public List<Chain> getChainList() { return PChainList; }
    public void setChainList(List<Chain> newchainlist) { PChainList = newchainlist; }
    public List<Chain> getSellChains() { return PSellChains; }
    public void setSellChains(List<Chain> newsellchains) { PSellChains = newsellchains; }
    public Player getPlayer() { return PPlayer; }
    public void setPlayer(Player newplayer) { PPlayer = newplayer; }
    public Player getEndPlayer() { return PEndPlayer; }
    public void setEndPlayer(Player newendplayer) { PEndPlayer = newendplayer; }
    public int getNShares() { return PNShares; }
    public void setNShares(int newnshares) { PNShares = newnshares; }
    //public List<Chain> getChainList() { return PChainList; }
    //public void setChainList(List<Chain> newchainlist) { PChainList = newchainlist; }
    //public List<Chain> getFinishedChains() { return FinishedChains; }
    //public void setFinishedChains(List<Chain> newchainlist) { FinishedChains = newchainlist; }
    //public int getNTokensToBuy() { return NTokensToBuy; }
    //public void setNTokensToBuy(int newntokens) { NTokensToBuy = newntokens; }

}

