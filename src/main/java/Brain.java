import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.results.ResultList;

import javax.management.MBeanServerInvocationHandler;

public class Brain {
    // Si pasen 10 segons i no hi ha resposta, good bye!
    public static final long APPEAL_TIME = 10 * 1000;
    public static final long VIBRATE_SCREEN_TIME = 15 * 1000;

    private UserInteraction ui;
    private Finestra f;

    public Brain() {
        ui = new UserInteraction(this);
    }

    private String computeResponse(DigestedInput di) {
        String response = "";
        String action = di.getAction();

        if (action == null) return Behaviour.NLP_FAULT.getRandom();

        try {
            switch (action) {
                case "describe" -> response = computeDescribe(di);
                case "popular" -> response = computeTrending(di);
                case "actor" -> response = computeActor(di);
                case "think" -> response = "Not yet bro!"; //TODO
                case "release" -> response = computeYear(di);
                case "similar" -> response = "Not yet bro!"; //TODO
                case "you're useless" -> response = Behaviour.NLP_INSULT.getRandom();
                case "my name is" -> response = updateUserName(di); //TODO
            }
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String updateUserName(DigestedInput di) {
        ui.setUserName(di.getMovieName());
        return "Nice to meet you " + di.getMovieName() + "!";
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

    private String computeActor(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null) return Behaviour.NLP_FAULT.getRandom();
        return (String) DB.getInstance().getFilmActors(di.getMovieName(), new Fallback<MovieInfo>() {
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
        if (di.getMovieName() == null) return Behaviour.NLP_FAULT.getRandom(); //TODO: Algo més específic?
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
        f.addToChat("Filme", Behaviour.WELCOME_MSG.getRandom());

        do {
            if (ui.hasInput()) {
                String input = ui.getInput();
                DigestedInput di = NLP.getInstance().process(input);
                if(di.userWantsToLeave()){
                    f.addToChat("Filme", Behaviour.DISMISS.getRandom());
                    break;
                }
                String response = computeResponse(di);
                f.addToChat("Filme",response);
            } else {

                if (ui.timeSinceLastInteraction() > APPEAL_TIME) {
                    f.addToChat("Filme", Behaviour.UI_APPEAL.getRandom());
                    ui.interacted();
                }


                if (ui.timeSinceLastZoombido() > VIBRATE_SCREEN_TIME) {
                    f.addToChat("Filme", Behaviour.UI_APPEAL_SAD.getRandom());
                    f.zumbido();
                    ui.interactedZoombido();
                }

            }
            //System.out.println(ui.timeSinceLastInteraction());

        } while (true);
    }

    public static void main(String[] args) {
        Brain brain = new Brain();
        brain.think();
    }

    public Finestra getWindow() {
        return f;
    }
}
