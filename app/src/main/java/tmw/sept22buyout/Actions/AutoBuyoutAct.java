package tmw.sept22buyout.Actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import tmw.sept22buyout.Board;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.Players;
import tmw.sept22buyout.R;

public class AutoBuyoutAct extends AppCompatActivity {

    private static final String TAG = AutoBuyoutAct.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.automatic_start);

        Log.d(TAG, "Log is working");

        // These values should be read from the config or resources
        // Of course, ultimately we might want the names to come
        // from the different subclasses of MachinePlayer rather than
        // being random, but that's a later tweak.
        int numPlayers = 4;
        String[] humanNames = new String[0];
        String[] machineNames = new String[7];
        machineNames[0] = "Ultron";
        machineNames[1] = "Marvin";
        machineNames[2] = "Robby";
        machineNames[3] = "HAL";
        machineNames[4] = "T9000";
        machineNames[5] = "Skynet";
        machineNames[6] = "Wall-E";

        // Do the initialization and start the game.
        Players.instance(numPlayers,
                         numPlayers,
                         humanNames,
                         machineNames);
        Chains.instance();
        Board.initialize(9, 12);

        Intent intent = new Intent(this, PlayGameAct.class);
        startActivity(intent);
    }



}
