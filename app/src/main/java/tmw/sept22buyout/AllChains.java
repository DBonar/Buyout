package tmw.sept22buyout;
//
// AllChains.java
//
// A list of all bank Chains, and various access fns.
//

public class AllChains {

    private static AllChains Instance = null;
    private LList<Chain> AllBankChains;

    protected AllChains() {
        AllBankChains = new LList<Chain>();
        // Note that chain list will come out in the opposite sequence that
        // they are added (since we add to the beginning of a list).
        AllBankChains.add(new Chain("Stearns",  Chain.BankClass.Investment,
                BOGlobals.ClrInvChain2));
        AllBankChains.add(new Chain("Peoples",  Chain.BankClass.Investment,
                BOGlobals.ClrInvChain1));
        AllBankChains.add(new Chain("NatWest",  Chain.BankClass.SnL,
                BOGlobals.ClrSnLChain3));
        AllBankChains.add(new Chain("Morgan",   Chain.BankClass.SnL,
                BOGlobals.ClrSnLChain2));
        AllBankChains.add(new Chain("Fleet",    Chain.BankClass.SnL,
                BOGlobals.ClrSnLChain1));
        AllBankChains.add(new Chain("Downtown", Chain.BankClass.Community,
                BOGlobals.ClrCommChain2));
        AllBankChains.add(new Chain("Chemical", Chain.BankClass.Community,
                BOGlobals.ClrCommChain1));
    }

    static public AllChains instance() {
        if (Instance == null) Instance = new AllChains();
        return Instance;
    }

    public LList<Chain> getAllChains() { return AllBankChains; }
    public int nChains() { return AllBankChains.length(); }

    public LList<Chain> allUnplacedChains() {
        LList<Chain> result = new LList<Chain>();
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllBankChains);
        while ((onechain = chains.getNext()) != null) {
            if (! onechain.isOnBoard()) result.append(onechain);
        }
        return result;
    }

    public LList<Chain> allPlacedChains() {
        LList<Chain> result = new LList<Chain>();
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllBankChains);
        while ((onechain = chains.getNext()) != null) {
            if (onechain.isOnBoard()) result.append(onechain);
        }
        return result;
    }

    public Chain findabbr(String chainnameabbr) {
        // Find a chain whose name looks similar to chainnameabbr.
        // For now, we just compare the first letter (caselessly).
        if (chainnameabbr.length() < 1) return null;
        String compname = chainnameabbr.substring(0, 1).toUpperCase();
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllBankChains);
        while ((onechain = chains.getNext()) != null) {
            if (compname.equals(onechain.
                    getName().substring(0, 1).toUpperCase()))
                return onechain;
        }
        return null;
    }

    public boolean isAllOnBoardChainsSafe() {
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllBankChains);
        while ((onechain = chains.getNext()) != null) {
            if (onechain.getBoardCount() > 0 &&
                    onechain.getBoardCount() < Chain.MinSafeChainSize)
                return false;
        }
        return true;
    }

//    public void print() {
//        Chain onechain;
//        ListIterator<Chain> chains = new ListIterator<Chain>(AllBankChains);
//        while ((onechain = chains.getNext()) != null)
//            System.out.println(onechain.toFullString());
//    }

}
