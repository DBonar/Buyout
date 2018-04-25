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

    private AllPlayers() {}  // default constructor disallowed.

    protected AllPlayers(int nplayers, int nmachines) {
        // System.out.println("Entered: AllPlayers(" + nplayers + ", " + nmachines + ")");
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
                newplayer = new MachinePlayer();
                newplayer.setMachine();
                newplayer.setPlayerName("Machine Player #" +
                        (playern - nplayers + nmachines + 1));
                // System.out.println("A machine player has been named " + newplayer.getPlayerName());
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
//        for (int playern = 0; playern < nplayers; playern++) {
//            System.out.println("Player #" + (playern + 1) + ": " + Players[playern].getPlayerName());
//        }
    } // AllPlayers()

    public static AllPlayers instance(int nplayers, int nmachines) {
        // There is one and only one AllPlayers instance.
        if (Instance == null) Instance = new AllPlayers(nplayers, nmachines);
        if (nplayers == Instance.NPlayers) return Instance;
        else PlayGameAct.inst().msgSet("Erroneous call to AllPlayers.instance(int, int)");
        return null;
    }

    public static AllPlayers instance() {
        if (Instance != null) return Instance;
        PlayGameAct.inst().msgSet("Erroneous call to AllPlayers.Instance()");
        return null;
    }

    public Player getPlayerN(int arg) {
        if (arg < 0 || arg >= NPlayers) return null;
        return Players[arg];
    }

    public int length() {
        return NPlayers;
    }

    public Player firstPlayer() { return Players[0]; }

    // public void chooseMachinePlayers(int nmachines) {
    // 	int machinesleft = nmachines;
    // 	int playersleft = length();
    // 	Player thisplayer = firstPlayer();
    // 	while (playersleft > 0 && machinesleft > 0) {
    // 	    // this player has a (machinesleft / playersleft) chance of
    // 	    // being a machine.
    // 	    int rand = (int)(Math.random() * playersleft);
    // 	    if (rand < machinesleft) {
    // 		// Player thisplayer is a machine
    // 		thisplayer.setMachine();
    // 		machinesleft--;
    // 	    }
    // 	    playersleft--;
    // 	    thisplayer = thisplayer.nextPlayer();
    // 	} // while nmachines
    // 	if (machinesleft != 0) {
    // 	    System.out.println("ChooseMachinesPlayers #1: " +
    // 			       "Tim screwed up his logic again.");
    // 	    System.exit(1); }
    // } // void chooseMachinePlayers()

}
