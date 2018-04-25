package tmw.sept22buyout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NewPlayerSellAct extends AppCompatActivity {

    private static final String Tag = NewPlayerSellAct.class.getSimpleName();
    TextView wLblPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Tag, "NewPlayerSellAct.onCreate() has started");
        setContentView(R.layout.activity_new_player_sell2);

        wLblPlayerName = (TextView) findViewById(R.id.lblPlayerName);
        WhereAmIStack stack = WhereAmIStack.inst();
        WhereAmI wai = WhereAmIStack.inst().look();
        String playername = wai.getPlayer().getPlayerName();
        String chain = wai.getChain().getName();
        int nshares = wai.getNShares();
        wLblPlayerName.setText("It is time for " + playername + " to dispose of " + nshares +
                " shares of stock in " + chain);
    }

    public void btnDoneClicked(View view) {
        // This is the way it should be:
        //    Intent intent = new Intent(this, SellPlayerStockAct.class);
        // This is for testing:
        Intent intent = new Intent(this, MergerSellAct.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

}
