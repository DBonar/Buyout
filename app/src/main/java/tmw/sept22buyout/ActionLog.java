package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// ActionLog.java
//
// A sequenced list of all actions taken by the players.
//

public class ActionLog {

    private static ActionLog Instance = null;
    private LList<ActionRecord> Log;

    protected ActionLog() { // This is a singleton class. No public constructor
        Log = new LList<ActionRecord>();
    }

    public static ActionLog instance() {
        if (Instance == null) Instance = new ActionLog();
        return Instance;
    }

    public static ActionLog inst() {
        // just an abbreviation for instance()
        return instance();
    }

    public void add(Player newplayer, String newaction) {
        // We always/only add to the end of the ActionLog.
        Log.append(new ActionRecord(newplayer, newplayer, newaction));
    }

    public void add(Player playerturn, Player theplayer, String newaction) {
        Log.append(new ActionRecord(playerturn, theplayer, newaction));
    }

    public LList<ActionRecord> getLog() { return Log; }

    // public ActionRecord getRecordN(int neededrec) {
    // 	return(Log.find(neededrec));
    // }

    public void prune(Player player) {
        // Removes all actions performed on player's turn from the front of the
        // list.
        boolean success = true;
        while (success) {
            if (Log.length() > 0 &&
                    Log.find(0).getPlayerTurn() == player) Log.takeFirst();
            else success = false;
        }
    }

}


