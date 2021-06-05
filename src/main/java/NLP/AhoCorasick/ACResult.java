package NLP.AhoCorasick;

public class ACResult {

    private final ACNodeType type;
    private final String value;

    public ACResult(ACNodeType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ACNodeType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        String type;

        switch (this.type) {
            case MOVIE -> type = "Movie";
            case PERSON -> type = "Person";
            case OBJECT -> type = "Object";
            case ACTION -> type = "Action";
            case PRECEDENT -> type = "Precedent";
            case AFFIRMATION -> type = "Affirmation";
            case NEGATION -> type = "Negation";
            case HELLO -> type = "Hello";
            case HELP -> type = "Help";
            case TIME -> type = "Time";
            case EXIT -> type = "Exit";
            case HOW -> type = "How";
            case WHO -> type = "Who";
            default -> type = "<None>";
        }

        return type + ": " + value;
    }
}
