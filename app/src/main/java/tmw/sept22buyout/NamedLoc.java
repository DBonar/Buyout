package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/13/2017.
 */
//
// NamedLoc.java
//
// Represents anything that is located on a 2-dimensional grid and named
// accordingly, such as location 4 down and 2 over named "D2".
//

public class NamedLoc {

    private int XLocation;
    private int YLocation;
    private String Name;

    public NamedLoc(int col, int row) {
        // System.out.println("col = " + col + "; row = " + row);
        XLocation = col;
        YLocation = row;
        Name = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(col, col + 1) +
                Integer.toString(row + 1);
        // System.out.println("Name = " + Name);
    }

    public int getCol() { return XLocation; }
    public int getRow() { return YLocation; }
    public String getName() { return Name; }
    public String toString() { return Name; }

} // class NamedLoc

