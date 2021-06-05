package Common;

import NLP.NLP;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.results.ResultList;
import java.time.format.DateTimeFormatter;

import java.util.LinkedList;

//TODO: Afegir respostes a thanks
//TODO: Afegir regex replaceAll("<.*?>",""); al reviews! (Elimina html)

public class Brain {
    // Si pasen 10 segons i no hi ha resposta, appeal!
    public static final long APPEAL_TIME = 20 * 1000;
    public static final int TEXT_SIZE = 16;

    private final UserInteraction ui;
    private Finestra f;
    private boolean vibrateNextTime;

    public Brain() {
        ui = new UserInteraction(this);
        vibrateNextTime = false;
    }

    private DBR computeResponse(DigestedInput di) {
        DBR response = new DBR(Behaviour.NLP_FAULT.getRandom());
        String action = di.getAction();

        try {
            if (action != null) {
                switch (action) {
                    case "describe" -> response = computeDescribe(di);
                    case "popular" -> response = computeTrending(di);
                    case "actor" -> response = computeActor(di);
                    case "think" -> response = computeReview(di);
                    case "released" -> response = computeYear(di);
                    case "similar" -> response = computeSimilar(di);
                    case "you're useless" -> response = new DBR(Behaviour.NLP_INSULT.getRandom());
                    case "fuck" -> response = new DBR(Behaviour.NLP_HARD_INSULT.getRandom());
                    case "my name is" -> response = updateUserName(di);
                    case "show" -> response = computeShowImage(di);
                }

            } else {
                LinkedList<InputType> inputType = di.getInputType().decompose();

                if (inputType.contains(InputType.HELP)) {
                    response = new DBR(Behaviour.HELP.getRandom());
                } else if (inputType.contains(InputType.HELLO)) {
                    response = new DBR(Behaviour.HELLO_MSG.getRandom());
                } else if (inputType.contains(InputType.AFFIRMATIVE) || inputType.contains(InputType.NEGATIVE)) {
                    response = new DBR(Behaviour.MEH_MSG.getRandom());
                } else if (inputType.contains(InputType.TIME)) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    response = new DBR(Behaviour.TIME.getRandom().formatted(dtf.format(java.time.LocalDateTime.now())));
                }
            }
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return response;
    }

    private DBR computeShowImage(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return new DBR(Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom());

        return DB.getInstance().getFilmImage(di.getMovieName(), new DefaultFallback<>(Behaviour.NLP_MOVIE_NOT_DETECTED));
    }

    private DBR updateUserName(DigestedInput di) {
        ui.setUserName(di.getMovieName());
        return new DBR(Behaviour.RESPONSE_FIRST_MEETING.getRandom().formatted(di.getMovieName()));
    }

    private DBR computeYear(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return new DBR(Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom());

        return DB.getInstance().getFilmDate(di.getMovieName(), new DefaultFallback<>(Behaviour.RESPONSE_NO_RESULTS_RELEASE));
    }

    private DBR computeReview(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return new DBR(Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom());

        return DB.getInstance().getMovieReview(di.getMovieName(), new DefaultFallback<>(Behaviour.RESPONSE_REVIEW_NOT_FOUND));
    }

    private DBR computeSimilar(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return new DBR(Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom());

        return DB.getInstance().getSimilarMovie(di.getMovieName(), new DefaultFallback<>(Behaviour.RESPONSE_NO_RESULTS_SIMILAR));
    }

    private DBR computeActor(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return new DBR(Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom());

        return DB.getInstance().getFilmActors(di.getMovieName(), new DefaultFallback<>(Behaviour.RESPONSE_NO_RESULTS_ACTORS));
    }

    private DBR computeTrending(DigestedInput di) throws MovieDbException {
        return new DBR(DB.getInstance().getTrendingMovie());
    }

    private DBR computeDescribe(DigestedInput di) throws MovieDbException {
        if (di.getMovieName() == null || di.getMovieName().isBlank())
            return new DBR(Behaviour.NLP_MOVIE_NOT_DETECTED.getRandom());

        return DB.getInstance().getFilmDescription(di.getMovieName(), new DefaultFallback<>(Behaviour.RESPONSE_NO_RESULTS_DESCRIPTION));
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

                if(di.getInputType().decompose().contains(InputType.EXIT)){
                    f.addToChat("Filme", Behaviour.DISMISS.getRandom());
                    break;
                }

                DBR response = computeResponse(di);
                ui.updateTimeToRead(response.getResponseText());

                if(response.isImage() && !response.isError()){
                    f.addImageToChat("Filme", response.getResponseText(), response.getImgUrl());
                }else{
                    f.addToChat("Filme",response.getResponseText());
                }
            } else {

                if (ui.timeSinceLastInteraction() > APPEAL_TIME) {
                    String message = !vibrateNextTime ? Behaviour.UI_APPEAL.getRandom() : Behaviour.UI_APPEAL_SAD.getRandom();

                    f.addToChat("Filme", message);
                    ui.interacted();

                    if(vibrateNextTime) f.zumbido();

                    vibrateNextTime = !vibrateNextTime;
                }
            }

//            System.out.println(ui.timeSinceLastInteraction());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (true);
        f.disableTextbox();
    }

    public static void main(String[] args) {
        Brain brain = new Brain();
        brain.think();
    }

    public Finestra getWindow() {
        return f;
    }
}
