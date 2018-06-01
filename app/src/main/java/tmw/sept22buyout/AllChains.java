package tmw.sept22buyout;
//
// AllChains.java
//
// A list of all bank Chains, and various access fns.
//

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private LList<Chain> getAllChains() { return AllBankChains; }
    private int nChains() { return AllBankChains.length(); }

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


    ChainButton[] buttons;
    TextView[] texts;
    public LinearLayout buildLayout(Context context,
                                    @Nullable View.OnClickListener chainCallback ) {
        LinearLayout ret = new LinearLayout(context);

        LinearLayout.LayoutParams overall_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        overall_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        overall_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        ret.setOrientation((LinearLayout.VERTICAL));
        ret.setLayoutParams(overall_params);

        LinearLayout.LayoutParams row_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        row_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        row_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        LinearLayout.LayoutParams label_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        label_params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        label_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        label_params.weight = 1;

        LinearLayout.LayoutParams wide_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        wide_params.width = 0;
        wide_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        wide_params.weight = 7;
        wide_params.leftMargin = 15;

        buttons = new ChainButton[nChains()];
        texts = new TextView[nChains()];
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllChains.instance().getAllChains());
        int chainn = 0;
        while ((onechain = chains.getNext()) != null) {
            LinearLayout chain_row = new LinearLayout(context);
            chain_row.setOrientation(LinearLayout.HORIZONTAL);
            chain_row.setLayoutParams(row_params);
            chain_row.setBaselineAligned(false);

            ChainButton chainbtn = new ChainButton(context, onechain);
            chainbtn.setText(onechain.toString());
            chainbtn.setLayoutParams(label_params);
            chainbtn.setIncludeFontPadding(false);
            chainbtn.setMinHeight(1);
            chainbtn.setMinimumHeight(1);
            chainbtn.setBackgroundColor(onechain.getChainColor());
            buttons[chainn] = chainbtn;

            if (chainCallback != null) {
                chainbtn.setOnClickListener(chainCallback);
            }
            chain_row.addView(chainbtn);

            TextView lblChainStatus = new TextView(context);
            lblChainStatus.setText("is not on board.\n second line");
            lblChainStatus.setLayoutParams(wide_params);
            texts[chainn++] = lblChainStatus;
            chain_row.addView(lblChainStatus);

            ret.addView(chain_row);
        }

        return ret;
    }

    public void updateLabels(Player player) {
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllChains.instance().getAllChains());
        int chainn = 0;
        while ((onechain = chains.getNext()) != null) {
            texts[chainn++].setText(onechain.toFullString(player));
        }
    }

    public void updateCallbacks(View.OnClickListener chainCallback) {
        Chain onechain;
        ListIterator<Chain> chains = new ListIterator<Chain>(AllChains.instance().getAllChains());
        int chainn = 0;
        while ((onechain = chains.getNext()) != null) {
            buttons[chainn++].setOnClickListener(chainCallback);
        }
    }
}
