package NLP;

import Common.DigestedInput;

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
        boolean isExit = isInGroup(keywords.getExitExpressions());
        boolean isAffirmative = isInGroup(keywords.getAffirmations());
        boolean isNegative = isInGroup(keywords.getNegations());
        boolean isHello = isInGroup(keywords.getHelloExpressions());
        boolean isHelp = isInGroup(keywords.getHelpExpressions());
        boolean isTime = isInGroup(keywords.getTimeExpressions());

        /*
        System.out.println("Detected object: " + object);
        System.out.println("Detected action: " + action);
        System.out.println("Detected movie name: " + movieName);
        System.out.println("Is exit: " + isExit);
        System.out.println("Is affirmative: " + isAffirmative);
        System.out.println("Is negative: " + isNegative);
        System.out.println("Is hello: " + isHello);
        System.out.println("Is help: " + isHelp);
         */
        return new DigestedInput(object, action, movieName, isAffirmative, isNegative, isExit, isHello, isHelp, isTime);
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