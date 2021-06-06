package Common;

import Corrector.Symspell;
import NLP.AhoCorasick.ACLoader;
import NLP.AhoCorasick.AhoCorasick;
import NLP.NLP;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import io.github.mightguy.spellcheck.symspell.exception.SpellCheckException;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

public class Brain {
    // Si pasen 10 segons i no hi ha resposta, appeal!
    public static final long APPEAL_TIME = 20 * 1000;
    public static final int TEXT_SIZE = 16;

    private static final boolean TEST_SUITE = true;

    private final UserInteraction ui;
    private Finestra f;
    private boolean vibrateNextTime;

    public Brain() {
        ui = new UserInteraction(this);
        vibrateNextTime = false;
    }

    // Donat un digested input -> genera una resposta (DBR)
    private ArrayList<DBR> computeResponse(DigestedInput di)  {
        ArrayList<DBR> response = new ArrayList<>();


        String action = di.getAction().isEmpty() ? "" : di.getAction().get(0);

        if (!action.equals("")) {
            switch (action) {
                case "describe" -> response.addAll(computeDescribe(di));
                case "popular" -> response.addAll(computeTrending(di));
                case "actor" -> response.addAll(computeActor(di));
                case "think" -> response.addAll(computeReview(di));
                case "released" -> response.addAll(computeYear(di));
                case "similar" -> response.addAll(computeSimilar(di));
                case "you're useless" -> response.add(new DBR(Behaviour.NLP_INSULT.getRandom()));
                case "fuck" -> response.add(new DBR(Behaviour.NLP_HARD_INSULT.getRandom()));
                case "my name is" -> response.addAll(updateUserName(di));
                case "show" -> response.addAll(computeShowImage(di));
            }
        } else {
            ArrayList<InputType> inputType = di.getInputType();

            if (inputType.contains(InputType.HELP)) {
                response.add(new DBR(Behaviour.HELP.getRandom()));

            } else if (inputType.contains(InputType.HELLO)) {
                response.add(new DBR(Behaviour.HELLO_MSG.getRandom()));

            } else if (inputType.contains(InputType.AFFIRMATIVE) || inputType.contains(InputType.NEGATIVE)) {
                response.add(new DBR(Behaviour.MEH_MSG.getRandom()));

            } else if (inputType.contains(InputType.TIME)) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                response.add(new DBR(Behaviour.TIME.getRandom().formatted(dtf.format(java.time.LocalDateTime.now()))));

            } else if (inputType.contains(InputType.HOW)) {
                response.add(new DBR(Behaviour.HOW.getRandom()));

            } else if (inputType.contains(InputType.WHO)) {
                response.add(new DBR(Behaviour.WHO.getRandom()));
            }
        }

        // Fallbck per si no es detecta res de res.
        if (response.isEmpty()) response.add(new DBR(Behaviour.NLP_FAULT.getRandom()));

        return response;
    }


    private class GenericHelper {
        private final String content;
        private final Fallback<MovieInfo> fallback;

        public GenericHelper(String content, Fallback<MovieInfo> fallback) {
            this.content = content;
            this.fallback = fallback;
        }

        public String getContent() {
            return content;
        }

        public Fallback<MovieInfo> getFallback() {
            return fallback;
        }
    }

    private ArrayList<DBR> genericMultipleCompute(DigestedInput di, Behaviour errorBehaviour, Behaviour errorQuery, Function<DigestedInput, ArrayList<String>> extractArray,
                                                  Function<GenericHelper, DBR> DBQuery) {
        ArrayList<DBR> res = new ArrayList<>();

        if (extractArray.apply(di) == null || extractArray.apply(di).isEmpty()) {
            res.add(new DBR(errorBehaviour.getRandom()));
            return res;
        }


        for (String movie : extractArray.apply(di)) {
            DBR result = DBQuery.apply(new GenericHelper(movie, new DefaultFallback<>(errorQuery)));
            if (result != null) res.add(result);
        }

        return res;
    }

    private ArrayList<DBR> computeShowImage(DigestedInput di) {
        return genericMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.NLP_MOVIE_NOT_DETECTED,
                (DigestedInput::getMovieName), x -> DB.getInstance().getFilmImage(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeYear(DigestedInput di) {
        return genericMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_RELEASE,
                (DigestedInput::getMovieName), x -> DB.getInstance().getFilmDate(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeReview(DigestedInput di) {
        return genericMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_REVIEW_NOT_FOUND,
                (DigestedInput::getMovieName), x -> DB.getInstance().getMovieReview(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeSimilar(DigestedInput di) {
        return genericMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_SIMILAR,
                (DigestedInput::getMovieName), x -> DB.getInstance().getSimilarMovie(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeActor(DigestedInput di) {
        return genericMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_ACTORS,
                (DigestedInput::getMovieName), x -> DB.getInstance().getFilmActors(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeTrending(DigestedInput di) {
        ArrayList<DBR> res = new ArrayList<>();
        res.add(DB.getInstance().getTrendingMovie());
        return res;
    }

    private ArrayList<DBR> computeDescribe(DigestedInput di) {
        return genericMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_DESCRIPTION,
                (DigestedInput::getMovieName), x -> DB.getInstance().getFilmDescription(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> updateUserName(DigestedInput di) {
        ArrayList<DBR> res = new ArrayList<>();
        ui.setUserName(di.getMovieName().get(0));
        res.add(new DBR(Behaviour.RESPONSE_FIRST_MEETING.getRandom().formatted(di.getMovieName().get(0))));
        return res;
    }

    public void think() {
        f = new Finestra();
        f.attach(ui);
        f.setVisible(true);
        f.requestWrite();
        f.addToChat("Filme", Behaviour.WELCOME_MSG.getRandom());

        do {
            if (ui.hasInput()) {
                String input = ui.getInput();
                DigestedInput di = NLP.getInstance().process(input);

                if (di.getInputType().contains(InputType.EXIT)) {
                    f.addToChat("Filme", Behaviour.DISMISS.getRandom());
                    break;
                }

                ArrayList<DBR> dbresponse = computeResponse(di);
                DBR response = dbresponse.get(0);

                ui.updateTimeToRead(response.getResponseText());

                if (response.isImage() && !response.isError()) {
                    f.addImageToChat("Filme", response.getResponseText(), response.getImgUrl());
                } else {
                    f.addToChat("Filme", response.getResponseText());
                }
            } else {

                if (ui.timeSinceLastInteraction() > APPEAL_TIME) {
                    String message = !vibrateNextTime ? Behaviour.UI_APPEAL.getRandom() : Behaviour.UI_APPEAL_SAD.getRandom();

                    f.addToChat("Filme", message);
                    ui.interacted();

                    if (vibrateNextTime) f.zumbido();

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

    //
    public static void main(String[] args) {

        ACLoader.loadData();

        NLP.getInstance();
        AhoCorasick.getInstance().init();

        Brain brain = new Brain();
        try {
            System.out.println("Loading English words...");
            Symspell.getInstance().loadDict();
        } catch (IOException | SpellCheckException e) {
            e.printStackTrace();
        }

        if (TEST_SUITE) {
            ArrayList<String> questions = new ArrayList<>(Arrays.asList("How are you?", "What do you know about cars?",
                    "What is you opinion on Cars?", "Yes or no?", "Hello", "Hello, who are you?", "Can you help me?",
                    "What time is it?", "What's the hottest movie atm?", "What are your thoughts on Cars?",
                    "Describe Cars", "When did Cars come out?", "Name the actors from Cars", "Give me similar movies to Cars!"));

            for (String question : questions) {
                System.out.println("**********************************");
                System.out.println("Question: " + question);
                String filteredQuestion = brain.filterLine(question);
                System.out.println("Filtered: " + filteredQuestion);
                DigestedInput di = NLP.getInstance().process(filteredQuestion);

                if (di.getInputType().contains(InputType.EXIT)) {
                    System.out.println("!Exit executed!");
                    continue;
                }

                ArrayList<DBR> dbresponse = brain.computeResponse(di);
                int i = 0;
                for(DBR response : dbresponse){
                    System.out.println("Response" + i + ": " + (response.isError() ? "is an error." : (response.isImage() ? "is an image" : response.getResponseText())) + "\n");
                    i++;
                }
            }
        } else {
            brain.think();
        }
    }

    private String filterLine(String line) {
        //Eliminem caràcters que no siguin ascii
        line = line.replaceAll("[^\\p{ASCII}]", "");

        line = line.replaceAll("[ñÑ]", "n");
        line = line.replaceAll("[çÇ]", "c");

        //Eliminem simbols ascii que no son d'interes.
        line = line.replaceAll("[^a-zA-Z\\d\\s:,.]", "");

        // Eliminem espais i salts de linia multiples.
        line = line.trim().replaceAll("( )+", " ");
        line = line.replaceAll("(\n)+", "\n");
        return line;
    }

    public Finestra getWindow() {
        return f;
    }

}
