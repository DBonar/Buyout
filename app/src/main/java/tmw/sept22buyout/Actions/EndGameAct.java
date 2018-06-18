package tmw.sept22buyout.Actions;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;

        import java.util.Iterator;

        import tmw.sept22buyout.ActionLog;
        import tmw.sept22buyout.ActionRecord;
        import tmw.sept22buyout.Chain;
        import tmw.sept22buyout.Chains;
        import tmw.sept22buyout.ListIterator;
        import tmw.sept22buyout.Player;
        import tmw.sept22buyout.Players;
        import tmw.sept22buyout.R;
        import tmw.sept22buyout.StockShares;

public class EndGameAct extends AppCompatActivity {

    private static final String TAG = EndGameAct.class.getSimpleName();
    TextView textOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Entered EndGameAct.onCreate()");
        setContentView(R.layout.activity_end_game);

        textOutput = (TextView) findViewById(R.id.textView);

        textOutput.setText("This is a test of the endgame.");
        endOfGame();
    }

    protected void endOfGame() {
        String output = "";
        output += "THE GAME IS OVER!" + "\n";
        output += "These are the last few moves:\n";

        ActionRecord record;
        ListIterator<ActionRecord> logiter =
                new ListIterator<ActionRecord>(ActionLog.inst().getLog());
        while ((record = logiter.getNext()) != null)
            output += record.toString() + "\n";
        output += "These are the end-game bonuses:\n";

        // Pay the shareholder bonuses for all chains
        Iterator<Chain> chainiter = Chains.instance().allPlacedChains().iterator();
        while (chainiter.hasNext()) {
            output += chainiter.next().payShareholderBonuses(null);
        }

        // Write log to output.
        // Sell each players stock
        Players allplayers = Players.instance();
        for (int playern = 0; playern < allplayers.length(); playern++) {
            Player player = allplayers.getPlayerN(playern);
            output += "\n";
            output += "Player #" + (playern+1) + ": " + player.getPlayerName() + "\n";

            Iterator<StockShares> stockpile = player.getOwnedStock().iterator();
            while (stockpile.hasNext()) {
                StockShares oneshareset = stockpile.next();
                if (oneshareset.getNShares() != 0) {
                    int stockvalue = oneshareset.getChain().getPricePerShare() *
                            oneshareset.getNShares();
                    output += "    Sells " + oneshareset.getNShares() + " of " +
                            oneshareset.getChain().toStringWClass() + " for $" + stockvalue
                            + "\n";
                    player.sellStock(oneshareset.getChain(),
                            oneshareset.getNShares());
                } // if oneshareset
            } // while oneshareset
            output += "    Final total = $" + player.getMoney() + "\n";
        } // for playern

        // Find the winner(s)
        Player bestplayer = null;
        Boolean tiedgame = false;
        for (int playern = 0; playern < allplayers.length(); playern++) {
            Player theplayer = allplayers.getPlayerN(playern);
            if (bestplayer != null && theplayer.getMoney() == bestplayer.getMoney())
                tiedgame = true;
            if (bestplayer == null || theplayer.getMoney() > bestplayer.getMoney()) {
                bestplayer = theplayer;
                tiedgame = false;
            }
        }
        if (! tiedgame) output += "\n!! " + bestplayer.getPlayerName().toUpperCase() +
                " IS THE WINNER !!\n";
        else output += "\nThere is no winner.  The top score is tied at " +
                bestplayer.getMoney() + "\n";
        // Now print the final scores
        textOutput.setText(output);
    } // void endOfGame()


    public void doneBtnCB(View view) {
        // You cannot easily "exit" a program on an Android, so it is
        // better not to try.
    }

    @Override
    public void onBackPressed() {
    }

}
