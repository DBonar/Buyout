package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/26/2017.
 */
// A stack reflecting Buyout game phases.  Used for determining what
// we are doing while a user gives input.
// This is a singleton class.

public class WhereAmIStack {

    private static WhereAmIStack Instance = null;
    private LList<WhereAmI> Location = null;

    private void WhereAmIStack() {}  // default constructor disallowed.

    protected WhereAmIStack() {
        Location = new LList<WhereAmI>();
    }

    public static WhereAmIStack inst() {
        // There is one and only one WhereAmIStack
        if (Instance == null) Instance = new WhereAmIStack();
        return Instance;
    }

    public void push(WhereAmI newlocation) {
        Location.add(newlocation);
    }

    public WhereAmI pop() {
        return Location.takeFirst();
    }

    public WhereAmI look() {
        return Location.getFirst();
    }

}
