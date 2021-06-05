package NLP;

import NLP.AhoCorasick.ACNodeType;
import NLP.AhoCorasick.AhoCorasick;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private String[] timeExpressions;
    private String[] howExpressions;
    private String[] whoExpressions;
    private final HashMap<String, String[]> synonyms;

    public Keywords(String fileName) {
        synonyms = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) processLine(scanner.nextLine());
            scanner.close();

            AhoCorasick.getInstance().setCurrentType(ACNodeType.OBJECT);
            for (String o: objects)
                for (String os: synonyms.get(o)) AhoCorasick.getInstance().insert(os);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.ACTION);
            for (String a: actions)
                for (String as: synonyms.get(a)) {
                    System.out.println("inserting word " + as);
                    AhoCorasick.getInstance().insert(as);
                }

            AhoCorasick.getInstance().setCurrentType(ACNodeType.PRECEDENT);
            for (String p: precedents) AhoCorasick.getInstance().insert(p);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.AFFIRMATION);
            for (String o: affirmations) AhoCorasick.getInstance().insert(o);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.NEGATION);
            for (String o: negations) AhoCorasick.getInstance().insert(o);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.EXIT);
            for (String o: exitExpressions) AhoCorasick.getInstance().insert(o);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.HELLO);
            for (String o: helloExpressions) AhoCorasick.getInstance().insert(o);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.HELP);
            for (String o: helpExpressions) AhoCorasick.getInstance().insert(o);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.TIME);
            for (String o: timeExpressions) AhoCorasick.getInstance().insert(o);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.HOW);
            for (String o: howExpressions) AhoCorasick.getInstance().insert(o);

            AhoCorasick.getInstance().setCurrentType(ACNodeType.WHO);
            for (String o: whoExpressions) AhoCorasick.getInstance().insert(o);

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
            case "Time" -> timeExpressions = values;
            case "How" -> howExpressions = values;
            case "Who" -> whoExpressions = values;
            case "Synonyms" -> synonyms.put(values[0], values);
            //default -> throw new IllegalStateException("Unexpected value: " + line.split(":")[0]);
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

    public String[] getTimeExpressions() {
        return timeExpressions;
    }

    public String[] getHowExpressions() {
        return howExpressions;
    }

    public String[] getWhoExpressions() {
        return whoExpressions;
    }

}
