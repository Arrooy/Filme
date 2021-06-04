package NLP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Keywords {
    private String[] objects;
    private String[] actions;
    private String[] precedents;
    private String[] affirmations;
    private String[] negations;
    private String[] exitExpressions;
    private String[] helloExpressions;
    private String[] helpExpressions;
    private final HashMap<String, String[]> synonyms;

    public Keywords(String fileName) {
        synonyms = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) processLine(scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("No s'ha trobat el fitxer de NLP.Keywords!");
            e.printStackTrace();
        }
    }

    private void processLine(String line) {
        if (line.isBlank()) return;
        String[] values = line.split(":")[1].split("--");
        switch (line.split(":")[0]) {
            case "Objects" -> objects = values;
            case "Actions" -> actions = values;
            case "Precedents" -> precedents = values;
            case "Affirmations" -> affirmations = values;
            case "Negations" -> negations = values;
            case "Exit" -> exitExpressions = values;
            case "Hello" -> helloExpressions = values;
            case "Help" -> helpExpressions = values;
            case "Synonyms" -> synonyms.put(values[0], values);
        }
    }

    public String[] getAffirmations() {
        return affirmations;
    }

    public String[] getNegations() {
        return negations;
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

    public String[] getHelloExpressions() {
        return helloExpressions;
    }
    
    public String[] getHelpExpressions() {
        return helpExpressions;
    }

}
