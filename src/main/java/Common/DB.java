package Common;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.enumeration.SortBy;
import com.omertron.themoviedbapi.model.credits.MediaCreditCast;
import com.omertron.themoviedbapi.model.discover.Discover;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.review.Review;
import com.omertron.themoviedbapi.results.ResultList;

import java.util.List;
import java.util.function.Function;

//TODO: posar els errors integrats en el fallback.

public class DB {

    private static final int NUMBER_OF_SIMILAR_MOVIES = 5;
    private static final int NUMBER_OF_ACTORS_FROM_MOVIE = 5;

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

    public DBR getFilmActors(String filmName, Fallback<MovieInfo> fallback) {

        try {
            ResultList<MovieInfo> res = dbApi.searchMovie(filmName, 0, "en-US", false, 0, 0, SearchType.NGRAM);
            return switch (res.getTotalResults()) {
                case 0 -> fallback.noResult(filmName);
                case 1 -> new DBR(getTheTopActorNames(filmName, dbApi.getMovieCredits(res.getResults().get(0).getId()).getCast()));
                default -> new DBR(getTheTopActorNames(filmName, dbApi.getMovieCredits((fallback.tooManyResults(filmName, res).getId())).getCast()));
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


            ResultList<MovieInfo> res = dbApi.searchMovie(filmName, 0, "en-US", false, 0, 0, SearchType.NGRAM);

            if (res.getTotalResults() == 0) {
                return fallback.noResult(filmName);
            }

            int id = res.getTotalResults() == 1 ? res.getResults().get(0).getId() : fallback.tooManyResults(filmName, res).getId();

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

    public DBR getTrendingMovie() {
        try {

            Discover dis = new Discover();
            dis.sortBy(SortBy.POPULARITY_DESC);

            ResultList<MovieBasic> res = dbApi.getDiscoverMovies(dis);
            if (res.getTotalResults() == 0) {
                return new DBR(Behaviour.RESPONSE_NO_RESULTS_TRENDING.getRandom());
            } else {
                return new DBR(Behaviour.RESPONSE_N_RESULTS_TRENDING.getRandom().formatted(res.getResults().get(0).getTitle()));
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
            } else {
                ResultList<MovieInfo> resultList = dbApi.getSimilarMovies(res.getTotalResults() == 1 ?
                        res.getResults().get(0).getId()
                        : (fallback.tooManyResults(movieName, res).getId()), 0, "en-US");

                if (resultList.getTotalResults() == 0)
                    return fallback.noResult(movieName);
                else {
                    return new DBR(generateListOfSimilarMovies(movieName, resultList));
                }
            }
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getTheTopActorNames(String movie, List<MediaCreditCast> cast) {
        StringBuilder names = new StringBuilder();
        int num = 0;
        int lastName = Math.min(NUMBER_OF_ACTORS_FROM_MOVIE, cast.size() - 1);

        for (MediaCreditCast mcc : cast) {
            names.append(mcc.getName()).append(num == lastName - 1 ? " and " : (num == lastName) ? "" : ", ");
            if (num >= (lastName)) break;
            num++;
        }
        return Behaviour.RESPONSE_N_RESULTS_ACTORS.getRandom().formatted(movie, names);
    }

    private String generateListOfSimilarMovies(String filmName, ResultList<MovieInfo> resultList) {
        StringBuilder result = new StringBuilder();
        int num = 0;
        int lastName = Math.min(NUMBER_OF_SIMILAR_MOVIES, resultList.getResults().size() - 1);

        for (MovieInfo mi : resultList.getResults()) {
            result.append(mi.getTitle()).append(num == lastName - 1 ? " and " : (num == lastName) ? "" : ", ");
            if (num >= (lastName)) break;
            num++;
        }

        return Behaviour.RESPONSE_N_RESULTS_SIMILAR.getRandom().formatted(filmName, result);
    }
}