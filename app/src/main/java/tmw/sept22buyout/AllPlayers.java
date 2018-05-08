package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/10/2017.
 */

//
// AllPlayers.java
//
// All players of Buyout, accessible as a cycled list and in other ways.
// This is a Singleton class -
// Make an AllPlayers instance by calling AllPlayers.instance(int, int) and
// refer to it with AllPlayers.instance()
//

import java.lang.Math;

public class AllPlayers {

    private static AllPlayers Instance = null;
    private Player Players[];
    private int NPlayers;
    private int n;

    private AllPlayers() {}  // default constructor disallowed.

    protected AllPlayers(int nplayers, int nmachines, String[] machineNames) {
        // System.out.println("Entered: AllPlayers(" + nplayers + ", " + nmachines + ")");
        MachinePlayer.setupNames( nmachines, machineNames );
        Players = new Player[nplayers];
        NPlayers = nplayers;
        for (int playern = 0; playern < nplayers; playern++) {
            Player newplayer = null;
            if (playern < nplayers - nmachines) {
                // Player is human
                newplayer = new Player();
                newplayer.setPlayerName(Intro2Act.HumanNames[playern]);
            } // if playern
            else {
                // Player is machine
                newplayer = new MachinePlayer(1 + playern - nplayers + nmachines);
            } // else
            Players[playern] = newplayer;
        }
        // Shuffle the players and assign sequence
        // For historical reasons, I assume the sequence on the array must
        // must be the same as the sequence of moves.  This may or may not be
        // true.  tmw, 6/2017
        for (int playern = 0; playern < nplayers; playern++) {
            // Exchange [playern] with a random succeeding one.
            int rand = (int)(Utils.random() * (nplayers - playern));
            int randindex = playern + rand;
            if (randindex != playern) {
                // Swap playern with randindex
                Player swapvalue = Players[playern];
                Players[playern] = Players[randindex];
                Players[randindex] = swapvalue;
            } // if randindex
            if (playern != 0)
                Players[playern - 1].setNextPlayer(Players[playern]);
            if (playern == nplayers - 1)
                Players[playern].setNextPlayer(Players[0]);
        } // for playern = 0
        n = 0;  // i.e. the first player is the first player
    } // AllPlayers()

    public static AllPlayers instance(int nplayers, int nmachines, String[] machineNames) {
        if (Instance == null) Instance = new AllPlayers(nplayers, nmachines, machineNames);
        return Instance;
    }

    public static AllPlayers instance() {
        return Instance;
    }

    public Player getPlayerN(int arg) {
        if (arg < 0 || arg >= NPlayers) return null;
        return Players[arg];
    }

    public int length() {
        return NPlayers;
    }

    public Player firstPlayer() { return Players[n]; }
    public void nextPlayer() { n = n + 1; if (n == NPlayers) n = 0;}

}
