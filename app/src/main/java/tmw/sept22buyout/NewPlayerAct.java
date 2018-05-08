package tmw.sept22buyout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class NewPlayerAct extends AppCompatActivity {

    private static final String TAG = NewPlayerAct.class.getSimpleName();
    TextView wLblPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "NewPlayerAct.onCreate() has started");
        setContentView(R.layout.activity_new_player);

        wLblPlayerName = (TextView) findViewById(R.id.lblPlayerName);
        String playername = AllPlayers.instance().firstPlayer().getPlayerName();
        wLblPlayerName.setText("It is " + playername + "'s turn!");
    }

//    public void btnDoneClicked(View view) {
//        if (BOGlobals.CurrentPlayer.isMachine()) {
//            // This is for the case when the very first move is made by a machine
//            Probably we need to initialize the system before jumping to token selection
//            BOGlobals.CurrentPlayer.beginTokenSelection();
//        }
//        else {
//            Intent intent = new Intent(this, PlayGameAct.class);
//            startActivity(intent);
//        }
//    }

    public void btnDoneClicked(View view) {
        Intent intent = new Intent(this, PlayGameAct.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

}
