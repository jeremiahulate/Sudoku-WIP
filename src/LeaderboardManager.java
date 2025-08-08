
import java.util.*;
import java.io.*;
import java.lang.reflect.Type;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class LeaderboardManager {
    private static final String FILE = "leaderboard.json";
    private Gson gson = new Gson();

    public List<LeaderboardEntry> load() {
        try (Reader reader = new FileReader(FILE)) {
            return gson.fromJson(reader, new TypeToken<List<LeaderboardEntry>>() {}.getType());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public void save(List<LeaderboardEntry> entries) {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(entries, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEntry(LeaderboardEntry entry) {
        List<LeaderboardEntry> entries = load();
        entries.add(entry);
        save(entries);
    }


    public void updatePuzzleLeaderboard(String puzzle, String username, int timeTaken, int errorsMade) {
        Map<String, List<Map<String, Object>>> leaderboard = loadLeaderboard();
        List<Map<String, Object>> puzzleLeaderboard = leaderboard.getOrDefault(puzzle, new ArrayList<>());

        Map<String, Object> entry = new HashMap<>();
        entry.put("username", username);
        entry.put("time", timeTaken);
        entry.put("errors", errorsMade);

        puzzleLeaderboard.add(entry);
        leaderboard.put(puzzle, puzzleLeaderboard);

        saveLeaderboard(leaderboard);
    }

    public void updateOverallLeaderboard(UserManager userManager) {
        Map<String, List<Map<String, Object>>> leaderboard = loadLeaderboard();
        List<Map<String, Object>> overallStats = new ArrayList<>();

        for (String username : userManager.getUsernames()) {
            PlayerStats stats = userManager.getOrCreateUser(username);

            Map<String, Object> entry = new HashMap<>();
            entry.put("username", username);
            entry.put("games played", stats.gamesPlayed);
            entry.put("games won", stats.gamesWon);
            entry.put("accuracy", stats.getAccuracy());

            overallStats.add(entry);
        }

        leaderboard.put("Overall Stats", overallStats);
        saveLeaderboard(leaderboard);
    }

    private void saveLeaderboard(Map<String, List<Map<String, Object>>> leaderboard) {
        try (Writer writer = new FileWriter(FILE)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(leaderboard, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<Map<String, Object>>> loadLeaderboard() {
        try (Reader reader = new FileReader(FILE)) {
            Type type = new TypeToken<Map<String, List<Map<String, Object>>>>() {}.getType();
            return new Gson().fromJson(reader, type);
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}
