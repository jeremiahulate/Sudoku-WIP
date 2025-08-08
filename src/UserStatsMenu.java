import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UserStatsMenu {
    public UserStatsMenu(PlayerStats stats, String username) {
        
        JFrame statsFrame = new JFrame( username + "'s Stats");
        statsFrame.setSize(400, 300);
        statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statsFrame.setLocationRelativeTo(null);
        statsFrame.setLayout(new BorderLayout());

        //stats panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        //display stats
        statsPanel.add(new JLabel("Username: " + username));
        statsPanel.add(new JLabel("Games Played: " + stats.gamesPlayed));
        statsPanel.add(new JLabel("Games Won: " + stats.gamesWon));
        statsPanel.add(new JLabel("Total Time (seconds): " + stats.totalTimeSeconds));
        statsPanel.add(new JLabel("Fastest Time: " + stats.getFromattedFastestTime()));
        statsPanel.add(new JLabel("Total Errors: " + stats.totalErrors));
        statsPanel.add(new JLabel("Total Correct Moves: " + stats.totalCorrectMoves));
        statsPanel.add(new JLabel("Mistake Frequency:"));
        Map<String, Integer> mistakeFrequency = stats.getMistakeFrequency();
        for (Map.Entry<String, Integer> entry : mistakeFrequency.entrySet()) {
            statsPanel.add(new JLabel("Number " + entry.getKey() + ": " + entry.getValue() + " times"));
        }

        //nav buttons
        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> {
            statsFrame.dispose(); //close stats window
            new SudokuLauncher(username, stats, new UserManager()); //reopen launcher
        });

        //close operation
        statsFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                statsFrame.dispose();
                new SudokuLauncher(username, stats, new UserManager());
            }
        });

        statsPanel.add(Box.createRigidArea(new Dimension( 0, 10)));
        statsPanel.add(backButton);

        statsFrame.add(statsPanel, BorderLayout.CENTER);
        statsFrame.setVisible(true);
    }
    
}
