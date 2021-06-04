package NLP;

import Common.DigestedInput;
import Common.InputType;

public class NLP {

    private final Keywords keywords;
    private String currentSentence;

    private static NLP singleton;


    public NLP() {
        keywords = new Keywords("res/keywords.txt");
    }

    public static NLP getInstance() {
        if (singleton == null) singleton = new NLP();
        return singleton;
    }

    public DigestedInput process(String input) {
        if (input.endsWith("?")) input = input.substring(0, input.length()-1);

        currentSentence = input;

        String action = getAction();
        String object = getObject();
        String movieName = getMovieName();
        if(movieName != null) movieName = movieName.trim();

        InputType inputType = InputType.BASE;

        if(isInGroup(keywords.getExitExpressions())) inputType = inputType.add(InputType.EXIT);
        if(isInGroup(keywords.getAffirmations())) inputType = inputType.add(InputType.AFFIRMATIVE);
        if(isInGroup(keywords.getNegations())) inputType = inputType.add(InputType.NEGATIVE);
        if(isInGroup(keywords.getHelloExpressions())) inputType = inputType.add(InputType.HELLO);
        if(isInGroup(keywords.getHelpExpressions())) inputType = inputType.add(InputType.HELP);
        if(isInGroup(keywords.getTimeExpressions())) inputType = inputType.add(InputType.TIME);

        System.out.println("Detected object: " + object);
        System.out.println("Detected action: " + action);
        System.out.println("Detected movie name: " + movieName);

        return new DigestedInput(object, action, movieName, inputType);
    }

    private String getObject() {
        for (String object: keywords.getObjects())
            for (String synonym: keywords.getSynonyms().get(object))
                if (currentSentence.contains(synonym)) {
                    deleteIfNecessary(synonym);
                    return object;
                }
        return null;
    }

    private String getAction() {
        String result = null;
        for (String action: keywords.getActions()) {
            for (String synonym : keywords.getSynonyms().get(action))
                if (currentSentence.contains(synonym)) {
                    result = action;
                    deleteIfNecessary(synonym);
                    break;
                }
            if (result != null) return result;
        }
        return null;
    }

    private void deleteIfNecessary(String s) {
        int lastIndex = currentSentence.lastIndexOf(s) + s.length();
        if (lastIndex == currentSentence.length())
            currentSentence = currentSentence.replaceAll(s, "");
    }

    //S'agafa tota la string a partir de després de l'últim precedent
    //Si no est troba un precedent, es podria buscar el nom de la película a partir del nom de l'action o l'object
    private String getMovieName() {
        int lastIndex = -1;
        int newLastIndex;
        for (String precedent: keywords.getPrecedents()) {
            int index = currentSentence.lastIndexOf(precedent);
            newLastIndex = index + precedent.length();
            if (index != -1 && lastIndex < newLastIndex)
                lastIndex = newLastIndex;
        }
        /*

        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        lastIndex = searchLastIndex(keywords.getActions());
        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        lastIndex = searchLastIndex(keywords.getSpecialActions());
        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        lastIndex = searchLastIndex(keywords.getObjects());
        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        return null;*/

        newLastIndex = searchLastIndex(keywords.getActions());
        if (lastIndex < newLastIndex) lastIndex = newLastIndex;

        newLastIndex = searchLastIndex(keywords.getObjects());
        if (lastIndex < newLastIndex) lastIndex = newLastIndex;

        if (lastIndex == -1) return null;
        return currentSentence.substring(lastIndex);
    }

    private int searchLastIndex(String[] options) {
        int lastIndex = -1;
        for (String spAction: options)
            for (String synonym: keywords.getSynonyms().get(spAction))
                if (currentSentence.contains(synonym)) {
                    int index = currentSentence.lastIndexOf(synonym);
                    int newLastIndex = index + synonym.length();
                    if (index != -1 && lastIndex < newLastIndex)
                        lastIndex = newLastIndex;
                }
        return lastIndex;
    }

    public boolean isInGroup(String[] group) {
        for (String s: group)
            if (currentSentence.contains(s)) return true;
        return false;
    }
}