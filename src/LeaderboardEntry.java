public class LeaderboardEntry {
    String name;
    String time;
    String difficulty;
    int puzzleIndex;

    public LeaderboardEntry(String name, String time, String difficulty, int puzzleIndex) {
        this.name = name;
        this.time = time;
        this.difficulty = difficulty;
        this.puzzleIndex = puzzleIndex;
    }
}
