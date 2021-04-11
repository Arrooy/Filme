public class DigestedInput {
    private String object;
    private String action;
    private String movieName;

    private boolean isAffirmative;
    private boolean isNegative;
    private boolean exit;
    private boolean hello;

    public DigestedInput(String object, String action, String movieName, boolean isAffirmative, boolean isNegative, boolean exit, boolean hello) {
        this.object = object;
        this.action = action;
        this.movieName = movieName;
        this.isAffirmative = isAffirmative;
        this.isNegative = isNegative;
        this.exit = exit;
        this.hello = hello;
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

    public boolean isAffirmative() {
        return isAffirmative;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public boolean isExit() {
        return exit;
    }

    public boolean isHello() {
        return hello;
    }
}
