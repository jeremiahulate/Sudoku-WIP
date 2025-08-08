import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.util.*;

public class PuzzleLoader {
    public Map<String, List<SudokuPuzzle>> load() throws Exception {
        String filename = "c:/Users/jeremiah/source/repos/Sudoku/src/resources/puzzles.json";
        Gson gson = new Gson();
        FileReader reader = new FileReader(filename);
        TypeToken<Map<String, List<SudokuPuzzle>>> type = new TypeToken<>() {};
        return gson.fromJson(reader, type.getType());
    }
}
