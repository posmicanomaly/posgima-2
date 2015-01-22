package posgima2.swing.popup;

import posgima2.item.Item;
import posgima2.item.armor.Armor;
import posgima2.item.weapon.Weapon;
import posgima2.world.entity.player.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public class InventoryPanel extends InfoWindow{
    private JTable inventoryTable;
    private JScrollPane inventoryTableScrollPane;
    private DefaultTableModel inventoryModel;
    private Player player;
    //panelName for PopupWinow to display
    private String panelName;

    public InventoryPanel(Player player) {
        super(new GridLayout(1, 1));
        this.player = player;
        //JLabel inventoryLabel = new JLabel(player + " Inventory");
        panelName = player + "'s Inventory";
        inventoryModel = new DefaultTableModel();
        inventoryTable = new JTable(inventoryModel);
        inventoryTableScrollPane = new JScrollPane(inventoryTable);
        update();

        //add(inventoryLabel);
        add(inventoryTableScrollPane);
    }

    public void update() {
        inventoryModel = new DefaultTableModel();
        inventoryModel.addColumn("Item");
        for(Item i : player.getInventory()) {
            Object[] rowText = new Object[]{i.toString()};
            if(i instanceof Armor || i instanceof Weapon) {
                if(player.hasItemEquipped(i))
                {
                    rowText = new Object[]{i.toString() + "(equipped)"};
                }
            }
            inventoryModel.addRow(rowText);
        }
        inventoryTable.setModel(inventoryModel);
    }
}
