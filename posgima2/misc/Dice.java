package posgima2.misc;

/**
 * Created by Jesse Pospisil on 1/8/2015.
 */
public abstract class Dice {
    public static final int D20 = 4;
    public static final int D10 = 3;
    private static final int INVALID = -1;
    public static final int D6 = 1;
    public static final int D8 = 2;
    public static final int D4 = 0;

    public static int roll(int type) {
        switch(type) {
            case D20:
                return (int)(Math.random() * 20) + 1;
            case D10:
                return (int)(Math.random() * 10) + 1;
            case D8:
                return (int)(Math.random() * 8) + 1;
            case D6:
                return (int)(Math.random() * 6) + 1;
            case D4:
                return (int)(Math.random() * 4) + 1;
        }
        return INVALID;
    }

    public static int roll(int amount, int type) {
        int total = 0;
        for(int i = 0; i < amount; i++) {
            total += roll(type);
        }
        return total;
    }
}
