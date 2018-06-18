package tmw.sept22buyout;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.List;

import tmw.sept22buyout.States.GameState;
import tmw.sept22buyout.States.PlayToken;
import tmw.sept22buyout.States.StartTurn;


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
    private Button           EndGameButton;
    private LinearLayout     mainDisplay;      // created and built in onCreate()
    private ConstraintLayout courtesyPanel;    // Hides the screen at start of a player's turn.
    private TextView         courtesyLabel;    // The text on the playerTurnPanel
    private Button           courtesyButton;   // The button to hide the panel
    private TextView         LblMessage;       // Used for instructional messages


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
        mainDisplay = (LinearLayout) findViewById(R.id.MainDisplay);
        courtesyPanel = (ConstraintLayout) findViewById(R.id.PlayerTurnPanel);
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
        vlparams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        vlparams.height = LinearLayout.LayoutParams.MATCH_PARENT;

        mainDisplay = new LinearLayout(this);
        mainDisplay.setOrientation(LinearLayout.VERTICAL);
        mainDisplay.setLayoutParams(vlparams);
        this.addContentView(mainDisplay, vlparams);

        // Create the board
        Board board = Board.initialize(9, 12, this);

        // The three main areas, each drawn by the associated class.
        mainDisplay.addView( board.buildLayout(this) );
        mainDisplay.addView( Players.instance().buildLayout(this, null) );
        mainDisplay.addView( Chains.instance().buildLayout(this, null) );

        // The space for displaying messages / instructions
        {
            LinearLayout.LayoutParams spacer_params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
            spacer_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            spacer_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
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
                            LinearLayout.LayoutParams.MATCH_PARENT);
            bottom_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            bottom_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            bottom_params.weight = 1;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(bottom_params);

            LinearLayout.LayoutParams btnparams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            btnparams.width = 0;
            btnparams.height = LinearLayout.LayoutParams.MATCH_PARENT;
            btnparams.weight = 1;

            ContinueButton = new Button(this);
            ContinueButton.setText("Continue");
            ContinueButton.setLayoutParams(btnparams);
            ContinueButton.setOnClickListener(this::meaninglessClick);
            ContinueButton.setMinHeight(1);
            ContinueButton.setMinimumHeight(1);
            row.addView(ContinueButton);

            EndGameButton = new Button(this);
            EndGameButton.setText("");
            EndGameButton.setLayoutParams(btnparams);
            EndGameButton.setOnClickListener(this::endGameClicked);
            EndGameButton.setMinHeight(1);
            EndGameButton.setMinimumHeight(1);
            row.addView(EndGameButton);

            mainDisplay.addView(row);
        }

    }

    public void refreshScreen(Player player) {
        Board.instance().updateHighlights(player);
        Players.instance().updatePlayerData(player);
        Chains.instance().updateLabels(player);

        if (BOGlobals.EndOfGameOption) EndGameButton.setText("End Game");
        else EndGameButton.setText("Show Log");
    } // end refreshScreen()

    public void showCourtesyPanel(Player player,
                                  String msg,
                                  View.OnClickListener buttonCallback) {
        courtesyLabel.setText("It is " + player.getPlayerName() + "'s " + msg);
        courtesyPanel.setVisibility(View.VISIBLE);
        mainDisplay.setVisibility(View.INVISIBLE);
        courtesyButton.setOnClickListener(buttonCallback);
        refreshScreen(player);
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
        gameLoop();
    }

    public void gameLoop() {
        GameState nextState = new StartTurn(this);
        nextState.enter(Players.instance().getPlayerN(0));
    }


    @Override
    public void onBackPressed() {}

    public void meaninglessClick(View view) {
    }



    //
    //  Left over bits
    //

    public void endGameClicked(View view) {
        if (BOGlobals.EndOfGameOption) {
            startEndGame();
        }
        else {
            Intent intent = new Intent(this, DisplayLogAct.class);
            startActivity(intent);
        }
    }


    public void startEndGame() {
        Intent intent = new Intent(this, EndGameAct.class);
        startActivity(intent);
    }



} // end chain PlayGameAct
