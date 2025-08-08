import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;


public class UserManager {
    private static final String FILE_PATH = "c:/Users/jeremiah/source/repos/Sudoku/src/resources/player_stats.json";
    private Map<String, PlayerStats> users;

    public UserManager() {
        loadUsers();
    }

    private void loadUsers() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<Map<String, PlayerStats>>() {}.getType();
            users = new Gson().fromJson(reader, type);
            if (users == null) users = new HashMap<>();
        } catch (IOException e) {
            users = new HashMap<>();
        }
    }

    public void saveUsers() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getUsernames() {
        return users.keySet();
    }

    public PlayerStats getOrCreateUser(String name) {
        if (name == null || name.trim().isEmpty()) {
            name = generateUniqueGuestName();
        }
        return users.computeIfAbsent(name, PlayerStats::new);
    }

    private String generateUniqueGuestName() {
        String baseName = "Guest";
        int count = 1;
        while (users.containsKey(baseName + count)) {
            count++;
        }
        return baseName + count;
    }

    public void updateStats(PlayerStats stats) {
        users.put(stats.username, stats);
        saveUsers();
    }
}
