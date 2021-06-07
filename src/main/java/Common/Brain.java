package Common;

import Corrector.Symspell;
import NLP.AhoCorasick.ACLoader;
import NLP.AhoCorasick.AhoCorasick;
import NLP.NLP;
import com.omertron.themoviedbapi.model.person.PersonFind;
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

    private static final boolean TEST_SUITE = false;
    private static final boolean DEBUG = false;


    private final UserInteraction ui;
    private Finestra f;
    private boolean vibrateNextTime;

    public Brain() {
        ui = new UserInteraction(this);
        vibrateNextTime = false;
    }

    // Per si en un futur s'implanta preferencia d'accions.
    private String getPriorityAction(DigestedInput di) {
        return di.getAction().get(0);
    }

    // Donat un digested input -> genera una resposta (DBR)
    private ArrayList<DBR> computeResponse(DigestedInput di) {

        ArrayList<DBR> response = new ArrayList<>();
        boolean responseNotFound = true;

        String action = di.getAction().isEmpty() ? "" : getPriorityAction(di);

        // Genre no es una acció, pero té comportaments associats quan es troba sense accio.
        // Si no hi ha accio, pero apareix genre ->  mirar genre duna peliucla.
        if (di.getAction().isEmpty() && di.getObject().contains("genre")) {
            response.addAll(computeFilmGenre(di));
            responseNotFound = false;
        }

        // Proporcionar les pelicules d'un actor en concret.
        else if (di.getAction().isEmpty() && di.getObject().contains("movie") && !di.getPeople().isEmpty()) {
            response.addAll(computeFilmsFromActor(di));
            responseNotFound = false;
        }

        // La action acted no es correcte. Realment l'usuari vol saber quines pelis han participat els actors.
        // Si es troba together a object, es farà cerca de les pelicules conjnutes.
        else if (action.equals("acted") && di.getMovieName().isEmpty() && !di.getPeople().isEmpty()) {
            response.addAll(computeFilmsFromActor(di));
            responseNotFound = false;
        } else if (di.getAction().isEmpty() && di.getMovieName().isEmpty() && !di.getPeople().isEmpty() && di.getObject().contains("together")) {
            response.addAll(computeFilmsFromActor(di));
            responseNotFound = false;
        }

        if (responseNotFound) {
            if (!action.equals("")) {
                switch (action) {
                    case "describe" -> response.addAll(computeDescribe(di));
                    case "popular" -> response.addAll(computeTrending(di));
                    case "acted" -> response.addAll(computeActor(di));
                    case "think" -> response.addAll(computeReview(di));
                    case "released" -> response.addAll(computeYear(di));
                    case "similar" -> response.addAll(computeSimilar(di));
                    case "you're useless" -> response.add(new DBR(Behaviour.NLP_INSULT.getRandom()));
                    case "fuck" -> response.add(new DBR(Behaviour.NLP_HARD_INSULT.getRandom()));
                    case "my name is" -> response.addAll(updateUserName(di));
                    case "image" -> response.addAll(computeShowImage(di));
                    case "age" -> response.addAll(computeActorAge(di));
                }
            } else {
                ArrayList<InputType> inputType = di.getInputType();

                // Casos concrets per afegeix cooerencia a les preguntes
                if (inputType.containsAll(Arrays.asList(InputType.HELLO, InputType.WHO))) {
                    response.add(new DBR(Behaviour.HELLO_MSG.getRandom() + " " + Behaviour.WHO.getRandom()));

                } else if (inputType.containsAll(Arrays.asList(InputType.HELLO, InputType.HOW))) {
                    response.add(new DBR(Behaviour.HELLO_MSG.getRandom() + " " + Behaviour.HOW.getRandom()));

                    // Casos base amb respostes directes.
                } else if (inputType.contains(InputType.HELP)) {
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
        }

        // Fallback per si no es detecta res de res.
        if (response.isEmpty()) response.add(new DBR(Behaviour.NLP_FAULT.getRandom()));

        return response;
    }

    // TODO: Es pot juntar es dos generic Multiple compute?
    private ArrayList<DBR> genericPesonMultipleCompute(DigestedInput di, Behaviour errorBehaviour, Behaviour errorQuery, Function<DigestedInput, ArrayList<String>> extractArray,
                                                       Function<GenericPeopleHelper, DBR> DBQuery) {
        ArrayList<DBR> res = new ArrayList<>();

        if (extractArray.apply(di) == null || extractArray.apply(di).isEmpty()) {
            res.add(new DBR(errorBehaviour.getRandom()));
            return res;
        }

        for (String movie : extractArray.apply(di)) {
            DBR result = DBQuery.apply(new GenericPeopleHelper(movie, new DefaultFallback<PersonFind>(errorQuery)));
            if (result != null) res.add(result);
        }

        return res;
    }

    private ArrayList<DBR> genericMovieMultipleCompute(DigestedInput di, Behaviour errorBehaviour, Behaviour errorQuery,
                                                       Function<DigestedInput, ArrayList<String>> extractArray,
                                                       Function<GenericMovieHelper, DBR> DBQuery) {
        ArrayList<DBR> res = new ArrayList<>();

        if (extractArray.apply(di) == null || extractArray.apply(di).isEmpty()) {
            res.add(new DBR(errorBehaviour.getRandom(), true));
            return res;
        }

        for (String movie : extractArray.apply(di)) {
            DBR result = DBQuery.apply(new GenericMovieHelper(movie, new DefaultFallback<>(errorQuery)));
            if (result != null) res.add(result);
        }

        return res;
    }


    private ArrayList<DBR> computeFilmsFromActor(DigestedInput di) {
        if (di.getObject().contains("together")) {
            ArrayList<DBR> res = new ArrayList<>();
            res.add(DB.getInstance().getActorFilmsTogether(di.getPeople(), new DefaultFallback<>(Behaviour.RESPONSE_NOT_RESULTS_ACTOR_FILMS_TOGETHER)));
            return res;
        } else {
            return genericPesonMultipleCompute(di, Behaviour.NLP_ACTOR_NOT_DETECTED, Behaviour.RESPONSE_NOT_RESULTS_ACTOR_FILMS,
                    (DigestedInput::getPeople), x -> DB.getInstance().getActorFilms(x.getContent(), x.getFallback()));
        }
    }

    private ArrayList<DBR> computeActorAge(DigestedInput di) {
        return genericPesonMultipleCompute(di, Behaviour.NLP_ACTOR_NOT_DETECTED, Behaviour.RESPONSE_NOT_RESULTS_AGE,
                (DigestedInput::getPeople), x -> DB.getInstance().getActorAge(x.getContent(), x.getFallback()));
    }


    private ArrayList<DBR> computeFilmGenre(DigestedInput di) {
        return genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NOT_RESULTS_MOVIE_GENRE,
                (DigestedInput::getMovieName), x -> DB.getInstance().getFilmGenre(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeShowImage(DigestedInput di) {
        ArrayList<DBR> res = new ArrayList<>();
        if (!di.getPeople().isEmpty()) {
            res.addAll(genericPesonMultipleCompute(di, Behaviour.NLP_ACTOR_NOT_DETECTED, Behaviour.RESPONSE_NOT_RESULTS_IMAGE,
                    (DigestedInput::getPeople), x -> DB.getInstance().getActorImage(x.getContent(), x.getFallback())));

            // Ens interessa forçar l'error.
            res.addAll(genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NOT_RESULTS_IMAGE,
                    (DigestedInput::getMovieName), x -> DB.getInstance().getFilmImage(x.getContent(), x.getFallback())));
            return res;
        } else {
            return genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NOT_RESULTS_IMAGE,
                    (DigestedInput::getMovieName), x -> DB.getInstance().getFilmImage(x.getContent(), x.getFallback()));
        }
    }

    private ArrayList<DBR> computeYear(DigestedInput di) {
        return genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_RELEASE,
                (DigestedInput::getMovieName), x -> DB.getInstance().getFilmDate(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeReview(DigestedInput di) {
        return genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_REVIEW_NOT_FOUND,
                (DigestedInput::getMovieName), x -> DB.getInstance().getMovieReview(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeSimilar(DigestedInput di) {
        return genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_SIMILAR,
                (DigestedInput::getMovieName), x -> DB.getInstance().getSimilarMovie(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeActor(DigestedInput di) {
        return genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_ACTORS,
                (DigestedInput::getMovieName), x -> DB.getInstance().getFilmActors(x.getContent(), x.getFallback()));
    }

    private ArrayList<DBR> computeTrending(DigestedInput di) {
        ArrayList<DBR> res = new ArrayList<>();

        for (String obj : di.getObject()) {
            DBR dbRes = null;
            if (obj.equalsIgnoreCase("movie"))
                dbRes = DB.getInstance().getTrendingMovie();
            else if (obj.equalsIgnoreCase("actor"))
                dbRes = DB.getInstance().getTrendingActor();
            else if (obj.equalsIgnoreCase("genre"))
                dbRes = DB.getInstance().getTrendingGenre();

            if (dbRes != null) res.add(dbRes);

        }
        return res;
    }

    private ArrayList<DBR> computeDescribe(DigestedInput di) {
        return genericMovieMultipleCompute(di, Behaviour.NLP_MOVIE_NOT_DETECTED, Behaviour.RESPONSE_NO_RESULTS_DESCRIPTION,
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
                if (DEBUG) {
                    System.out.println("**********************************");
                    System.out.println("Question: " + input);
                }

                DigestedInput di = NLP.getInstance().process(input);

                if (di.getInputType().contains(InputType.EXIT)) {
                    f.addToChat("Filme", Behaviour.DISMISS.getRandom());
                    break;
                }

                ArrayList<DBR> dbresponse = computeResponse(di);
                StringBuilder fullText = new StringBuilder();

                for (DBR response : dbresponse) {
                    if (response.isImage() && !response.isError()) {
                        f.addImageToChat("Filme", response.getResponseText(), response.getImgUrl());
                    } else {

                        f.addToChat("Filme", response.getResponseText());
                    }
                    fullText.append(response.getResponseText());
                }

                if (DEBUG)
                    System.out.println("Text resopnded: " + fullText);

                ui.updateTimeToRead(fullText.toString());
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
            ArrayList<String> questions =

//                    new ArrayList<>(Arrays.asList());


//                            "What are the best films from tom cruise and vin diesel?", "What are the best films from tom cruise and vin diesel together?",
//                            "What are the best films from tom cruise and vin diesel at the same time?", "What film appear vin diesel and tom cruise together?"));
//
                    new ArrayList<>(Arrays.asList("How are you?", "What do you know about cars?",
                            "What is you opinion on Cars?", "Yes or no?", "Hello", "Hello, who are you?", "Can you help me?",
                            "What time is it?", "What's the hottest movie atm?", "What are your thoughts on Cars?",
                            "Describe Cars", "When did Cars come out?", "Name the actors from Cars", "Give me similar movies to Cars!",
                            "What is the birthdate of tom cruise?", "What are the best actors from Narnia?", "How old is vin diesel?", "What is the trending film now?",
                            "Givbe me the performers from Inception", "What are the best actors from Inception?", "Whats the most popular film?", "Whats the most poular actor?", "Give me the most popular film and actor", "Want some similar films to inception please",
                            "What is the inception genre?", "What are the most popular genres?",
                            "How old is vin diesel?", "Give me an image of vin diesel, ppelase!", "Give me an image of Cars.", "Give me the best films from vin diesel",
                            "Where does Lewis Tan appear with Josh Lawson?",
                            "Where does Lewis Tan and Josh Lawson appear together?",
                            "Where does Lewis Tan appear with Josh Lawson together?"
                    ));

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
                for (DBR response : dbresponse) {
                    System.out.println("Response" + i + ": " + (response.isError() ? "is an error. " + response.getResponseText() : (response.isImage() ? "is an image" : response.getResponseText())) + "\n");
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
