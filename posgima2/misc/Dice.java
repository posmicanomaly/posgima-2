package posgima2.misc;

/**
 * Created by Jesse Pospisil on 1/8/2015.
 */
public abstract class Dice {
    public static final int D20 = 20;
    public static final int D10 = 10;
    public static final int D6 = 6;
    public static final int D8 = 8;
    public static final int D4 = 4;

    public static int roll(int type) {
        return (int)(Math.random() * type) + 1;
    }

    public static int roll(int amount, int type) {
        int total = 0;
        for(int i = 0; i < amount; i++) {
            total += roll(type);
        }
        return total;
    }
}
