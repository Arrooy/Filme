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
            case ACTION -> type = "Action";
            default -> type = "<None>";
        }

        return type + ": " + value;
    }
}
