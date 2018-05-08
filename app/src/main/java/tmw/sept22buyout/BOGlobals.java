package tmw.sept22buyout;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by Tim Weinrich on 10/2/2017.
 */

public class BOGlobals {

    static boolean EndOfGameOption = false;
    static boolean GameOver = false;
    static boolean Cheat = false;

    static @ColorInt int ClrEmptySpace = Color.rgb(160, 160, 160);
    static @ColorInt int ClrFullSpace  = Color.rgb(  0,   0,   0);
    static @ColorInt int ClrTokenSpace = Color.rgb(255, 255, 255);
    static @ColorInt int ClrChoseSpace = Color.rgb( 80,  80,  80);
    static @ColorInt int ClrCommChain1 = Color.rgb(  0, 255,   0);
    static @ColorInt int ClrCommChain2 = Color.rgb(255, 255,   0);
    static @ColorInt int ClrSnLChain1  = Color.rgb(255,   0,   0);
    static @ColorInt int ClrSnLChain2  = Color.rgb(255,   0, 127);
    static @ColorInt int ClrSnLChain3  = Color.rgb(255, 127,   0);
    static @ColorInt int ClrInvChain1  = Color.rgb(  0,   0, 255);
    static @ColorInt int ClrInvChain2  = Color.rgb(127,   0, 255);
}
