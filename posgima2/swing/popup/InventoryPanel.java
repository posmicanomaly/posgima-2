package posgima2.swing.popup;

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
    private JScrollPane inventoryTableScrollPane;
    private DefaultTableModel inventoryModel;
    private Player player;

    public InventoryPanel(Player player) {
        super(new GridLayout(2, 1));
        this.player = player;
        JLabel inventoryLabel = new JLabel(player + " Inventory");
        inventoryModel = new DefaultTableModel();
        inventoryTable = new JTable(inventoryModel);
        inventoryTableScrollPane = new JScrollPane(inventoryTable);
        update();

        add(inventoryLabel);
        add(inventoryTableScrollPane);
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
