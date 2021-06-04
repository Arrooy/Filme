package Common;

public class DigestedInput {
    private final String object;
    private final String action;
    private final String movieName;

    private final boolean isAffirmative;
    private final boolean isNegative;
    private final boolean exit;
    private final boolean hello;
    private final boolean help;
    private final boolean time;

    public DigestedInput(String object, String action, String movieName, boolean isAffirmative, boolean isNegative, boolean exit, boolean hello, boolean isHelp, boolean isTime) {
        this.object = object;
        this.action = action;
        this.movieName = movieName;
        this.isAffirmative = isAffirmative;
        this.isNegative = isNegative;
        this.exit = exit;
        this.hello = hello;
        this.help = isHelp;
        this.time = isTime;
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

    public boolean isHelp() {
        return help;
    }

    public boolean isTime() {
        return time;
    }
}
