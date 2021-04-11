import java.util.Arrays;

public class NLP {

    private Keywords keywords;
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

        System.out.println("Detected object: " + object);
        System.out.println("Detected action: " + action);
        System.out.println("Detected movie name: " + movieName);

        return new DigestedInput(object, action, movieName);
    }

    private String getObject() {
        for (String object: keywords.getObjects())
            for (String synonym: keywords.getSynonyms().get(object))
                if (currentSentence.contains(synonym)) return object;
        return null;
    }

    private String getAction() {
        String result = null;
        for (String action: keywords.getActions())
            for (String synonym: keywords.getSynonyms().get(action))
                if (currentSentence.contains(synonym)) {
                    result = action;
                    break;
                }
        if (result != null) return result;

        for (String spAction: keywords.getSpecialActions())
            for (String synonym: keywords.getSynonyms().get(spAction))
                if (currentSentence.contains(synonym)) {
                    result = spAction;
                    currentSentence = currentSentence.replaceAll(synonym, "");
                    break;
                }
        return result;
    }

    //S'agafa tota la string a partir de després de l'últim precedent
    //Si no est troba un precedent, es podria buscar el nom de la película a partir del nom de l'action o l'object
    private String getMovieName() {
        int lastIndex = -1;
        for (String precedent: keywords.getPrecedents()) {
            int index = currentSentence.lastIndexOf(precedent);
            int newLastIndex = (index + precedent.length() + 1);
            if (index != -1 && lastIndex < newLastIndex)
                lastIndex = newLastIndex;
        }
        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        lastIndex = searchLastIndex(keywords.getActions());
        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        lastIndex = searchLastIndex(keywords.getSpecialActions());
        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        lastIndex = searchLastIndex(keywords.getObjects());
        if (lastIndex != -1) return currentSentence.substring(lastIndex);

        return null;
    }

    private int searchLastIndex(String[] options) {
        int lastIndex = -1;
        for (String spAction: options)
            for (String synonym: keywords.getSynonyms().get(spAction))
                if (currentSentence.contains(synonym)) {
                    int index = currentSentence.lastIndexOf(synonym);
                    int newLastIndex = (index + synonym.length() + 1);
                    if (index != -1 && lastIndex < newLastIndex)
                        lastIndex = newLastIndex;
                }
        return lastIndex;
    }

    public boolean isInputExit (String input) {
        for (String s: keywords.getExitExpressions())
            if (input.equals(s)) return true;
        return false;
    }
}