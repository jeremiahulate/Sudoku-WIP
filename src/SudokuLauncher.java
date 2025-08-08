import java.awt.*;
import javax.swing.*;
import java.util.Random;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class SudokuLauncher {
    JComboBox<String> difficultySelector;
    JComboBox<String> puzzleSelector;

    public SudokuLauncher (String username, PlayerStats stats, UserManager userManager) {
        JFrame launcherFrame = new JFrame("Sudoku Launcher");

        //window icon
        ImageIcon icon= new ImageIcon(getClass().getResource("/img/sudoku.jpg"));
        launcherFrame.setIconImage(icon.getImage());

        launcherFrame.setSize (600,400);
        launcherFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launcherFrame.setLocationRelativeTo(null);
        launcherFrame.setLayout(new BorderLayout());

        //load puzzles from JSON file
        PuzzleLoader loader = new PuzzleLoader();
        Map<String, List<SudokuPuzzle>> allPuzzles;
        try {
            allPuzzles = loader.load();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load puzzle file");
            return;
        }

         //main container panel with padding and vertical layout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,30,20,30));

        //title label
        JLabel titleLabel = new JLabel("Select a Sudoku Puzzle: ", SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10,0,20,0));
        mainPanel.add(titleLabel);

        //difficulty selector
        difficultySelector = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultySelector.setFont(new Font("Arial", Font.PLAIN, 20));
        difficultySelector.setMaximumSize(new Dimension(200, 35));
        difficultySelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(difficultySelector);
        mainPanel.add(Box.createRigidArea(new Dimension( 0, 10))); //spacer

        //puzzle selector dropdown box
        puzzleSelector = new JComboBox<>();
        puzzleSelector.setFont(new Font("Arial", Font.PLAIN, 20));
        puzzleSelector.setMaximumSize(new Dimension(200, 35));
        puzzleSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(puzzleSelector);

        //load initial puzzle list (easy by default)
        String defaultDifficulty = "easy";
        List<SudokuPuzzle> defaultPuzzles = allPuzzles.get(defaultDifficulty);
        for (int i = 0; i < defaultPuzzles.size(); i++) {
            puzzleSelector.addItem("Puzzle " + (i + 1));
        }
        if (puzzleSelector.getItemCount() > 0) {
            puzzleSelector.setSelectedIndex(0);
        }

        //update puzzle list when difficulty changes
        difficultySelector.addActionListener( e -> {
            String selectedDifficulty = ((String) difficultySelector.getSelectedItem()).toLowerCase();
            List<SudokuPuzzle> puzzles = allPuzzles.get(selectedDifficulty);
            puzzleSelector.removeAllItems(); //clear previous difficulties
            for (int i = 0; i < puzzles.size(); i++) {
                puzzleSelector.addItem("Puzzle " + (i + 1));
            }
            if (puzzleSelector.getItemCount() > 0) {
                puzzleSelector.setSelectedIndex(0);
            }
        });

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); //spacer

        //button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton startButton = new JButton("Start Selected Puzzle");
        JButton randomButton = new JButton("Start Random Puzzle");
        JButton viewStatsButton = new JButton("View Your Stats");
        JButton puzzleLBbutton = new JButton("Puzzle Leaderboard");
        JButton overallLBbutton = new JButton("User Stats Leaderboard");

        startButton.setFont(new Font("Arial", Font.PLAIN, 15));
        randomButton.setFont(new Font("Arial", Font.PLAIN, 15));
        viewStatsButton.setFont(new Font("Arial", Font.PLAIN, 15));
        puzzleLBbutton.setFont(new Font("Arial", Font.PLAIN, 15));
        overallLBbutton.setFont(new Font("Arial", Font.PLAIN, 15));

        startButton.setFocusPainted(false);
        randomButton.setFocusPainted(false);
        viewStatsButton.setFocusPainted(false);
        puzzleLBbutton.setFocusPainted(false);
        overallLBbutton.setFocusPainted(false);

        buttonPanel.add(startButton);
        buttonPanel.add(randomButton);
        buttonPanel.add(viewStatsButton);
        buttonPanel.add(puzzleLBbutton);
        buttonPanel.add(overallLBbutton);
        mainPanel.add(buttonPanel);

        //start selected puzzle
        startButton.addActionListener(e -> {
            try{
               String difficulty = ((String) difficultySelector.getSelectedItem()).toLowerCase();
               int index = puzzleSelector.getSelectedIndex();
               List<SudokuPuzzle> puzzles = allPuzzles.get(difficulty);
               SudokuPuzzle selectedPuzzle = puzzles.get(index);

               launcherFrame.dispose(); //close launcher
               new Sudoku(selectedPuzzle.puzzle, selectedPuzzle.solution, username, stats, userManager); //open selected puzzle
            }catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to load puzzle");
            }
        });

        //random selected puzzle
        randomButton.addActionListener(e -> {
             try{
                String difficulty = ((String) difficultySelector.getSelectedItem()).toLowerCase();
                List<SudokuPuzzle> puzzles = allPuzzles.get(difficulty);
                int randomIndex = new Random().nextInt(puzzles.size());
                SudokuPuzzle randomPuzzle = puzzles.get(randomIndex);

                launcherFrame.dispose(); //close launcher
                new Sudoku(randomPuzzle.puzzle, randomPuzzle.solution, username, stats, userManager); //open selected puzzle
            }catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to load puzzle");
            }
        });

        //view user stats
        viewStatsButton.addActionListener(e -> {
            launcherFrame.dispose();
            new UserStatsMenu(stats, username);
        });
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); //spacer

        //puzzle leaderboard
        puzzleLBbutton.addActionListener(e -> {
            JFrame puzzleLBFrame = new JFrame( "Puzzle Leaderboard");
            puzzleLBFrame.setSize(400, 300);
            puzzleLBFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            puzzleLBFrame.setLocationRelativeTo(null);

            LeaderboardManager leaderboardManager = new LeaderboardManager();
            List<LeaderboardEntry> puzzleLeaderboard = leaderboardManager.load();

            StringBuilder sb = new StringBuilder("<html><h2>Puzzle Leaderboard</h2><ul>");
            puzzleLeaderboard.stream()
                .sorted(Comparator.comparing((LeaderboardEntry e) -> Integer.parseInt(e.time))) //sorting by time
                .limit(10) //limit to top 10
                .forEach(entry -> sb.append(
                    String.format("<li>%s = %s (%s #%d)</li>",
                    entry.name, entry.time, entry.difficulty, entry.puzzleIndex)
                ));
            sb.append("</ul></html>");

            JLabel leaderboardLabel = new JLabel(sb.toString());
            leaderboardLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            leaderboardLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            puzzleLBFrame.add(leaderboardLabel, BorderLayout.CENTER);
            puzzleLBFrame.setVisible(true);
        });

        //User leaderboard
        overallLBbutton.addActionListener(e -> {
            JFrame overallLBFrame = new JFrame("User Stats Leaderboard");
            overallLBFrame.setSize(400, 300);
            overallLBFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            overallLBFrame.setLocationRelativeTo(null);

            LeaderboardManager leaderboardManager = new LeaderboardManager();
            List<LeaderboardEntry> overallLeaderboard = leaderboardManager.load();

            StringBuilder sb = new StringBuilder("<html><h2>User Stats Leaderboard</h2><ul>");
            overallLeaderboard.stream()
                .sorted(Comparator.comparing((LeaderboardEntry e) -> e.accuracy).reversed()) //sorting by accuracy
                .limit(10) //limit to top 10
                .forEach(entry -> sb.append(
                    String.format("<li>%s: Games Played = %d, Games Won = %d, Accuracy = %.2f</li>",
                    entry.username, entry.gamesPlayed, entry.gamesWon, entry.accuracy)
                ));
            sb.append("</ul></html>");

            JLabel leaderboardLabel = new JLabel(sb.toString());
            leaderboardLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            leaderboardLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            overallLBFrame.add(leaderboardLabel, BorderLayout.CENTER);
            overallLBFrame.setVisible(true);
        });

        launcherFrame.getRootPane().setDefaultButton(startButton);

        launcherFrame.add(mainPanel, BorderLayout.CENTER);
        launcherFrame.setVisible(true);
    }
}
