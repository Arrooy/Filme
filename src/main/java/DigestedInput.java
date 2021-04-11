public class DigestedInput {
    private String object;
    private String action;
    private String movieName;

    private boolean isAffirmative;
    private boolean isNegative;
    private boolean exit;

    public DigestedInput(String object, String action, String movieName, boolean isAffirmative, boolean isNegative, boolean exit) {
        this.object = object;
        this.action = action;
        this.movieName = movieName;
        this.isAffirmative = isAffirmative;
        this.isNegative = isNegative;
        this.exit = exit;
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

    public boolean userWantsToLeave() {
        return exit;
    }
}
