package Common;

import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.omertron.themoviedbapi.results.ResultList;

public class DefaultFallback <T extends MovieBasic> implements Fallback<T> {

    private final Behaviour defaultBehaviour;

    public DefaultFallback(Behaviour defaultBehaviour) {
        this.defaultBehaviour = defaultBehaviour;
    }

    @Override
    public String noResult(String queryUsed) {
        return defaultBehaviour.getRandom();
    }

    @Override
    public T tooManyResults(String queryUsed, ResultList<T> results) {
        return results.getResults().get(0);
    }
}
