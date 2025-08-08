import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() ->{
            UserManager userManager = new UserManager();
            LoginDialog loginDialog = new LoginDialog();
            String username = loginDialog.promptUsername(userManager);
            PlayerStats stats = userManager.getOrCreateUser(username);
            new SudokuLauncher(username, stats, userManager); //opens puzzle selection screen
        });
    }
}
