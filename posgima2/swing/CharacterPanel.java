package posgima2.swing;

import posgima2.item.Item;
import posgima2.world.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/7/2015.
 */
public class CharacterPanel extends InfoWindow {
    private Player player;
    private DefaultTableModel equipmentModel;
    private JTable equipmentTable;
    public CharacterPanel(Player player) {
        super(new GridLayout(2, 1));
        this.player = player;
        equipmentModel = new DefaultTableModel();
        equipmentTable = new JTable(equipmentModel);
        update();
        JLabel playerLabel = new JLabel(player.toString());
        add(playerLabel);
        add(equipmentTable);
    }

    public void update() {
        updateEquipmentTable();
    }

    private void updateEquipmentTable() {
        equipmentModel = new DefaultTableModel();
        equipmentModel.addColumn("Slot");
        equipmentModel.addColumn("Item");

        equipmentModel.addRow(new Object[]{"Head", player.getHeadSlot()});
        equipmentModel.addRow(new Object[]{"Chest", player.getChestSlot()});
        equipmentModel.addRow(new Object[]{"Arms", player.getArmSlot()});
        equipmentModel.addRow(new Object[]{"Legs", player.getLegSlot()});
        equipmentModel.addRow(new Object[]{"Hands", player.getHandSlot()});
        equipmentModel.addRow(new Object[]{"Main Hand", player.getMainHand()});

        equipmentTable.setModel(equipmentModel);
    }
}
