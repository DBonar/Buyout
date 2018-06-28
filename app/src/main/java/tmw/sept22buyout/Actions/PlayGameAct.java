package tmw.sept22buyout.Actions;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import tmw.sept22buyout.ActionLog;
import tmw.sept22buyout.ActionRecord;
import tmw.sept22buyout.BOGlobals;
import tmw.sept22buyout.Board;
import tmw.sept22buyout.Chains;
import tmw.sept22buyout.ListIterator;
import tmw.sept22buyout.Player;
import tmw.sept22buyout.Players;
import tmw.sept22buyout.R;
import tmw.sept22buyout.GameLogic.GameState;
import tmw.sept22buyout.GameLogic.StartTurn;


public class PlayGameAct extends AppCompatActivity {

    private static final String TAG = PlayGameAct.class.getSimpleName();

    // I'd rather this wasn't an explicit singleton, but
    // for now it is.  Instance is set in onCreate()
    // This seems to be used for logging messages.
    private static PlayGameAct Instance;
    public static PlayGameAct inst() { return Instance;  }

    // View items that get manipulated -- mostly different onClick
    // listeners get set -- as we move between different phases of play
    public Button            ContinueButton;
    private Button           LogButton;
    private FrameLayout      frame;
    private LinearLayout     mainDisplay;      // created and built in onCreate()
    private ConstraintLayout courtesyPanel;    // Hides the screen at start of a player's turn.
    private TextView         courtesyLabel;    // The text on the playerTurnPanel
    private Button           courtesyButton;   // The button to hide the panel
    private TextView         LblMessage;       // Used for instructional messages
    private PopupWindow      pop;              // used for the log

    //
    // Create the layout in the onCreate method.
    // Later, call refreshScreen as necessary to
    // update the state based on current data.
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "PlayGameAct.onCreate() has started");
        Instance = this;

        setContentView(R.layout.activity_play_game);
        frame = (FrameLayout) findViewById(R.id.PlayGameFrame);
        mainDisplay = (LinearLayout) findViewById(R.id.MainDisplay);
        courtesyPanel = (ConstraintLayout) findViewById(R.id.CourtesyPanel);
        courtesyLabel = (TextView) findViewById(R.id.PlayerNameLabel);
        courtesyButton = (Button) findViewById(R.id.StartTurnButton);

        // Create the display, a vertical stack of items.
        // 1 for the board
        // 1 for player's tiles and cash
        // 1 for the chains
        // 1 for the message
        // and a final row (details left to the caller)
        LinearLayout.LayoutParams vlparams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

        mainDisplay = new LinearLayout(this);
        mainDisplay.setOrientation(LinearLayout.VERTICAL);
        mainDisplay.setLayoutParams(vlparams);
        this.addContentView(mainDisplay, vlparams);

        // The three main areas, each drawn by the associated class.
        mainDisplay.addView( Board.instance().buildLayout(this) );
        mainDisplay.addView( Players.instance().buildLayout(this, null) );
        mainDisplay.addView( Chains.instance().buildLayout(this, null) );

        // The space for displaying messages / instructions
        {
            LinearLayout.LayoutParams spacer_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            spacer_params.weight = 2;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(spacer_params);
            LblMessage = new TextView(this);
            LblMessage.setText("Please click the token you wish to place.");
            row.addView(LblMessage);

            mainDisplay.addView(row);
        }

        // And space for the last row, a 'continue' button and an 'end game' button
        {
            LinearLayout.LayoutParams bottom_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            bottom_params.weight = 1;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(bottom_params);

            LinearLayout.LayoutParams btnparams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            btnparams.width = 0;
            btnparams.weight = 1;

            ContinueButton = new Button(this);
            ContinueButton.setText("Continue");
            ContinueButton.setLayoutParams(btnparams);
            ContinueButton.setOnClickListener(this::meaninglessClick);
            ContinueButton.setMinHeight(1);
            ContinueButton.setMinimumHeight(1);
            row.addView(ContinueButton);

            LogButton = new Button(this);
            LogButton.setText("Show Log");
            LogButton.setLayoutParams(btnparams);
            LogButton.setOnClickListener(this::logClicked);
            LogButton.setMinHeight(1);
            LogButton.setMinimumHeight(1);
            row.addView(LogButton);

            mainDisplay.addView(row);
        }

    }

    public void refreshScreen(Player player) {
        Board.instance().updateHighlights(player);
        Players.instance().updatePlayerData(player);
        Chains.instance().updateLabels(player);

        //if (BOGlobals.EndOfGameOption) LogButton.setText("End Game");
        //else LogButton.setText("Show Log");
    } // end refreshScreen()

    public void showCourtesyPanel(Player player,
                                  String msg,
                                  View.OnClickListener buttonCallback) {
        if (!player.isMachine()) {
            courtesyLabel.setText("It is " + player.getPlayerName() + "'s " + msg);
            courtesyPanel.setVisibility(View.VISIBLE);
            mainDisplay.setVisibility(View.INVISIBLE);
            courtesyButton.setOnClickListener(buttonCallback);
            refreshScreen(player);
        }
    }

    public void hideCourtesyPanel() {
        courtesyPanel.setVisibility(View.INVISIBLE);
        mainDisplay.setVisibility(View.VISIBLE);
    }


    public void msgSet(Player player, String msg) {
        LblMessage.setText(player.getPlayerName() + ": " + msg);
    }

    public void log(String msg) { Log.d(TAG, msg); }



    @Override
    public void onResume() {
        super.onResume();
        GameState nextState = new StartTurn(this);
        nextState.enter(Players.instance().getPlayerN(0));
    }



    @Override
    public void onBackPressed() {}

    public void meaninglessClick(View view) {
    }



    //
    //  Log panel
    //

    // Used for the lower right hand button.  Could be 'End Game' or 'Show Log'
    public void logClicked(View view) {
        if (BOGlobals.EndOfGameOption) {
            Intent intent = new Intent(this, EndGameAct.class);
            startActivity(intent);
        }
        else {
            // Hmm.  I want to get back to the game state I left.
            // Even if it is half-way through a compound action like
            // buying stocks.
            //Intent intent = new Intent(this, DisplayLogAct.class);
            //startActivity(intent);
            ScrollView log = new ScrollView(this );
            ScrollView.LayoutParams p1 = new ScrollView.LayoutParams(
                                      ScrollView.LayoutParams.MATCH_PARENT,
                                      ScrollView.LayoutParams.MATCH_PARENT );
            log.setLayoutParams(p1);

            LinearLayout layout = new LinearLayout(this );
            LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(
                                                   LinearLayout.LayoutParams.MATCH_PARENT,
                                                   LinearLayout.LayoutParams.WRAP_CONTENT );
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(p2);

            Button but = new Button(this );
            LinearLayout.LayoutParams p3 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT );
            but.setLayoutParams(p3);
            but.setText("Done");
            but.setOnClickListener(this::logDoneClick);
            layout.addView(but);

            TextView text = new TextView( this );
            LinearLayout.LayoutParams p4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );
            p4.topMargin = 20;
            text.setLayoutParams(p4);
            {
                String output = "";
                output += "ACTIVITY LOG\n\n";
                ActionRecord record;
                ListIterator<ActionRecord> logiter =
                        new ListIterator<ActionRecord>(ActionLog.inst().getLog());
                while ((record = logiter.getNext()) != null)
                    output += record.toString() + "\n";
                text.setText(output);
            }
            layout.addView(text);
            log.addView(layout);

            // I have to hid the main display because the background
            // of the popup window is transparent, so the main display
            // would show through.
            mainDisplay.setVisibility(View.INVISIBLE);
            pop = new PopupWindow(log,
                                  ConstraintLayout.LayoutParams.MATCH_PARENT,
                                  ConstraintLayout.LayoutParams.MATCH_PARENT );
            pop.showAtLocation(mainDisplay, Gravity.CENTER, 0, 0);
        }
    }

    public void logDoneClick(View view) {
        pop.dismiss();
        pop = null;
        mainDisplay.setVisibility(View.VISIBLE);
    }




} // end chain PlayGameAct
