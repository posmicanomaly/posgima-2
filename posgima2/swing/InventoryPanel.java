package posgima2.swing;

import posgima2.item.Item;
import posgima2.world.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public class InventoryPanel extends InfoWindow{
    private JTable inventoryTable;
    private DefaultTableModel inventoryModel;
    private Player player;

    public InventoryPanel(Player player) {
        super(new GridLayout(2, 1));
        this.player = player;
        JLabel inventoryLabel = new JLabel(player + " Inventory");
        inventoryModel = new DefaultTableModel();
        inventoryTable = new JTable(inventoryModel);
        update();

        add(inventoryLabel);
        add(inventoryTable);
    }

    public void update() {
        inventoryModel = new DefaultTableModel();
        inventoryModel.addColumn("Item");
        for(Item i : player.getInventory()) {
            inventoryModel.addRow(new Object[]{i.toString()});
        }
        inventoryTable.setModel(inventoryModel);
    }
}
