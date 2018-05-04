package tmw.sept22buyout;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Tim Weinrich on 10/18/2017.
 */

public class ChainButton extends Button {

    private Chain PChain;

    public ChainButton(Context context, Chain newchain) {
        super(context);
        PChain = newchain;
    }

    public Chain getChain() { return PChain; }
    public void setChain(Chain newchain) { PChain = newchain; }

}
