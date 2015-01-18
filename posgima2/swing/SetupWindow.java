package posgima2.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private WindowFrame windowFrame;

    public SetupWindow(WindowFrame windowFrame) {
        super();
        this.windowFrame = windowFrame;
        setTitle("Posgima-2 Setup");
        setLayout(new GridLayout(3, 1));
        console = new JTextArea();
        consoleScroller = new JScrollPane(console);
        JPanel titlePanel = new JPanel();
        add(titlePanel);
        JPanel fieldPanel = new JPanel();
        JTextField nameField = new JTextField(16);
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = nameField.getText();
                if(playerName.length() > 16) {
                    playerName = playerName.substring(0, 15);
                } else if(playerName.length() == 0) {
                    playerName = "Noname";
                }
                windowFrame.startGame(playerName);
            }
        });
        fieldPanel.add(new JLabel("Name: "));
        fieldPanel.add(nameField);
        fieldPanel.add(new JLabel("Click to start -> "));
        fieldPanel.add(startButton);
        add(fieldPanel);
        add(consoleScroller);
        setPreferredSize(new Dimension(400, 600));
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
