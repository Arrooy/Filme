package Common;

import com.omertron.themoviedbapi.model.movie.MovieInfo;

public class GenericMovieHelper {
    private final String content;
    private final Fallback<MovieInfo> fallback;

    public GenericMovieHelper(String content, Fallback<MovieInfo> fallback) {
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