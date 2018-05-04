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

    public LList() {
        ListStart = null;
        Length = 0;
    }

    public LList(obj ele1) { add(ele1); }

    public LList(obj ele1, obj ele2) {
        add(ele2);
        add(ele1);
    }

    public LList(obj ele1, obj ele2, obj ele3) {
        add(ele3);
        add(ele2);
        add(ele1);
    }

    public LList(obj ele1, obj ele2, obj ele3, obj ele4) {
        add(ele4);
        add(ele3);
        add(ele2);
        add(ele1);
    }

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

    public void remove(obj removeele) {
        // Removes from the LList the first element which is == to removeele.
        // Notice this will not remove Strings that are equals() but not ==.

        // Special case to handle the head of the list:
        // System.out.println("Entering: LList.remove() - length = " + length());
        if (ListStart.value() == null) return;
        if (ListStart.value() == removeele) {
            // Note!  If you remove the head of the list after you have
            // created a ListIterator, the ListIterator will still include the
            // removed item.
            ListStart = ListStart.next();
            Length--;
        }
        else {
            ListCell<obj> thiscell;
            ListCell<obj> prevcell = ListStart;
            while ((thiscell = prevcell.next()) != null) {
                if (thiscell.value() == removeele) {
                    // Move prevcell ptr to skip this cell
                    prevcell.setNext(thiscell.next());
                    Length--;
                    // System.out.println("Exiting LList.remove() - length = " +
                    // length());
                    return;
                }
                prevcell = thiscell;
            }
        }
        // System.out.println("Exiting LList.remove() - length = " + length());
    } // void remove()

    public boolean find(obj target) {
        ListCell<obj> thiscell = ListStart;
        while (thiscell != null) {
            if (thiscell.value() == target) return true;
            thiscell = thiscell.next();
        }
        return false;
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

    public void copy(LList<obj> orig) {
        // Copy orig into this
        // We want the copy to be in the same sequence as the original, so
        // we cannot simply add() each successive element.
        ListCell<obj> origpt;
        ListCell<obj> thisoldpt = null;
        ListCell<obj> newcell;
        for (origpt = orig.ListStart;
             (origpt != null);
             origpt = origpt.next()) {
            if (origpt == orig.ListStart) {
                ListStart = new ListCell<obj>(origpt.value(), null);
                thisoldpt = ListStart;
            }
            else {
                newcell = new ListCell<obj>(origpt.value(), null);
                thisoldpt.setNext(newcell);
                thisoldpt = newcell;
            }
        }
        this.Length = orig.Length;
    } // end copy()

    public LList<obj> copy() {
        // We want the copy to be in the same sequence as the original, so
        // we cannot simply add() each successive element.
        ListCell<obj> origcell = ListStart;
        ListCell<obj> newcell = new ListCell<obj>(origcell.value(), null);
        LList<obj> result = new LList<obj>();
        result.ListStart = newcell;
        result.Length = 1;
        while ((origcell = origcell.next()) != null) {
            ListCell<obj> oldcell = newcell;
            newcell = new ListCell<obj>(origcell.value(), null);
            oldcell.setNext(newcell);
            result.Length++;
        }
        return result;
    } // copy()

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

    public obj getFirst() { if (ListStart == null) return null; else return ListStart.value(); }

    public ListCell<obj> getFirstCell() { return ListStart; }

} // class LList
