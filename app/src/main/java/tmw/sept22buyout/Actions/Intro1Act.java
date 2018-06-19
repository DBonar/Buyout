package tmw.sept22buyout.Actions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.HashMap;
import java.util.Map;

import tmw.sept22buyout.R;

public class Intro1Act extends AppCompatActivity {

    private static final String TAG = Intro1Act.class.getSimpleName();

    public static final String msgNPlayers = "tmw.Buyout.NumberPlayers";
    public static final String msgNMachines = "tmw.Buyout.NumberMachines";

    Map<Integer,Button> humanButtons = new HashMap<Integer,Button>();
    Map<Integer,Button> machineButtons = new HashMap<Integer, Button>();
    int numHumans = -1;
    int numMachines = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro1);

        Log.d(TAG, "Log is working");

        // Set up button arrays
        for (int i = 0; i <= 6; i++) {
            int hResID = getResources().getIdentifier("HButton" + Integer.toString(i),
                                                       "id", getPackageName() );
            humanButtons.put( hResID, (Button) findViewById(hResID) );
            int mResID = getResources().getIdentifier("MButton" + Integer.toString(i),
                    "id", getPackageName() );
            machineButtons.put( mResID, (Button) findViewById(mResID) );
        }

        // Default state, 1 human and 2 machine players
        int hResID = getResources().getIdentifier("HButton" + Integer.toString(1),
                "id", getPackageName() );
        humanButtons.get(hResID).performClick();
        int mResID = getResources().getIdentifier("MButton" + Integer.toString(2),
                "id", getPackageName() );
        machineButtons.get(mResID).performClick();
    }

    public void buttonClick(View view) {
        Button but = (Button) view;
        int resID = but.getId();
        if (humanButtons.containsKey(resID)) {
            numHumans = toInt(but.getText().toString());
            but.setActivated(true);
            for (Object value : humanButtons.values()) {
                Button trialBut = (Button) value;
                if (   (trialBut.getId() != resID)
                    && (trialBut.isActivated())      ) {
                    trialBut.setActivated(false);
                }
            }
        } else if (machineButtons.containsKey(resID)) {
            numMachines = toInt(but.getText().toString());
            but.setActivated(true);
            for (Object value : machineButtons.values()) {
                Button trialBut = (Button) value;
                if (   (trialBut.getId() != resID)
                        && (trialBut.isActivated())      ) {
                    trialBut.setActivated(false);
                }
            }
        } else {
            // Really?  Someone configured a button wrong.
        }

        // Now we set the status of the Done button based on
        // the sum on numHumans and numMachines
        Button btn = (Button) findViewById(R.id.btnIntro1Done);
        if (   (numHumans+numMachines >= 3)
            && (numHumans+numMachines <= 6) ) {
            btn.setEnabled(true);
        } else {
            btn.setEnabled(false);
        }
    }


    public void doneClicked(View view) {
        int num = numHumans + numMachines;
        if (   (num >= 3)
            && (num <= 6) ) {
            Intent intent = new Intent(this, Intro2Act.class);
            // intent.putExtra(strNPlayers, Integer.toString(nplayers));
            intent.putExtra(msgNPlayers, num);
            intent.putExtra(msgNMachines, numMachines);
            startActivity(intent);
        }
    }

    int toInt(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            if (arg.length() < 1) return 0;
            else return -1000000;
        }
    }

    @Override
    public void onBackPressed() {
    }

}
