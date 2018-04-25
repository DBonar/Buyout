package tmw.sept22buyout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class Intro1Act extends AppCompatActivity {

    private static final String TAG = Intro1Act.class.getSimpleName();

    public static final String msgNPlayers = "tmw.Buyout.NumberPlayers";
    public static final String msgNMachines = "tmw.Buyout.NumberMachines";
    EditText wEditNHumans;
    EditText wEditNMachines;
    TextView wLabelNPlayers;
    TextView wLabelIntro1Error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro1);

        Log.d(TAG, "Log is working");
        wEditNHumans = (EditText) findViewById(R.id.editNHumans);
        wEditNMachines = (EditText) findViewById(R.id.editNMachines);
        wLabelNPlayers = (TextView) findViewById(R.id.labelNPlayers);
        wLabelIntro1Error = (TextView) findViewById(R.id.labelIntro1Error);
        // EditText editText = (EditText) findViewById(R.id.editNHumans);
        wEditNHumans.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                editDone(view, hasFocus);
            }
        });
        wEditNMachines.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                editDone(view, hasFocus);
            }
        });
    }

    public void editDone(View view, boolean hasFocus) {
        int pval = toInt(wEditNHumans.getText().toString());
        int mval = toInt(wEditNMachines.getText().toString());
        int nplayers = pval + mval;
        wLabelNPlayers.setText("Total Players = " + nplayers + ".");
        if (nplayers >= 3 && nplayers <= 6)
            wLabelIntro1Error.setText("");
    }

    public void intro1DoneClicked(View view) {
        int pval = toInt(wEditNHumans.getText().toString());
        int mval = toInt(wEditNMachines.getText().toString());
        int nplayers = pval + mval;
        if (nplayers < 3) wLabelIntro1Error.setText("There must be at least 3 players.");
        else if (nplayers > 6) wLabelIntro1Error.setText("There cannot be more than 6 players.");
        else {
            Intent intent = new Intent(this, Intro2Act.class);
            // intent.putExtra(strNPlayers, Integer.toString(nplayers));
            intent.putExtra(msgNPlayers, nplayers);
            intent.putExtra(msgNMachines, mval);
            startActivity(intent);
        }
    }

    public int toInt(String arg) {
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
