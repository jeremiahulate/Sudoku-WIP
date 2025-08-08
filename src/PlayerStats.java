import java.util.HashMap;
import java.util.Map;

public class PlayerStats {
    public String username;
    public int gamesPlayed = 0;
    public int gamesWon = 0;
    public int totalTimeSeconds = 0;
    public int fastestTime = Integer.MAX_VALUE;
    public int totalErrors = 0;
    public int totalCorrectMoves = 0;

    public Map<String, Integer> mistakeFrequency = new HashMap<>(); // key = wrong number

    public PlayerStats(String username) {
        this.username = username;
    }

    public void recordGame(int timeInSeconds, boolean won, int correctMoves, int errors, String[] incorrectGuesses) {
        gamesPlayed++;
        totalCorrectMoves += correctMoves;
        totalErrors += errors;
        totalTimeSeconds += timeInSeconds;

        if (won) {
            gamesWon++;
            if(timeInSeconds < fastestTime) {
                fastestTime = timeInSeconds;
            }
        }

        for (String wrongNum : incorrectGuesses) {
            mistakeFrequency.put(wrongNum, mistakeFrequency.getOrDefault(wrongNum, 0) + 1);
        }
    }

    public int getAverageTime() {
        return gamesPlayed == 0 ? 0 : totalTimeSeconds / gamesPlayed;
    }

    public double getAccuracy() {
        int total = totalCorrectMoves + totalErrors;
        return total == 0 ? 1.0 : (double) totalCorrectMoves / total;
    }

    public String getMostCommonMistake() {
        return mistakeFrequency.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
     }

     public String getFromattedFastestTime() {
        if (fastestTime == Integer.MAX_VALUE) return "N/A";
        int min = fastestTime / 60;
        int sec = fastestTime % 60;
        return String.format("%02d:%02d", min, sec);
     }
     
     public Map<String, Integer> getMistakeFrequency() {
        return mistakeFrequency;
     }
}
