package Common;

import com.omertron.themoviedbapi.model.movie.MovieInfo;

public class GenericHelper {
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