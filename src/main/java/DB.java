import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.TheMovieDbApi;
import com.omertron.themoviedbapi.enumeration.SearchType;
import com.omertron.themoviedbapi.enumeration.SortBy;
import com.omertron.themoviedbapi.model.discover.Discover;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.omertron.themoviedbapi.model.review.Review;
import com.omertron.themoviedbapi.results.ResultList;

public class DB {

    private final String api_key = "08e795429cbf9b280d0790fcd324aa40";
    private TheMovieDbApi dbApi;
    private static DB db;

    public static void main(String[] args) throws MovieDbException {

//        String response = DB.getInstance().getFilmDescription("Iron man", new Fallback<MovieInfo>() {
//            @Override
//            public String noResult(String queryUsed) {
//                return "Film " + queryUsed + "not found!";
//            }
//
//            @Override
//            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
//                return  results.getResults().get(0);
//            }
//        });
//
//
//        DB.getInstance().getFilmActors("Iron man", new Fallback<MovieInfo>() {
//            @Override
//            public String noResult(String queryUsed) {
//                return null;
//            }
//
//            @Override
//            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
//
//                //int index = NLP.getInstance().resolveTrouble(results);
//
//                return results.getResults().get(0);
//            }
//        });

//
//
//        Discover dis = new Discover();
//        dis.withPeople("108916,7467");
//
//        try {
//            System.out.println(DB.getInstance().dbApi.getDiscoverMovies(dis).getResults().get(0).getTitle());
//
//        } catch (MovieDbException e) {
//            e.printStackTrace();
//        }

        System.out.println(DB.getInstance().getMovieReview("Cars", new Fallback<MovieInfo>() {
            @Override
            public String noResult(String queryUsed) {
                return null;
            }

            @Override
            public MovieInfo tooManyResults(String queryUsed, ResultList<MovieInfo> results) {
                return results.getResults().get(0);
            }
        }));
    }

    private DB(){
        try {
            dbApi = new TheMovieDbApi(this.api_key);
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
            default -> fallback.tooManyResults(filmName, res).getOverview();
        };
    }

    public String getFilmDate(String filmName, Fallback<MovieInfo> fallback) throws MovieDbException {
        ResultList<MovieInfo> res = dbApi.searchMovie(filmName,0,"en-US",false,0,0, SearchType.NGRAM);
        return switch (res.getTotalResults()) {
            case 0 -> fallback.noResult(filmName);
            case 1 -> res.getResults().get(0).getReleaseDate();
            default -> fallback.tooManyResults(filmName, res).getReleaseDate();
        };
    }

    // POT RETORNAT ArrayList<MediaCreditCast> amb els actors
    // O UNA STRING AM UN ERROR
    public Object getFilmActors(String filmName, Fallback<MovieInfo> fallback) throws MovieDbException {
        ResultList<MovieInfo> res = dbApi.searchMovie(filmName,0,"en-US",false,0,0, SearchType.NGRAM);

        return switch (res.getTotalResults()) {
            case 0 -> fallback.noResult(filmName);
            case 1 -> dbApi.getMovieCredits(res.getResults().get(0).getId()).getCast();
            default ->  dbApi.getMovieCredits((fallback.tooManyResults(filmName, res).getId())).getCast();
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
            return "Trending movie is not available.";
        }{
            return res.getResults().get(0).getTitle();
        }
    }
}
