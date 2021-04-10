import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;

public class Brain {

    public Brain() {

    }

    private void processInput(String input) {

    }

    private boolean isUserActive() {
        return true;
    }

    private String computeResponse() {
        return "";
    }

    public static void main(String[] args) {
        try {
            TheMovieDbApi a = new TheMovieDbApi("08e795429cbf9b280d0790fcd324aa40");
            var x = a.searchMovie("Hollywood",0,"en-US",false,0,0, SearchType.PHRASE);
            System.out.println(x.getResults().get(0).getTitle());
        } catch (MovieDbException e) {
            e.printStackTrace();
        }

//        UserInteraction ui = new UserInteraction();
//        Brain brain = new Brain();
//        do{
//            String input = ui.getInput();
//            brain.processInput(input);
//            ui.ask(brain.computeResponse());
//        }while (brain.isUserActive());
    }
}
