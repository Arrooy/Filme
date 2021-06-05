package Corrector;

import io.gitlab.rxp90.jsymspell.SymSpell;
import io.gitlab.rxp90.jsymspell.SymSpellBuilder;
import io.gitlab.rxp90.jsymspell.api.Bigram;
import io.gitlab.rxp90.jsymspell.api.SuggestItem;
import io.gitlab.rxp90.jsymspell.exceptions.NotInitializedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Symspell {
    private static final int MAX_EDIT_DISTANCE = 2;

    public Symspell() throws IOException, NotInitializedException {

        Map<Bigram, Long> bigrams = Files.lines(Paths.get("res/bigrams.txt"))
                .map(line -> line.split(" "))
                .collect(Collectors.toMap(tokens -> new Bigram(tokens[0], tokens[1]), tokens -> Long.parseLong(tokens[2])));

        Map<String, Long> unigrams = Files.lines(Paths.get("res/words.txt"))
                .map(line -> line.split(" "))
                .collect(Collectors.toMap(tokens -> tokens[0], tokens -> Long.parseLong(tokens[1])));

        SymSpell symSpell = new SymSpellBuilder().setUnigramLexicon(unigrams)
                .setBigramLexicon(bigrams)
                .setMaxDictionaryEditDistance(MAX_EDIT_DISTANCE)
                .setStringDistanceAlgorithm((string1, string2, maxDistance) -> {
                    if (string1.length() != string2.length()){
                        return -1;
                    }
                    char[] chars1 = string1.toCharArray();
                    char[] chars2 = string2.toCharArray();
                    int distance = 0;
                    for (int i = 0; i < chars1.length; i++) {
                        if (chars1[i] != chars2[i]) {
                            distance += 1;
                        }
                    }
                    return distance;
                })
                .createSymSpell();

        boolean includeUnknowns = true;

        List<SuggestItem> suggestions = symSpell.lookupCompound("hary potter", MAX_EDIT_DISTANCE, includeUnknowns);
        for(SuggestItem it : suggestions){
            System.out.println("Suiggestioon : " + it.getSuggestion());
        }
        System.out.println(suggestions.get(0).getSuggestion());
    }

    public static void main(String[] args) throws NotInitializedException, IOException {
        Symspell a = new Symspell();
    }
}
