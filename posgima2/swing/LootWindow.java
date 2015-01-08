package posgima2.swing;

import posgima2.game.Game;
import posgima2.item.Item;
import posgima2.world.Player;
import posgima2.world.dungeonSystem.dungeon.Tile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/6/2015.
 */
public class LootWindow extends JFrame implements WindowListener{
    private JPanel lootPanel;
    private JTable lootTable;
    private JButton doneButton;
    private JButton cancelButton;

    private boolean doneLooting = false;

    /*
    This handle is for if player closes the window, it will do the same thing as cancel button
     */
    private Player player;

    public LootWindow(final Tile tile, final Player player) {
        this.player = player;

        addWindowListener(this);
        lootPanel = new JPanel(new GridLayout(4, 1));
        final DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Loot?");
        tableModel.addColumn("posgima2.item.weapon.Item Name");
        lootTable = new JTable(tableModel);
        JLabel sourceLabel = new JLabel(tile.toString());

        for(Item i : tile.getItems()) {
            tableModel.addRow(new Object[]{i.toString()});
        }

        doneButton = new JButton("done");
        cancelButton = new JButton("cancel");
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.setState(Game.STATE_LOOTED);
                ArrayList<Item> itemsLooting = new ArrayList<Item>();
                for(int i = 0; i < lootTable.getSelectedRows().length; i++) {
                    itemsLooting.add(tile.getItems().get(i));
                }
                for(Item i : itemsLooting) {
                    player.addInventory(i);
                    tile.getItems().remove(i);
                }

                WindowFrame.forceGameUpdate();
                closeWindow();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playCanceledWindow(player);
            }
        });
        lootPanel.add(sourceLabel);
        lootPanel.add(lootTable);
        lootPanel.add(doneButton);
        lootPanel.add(cancelButton);
        add(lootPanel);
        pack();
        setVisible(true);
    }

    public void closeWindow() {
        setVisible(false);
    }

    private void playCanceledWindow(Player player) {
        player.setState(Game.STATE_CANCEL);
        WindowFrame.forceGameUpdate();
        closeWindow();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        playCanceledWindow(player);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
