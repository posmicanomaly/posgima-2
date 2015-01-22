package posgima2.swing;

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

    private String playerName;

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

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public void insertText(String text) {
        Color color = Color.white;
        String message = text;

        if(message.contains(playerName)) {
            message = message.replace(playerName, "you");
        }
        if(message.contains("/combat/")) {
            message = message.replace("/combat/", "");
            if(message.contains("/killed/")) {
                message = message.replace("/killed/", "");
                color = Color.cyan;
            } else if(message.startsWith("you")) {
                //message = message.replace("/atk/", "");
                color = Color.yellow;
            } else if(message.contains("you")) {
                //message = message.replace("/def/", "");
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
            int offset = 0;
            if(doc.getLength() > 2000) {
                String oldText = doc.getText(0, doc.getLength());
                int difference = doc.getLength() - 2000;
                doc.insertString(0, oldText.substring(difference, oldText.length()), style);
            }
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void clearAll() {
        document = new DefaultStyledDocument(context);
        combatDoc = new DefaultStyledDocument(combatContext);
    }
}
