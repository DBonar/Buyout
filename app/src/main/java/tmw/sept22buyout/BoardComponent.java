package tmw.sept22buyout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

//
//  A custom layout element.  That way we can use a
//  BoardComponent in XML when configuring the
//  main activity.
//  I'm extending LinearLayout because that is the
//  display functionality I need.  I expect the XML
//  to just have my custom fields, not any predefined
//  sub-elements.
//

public class BoardComponent extends LinearLayout {

    private int nrows;
    private int ncols;

    public BoardComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.custom_attributes, 0, 0);
        nrows = a.getInt(R.styleable.custom_attributes_num_rows, 9);
        ncols = a.getInt(R.styleable.custom_attributes_num_cols, 12);
        a.recycle();

        buildLayout(context);
    }

    // For use in building on of these in code rather than from XML
    public BoardComponent(Context context, int nRows, int nCols) {
        super(context);

        nrows = nRows;
        ncols = nCols;

        buildLayout(context);
    }

    private void buildLayout(Context context) {
        // Each hoizontal line needs to be sized.  So, we
        // can't just use the params from above, we need one
        // that is sized based on the Text boxes it contains.
        LinearLayout.LayoutParams row_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        row_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        row_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        row_params.weight = 1;

        // TODO It would be nice to have the text more centered
        LinearLayout.LayoutParams cell_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        cell_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        cell_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        cell_params.weight = 1;
        cell_params.leftMargin = 2;

        // This is the overall element for the board.  Note
        // that it's height is based on its content just as
        // the height of the individual rows in it is based on
        // content.  So the size of this element should be
        // mostly independant of the screen size.
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(row_params);

        // It will have YSize rows each of which is a
        // horizontal LinearLayout holding XSize 'buttons'
        Board board = Board.instance();
        for (int rln = 0; rln < Board.BoardYSize; rln++) {
            LinearLayout row = new LinearLayout(context);
            row.setOrientation((LinearLayout.HORIZONTAL));
            row.setLayoutParams(row_params);

            for (int cln = 0; cln < Board.BoardXSize; cln++) {
                BoardSpace space = board.getSpace(cln, rln);
                String spacename = space.getName();
                TextView cell = new TextView(context);
                cell.setPadding(8,0,0,0);
                cell.setText(spacename);
                cell.setLayoutParams(cell_params);
                row.addView(cell);
                space.setDisplay(cell);
            }

            addView(row);
        }
    }
}
