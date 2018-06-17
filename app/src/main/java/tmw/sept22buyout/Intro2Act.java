package tmw.sept22buyout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class Intro2Act extends AppCompatActivity {

    private static Intro2Act Instance = null;
    private static final String TAG = Intro2Act.class.getSimpleName();

    static String HumanNames[];
    static int NPlayers;
    static int NHumans;
    static int NMachines;

    EditText wEditPlayer1Name;
    EditText wEditPlayer2Name;
    EditText wEditPlayer3Name;
    EditText wEditPlayer4Name;
    EditText wEditPlayer5Name;
    EditText wEditPlayer6Name;
    TextView wLblAskPlayer1Name;
    TextView wLblAskPlayer2Name;
    TextView wLblAskPlayer3Name;
    TextView wLblAskPlayer4Name;
    TextView wLblAskPlayer5Name;
    TextView wLblAskPlayer6Name;
    TextView wLblIntro2Error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro2);

        Instance = this;
        Log.d(TAG, "Log is working today. ");
        wEditPlayer1Name = (EditText) findViewById(R.id.editPlayer1Name);
        wEditPlayer2Name = (EditText) findViewById(R.id.editPlayer2Name);
        wEditPlayer3Name = (EditText) findViewById(R.id.editPlayer3Name);
        wEditPlayer4Name = (EditText) findViewById(R.id.editPlayer4Name);
        wEditPlayer5Name = (EditText) findViewById(R.id.editPlayer5Name);
        wEditPlayer6Name = (EditText) findViewById(R.id.editPlayer6Name);
        wLblAskPlayer1Name = (TextView) findViewById(R.id.labelAskPlayer1Name);
        wLblAskPlayer2Name = (TextView) findViewById(R.id.labelAskPlayer2Name);
        wLblAskPlayer3Name = (TextView) findViewById(R.id.labelAskPlayer3Name);
        wLblAskPlayer4Name = (TextView) findViewById(R.id.labelAskPlayer4Name);
        wLblAskPlayer5Name = (TextView) findViewById(R.id.labelAskPlayer5Name);
        wLblAskPlayer6Name = (TextView) findViewById(R.id.labelAskPlayer6Name);
        wLblIntro2Error = (TextView) findViewById(R.id.labelIntro2Error);
        Intent intent = getIntent();

        NPlayers = intent.getIntExtra(Intro1Act.msgNPlayers, 3);
        NMachines = intent.getIntExtra(Intro1Act.msgNMachines, 0);
        NHumans = NPlayers - NMachines;
        if (NHumans < 6) {
            wLblAskPlayer6Name.setVisibility(View.INVISIBLE);
            wEditPlayer6Name.setVisibility(View.INVISIBLE);
        }
        if (NHumans < 5) {
            wLblAskPlayer5Name.setVisibility(View.INVISIBLE);
            wEditPlayer5Name.setVisibility(View.INVISIBLE);
        }
        if (NHumans < 4) {
            wLblAskPlayer4Name.setVisibility(View.INVISIBLE);
            wEditPlayer4Name.setVisibility(View.INVISIBLE);
        }
        if (NHumans < 3) {
            wLblAskPlayer3Name.setVisibility(View.INVISIBLE);
            wEditPlayer3Name.setVisibility(View.INVISIBLE);
        }
        if (NHumans < 2) {
            wLblAskPlayer2Name.setVisibility(View.INVISIBLE);
            wEditPlayer2Name.setVisibility(View.INVISIBLE);
        }
        if (NHumans < 1) {
            wLblAskPlayer1Name.setVisibility(View.INVISIBLE);
            wEditPlayer1Name.setVisibility(View.INVISIBLE);
        }

    }

    public static Intro2Act inst() {
        if (Instance == null) Instance = new Intro2Act();
        return Instance;
    }

    public void startGameClicked(View view) {
        makePlayerNameArray();
        if (checkPlayerNames()) {
            Utils.FakeRandomNumbers = true;
            // Initialize two statics
            Players.instance(Intro2Act.NPlayers,
                             Intro2Act.NMachines,
                             getResources().getStringArray(R.array.machine_names));
            Chains.instance();  // initialize the chains.
            Intent intent = new Intent(this, PlayGameAct.class);
            startActivity(intent);
        }
    }


    protected void makePlayerNameArray() {
        HumanNames = new String[NHumans];
        if (NHumans > 0) HumanNames[0] = wEditPlayer1Name.getText().toString().trim();
        if (NHumans > 1) HumanNames[1] = wEditPlayer2Name.getText().toString().trim();
        if (NHumans > 2) HumanNames[2] = wEditPlayer3Name.getText().toString().trim();
        if (NHumans > 3) HumanNames[3] = wEditPlayer4Name.getText().toString().trim();
        if (NHumans > 4) HumanNames[4] = wEditPlayer5Name.getText().toString().trim();
        if (NHumans > 5) HumanNames[5] = wEditPlayer6Name.getText().toString().trim();
    }

    protected boolean checkPlayerNames() {
        // Check for empty or dumplicate names.
        for (int playern = 0; playern < NHumans; playern++) {
            if (HumanNames[playern].length() < 1) {
                wLblIntro2Error.setText("Every human player must have a name.");
                return false;
            }
            for (int otherplayer = playern + 1; otherplayer < NHumans; otherplayer++) {
                if (HumanNames[playern].equals(HumanNames[otherplayer])) {
                    wLblIntro2Error.setText("No two people may have the same name.");
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onBackPressed() {
    }

}