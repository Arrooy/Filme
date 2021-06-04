package Common;

public class DigestedInput {
    private final String object;
    private final String action;
    private final String movieName;

    private final InputType inputType;

    public DigestedInput(String object, String action, String movieName, InputType inputType) {
        this.object = object;
        this.action = action;
        this.movieName = movieName;
        this.inputType = inputType;
    }

    public InputType getInputType(){
        return inputType;
    }

    public String getObject() {
        return object;
    }

    public String getAction() {
        return action;
    }

    public String getMovieName() {
        return movieName;
    }
}
