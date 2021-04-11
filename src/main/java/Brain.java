import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.results.ResultList;

public class Brain {
    // Si pasen 10 segons i no hi ha resposta, appeal!
    public static final long APPEAL_TIME = 20 * 1000;

    private final UserInteraction ui;
    private Finestra f;
    private boolean vibrateNextTime;

    public Brain() {
        ui = new UserInteraction(this);
        vibrateNextTime = false;
    }

    private String computeResponse(DigestedInput di) {
        String response = "";
        String action = di.getAction();

        try {
            if(action != null){
                switch (action) {
                    case "describe" -> response = computeDescribe(di);
                    case "popular" -> response = computeTrending(di);
                    case "actor" -> response = computeActor(di);
                    case "think" -> response = computeReview(di);
                    case "released" -> response = computeYear(di);
                    case "similar" -> response = computeSimilar(di);
                    case "you're useless" -> response = Behaviour.NLP_INSULT.getRandom();
                    case "fuck" -> response = Behaviour.NLP_HARD_INSULT.getRandom();
                    case "my name is" -> response = updateUserName(di);
                }

            }else{
                if (di.isHello()) {
                    response = Behaviour.HELLO_MSG.getRandom();
                } else if (di.isHelp()) {
                    response = Behaviour.HELP.getRandom();
                }else if (di.isAffirmative() || di.isNegative()) {
                    response = Behaviour.MEH_MSG.getRandom();
                }
                if(response.isBlank()) return Behaviour.NLP_FAULT.getRandom();
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
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom();
        return DB.getInstance().getFilmDate(di.getMovieName(), new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return Behaviour.RESPONSE_NO_RESULTS_RELEASE.getRandom().formatted(queryUsed);
            }

            @Override
            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
                return results.getResults().get(0);
            }
        });
    }

    private String computeReview(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom();

        return DB.getInstance().getMovieReview(di.getMovieName(), new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return "I'm afraid i didn't watch that film...";
            }

            @Override
            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
                return results.getResults().get(0);
            }
        }).replaceAll("<.*?>","");
    }

    private String computeSimilar(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom();

        return DB.getInstance().getSimilarMovie(di.getMovieName(), new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return "I'm afraid i didn't watch that film...";
            }

            @Override
            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
                return results.getResults().get(0);
            }
        });
    }

    private String computeActor(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom();
        return (String) DB.getInstance().getFilmActors(di.getMovieName(), new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return Behaviour.RESPONSE_NO_RESULTS_ACTORS.getRandom();
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
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom();
        return DB.getInstance().getFilmDescription(di.getMovieName(), new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return Behaviour.RESPONSE_NO_RESULTS_DESCRIPTION.getRandom().formatted(queryUsed);
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
                if(di.isExit()){
                    f.addToChat("Filme", Behaviour.DISMISS.getRandom());
                    break;
                }
                String response = computeResponse(di);
                ui.updateTimeToRead(response);
                f.addToChat("Filme",response);
            } else {

                if (ui.timeSinceLastInteraction() > APPEAL_TIME) {
                    String message = !vibrateNextTime ? Behaviour.UI_APPEAL.getRandom() : Behaviour.UI_APPEAL_SAD.getRandom();

                    f.addToChat("Filme", message);
                    ui.interacted();

                    if(vibrateNextTime) f.zumbido();

                    vibrateNextTime = !vibrateNextTime;
                }
            }

            System.out.println(ui.timeSinceLastInteraction());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
        f.disableTextbox();
        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        f.dispose();
        System.exit(0);*/
    }

    public static void main(String[] args) {
        Brain brain = new Brain();
        brain.think();
    }

    public Finestra getWindow() {
        return f;
    }
}
