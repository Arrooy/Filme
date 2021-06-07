package Common;

public class GenericHelper<T> {
    private final String content;
    private final Fallback<T> fallback;

    public GenericHelper(String content, Fallback<T> fallback) {
        this.content = content;
        this.fallback = fallback;
    }

    public String getContent() {
        return content;
    }

    public Fallback<T> getFallback() {
        return fallback;
    }
}