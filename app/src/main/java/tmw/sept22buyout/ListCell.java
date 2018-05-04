package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// ListCell.java
//
// One element of a LList.
//

public class ListCell<obj> {

    private obj Value = null;
    private ListCell<obj> Next = null;

    public ListCell(obj newvalue, ListCell<obj> newnext) {
        Value = newvalue;
        Next = newnext;
    }

    public obj value() { return Value; }
    public ListCell<obj> next() { return Next; }
    public void setNext(ListCell newnext) { Next = newnext; }

}



