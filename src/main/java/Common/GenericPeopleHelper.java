package Common;

import com.omertron.themoviedbapi.model.person.PersonFind;

public class GenericPeopleHelper {

    private final String content;
    private final Fallback<PersonFind> fallback;

    public GenericPeopleHelper(String content, Fallback<PersonFind> fallback) {
        this.content = content;
        this.fallback = fallback;
    }

    public String getContent() {
        return content;
    }

    public Fallback<PersonFind> getFallback() {
        return fallback;
    }
}
