package tmw.sept22buyout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DisplayLogAct extends AppCompatActivity {

    private static final String TAG = DisplayLogAct.class.getSimpleName();
    TextView textOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Entered DisplayLog.onCreate()");
        setContentView(R.layout.activity_display_log);

        textOutput = (TextView) findViewById(R.id.textView);

        String output = "";
        output += "ACTIVITY LOG\n\n";
        ActionRecord record;
        ListIterator<ActionRecord> logiter =
                new ListIterator<ActionRecord>(ActionLog.inst().getLog());
        while ((record = logiter.getNext()) != null)
            output += record.toString() + "\n";
        textOutput.setText(output);
    } // end onCreate()

    public void doneBtnCB(View view) {
        Intent intent = new Intent(this, PlayGameAct.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }

}
