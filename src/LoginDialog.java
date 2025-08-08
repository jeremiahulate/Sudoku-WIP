import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginDialog {
    private String selectedUser;

    public String promptUsername(UserManager userManager) {
        JDialog logindialog = new JDialog((Frame) null, "Login", true);
        logindialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        logindialog.setSize(400, 200);
        logindialog.setLayout(new BorderLayout());
        logindialog.setLocationRelativeTo(null);

        //login panel
        JPanel loginPanel = new JPanel(/*new GridLayout(3, 1)*/);
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        //login label
        JLabel loginLabel = new JLabel("Choose or create a user:");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(loginLabel);

        //dropdown for existing users
        JComboBox<String> existingUsers = new JComboBox<>(userManager.getUsernames().toArray(new String[0]));
        existingUsers.setFont(new Font("Arial", Font.PLAIN, 14));
        existingUsers.setMaximumSize(new Dimension(300, 30));
        existingUsers.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10))); //spacer
        loginPanel.add(existingUsers);

        //textfield for new user input
        JTextField newUserField = new JTextField();
        newUserField.setFont(new Font("Arial", Font.PLAIN, 14));
        newUserField.setMaximumSize(new Dimension(300, 30));
        newUserField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));//spacer
        loginPanel.add(newUserField);

        //login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> {
            String newUser = newUserField.getText().trim();
            if (!newUser.isEmpty()) {
                selectedUser = newUser;
                userManager.getOrCreateUser(newUser); // Save new user
                userManager.saveUsers(); // Persist changes
            } else {
                selectedUser = (String) existingUsers.getSelectedItem();
            }
            logindialog.dispose();
        });
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10))); //spacer
        loginPanel.add(loginButton);
        
        //close operation
        logindialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        logindialog.getRootPane().setDefaultButton(loginButton);

        logindialog.add(loginPanel, BorderLayout.CENTER);
        logindialog.setVisible(true);

        return selectedUser;
    }
}
