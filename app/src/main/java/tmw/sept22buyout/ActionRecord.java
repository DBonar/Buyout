package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// ActionRecord.java
//
// A description of one action taken by one player.
//

public class ActionRecord {

    private Player Turn; // The person whose turn it was
    private Player Actor; // The person who took the action
    private String Action; // A desctription of the action

    public ActionRecord(Player newturn, Player newactor, String newaction) {
        Turn = newturn;
        Actor = newactor;
        Action = newaction;
    }

    public Player getPlayerTurn() { return Turn; }

    public String toString() {
        if (Turn == Actor) return (Actor.toString() + " " + Action);
        else return "On " + Turn.toString() + "'s turn, " + Actor.toString() + " " + Action;
    }

}
