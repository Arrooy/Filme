import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;

public class Brain {

    private boolean exit = false;
    private String input;
    private String answer;

    public Brain() {

    }

    private void processInput () {
        NLP nlp = new NLP();
        exit = nlp.isInputExit(input);
        answer = nlp.process(input);
        //TODO: Tendremos que hacer "exit = false" si vemos que el usuario se despide de nosotros
    }

    private boolean isUserActive() {
        return !exit;
    }

    private String computeResponse() {
        // De momento printamos lo que nos viene del NLP tal cual, sin nada mas
        return answer;
    }

    public static void main(String[] args) {
        UserInteraction ui = new UserInteraction();
        Brain brain = new Brain();
        do {
            brain.input = ui.getInput();
            brain.processInput();
            ui.print(brain.computeResponse());
        } while (brain.isUserActive());
    }
}
