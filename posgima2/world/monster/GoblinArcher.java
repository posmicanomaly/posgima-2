package posgima2.world.monster;

/**
 * Created by Jesse Pospisil on 1/18/2015.
 */
public class GoblinArcher extends Goblin{
    public GoblinArcher(int level) {
        super(level);
        glyph = 'a';
        range = 5;
        ranged = true;
    }
}
