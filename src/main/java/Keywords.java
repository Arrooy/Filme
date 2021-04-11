import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Keywords {
    private String[] objects;
    private String[] actions;
    private String[] specialActions;
    private String[] precedents;
    private String[] exitExpressions;
    private HashMap<String, String[]> synonyms;

    public Keywords(String fileName) {
        synonyms = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) processLine(scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("No s'ha trobat el fitxer de Keywords!");
            e.printStackTrace();
        }
    }

    private void processLine(String line) {
        if (line.isBlank()) return;
        String[] values = line.split(":")[1].split("--");
        switch (line.split(":")[0]) {
            case "Objects" -> objects = values;
            case "Actions" -> actions = values;
            case "Special actions" -> specialActions = values;
            case "Precedents" -> precedents = values;
            case "Exit" -> exitExpressions = values;
            case "Synonyms" -> synonyms.put(values[0], values);
        }
    }

    public String[] getSpecialActions() {
        return specialActions;
    }

    public String[] getObjects() {
        return objects;
    }

    public String[] getActions() {
        return actions;
    }

    public String[] getPrecedents() {
        return precedents;
    }

    public HashMap<String, String[]> getSynonyms() {
        return synonyms;
    }

    public String[] getExitExpressions() {
        return exitExpressions;
    }
}
