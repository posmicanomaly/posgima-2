package posgima2;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Jesse Pospisil on 12/26/2014.
 */
public class StatisticsPanel extends JPanel{
    private JTextArea statisticsTextArea;
    private JScrollPane statisticsScroller;
    public StatisticsPanel() {
        setBackground(Color.black);

        statisticsTextArea = new JTextArea("posgima2.Player\nBarbarian\n\nHP: 15 / 16\nStr: 0\nAgi: 0\n\nNormal");
        statisticsTextArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        statisticsTextArea.setForeground(Color.white);
        statisticsTextArea.setOpaque(false);
        statisticsTextArea.setEditable(false);
        //statisticsTextArea.setWrapStyleWord(false);
        //statisticsTextArea.setLineWrap(true);

        statisticsScroller = new JScrollPane(statisticsTextArea);
        statisticsScroller.setOpaque(false);
        statisticsScroller.getViewport().setOpaque(false);
        statisticsScroller.setBorder(null);

        setLayout(new BorderLayout());
        add(statisticsScroller, BorderLayout.CENTER);
    }

    public void update(GameState gameState) {
        Player player = gameState.getPlayer();
        StringBuilder sb = new StringBuilder(16 * 6);
        sb.append(player).append("\n");
        sb.append("Barbarian").append("\n\n");
        sb.append(getStatsDisplayString(gameState));
        statisticsTextArea.setText(sb.toString());
    }

    private String getStatsDisplayString(GameState gameState) {
        StringBuilder result = new StringBuilder();
        Player p = gameState.getPlayer();
        result.append("HP:  ").append(p.getCurrentHP()).append(" / ").append(p.getMaxHP()).append("\n");
        result.append("STR: ").append(p.getStrength()).append("\n");
        result.append("AGI: ").append(p.getAgility()).append("\n");
        result.append("DEX: ").append(p.getDexterity()).append("\n");
        result.append("\n");
        result.append(p.getTile().getGlyph()).append("\n");
        if(p.getTile().hasItems()) {
            if(p.getTile().getItems().size() > 1) {
                result.append("items lay here").append("\n");
            } else {
                result.append(p.getTile().getItems().get(0)).append(" on ground\n");
            }
        } else {
            result.append("\n");
        }
        result.append("\n");
        result.append("turn: ").append(gameState.getTurns()).append("\n");
        if(gameState.monstersInView() > 0) {
            result.append(gameState.monstersInView() + " monsters in view").append("\n");
        } else {
            result.append("\n");
        }
        return result.toString();
    }
}