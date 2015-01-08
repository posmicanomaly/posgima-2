package posgima2;

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

    private JScrollPane combatScrollPane;
    private JTextPane combatTextPane;
    private StyledDocument combatDoc;
    private StyleContext combatContext;

    public ConsolePanel() {
        setBackground(Color.black);

        context = new StyleContext();
        combatContext = new StyleContext();

        document = new DefaultStyledDocument(context);
        combatDoc = new DefaultStyledDocument(combatContext);

        defaultStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setAlignment(defaultStyle, StyleConstants.ALIGN_LEFT);
        StyleConstants.setFontSize(defaultStyle, 12);

        consoleTextArea = new JTextPane(document);
        consoleTextArea.setEditable(false);
        consoleTextArea.setForeground(Color.white);
        consoleTextArea.setOpaque(false);
        consoleTextArea.setFont(new Font("Consolas", Font.PLAIN, 12));

        combatTextPane = new JTextPane(combatDoc);
        combatTextPane.setEditable(false);
        combatTextPane.setForeground(Color.yellow);
        combatTextPane.setOpaque(false);
        combatTextPane.setFont(new Font("Consolas", Font.ITALIC, 12));

        consoleScrollPane = new JScrollPane(consoleTextArea);
        consoleScrollPane.setOpaque(false);
        consoleScrollPane.getViewport().setOpaque(false);
        consoleScrollPane.setBorder(null);

        combatScrollPane = new JScrollPane(combatTextPane);
        combatScrollPane.setOpaque(false);
        combatScrollPane.getViewport().setOpaque(false);
        combatScrollPane.setBorder(null);

        setLayout(new GridLayout(1, 2));
        add(consoleScrollPane);
        add(combatScrollPane);
    }

    public void insertText(String text) {
        Color color = Color.white;
        String message = text;

        if(message.contains("/combat/")) {
            message = message.replace("/combat/", "");
            if(message.contains("/atk/")) {
                message = message.replace("/atk/", "");
                color = Color.yellow;
            } else if(message.contains("/def/")) {
                message = message.replace("/def/", "");
                color = Color.red;
            } else {
                color = Color.white;
            }
            insertTextToDocument(combatDoc, message, color, defaultStyle);
            combatTextPane.setCaretPosition(combatDoc.getLength());
        } else {
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
            insertTextToDocument(document, message, color, defaultStyle);
            consoleTextArea.setCaretPosition(document.getLength());
        }
    }

    private void insertTextToDocument(Document doc, String text, Color color, Style style) {
        try {
            StyleConstants.setForeground(style, color);
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}