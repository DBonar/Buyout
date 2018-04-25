package tmw.sept22buyout;

/**
 * Created by Tim Weinrich on 10/14/2017.
 */
//
// ListIterator.java
//
// Permits user to walk thru a List.
//

public class ListIterator<obj> {

    private List<obj> TheList = null;
    private ListCell<obj> Iterator = null;

    private ListIterator() {}

    public ListIterator(List newlist) {
        TheList = newlist;
        Iterator = TheList.getFirstCell();
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
        ListCell<obj> firstcell = TheList.getFirstCell();
        Iterator = firstcell.next(); // Iterator points to second ele.
        return firstcell.value();
    }

    public void reset() {
        Iterator = TheList.getFirstCell();
    }

} // class ListIterator
