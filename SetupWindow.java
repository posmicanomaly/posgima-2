import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 1/3/2015.
 */
public class SetupWindow extends JFrame {
    private static JTextArea console;
    private JScrollPane consoleScroller;

    public SetupWindow() {
        super();
        setTitle("Posgima-2 Setup");
        setLayout(new BorderLayout());
        console = new JTextArea();
        consoleScroller = new JScrollPane(console);
        add(consoleScroller);
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);
        pack();
        setVisible(true);
    }

    public static void println(String line) {
        console.append(line + "\n");
        console.setCaretPosition(console.getText().length());
    }
}