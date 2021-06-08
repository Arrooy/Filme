package NLP;

import Common.DigestedInput;
import Common.InputType;
import Corrector.Symspell;
import NLP.AhoCorasick.ACNodeType;
import NLP.AhoCorasick.ACResult;
import NLP.AhoCorasick.AhoCorasick;
import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class NLP {

    private ArrayList<ACResult> currentSentence;

    public NLP() {
        Keywords.getInstance();
    }

    private static NLP singleton;

    public static NLP getInstance() {
        if (singleton == null) singleton = new NLP();
        return singleton;
    }

    public DigestedInput process(String input) {
        String originalInput = input;
        System.out.println("Processing " + input);

        // Primera pasada del input. S'eliminen els noms propis.
        currentSentence = AhoCorasick.getInstance().analyzeString(input);
        AhoCorasick.getInstance().processResults(input, currentSentence);

        System.out.println("\nRaw detections: ");
        for (ACResult r : currentSentence) System.out.println(r);
        System.out.println("");

        ArrayList<String> movieNames = getMovieName();
        ArrayList<String> peopleNames = getPersonNames();

        // Elimina els noms propis.
        for (String mn : movieNames)
            input = input.replaceAll(mn, "");

        for (String pn : peopleNames)
            input = input.replaceAll(pn, "");

        if (input.length() != 0) {
            // Eliminem dobles spaces del input (apareixen al borrar noms propis)
            input = input.replaceAll("\\s+", " ");

            input = Symspell.getInstance().spellCheck(input);
            System.out.println("Spell check result: " + input);

            //Segona passada amb input sense noms propis + corregit.
            currentSentence = AhoCorasick.getInstance().analyzeString(input);
            AhoCorasick.getInstance().processResults(input, currentSentence);
        }

        System.out.println("\nRaw detections: ");
        for (ACResult r : currentSentence) System.out.println(r);
        System.out.println("");

        ArrayList<String> actions = Keywords.getInstance().getGenericName(getAction());
        ArrayList<String> objects = Keywords.getInstance().getGenericName(getObject());

        ArrayList<InputType> inputTypes = new ArrayList<>();

        if (AhoCorasick.existsType(currentSentence, ACNodeType.EXIT)) inputTypes.add(InputType.EXIT);
        if (AhoCorasick.existsType(currentSentence, ACNodeType.AFFIRMATION)) inputTypes.add(InputType.AFFIRMATIVE);
        if (AhoCorasick.existsType(currentSentence, ACNodeType.NEGATION)) inputTypes.add(InputType.NEGATIVE);
        if (AhoCorasick.existsType(currentSentence, ACNodeType.HELLO)) inputTypes.add(InputType.HELLO);
        if (AhoCorasick.existsType(currentSentence, ACNodeType.HELP)) inputTypes.add(InputType.HELP);
        if (AhoCorasick.existsType(currentSentence, ACNodeType.TIME)) inputTypes.add(InputType.TIME);
        if (AhoCorasick.existsType(currentSentence, ACNodeType.HOW)) inputTypes.add(InputType.HOW);
        if (AhoCorasick.existsType(currentSentence, ACNodeType.WHO)) inputTypes.add(InputType.WHO);


        ArrayList<String> cMovieNames = new ArrayList<String>();
        ArrayList<String> cPeopleNames = new ArrayList<String>();

        // Capitalització dels noms propis. El corrector elimina les majuscules...
        for (String mn : movieNames) {
            cMovieNames.add(WordUtils.capitalizeFully(mn));
        }

        for (String pn : peopleNames) {
            cPeopleNames.add(WordUtils.capitalizeFully(pn));
        }

        peopleNames = cPeopleNames;
        movieNames = cMovieNames;

        if (actions.contains("my name is") && peopleNames.isEmpty())
            peopleNames.add(originalInput.replaceAll("^.*?(\\w+)\\W*$", "$1"));

        System.out.println("Detected object: " + Arrays.toString(objects.toArray()));
        System.out.println("Detected action: " + Arrays.toString(actions.toArray()));
        System.out.println("Detected movie name: " + Arrays.toString(movieNames.toArray()));
        System.out.println("Detected people: " + Arrays.toString(peopleNames.toArray()));
        System.out.println("Input flags are: " + Arrays.toString(inputTypes.toArray()));

        return new DigestedInput(objects, actions, movieNames, peopleNames, inputTypes);
    }

    private ArrayList<String> getObject() {
        return AhoCorasick.getAllFromType(currentSentence, ACNodeType.OBJECT);
    }

    private ArrayList<String> getAction() {
        return AhoCorasick.getAllFromType(currentSentence, ACNodeType.ACTION);
    }

    private ArrayList<String> getMovieName() {
        return AhoCorasick.getAllFromType(currentSentence, ACNodeType.MOVIE);
    }

    private ArrayList<String> getPersonNames() {
        return AhoCorasick.getAllFromType(currentSentence, ACNodeType.PERSON);
    }
}