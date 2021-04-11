public class DigestedInput {
    private String object;
    private String action;
    private String movieName;

    public DigestedInput(String object, String action, String movieName) {
        this.object = object;
        this.action = action;
        this.movieName = movieName;
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
