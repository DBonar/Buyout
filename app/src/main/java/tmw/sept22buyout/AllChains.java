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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllChains {

    private static AllChains Instance = null;
    private List<Chain> AllBankChains;

    protected AllChains() {
        AllBankChains = new ArrayList<Chain>();
        // Note that chain list will come out in the opposite sequence that
        // they are added (since we add to the beginning of a list).
        AllBankChains.add(new Chain("Chemical", Chain.BankClass.Community,
                BOGlobals.ClrCommChain1));
        AllBankChains.add(new Chain("Downtown", Chain.BankClass.Community,
                BOGlobals.ClrCommChain2));
        AllBankChains.add(new Chain("Fleet",    Chain.BankClass.SnL,
                BOGlobals.ClrSnLChain1));
        AllBankChains.add(new Chain("Morgan",   Chain.BankClass.SnL,
                BOGlobals.ClrSnLChain2));
        AllBankChains.add(new Chain("NatWest",  Chain.BankClass.SnL,
                BOGlobals.ClrSnLChain3));
        AllBankChains.add(new Chain("Peoples",  Chain.BankClass.Investment,
                BOGlobals.ClrInvChain1));
        AllBankChains.add(new Chain("Stearns",  Chain.BankClass.Investment,
                BOGlobals.ClrInvChain2));
    }

    static public AllChains instance() {
        if (Instance == null) Instance = new AllChains();
        return Instance;
    }

    private List<Chain> getAllChains() { return AllBankChains; }
    private int nChains() { return AllBankChains.size(); }

    public List<Chain> allUnplacedChains() {
        List<Chain> result = new ArrayList<Chain>();
        Iterator<Chain> chains = AllBankChains.iterator();
        while (chains.hasNext()) {
            Chain chain = chains.next();
            if (! chain.isOnBoard()) result.add(chain);
        }
        return result;
    }

    public List<Chain> allPlacedChains() {
        List<Chain> result = new ArrayList<Chain>();
        Iterator<Chain> chains = AllBankChains.iterator();
        while (chains.hasNext()) {
            Chain chain = chains.next();
            if (chain.isOnBoard()) result.add(chain);
        }
        return result;
    }


    public boolean isAllOnBoardChainsSafe() {
        Iterator<Chain> chains = AllBankChains.iterator();
        while (chains.hasNext()) {
            Chain chain = chains.next();
            if (   chain.getBoardCount() > 0
                && chain.getBoardCount() < Chain.MinSafeChainSize)
                return false;
        }
        return true;
    }


    private ChainButton[] buttons;
    private TextView[] texts;
    public LinearLayout buildLayout(Context context,
                                    @Nullable View.OnClickListener chainCallback ) {
        LinearLayout ret = new LinearLayout(context);

        LinearLayout.LayoutParams overall_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        overall_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        overall_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        overall_params.topMargin = 2;
        overall_params.bottomMargin = 10;
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
        for (int i = 0; i < AllBankChains.size(); i++) {
            Chain chain = AllBankChains.get(i);

            LinearLayout chain_row = new LinearLayout(context);
            chain_row.setOrientation(LinearLayout.HORIZONTAL);
            chain_row.setLayoutParams(row_params);
            chain_row.setBaselineAligned(false);

            ChainButton chainbtn = new ChainButton(context, chain);
            chainbtn.setText(chain.toString());
            chainbtn.setLayoutParams(label_params);
            chainbtn.setIncludeFontPadding(false);
            chainbtn.setMinHeight(1);
            chainbtn.setMinimumHeight(1);
            chainbtn.setBackgroundColor(chain.getChainColor());
            buttons[i] = chainbtn;

            if (chainCallback != null) {
                chainbtn.setOnClickListener(chainCallback);
            }
            chain_row.addView(chainbtn);

            TextView lblChainStatus = new TextView(context);
            lblChainStatus.setText("is not on board.\n second line");
            lblChainStatus.setLayoutParams(wide_params);
            texts[i] = lblChainStatus;
            chain_row.addView(lblChainStatus);

            ret.addView(chain_row);
        }

        return ret;
    }

    public void updateLabels(Player player) {
        for (int i = 0; i < AllBankChains.size(); i++) {
            texts[i].setText(AllBankChains.get(i).toFullString(player));
        }
    }

    public void updateCallbacks(View.OnClickListener chainCallback) {
        for (int i = 0; i < AllBankChains.size(); i++) {
            buttons[i].setOnClickListener(chainCallback);
        }
    }
}
