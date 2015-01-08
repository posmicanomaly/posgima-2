package posgima2.swing;

import posgima2.item.Item;
import posgima2.world.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public class InventoryWindow extends JFrame {
    private JPanel inventoryPanel;
    private JTable inventoryTable;
    private DefaultTableModel inventoryModel;
    private Player player;

    public InventoryWindow(Player player) {
        this.player = player;
        JLabel inventoryLabel = new JLabel(player + " Inventory");
        inventoryModel = new DefaultTableModel();
        inventoryTable = new JTable(inventoryModel);
        update();
        inventoryPanel = new JPanel(new GridLayout(4, 1));
        inventoryPanel.add(inventoryLabel);
        inventoryPanel.add(inventoryTable);
        add(inventoryPanel);
        pack();
    }

    public void update() {
        inventoryModel = new DefaultTableModel();
        inventoryModel.addColumn("posgima2.item.weapon.Item");
        for(Item i : player.getInventory()) {
            inventoryModel.addRow(new Object[]{i.toString()});
        }
        inventoryTable.setModel(inventoryModel);
        pack();
    }

    public void showWindow() {
        setVisible(true);
    }

    public void hideWindow() {
        setVisible(false);
    }
}
