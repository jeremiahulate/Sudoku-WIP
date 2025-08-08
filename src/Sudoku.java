import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.Comparator;
import java.util.Map;

public class Sudoku {
    class Tile extends JButton {
        int row;
        int col;
        Tile(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
    int boardwidth = 600;
    int boardheight = 650;

    private final String username;
    private final PlayerStats stats;
    private final UserManager userManager;
    
    JFrame frame = new JFrame("Sudoku");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JButton numSelected = null;
    JButton nextButton = new JButton("Next Puzzle");
    JLabel timerLabel = new JLabel("Time: 00:00");
    JButton backButton = new JButton("Back");
    String[] puzzle;
    String[] solution;
    String currentDifficulty;
    Timer gameTimer;
    int secondsElapsed = 0;
    int currentPuzzleIndex = 0;
    int errors = 0;


    public Sudoku(String[] puzzle, String[] solution, String username, PlayerStats stats, UserManager userManager) {
        this.puzzle = puzzle;
        this.solution = solution;
        this.currentPuzzleIndex = 0;
        this.currentDifficulty = "Easy";
        this.stats = stats;
        this.userManager = userManager;
        this.username = username;
        setupUI();
    }
    private void setupUI() {
        frame.setSize(boardwidth, boardheight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout()); //border layout for placing elements in east west south north


        textLabel.setFont(new Font("Arial", Font.BOLD, 30));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Sudoku: 0");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textPanel.add(Box.createRigidArea(new Dimension(20, 0))); //spacer
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));
        backButton.setFocusable(false);
        
        textPanel.setLayout(new BorderLayout());
        textPanel.add(backButton, BorderLayout.WEST);
        textPanel.add(timerLabel, BorderLayout.CENTER);
        textPanel.add(textLabel, BorderLayout.EAST);

        backButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Return to main menu?", "Confirm?", JOptionPane.YES_NO_CANCEL_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if(gameTimer !=null) {
                    gameTimer.stop();
                }
                frame.dispose(); //close the game window
                new SudokuLauncher(username, stats, userManager); //show launcher again
            }
        });

        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(9, 9));
        setupTiles();
        frame.add(boardPanel, BorderLayout.CENTER);

        buttonsPanel.setLayout(new GridLayout(1, 9));
        setupButtons();
        frame.add(buttonsPanel, BorderLayout.SOUTH);

        startTimer();
        frame.setVisible(true);
    }
    
    void setupTiles() {
        for (int row = 0; row < 9; row++) {
            for(int col = 0; col < 9; col++) {
                Tile tile = new Tile(row, col);
                char tileChar = puzzle[row].charAt(col);
                if (tileChar != '-') {
                    tile.setFont(new Font("Arial", Font.BOLD, 20));
                    tile.setText(String.valueOf(tileChar));
                    tile.setBackground(Color.lightGray);
                }
                else {
                    tile.setFont(new Font("Arial", Font.PLAIN, 20));
                    tile.setBackground(Color.white);
                }
                if ((row == 2 && col == 2) || (row == 2 && col ==5) || (row == 5 && col == 2) || (row == 5 && col == 5) ){
                    tile.setBorder(BorderFactory.createMatteBorder(1,1,5,5, Color.black));
                }
                else if( row == 2 || row == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1,1,5,1, Color.black));
                }
                else if (col == 2 || col == 5) {
                    tile.setBorder(BorderFactory.createMatteBorder(1,1,1,5, Color.black));
                }
                else {
                    tile.setBorder(BorderFactory.createLineBorder(Color.black));
                }
                tile.setFocusable(false);
                boardPanel.add(tile);

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Tile tile = (Tile) e.getSource();
                        int row = tile.row;
                        int col = tile.col;
                        if (numSelected != null) {
                            if (tile.getText() != "") {
                                return; // if the tile already has a number, do nothing
                            }
                            String numSelectedText = numSelected.getText();
                            String tileSolution = String.valueOf(solution[row].charAt(col));
                            if (tileSolution.equals(numSelectedText)) {
                                tile.setText(numSelectedText);
                                stats.totalCorrectMoves++;
                                userManager.updateStats(stats); // Save stats after correct move

                                //check if board is complete
                                if (isBoardComplete()) {
                                    gameTimer.stop();
                                    JOptionPane.showMessageDialog(frame, "Puzzle Complete!\n" + timerLabel.getText());

                                    stats.gamesPlayed++;
                                    stats.gamesWon++;
                                    stats.totalTimeSeconds += secondsElapsed;
                                    stats.fastestTime = Math.min(stats.fastestTime, secondsElapsed);
                                    userManager.updateStats(stats); // Save stats after puzzle completion

                                    LeaderboardEntry entry = new LeaderboardEntry(username, timerLabel.getText(), currentDifficulty, currentPuzzleIndex);
                                    new LeaderboardManager().addEntry(entry);

                                    showLeaderboardAndOptions(currentDifficulty, currentPuzzleIndex);
                                }

                            }
                            else {
                                errors++;
                                stats.totalErrors++;
                                recordMistake(Integer.parseInt(numSelectedText));;
                                userManager.updateStats(stats); // Save stats after incorrect move
                                textLabel.setText("Sudoku: " + errors);
                            }
                        }
                    }
                });
            }
        }
    }

    void setupButtons() {
        for (int i = 1; i < 10; i++) {
            JButton button = new JButton();
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.setText(String.valueOf(i));
            button.setFocusable(false);
            button.setBackground(Color.white);
            buttonsPanel.add(button);

            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    JButton button = (JButton) e.getSource();
                    if (numSelected != null) {
                        numSelected.setBackground(Color.white);
                    }
                    numSelected = button;
                    numSelected.setBackground(Color.lightGray);  
                }
            });
        }
    }

    void startTimer() {
        gameTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                secondsElapsed++;
                int minutes = secondsElapsed / 60;
                int seconds = secondsElapsed % 60;
                timerLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
            }
        });
        gameTimer.start();
    }

    boolean isBoardComplete() {
        for (Component comp : boardPanel.getComponents()) {
            if (comp instanceof Tile tile) {
                if (tile.getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showLeaderboardAndOptions(String difficulty, int puzzleIndex) {
        LeaderboardManager lb = new LeaderboardManager();
        List<LeaderboardEntry> entries = lb.load();

        //create leaderboard text
        StringBuilder sb = new StringBuilder("<html><h2>Leaderboards</h2><ul>");
        entries.stream()
            .filter(entry -> !entry.name.equals("Anonymous")) // Exclude 'Anonymous' entries
            .sorted(Comparator.comparing(e -> e.time))
            .limit(10)
            .forEach(entry -> sb.append(
                String.format("<li>%s = %s (%s #%d)</li>",
                entry.name, entry.time, entry.difficulty, entry.puzzleIndex)
            ));
        sb.append("</ul></html>");

        JLabel lblabel = new JLabel(sb.toString());
        lblabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton nextButton = new JButton("Next Puzzle");
        JButton easierButton = new JButton("Easier difficulty");
        JButton harderButton = new JButton("Harder difficulty");
        JButton selectButton = new JButton("Pick Puzzle");

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.add(lblabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nextButton);
        buttonPanel.add(easierButton);
        buttonPanel.add(harderButton);
        buttonPanel.add(selectButton);
        optionsPanel.add(buttonPanel);

        JFrame lbpopup = new JFrame("Leaderboard and Options");
        lbpopup.add(optionsPanel);
        lbpopup.pack();
        lbpopup.setLocationRelativeTo(null);
        lbpopup.setVisible(true);

        nextButton.addActionListener(e -> {
            lbpopup.dispose();
            new SudokuLauncher(username, stats, userManager); // Restart launcher for next puzzle
        });

        easierButton.addActionListener(e -> {
            lbpopup.dispose();
            currentDifficulty = "Easy"; // Set difficulty to easier level
            new SudokuLauncher(username, stats, userManager); // Restart launcher
        });

        harderButton.addActionListener(e -> {
            lbpopup.dispose();
            currentDifficulty = "Hard"; // Set difficulty to harder level
            new SudokuLauncher(username, stats, userManager); // Restart launcher
        });

        selectButton.addActionListener(e -> {
            lbpopup.dispose();
            new SudokuLauncher(username, stats, userManager); // Allow user to pick puzzle
        });
    }

    public void recordMistake(int number) {
        PlayerStats stats = userManager.getOrCreateUser(username);
        Map<String, Integer> mistakeFrequency = stats.getMistakeFrequency();

        
         // Debugging: Print the current mistake frequency
        System.out.println("Before update: " + mistakeFrequency);


        mistakeFrequency.put(String.valueOf(number), mistakeFrequency.getOrDefault(String.valueOf(number), 0) + 1);

         // Debugging: Print the updated mistake frequency
        System.out.println("After update: " + mistakeFrequency);

        userManager.updateStats(stats);
        userManager.saveUsers();
    }
}
