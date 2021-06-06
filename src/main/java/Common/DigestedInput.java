package Common;

import java.util.ArrayList;
import java.util.LinkedList;

public class DigestedInput {

    private final ArrayList<String> object;
    private final ArrayList<String> action;
    private final ArrayList<String> movieName;

    private final ArrayList<InputType> inputType;

    public DigestedInput(ArrayList<String> object, ArrayList<String> action, ArrayList<String> movieName, ArrayList<InputType> inputType) {
        this.object = object;
        this.action = action;
        this.movieName = movieName;
        this.inputType = inputType;
    }

    public ArrayList<InputType> getInputType(){
        return inputType;
    }

    //TODO: MODIFICAR EL GET 0.
    public String getObject() {
        if(object.size() == 0)return "";
        return object.get(0);
    }

    public String getAction() {
        if(action.size() == 0)return "";
        return action.get(0);
    }

    public String getMovieName() {
        if(movieName.size() == 0)return "";
        return movieName.get(0);
    }
}
