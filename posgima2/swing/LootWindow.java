package posgima2.swing;

import posgima2.item.Item;
import posgima2.world.Player;
import posgima2.world.dungeonSystem.dungeon.Tile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Jesse Pospisil on 1/6/2015.
 */
public class LootWindow extends JFrame implements WindowListener{
    private JPanel lootPanel;
    private JTable lootTable;
    private JScrollPane lootTableScrollPane;
    private JButton doneButton;
    private JButton cancelButton;

    /*
    This handle is for if player closes the window, it will do the same thing as cancel button
     */
    private Player player;
    private Tile tile;

    public LootWindow(final Tile tile, final Player player) {
        this.player = player;
        this.tile = tile;

        addWindowListener(this);
        lootPanel = new JPanel(new BorderLayout());
        final DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Item Name");
        lootTable = new JTable(tableModel);
        /*
        Uncomment to force single selection mode, effectively making it one item loot per turn
         */
        //lootTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lootTableScrollPane = new JScrollPane(lootTable);
        JLabel sourceLabel = new JLabel(tile.toString());

        for(Item i : tile.getItems()) {
            tableModel.addRow(new Object[]{i.toString()});
        }

        doneButton = new JButton("done");
        doneButton.setPreferredSize(new Dimension(100, 50));
        cancelButton = new JButton("cancel");
        cancelButton.setPreferredSize(new Dimension(100, 50));
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLoot();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playCanceledWindow(player);
            }
        });
        lootPanel.add(sourceLabel, BorderLayout.NORTH);
        lootPanel.add(lootTableScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(doneButton);
        buttonPanel.add(cancelButton);

        lootPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(lootPanel);

        setPreferredSize(new Dimension(400, 300));
        pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setResizable(false);
        this.setLocation((dim.width / 2) - (getSize().width / 2), (dim.height / 2) - (this.getSize().height / 2));

        setVisible(true);
    }

    public void closeWindow() {
        setVisible(false);
    }

    private void playCanceledWindow(Player player) {
        player.setState(Player.STATE.CANCEL);
        WindowFrame.forceGameUpdate();
        closeWindow();
    }

    private void performLoot() {
        player.setState(Player.STATE.LOOTED);
        ArrayList<Item> itemsLooting = new ArrayList<Item>();
        for(int i = 0; i < lootTable.getRowCount(); i++) {
            if(lootTable.isRowSelected(i)) {
                itemsLooting.add(tile.getItems().get(i));
            }
        }
        for(Item i : itemsLooting) {
            player.addInventory(i, false);
            tile.getItems().remove(i);
        }

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
