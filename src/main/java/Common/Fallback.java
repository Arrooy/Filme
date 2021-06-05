package Common;

import com.omertron.themoviedbapi.results.ResultList;

public interface Fallback <T> {
    // S'executa quan no hi han resultats que corresponguin a la solicitud.
    String noResult(String queryUsed);

    // S'executa quan hi han masses resultats retornats d'una solicitud.
    T tooManyResults(String queryUsed, ResultList<T> results);
}
