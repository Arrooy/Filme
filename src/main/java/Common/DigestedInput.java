package Common;

import java.util.ArrayList;

public class DigestedInput {

    private final ArrayList<String> object;
    private final ArrayList<String> action;
    private final ArrayList<String> movieName;

    private final ArrayList<InputType> inputType;
    private final ArrayList<String> people;

    public DigestedInput(ArrayList<String> object, ArrayList<String> action, ArrayList<String> movieName,ArrayList<String> people, ArrayList<InputType> inputType) {
        this.object = object;
        this.action = action;
        this.movieName = movieName;
        this.inputType = inputType;
        this.people = people;
    }

    public ArrayList<InputType> getInputType(){
        return inputType;
    }

    public ArrayList<String> getObject() {
        return object;
    }

    public ArrayList<String> getAction() {
        return action;
    }

    public ArrayList<String> getMovieName() {
        return movieName;
    }

    public ArrayList<String> getPeople() {
        return people;
    }
}
