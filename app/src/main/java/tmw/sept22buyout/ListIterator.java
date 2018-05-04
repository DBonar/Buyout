package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// ListIterator.java
//
// Permits user to walk thru a LList.
//

public class ListIterator<obj> {

    private LList<obj> theLList = null;
    private ListCell<obj> Iterator = null;

    private ListIterator() {}

    public ListIterator(LList newlist) {
        theLList = newlist;
        Iterator = theLList.getFirstCell();
    }

    public obj getNext() {
        obj result;
        if (Iterator == null) result = null;
        else {
            result = Iterator.value();
            Iterator = Iterator.next();
        }
        return result;
    }

    public obj getFirst() {
        ListCell<obj> firstcell = theLList.getFirstCell();
        Iterator = firstcell.next(); // Iterator points to second ele.
        return firstcell.value();
    }

    public void reset() {
        Iterator = theLList.getFirstCell();
    }

} // class ListIterator
