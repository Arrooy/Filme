import java.util.Arrays;

public class NLP {

    private Keywords keywords;

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

        String object = getObject(input);
        String action = getAction(input);
        String movieName = getMovieName(input);

        System.out.println("Detected object: " + object);
        System.out.println("Detected action: " + action);
        System.out.println("Detected movie name: " + movieName);

        return new DigestedInput(object, action);
    }

    private String getObject(String input) {
        for (String object: keywords.getObjects())
            for (String synonym: keywords.getSynonyms().get(object))
                if (input.contains(synonym)) return object;
        return null;
    }

    private String getAction(String input) {
        for (String action: keywords.getActions())
            for (String synonym: keywords.getSynonyms().get(action))
                if (input.contains(synonym)) return action;
        return null;
    }

    private String getMovieName(String s) {
        for (String action: keywords.getPrecedents()) {
            int index = s.lastIndexOf(action);
            if (index != -1) return s.substring(index + action.length() + 1);
        }
        return null;
    }

    public boolean isInputExit (String input) {

        return false;
    }
}