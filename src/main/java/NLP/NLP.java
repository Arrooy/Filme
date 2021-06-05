package NLP;

import Common.DigestedInput;
import Common.InputType;
import NLP.AhoCorasick.ACNodeType;
import NLP.AhoCorasick.ACResult;
import NLP.AhoCorasick.AhoCorasick;

import java.util.ArrayList;
import java.util.Arrays;

public class NLP {
    private ArrayList<ACResult> currentSentence;

    public NLP() {
        new Keywords("res/keywords.txt");
    }

    private static NLP singleton;
    public static NLP getInstance() {
        if (singleton == null) singleton = new NLP();
        return singleton;
    }

    public DigestedInput process(String input) {
        if (input.endsWith("?")) input = input.substring(0, input.length()-1);

        // TODO: posar aquí codi de spellchecking i merdes
        currentSentence = AhoCorasick.getInstance().analyzeString(input);
        AhoCorasick.processResults(currentSentence);

        for (ACResult r: currentSentence) System.out.println(r);

        String action = getAction();
        String object = getObject();
        String movieName = getMovieName();
        ArrayList<String> peopleNames = getPersonNames();
        if(movieName != null) movieName = movieName.trim();

        InputType inputType = InputType.BASE;

        if(AhoCorasick.existsType(currentSentence, ACNodeType.EXIT)) inputType = inputType.add(InputType.EXIT);
        if(AhoCorasick.existsType(currentSentence, ACNodeType.AFFIRMATION)) inputType = inputType.add(InputType.AFFIRMATIVE);
        if(AhoCorasick.existsType(currentSentence, ACNodeType.NEGATION)) inputType = inputType.add(InputType.NEGATIVE);
        if(AhoCorasick.existsType(currentSentence, ACNodeType.HELLO)) inputType = inputType.add(InputType.HELLO);
        if(AhoCorasick.existsType(currentSentence, ACNodeType.HELP)) inputType = inputType.add(InputType.HELP);
        if(AhoCorasick.existsType(currentSentence, ACNodeType.TIME)) inputType = inputType.add(InputType.TIME);
        if(AhoCorasick.existsType(currentSentence, ACNodeType.HOW)) inputType = inputType.add(InputType.HOW);
        if(AhoCorasick.existsType(currentSentence, ACNodeType.WHO)) inputType = inputType.add(InputType.WHO);

        System.out.println("Detected object: " + object);
        System.out.println("Detected action: " + action);
        System.out.println("Detected movie name: " + movieName);
        System.out.println("Detected people: " + Arrays.toString(peopleNames.toArray()));

        return new DigestedInput(object, action, movieName, inputType);
    }

    private String getObject() {
        return AhoCorasick.getLongestFromType(currentSentence, ACNodeType.OBJECT);
    }

    private String getAction() {
        return AhoCorasick.getLongestFromType(currentSentence, ACNodeType.ACTION);
    }

    private String getMovieName() {
        return AhoCorasick.getLongestFromType(currentSentence, ACNodeType.MOVIE);
    }

    private ArrayList<String> getPersonNames() {
        return AhoCorasick.getAllFromType(currentSentence, ACNodeType.PERSON);
    }
}