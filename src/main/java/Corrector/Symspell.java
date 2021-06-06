package Corrector;

import NLP.AhoCorasick.AhoCorasick;
import io.github.mightguy.spellcheck.symspell.api.DataHolder;
import io.github.mightguy.spellcheck.symspell.common.*;
import io.github.mightguy.spellcheck.symspell.exception.SpellCheckException;
import io.github.mightguy.spellcheck.symspell.impl.InMemoryDataHolder;
import io.github.mightguy.spellcheck.symspell.impl.SymSpellCheck;

import java.io.*;
import java.util.List;

public class Symspell {

    private final DataHolder dataHolder;
    private final SymSpellCheck symSpellCheck;

    // Accés singleton a la instància de la classe
    private static Symspell singleton;
    public static Symspell getInstance() {
        if (singleton == null) singleton = new Symspell();
        return singleton;
    }

    public Symspell(){

        SpellCheckSettings spellCheckSettings = SpellCheckSettings.builder().build();

        dataHolder = new InMemoryDataHolder(spellCheckSettings, new Murmur3HashFunction());

        // Emprat per no corregir noms de persones i actors.
        // No s'ha posat directament els noms priopis a except perque empitjora la qualitat de la correxio.
        dataHolder.addExclusionItem("&", "&");
        dataHolder.addExclusionItem(" &", "&");
        dataHolder.addExclusionItem("& ", "&");


        WeightedDamerauLevenshteinDistance weightedDamerauLevenshteinDistance =
                new WeightedDamerauLevenshteinDistance(
                        spellCheckSettings.getDeletionWeight(),
                        spellCheckSettings.getInsertionWeight(),
                        spellCheckSettings.getReplaceWeight(),
                        spellCheckSettings.getTranspositionWeight(),
                        new QwertyDistance());

        symSpellCheck = new SymSpellCheck(dataHolder, weightedDamerauLevenshteinDistance,
                spellCheckSettings);
    }

    public void loadDict() throws IOException, SpellCheckException {

        loadUniGramFile(
                new File("res/words.txt"));
        loadBiGramFile(
                new File("res/bigrams.txt"));
    }


    public String spellCheck(String inputNoNames) {
        try {
            Composition composition = symSpellCheck.wordBreakSegmentation(inputNoNames, 10, 2);
            return composition.getCorrectedString();
        } catch (SpellCheckException e) {
            e.printStackTrace();
        }
        return inputNoNames;
    }

    private void loadUniGramFile(File file) throws IOException, SpellCheckException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("\\s+");
                dataHolder.addItem(new DictionaryItem(arr[0], Double.parseDouble(arr[1]), -1.0));
            }
        }
    }

    private void loadBiGramFile(File file) throws IOException, SpellCheckException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] arr = line.split("\\s+");
                dataHolder
                        .addItem(new DictionaryItem(arr[0] + " " + arr[1], Double.parseDouble(arr[2]), -1.0));
            }
        }
    }

}


    /*
     String inputTerm = "Rayo McQueen is a great film";
        List<SuggestionItem> suggestions = symSpellCheck.lookup(inputTerm);
        SuggestionItem compound = symSpellCheck.lookupCompound(inputTerm).get(0);
        Composition composition = symSpellCheck.wordBreakSegmentation(inputTerm, 10, 2);

        System.out.println("Input " + inputTerm);
        suggestions.stream()
                .limit(10)
                .forEach(suggestion -> System.out.println(
                        "Lookup suggestion: "
                                + suggestion.getTerm() + " "
                                + suggestion.getDistance() + " "
                                + suggestion.getCount()));
//
//        System.out.println(
//                "\nLookup suggestion: "
//                        + compound.getTerm() + " "
//                        + compound.getDistance() + " "
//                        + compound.getCount());
        System.out.println("Composition is: " + composition.getCorrectedString());
//        System.out.println("Composition is    :" + composition.getSegmentedString());
     */
