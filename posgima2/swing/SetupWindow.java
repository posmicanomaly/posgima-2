package posgima2.swing;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/3/2015.
 */
public class SetupWindow extends JFrame {
    private static JTextArea console;
    private static JLabel currentRoomsMadeLabel;
    private static JLabel totalRoomsLabel;
    private static JLabel totalConnectionsMadeLabel;
    private static JLabel currentOperationLabel;
    private JScrollPane consoleScroller;

    public SetupWindow() {
        super();
        setTitle("Posgima-2 Setup");
        setLayout(new BorderLayout());
        console = new JTextArea();
        consoleScroller = new JScrollPane(console);
        add(consoleScroller);
        setPreferredSize(new Dimension(400, 300));
        setResizable(false);
        pack();
    }

    public static void println(String line) {
        console.append(line + "\n");
        console.setCaretPosition(console.getText().length());
    }

    public void showWindow() {
        setVisible(true);
    }

    public void hideWindow() {
        setVisible(false);
    }
}
