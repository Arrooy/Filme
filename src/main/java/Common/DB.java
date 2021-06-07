package Common;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.enumeration.SortBy;
import com.omertron.themoviedbapi.model.Genre;
import com.omertron.themoviedbapi.model.artwork.Artwork;
import com.omertron.themoviedbapi.model.credits.CreditMovieBasic;
import com.omertron.themoviedbapi.model.credits.MediaCredit;
import com.omertron.themoviedbapi.model.discover.Discover;
import com.omertron.themoviedbapi.model.discover.WithBuilder;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.person.PersonCreditList;
import com.omertron.themoviedbapi.model.person.PersonFind;
import com.omertron.themoviedbapi.model.person.PersonInfo;
import com.omertron.themoviedbapi.model.review.Review;
import com.omertron.themoviedbapi.results.ResultList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

// TODO: posar els noms propis en majuscula!

public class DB {

    private static final int NUMBER_OF_SIMILAR_MOVIES = 5;
    private static final int NUMBER_OF_ACTORS_FROM_MOVIE = 5;
    private static final int NUMBER_OF_TOP_GENRES = 5;

    private final String API_KEY = "08e795429cbf9b280d0790fcd324aa40";
    private TheMovieDbApi dbApi;
    private static DB db;

    private DB() {
        try {
            dbApi = new TheMovieDbApi(this.API_KEY);
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
    }

    public static DB getInstance() {
        if (db == null) {
            db = new DB();
        }
        return db;
    }

    private <T extends MovieBasic> DBR processQuery(String filmName, Behaviour behaviour, ResultList<T> res, Fallback<T> fallback, Function<T, String> extractionMethod) {
        String result;
        switch (res.getTotalResults()) {
            case 0 -> {
                return fallback.noResult(filmName);
            }
            case 1 -> result = behaviour.getRandom().formatted(filmName, extractionMethod.apply(res.getResults().get(0)));
            default -> result = behaviour.getRandom().formatted(filmName, extractionMethod.apply(fallback.tooManyResults(filmName, res)));
        }

        return new DBR(result);
    }

    public DBR getFilmDescription(String filmName, Fallback<MovieInfo> fallback) {
        try {
            ResultList<MovieInfo> res = dbApi.searchMovie(filmName, 0, "en-US", false, 0, 0, SearchType.NGRAM);
            return this.<MovieInfo>processQuery(filmName, Behaviour.RESPONSE_N_RESULTS_DESCRIPTION, res, fallback, MovieBasic::getOverview);
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBR getFilmDate(String filmName, Fallback<MovieInfo> fallback) {
        try {
            ResultList<MovieInfo> res = dbApi.searchMovie(filmName, 0, "en-US", false, 0, 0, SearchType.NGRAM);

            return this.<MovieInfo>processQuery(filmName, Behaviour.RESPONSE_N_RESULTS_RELEASE, res, fallback, MovieBasic::getReleaseDate);
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DBR getFilmGenre(String filmName, Fallback<MovieInfo> fallback) {
        try {
            int id;
            try {
                id = getFilmId(filmName, fallback);
            } catch (Exception e) {
                return fallback.noResult(filmName);
            }

            MovieInfo movieInfo = dbApi.getMovieInfo(id, "en-US");
            List<Genre> genres = movieInfo.getGenres();

            if (genres == null || genres.size() == 0)
                return fallback.noResult(filmName);

            return new DBR(listToString(filmName, genres, Behaviour.RESPONSE_N_RESULTS_MOVIE_GENRE, 5, Genre::getName));
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBR getFilmActors(String filmName, Fallback<MovieInfo> fallback) {

        try {
            ResultList<MovieInfo> res = dbApi.searchMovie(filmName, 0, "en-US", false, 0, 0, SearchType.NGRAM);

            return switch (res.getTotalResults()) {
                case 0 -> fallback.noResult(filmName);

                case 1 -> new DBR(this.listToString(filmName, dbApi.getMovieCredits(res.getResults().get(0).getId()).getCast()
                        , Behaviour.RESPONSE_N_RESULTS_ACTORS, NUMBER_OF_ACTORS_FROM_MOVIE, MediaCredit::getName));
                default -> new DBR(this.listToString(filmName, dbApi.getMovieCredits((fallback.tooManyResults(filmName, res).getId())).getCast()
                        , Behaviour.RESPONSE_N_RESULTS_ACTORS, NUMBER_OF_ACTORS_FROM_MOVIE, MediaCredit::getName));
            };
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBR getFilmImage(String filmName, Fallback<MovieInfo> fallback) {
        ResultList<MovieInfo> res = null;
        try {
            res = dbApi.searchMovie(filmName, 0, "en-US", false, 0, 0, SearchType.NGRAM);

            if (res.getTotalResults() == 0) {
                return fallback.noResult(filmName);
            } else {
                String path = res.getTotalResults() == 1 ? res.getResults().get(0).getBackdropPath() : fallback.tooManyResults(filmName, res).getBackdropPath();

                if (path == null || path.isBlank()) {
                    return fallback.noResult(filmName);
                }


                return new DBR(Behaviour.RESPONSE_N_RESULTS_IMAGE.getRandom(), path, false);
            }
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DBR getMovieReview(String filmName, Fallback<MovieInfo> fallback) {
        try {

            int id;
            try {
                id = getFilmId(filmName, fallback);
            } catch (Exception e) {
                return fallback.noResult(filmName);
            }

            ResultList<Review> resultList = dbApi.getMovieReviews(id, 0, "en-US");
            if (resultList.getTotalResults() == 0) {
                return fallback.noResult(filmName);
            }

            // La regex elimina tags html i el seu contingut interior.
            return new DBR(resultList.getResults().get(0).getContent().replaceAll("<.*?>", ""));
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DBR getTrendingGenre() {

        try {
            List<MovieBasic> res = getTrendingMovies();
            ArrayList<String> topGenres = new ArrayList<>();
            int index = 0;
            for (MovieBasic film : res) {
                MovieInfo movieInfo = dbApi.getMovieInfo(film.getId(), "en-US");

                if (movieInfo.getGenres() != null && movieInfo.getGenres().size() != 0) {
                    for (Genre genre : movieInfo.getGenres()) {
                        if (!topGenres.contains(genre.getName()))
                            topGenres.add(genre.getName());
                    }

                    index++;
                    if (index >= NUMBER_OF_TOP_GENRES) break;
                }
            }

            if (topGenres.size() == 0) {
                return new DBR(Behaviour.RESPONSE_NO_RESULTS_TRENDING_GENRE.getRandom());
            }

            return new DBR(listToString("", topGenres, Behaviour.RESPONSE_N_RESULTS_TRENDING_GENRE, 10, x -> x));

        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBR getTrendingActor() {
        try {
            ResultList<PersonFind> results = dbApi.getPersonPopular(0);
            if (results.getTotalResults() == 0)
                return new DBR(Behaviour.RESPONSE_NO_RESULTS_TRENDING_ACTOR.getRandom());
            else {
                return new DBR(Behaviour.RESPONSE_N_RESULTS_TRENDING.getRandom().formatted(results.getResults().get(0).getName()));
            }
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<MovieBasic> getTrendingMovies() throws MovieDbException {
        Discover dis = new Discover();
        dis.sortBy(SortBy.POPULARITY_DESC);

        ResultList<MovieBasic> res = dbApi.getDiscoverMovies(dis);
        return res.getResults();
    }

    public DBR getTrendingMovie() {
        try {
            List<MovieBasic> res = getTrendingMovies();
            if (res.size() == 0) {
                return new DBR(Behaviour.RESPONSE_NO_RESULTS_TRENDING_MOVIE.getRandom());
            } else {
                return new DBR(Behaviour.RESPONSE_N_RESULTS_TRENDING.getRandom().formatted(res.get(0).getTitle()));
            }
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DBR getSimilarMovie(String movieName, Fallback<MovieInfo> fallback) {
        try {

            ResultList<MovieInfo> res = dbApi.searchMovie(movieName, 0, "en-US", false, 0, 0, SearchType.NGRAM);
            if (res.getTotalResults() == 0) {
                return fallback.noResult(movieName);
            }

            ResultList<MovieInfo> resultList = dbApi.getSimilarMovies(res.getTotalResults() == 1 ?
                    res.getResults().get(0).getId()
                    : (fallback.tooManyResults(movieName, res).getId()), 0, "en-US");

            if (resultList.getTotalResults() == 0)
                return fallback.noResult(movieName);
            else {

                return new DBR(this.listToString(movieName, resultList.getResults()
                        , Behaviour.RESPONSE_N_RESULTS_SIMILAR, NUMBER_OF_SIMILAR_MOVIES, MovieInfo::getTitle));
            }

        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DBR getActorImage(String actorName, Fallback<PersonFind> fallback) {
        int id;
        try {
            id = getActorId(actorName,fallback);
        } catch (Exception e) {
            return fallback.noResult(actorName);
        }
        try {
            ResultList<Artwork> images = dbApi.getPersonImages(id);
            if(images.getTotalResults() == 0)
                return fallback.noResult(actorName);

            String path = images.getResults().get(0).getFilePath();
            if (path == null || path.isBlank()) {
                return fallback.noResult(actorName);
            }
            return new DBR(Behaviour.RESPONSE_N_RESULTS_IMAGE.getRandom(), path, false);
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBR getActorFilms(String actorName, Fallback<PersonFind> fallback) {
        try {

            int id;
            try{
                id = getActorId(actorName,fallback);
            }catch (Exception e){
                return fallback.noResult(actorName);
            }

            System.out.println("Looking for credits for actor id " + id);
            PersonCreditList<CreditMovieBasic> info = dbApi.getPersonMovieCredits(id, "en-US");
            if(info.getCast() == null || info.getCast().size() == 0)
                return fallback.noResult(actorName);

            return new DBR(listToString(actorName, info.getCast(), Behaviour.RESPONSE_N_RESULTS_ACTOR_FILMS,6, CreditMovieBasic::getTitle));

        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DBR getActorAge(String actorName, Fallback<PersonFind> fallback) {
        try {

            int id;
            try{
                id = getActorId(actorName,fallback);
            }catch (Exception e){
                return fallback.noResult(actorName);
            }

            PersonInfo info = dbApi.getPersonInfo(id);


            return new DBR(Behaviour.RESPONSE_N_RESULTS_AGE.getRandom().formatted(actorName, info.getBirthday()));
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DBR getActorFilmsTogether(ArrayList<String> people, DefaultFallback<Object> fallback) {

        Discover dis = new Discover();
        WithBuilder wb = null;
        for(String p : people){
            if (wb == null)
                 wb = new WithBuilder(URLEncoder.encode(p, StandardCharsets.US_ASCII));
            else
                wb.and(URLEncoder.encode(p, StandardCharsets.US_ASCII));
        }

        dis.withPeople(wb);
        dis.sortBy(SortBy.POPULARITY_DESC);

        ResultList<MovieBasic> res;
        try {
            res = dbApi.getDiscoverMovies(dis);
            if(res == null || res.getTotalResults() == 0){
                return fallback.noResult("");
            }

            return new DBR(listToString("",res.getResults(),Behaviour.RESPONSE_N_RESULTS_ACTOR_FILMS_TOGETHER,8, MovieBasic::getTitle));
        } catch (MovieDbException e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T> String listToString(String title, List<T> list, Behaviour behaviour, int amountOfResults, Function<T, String> getString) {
        StringBuilder result = new StringBuilder();
        int lastObject = Math.min(amountOfResults, list.size() - 1);
        int num = 0;
        for (T obj : list) {
            result.append(getString.apply(obj)).append(num == lastObject - 1 ? " and " : (num == lastObject) ? "" : ", ");
            if (num >= (lastObject)) break;
            num++;
        }
        return behaviour.getRandom().formatted(title, result);
    }

    private int getActorId(String actorName, Fallback<PersonFind> fallback) throws Exception {
        ResultList<PersonFind> res = dbApi.searchPeople(actorName, 0, false, SearchType.NGRAM);
        if (res.getTotalResults() == 0) {
            throw new Exception("");
        }
        return res.getTotalResults() == 1 ? res.getId() : fallback.tooManyResults(actorName, res).getId();
    }

    private int getFilmId(String filmName, Fallback<MovieInfo> fallback) throws Exception {
        ResultList<MovieInfo> res = dbApi.searchMovie(filmName, 0, "en-US", false, 0, 0, SearchType.NGRAM);

        if (res.getTotalResults() == 0) {
            throw new Exception("");
        }

        return res.getTotalResults() == 1 ? res.getResults().get(0).getId() : fallback.tooManyResults(filmName, res).getId();
    }
}