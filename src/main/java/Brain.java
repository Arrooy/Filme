import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.results.ResultList;

public class Brain {
    // Si pasen 10 segons i no hi ha resposta, good bye!
    private static final long TIME_EQUALS_DISCONNECT = 10 * 1000;
    private UserInteraction ui;
    private Finestra f;

    public Brain() {
        ui = new UserInteraction(this);
    }

    private DigestedInput processInput (String input) {
        NLP nlp = NLP.getInstance();
        return nlp.process(input);
    }

    private boolean isUserActive() {
        return ui.timeSinceLastInteraction() > TIME_EQUALS_DISCONNECT;
    }

    private String computeResponse(DigestedInput di) {
        String response = "";
        String action = di.getAction();
        try {
            if(action.equals("describe")) {
                response = computeDescribe(di);
            }else if(action.equals("trending")){
                response = computeTrending(di);
            }else if(action.equals("year")){
                response = computeYear(di);
            }

        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String computeYear(DigestedInput di) throws MovieDbException {
        return DB.getInstance().getFilmDate(di.getMovieName(), new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return "Movie " + queryUsed + " not found!";
            }

            @Override
            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
                return results.getResults().get(0);
            }
        });
    }

    private String computeTrending(DigestedInput di) throws MovieDbException {
        return DB.getInstance().getTrendingMovie();
    }

    private String computeDescribe(DigestedInput di) throws MovieDbException {
        return DB.getInstance().getFilmDescription(di.getMovieName(), new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return "Description not found!";
            }

            @Override
            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
                return results.getResults().get(0);
            }
        });

    }

    public void think(){

        f = new Finestra();
        f.attach(ui);
        f.setVisible(true);
        f.requestWrite();

        do {
            if(ui.hasInput()){
                String input = ui.getInput();
                System.out.println(input);
            }
//            DigestedInput pi = processInput(input);
//            String response = computeResponse(pi);
//            ui.print(response);
        } while (isUserActive());
    }

    public static void main(String[] args) {
        Brain brain = new Brain();
        brain.think();
    }

    public Finestra getWindow() {
        return f;
    }
}
