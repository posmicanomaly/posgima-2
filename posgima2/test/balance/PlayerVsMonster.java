package posgima2.test.balance;

import posgima2.combat.Melee;
import posgima2.game.ItemGenerator;
import posgima2.item.Item;
import posgima2.item.armor.Armor;
import posgima2.item.armor.Plate;
import posgima2.world.entity.player.Player;
import posgima2.world.entity.monster.Goblin;
import posgima2.world.entity.monster.Kobold;
import posgima2.world.entity.monster.Monster;

import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/10/2015.
 */
public class PlayerVsMonster {
    public static void main(String[] args) {
        Melee.ANNOUNCE = false;
        Melee.SYSTEM_CONSOLE_OUTPUT = false;
        ArrayList<Monster> monsters = new ArrayList<>();
        addMonsterToTest(1, 10, new Goblin(1), monsters);
        for(int m = 0; m < monsters.size(); m++) {
            System.out.println("\n--------------------");
            int playerWon = 0;
            int monsterWon = 0;
            int totalEndingHPPlayer = 0;
            int totalEndingHPMonster = 0;
            int testRuns = 100;
            Player player = new Player('@');
            Monster monster = monsters.get(m);
            for (int i = 0; i < testRuns; i++) {
                player = new Player('@');
                player.setName("balance_tester");
                player.levelUp();
                monster = makeMonster(monsters.get(m));
                if(monster == null) {
                    System.out.println("monster null");
                    continue;
                }
                addPlayerInventory(player);
                addMonsterInventory(monster);
                while (true) {
                    player.setAttackedThisTurn(false);
                    monster.setAttackedThisTurn(false);
                    Melee.meleeCombat(player, monster, true);
                    if (!player.isAlive() && monster.isAlive()) {
                        monsterWon++;
                        totalEndingHPMonster += monster.getCurrentHP();
                        totalEndingHPPlayer += player.getCurrentHP();
                        break;
                    } else if (player.isAlive() && !monster.isAlive()) {
                        playerWon++;
                        totalEndingHPMonster += monster.getCurrentHP();
                        totalEndingHPPlayer += player.getCurrentHP();
                        break;
                    }

                }
            }
            System.out.println("Player[L " + player.getLevel() + "] vs " + monster + "[L " + monster.getLevel() + "]");
            System.out.println("\tPlayer won: " + playerWon + "/" + testRuns);
            System.out.println("\t\tAverage ending hp: " + (totalEndingHPPlayer / testRuns) + "/" + player.getMaxHP());
            System.out.println("\t" + monster + " won: " + monsterWon + "/" + testRuns);
            System.out.println("\t\tAverage ending hp; " + (totalEndingHPMonster / testRuns) + "/" + monster.getMaxHP());
        }
    }

    private static void addMonsterToTest(int startLevel, int endLevel, Monster monster, ArrayList<Monster> monsters) {
        Monster m = null;
        for(int i = startLevel; i <= endLevel; i++) {
            if(monster instanceof Goblin) {
                m = new Goblin(i);
            } else if(monster instanceof Kobold) {
                m = new Kobold(i);
            }
            if(m != null) {
                monsters.add(m);
            }
        }
    }

    private static Monster makeMonster(Monster monster) {
        if(monster instanceof Goblin) {
            return new Goblin(monster.getLevel());
        } else if(monster instanceof Kobold) {
            return new Kobold(monster.getLevel());
        }
        return null;
    }

    private static void addPlayerInventory(Player player) {
        player.addInventory(ItemGenerator.createWeapon(player.getLevel()), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_CHEST), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_ARM), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_HAND), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_LEG), true);
        player.addInventory(ItemGenerator.createArmor(player.getLevel(), Armor.SLOT_HEAD), true);
    }

    private static void addMonsterInventory(Monster m) {
        Plate chest = new Plate(1, Armor.SLOT_CHEST);
        Plate head = new Plate(1, Armor.SLOT_HEAD);
        Plate arm = new Plate(1, Armor.SLOT_ARM);
        Plate hand = new Plate(1, Armor.SLOT_HAND);
        Plate leg = new Plate(1, Armor.SLOT_LEG);

        //Sword mainH = new Sword(Dice.D4, 0, 0, 0, 0);
        //m.addInventory(chest, true);
        //m.addInventory(head, true);
        //m.addInventory(arm, true);
        //m.addInventory(hand, true);
        //m.addInventory(leg, true);
        //m.addInventory(mainH, true);
    }

    private static Item createRandomItem(int level) {
        Item item = null;
        return item;
    }
}
