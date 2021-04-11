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

public class DB {

    private static final int NUMBER_OF_SIMILAR_MOVIES = 5;
    private static final int NUMBER_OF_ACTORS_FROM_MOVIE = 5;

    private final String API_KEY = "08e795429cbf9b280d0790fcd324aa40";
    private TheMovieDbApi dbApi;
    private static DB db;

    private DB(){
        try {
            dbApi = new TheMovieDbApi(this.API_KEY);
        } catch (MovieDbException e) {
            e.printStackTrace();
        }
    }

    public static DB getInstance(){
        if (db == null){
            db = new DB();
        }
        return db;
    }

    public String getFilmDescription(String filmName, Fallback<MovieInfo> fallback) throws MovieDbException {
        ResultList<MovieInfo> res = dbApi.searchMovie(filmName,0,"en-US",false,0,0, SearchType.NGRAM);
        return switch (res.getTotalResults()) {
            case 0 -> fallback.noResult(filmName);
            case 1 -> res.getResults().get(0).getOverview();
            default -> Behaviour.RESPONSE_N_RESULTS_DESCRIPTION.getRandom().formatted(filmName, fallback.tooManyResults(filmName, res).getOverview());
        };
    }

    public String getFilmDate(String filmName, Fallback<MovieInfo> fallback) throws MovieDbException {
        ResultList<MovieInfo> res = dbApi.searchMovie(filmName,0,"en-US",false,0,0, SearchType.NGRAM);
        return switch (res.getTotalResults()) {
            case 0 -> fallback.noResult(filmName);
            case 1 -> res.getResults().get(0).getReleaseDate();
            default -> Behaviour.RESPONSE_N_RESULTS_RELEASE.getRandom().formatted(filmName, fallback.tooManyResults(filmName, res).getReleaseDate());
        };
    }

    public String getFilmActors(String filmName, Fallback<MovieInfo> fallback) throws MovieDbException {
        ResultList<MovieInfo> res = dbApi.searchMovie(filmName,0,"en-US",false,0,0, SearchType.NGRAM);
        return switch (res.getTotalResults()) {
            case 0 -> fallback.noResult(filmName);
            case 1 -> getTheTopActorNames(filmName,dbApi.getMovieCredits(res.getResults().get(0).getId()).getCast());
            default ->  getTheTopActorNames(filmName, dbApi.getMovieCredits((fallback.tooManyResults(filmName, res).getId())).getCast());
        };
    }

    public String getMovieReview(String filmName, Fallback<MovieInfo> fallback) throws  MovieDbException{
        ResultList<MovieInfo> res = dbApi.searchMovie(filmName,0,"en-US",false,0,0, SearchType.NGRAM);
        return switch (res.getTotalResults()) {
            case 0 -> fallback.noResult(filmName);
            case 1 -> {
               ResultList<Review> resultList = dbApi.getMovieReviews(res.getResults().get(0).getId(), 0, "en-US");
                if(resultList.getTotalResults() == 0)
                    yield fallback.noResult(filmName);
                else
                    yield resultList.getResults().get(0).getContent();
            }
            default ->{
                ResultList<Review> resultList = dbApi.getMovieReviews((fallback.tooManyResults(filmName, res).getId()), 0, "en-US");
                if(resultList.getTotalResults() == 0)
                    yield fallback.noResult(filmName);
                else
                    yield resultList.getResults().get(0).getContent();
            }
        };
    }

    public String getTrendingMovie() throws MovieDbException {
        Discover dis = new Discover();
        dis.sortBy(SortBy.POPULARITY_DESC);

        ResultList<MovieBasic> res  = dbApi.getDiscoverMovies(dis);
        if(res.getTotalResults() == 0){
            return Behaviour.RESPONSE_NO_RESULTS_TRENDING.getRandom();
        } else {
            return Behaviour.RESPONSE_N_RESULTS_TRENDING.getRandom().formatted(res.getResults().get(0).getTitle());
        }
    }


    public String getSimilarMovie(String movieName, Fallback<MovieInfo> fallback) throws MovieDbException {
        ResultList<MovieInfo> res = dbApi.searchMovie(movieName,0,"en-US",false,0,0, SearchType.NGRAM);
        return switch (res.getTotalResults()) {
            case 0 -> fallback.noResult(movieName);
            case 1 -> {

                ResultList<MovieInfo> resultList = dbApi.getSimilarMovies(res.getResults().get(0).getId(),0,"en-US");
                if(resultList.getTotalResults() == 0)
                    yield fallback.noResult(movieName);
                else{
                    yield generateListOfSimilarMovies(movieName, resultList);
                }
            }
            default ->{
                ResultList<MovieInfo> resultList = dbApi.getSimilarMovies((fallback.tooManyResults(movieName, res).getId()), 0, "en-US");
                if(resultList.getTotalResults() == 0)
                    yield fallback.noResult(movieName);
                else
                    yield generateListOfSimilarMovies(movieName, resultList);
            }
        };
    }

    private String getTheTopActorNames(String movie, List<MediaCreditCast> cast) {
        StringBuilder names = new StringBuilder();
        int num = 0;
        int lastName = Math.min(NUMBER_OF_ACTORS_FROM_MOVIE, cast.size() - 1);

        for (MediaCreditCast mcc : cast){
            names.append(mcc.getName()).append(num == lastName - 1 ? " and " : (num == lastName) ? "" : ", ");
            if(num >= (lastName)) break;
            num++;
        }
        return Behaviour.RESPONSE_N_RESULTS_ACTORS.getRandom().formatted(movie, names);
    }

    private String generateListOfSimilarMovies(String filmName, ResultList<MovieInfo> resultList){
        StringBuilder result = new StringBuilder();
        int num = 0;
        int lastName = Math.min(NUMBER_OF_SIMILAR_MOVIES, resultList.getResults().size() - 1);

        for(MovieInfo mi : resultList.getResults()){
            result.append(mi.getTitle()).append(num == lastName - 1 ? " and " : (num == lastName) ? "" : ", ");
            if(num >= (lastName)) break;
            num++;
        }

        return Behaviour.RESPONSE_N_RESULTS_SIMILAR.getRandom().formatted(filmName, result);
    }
}