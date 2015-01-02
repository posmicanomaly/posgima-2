import javax.naming.Context;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 12/26/2014.
 */
public class ConsolePanel extends JPanel{
    private JScrollPane consoleScrollPane;
    private JTextPane consoleTextArea;
    private StyledDocument document;
    private StyleContext context;
    private Style defaultStyle;

    public ConsolePanel() {
        setBackground(Color.black);

        context = new StyleContext();
        document = new DefaultStyledDocument(context);

        defaultStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(defaultStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFontSize(defaultStyle, 12);

        consoleTextArea = new JTextPane(document);
        consoleTextArea.setEditable(false);
        consoleTextArea.setForeground(Color.white);
        consoleTextArea.setOpaque(false);
        consoleTextArea.setFont(new Font("Consolas", Font.PLAIN, 16));

        consoleScrollPane = new JScrollPane(consoleTextArea);
        consoleScrollPane.setOpaque(false);
        consoleScrollPane.getViewport().setOpaque(false);
        consoleScrollPane.setBorder(null);

        setLayout(new BorderLayout());
        add(consoleScrollPane, BorderLayout.CENTER);
    }

    public void insertText(String text) {
        Color color = Color.white;
        String message = text;
        if(message.contains("/warning/")) {
            message = message.replace("/warning/" , "");
            color = Color.red;
        } else if(message.contains("/success/")) {
            message = message.replace("/success/", "");
            color = Color.green;
        } else if(message.contains("/info/")) {
            message = message.replace("/info/", "");
            color = Color.blue;
        }
        insertTextToDocument(message, color);
        consoleTextArea.setCaretPosition(document.getLength());
    }

    private void insertTextToDocument(String text, Color color) {
        try {
            StyleConstants.setForeground(defaultStyle, color);
            document.insertString(document.getLength(), text, defaultStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
