package tmw.sept22buyout;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Tim Weinrich on 10/18/2017.
 */

public class TokenButton extends android.support.v7.widget.AppCompatButton {

    private Token PToken;

    public TokenButton(Context context) {
        super(context);
    }

    public Token getToken() { return PToken; }
    public void setToken(Token newtoken) { PToken = newtoken; }

}
