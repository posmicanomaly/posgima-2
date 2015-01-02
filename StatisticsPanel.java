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

        statisticsTextArea = new JTextArea("Player\nBarbarian\n\nHP: 15 / 16\nStr: 0\nAgi: 0\n\nNormal");
        statisticsTextArea.setFont(new Font("Consolas", Font.BOLD, 16));
        statisticsTextArea.setForeground(Color.white);
        statisticsTextArea.setOpaque(false);
        statisticsTextArea.setEditable(false);

        statisticsScroller = new JScrollPane(statisticsTextArea);
        statisticsScroller.setOpaque(false);
        statisticsScroller.getViewport().setOpaque(false);
        statisticsScroller.setBorder(null);

        setLayout(new BorderLayout());
        add(statisticsScroller, BorderLayout.CENTER);
    }
}
