package tmw.sept22buyout;

import java.util.Random;

/**
 * Created by Tim Weinrich on 1/29/2018.
 */

public class Utils {

    // FakeRandomNumbers is true iff you want the same set of random numbers each time
    // you run.
    static boolean FakeRandomNumbers = false;
    static Random FakeRandom = null;
    static boolean FirstRand = true;

    static double random() {
        if (FirstRand) {
            if (FakeRandomNumbers) {
                FakeRandom = new Random((long) // 500);
                        305);
                // #2 tests: FakeRandom = new Random((long) 501);
            }
        }
        FirstRand = false;
        if (FakeRandomNumbers) return FakeRandom.nextDouble();
        else return Math.random();
    }

}
