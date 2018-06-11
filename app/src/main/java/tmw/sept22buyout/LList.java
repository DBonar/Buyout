package tmw.sept22buyout;

/*
 * Created by Tim Weinrich on 10/14/2017.
 *
 * LList.java
 *
 * A unidirectional linked list.
 *
*/

public class LList<obj> {

    private ListCell<obj> ListStart = null;
    private int Length = 0;

    public LList() {}


    public void add(obj newele) {
        ListCell<obj> newcell = new ListCell<obj>(newele, ListStart);
        // Note! If you add to the list after you have created a ListIterator,
        // the ListIterator will not see the new addition.
        ListStart = newcell;
        Length++;
    }

    public void append(obj newele) {
        // Adds newele to the end of this LList
        ListCell<obj> newcell = new ListCell<obj>(newele, null);
        ListCell<obj> cell = ListStart;
        if (cell == null) ListStart = newcell;
        else {
            while (cell.next() != null) cell = cell.next();
            cell.setNext(newcell); }
        Length++;
    }



    public obj find(int cellnum) {
        ListCell<obj> thiscell = ListStart;
        for (int n = 0; n < cellnum; n++) {
            if (thiscell == null) return null;
            thiscell = thiscell.next();
        }
        if (thiscell == null) return null;
        return thiscell.value();
    }



    public int length() {
        // if (ListStart == null) return 0;
        // int count = 1;
        // ListCell sublist = ListStart;
        // while ((sublist = sublist.next()) != null) count++;
        // return count;
        return Length;
    }

    public obj takeFirst() {
        // Removes the first member from the list and returns it.
        // If list is empty, returns null.
        if (ListStart == null) return null;
        obj result = ListStart.value();
        ListStart = ListStart.next();
        Length--;
        return result;
    }


    public ListCell<obj> getFirstCell() { return ListStart; }

} // class LList
