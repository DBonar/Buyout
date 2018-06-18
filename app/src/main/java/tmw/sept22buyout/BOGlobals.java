package tmw.sept22buyout;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by Tim Weinrich on 10/2/2017.
 */

public class BOGlobals {

    public static boolean EndOfGameOption = false;
    public static boolean GameOver = false;
    public static boolean Cheat = false;

    public static @ColorInt int ClrEmptySpace = Color.rgb(160, 160, 160);
    public  static @ColorInt int ClrFullSpace  = Color.rgb(  0,   0,   0);
    public static @ColorInt int ClrTokenSpace = Color.rgb(255, 255, 255);
    public static @ColorInt int ClrChoseSpace = Color.rgb( 80,  80,  80);
    public static @ColorInt int ClrCommChain1 = Color.rgb(  0, 255,   0);
    public static @ColorInt int ClrCommChain2 = Color.rgb(255, 255,   0);
    public static @ColorInt int ClrSnLChain1  = Color.rgb(255,   0,   0);
    public static @ColorInt int ClrSnLChain2  = Color.rgb(255,   0, 127);
    public static @ColorInt int ClrSnLChain3  = Color.rgb(255, 127,   0);
    public static @ColorInt int ClrInvChain1  = Color.rgb(  0,   0, 255);
    public static @ColorInt int ClrInvChain2  = Color.rgb(127,   0, 255);
}
