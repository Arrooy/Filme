package NLP.Aho_Corasick;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ACGenerator {
    public static void main(String[] args) {
        AhoCorasick ahoCorasick = AhoCorasick.getInstance();

        try (CSVReader reader = new CSVReader(new FileReader("res/IMDb_movies.csv"))) {
            System.out.println("Exporting CSV...");
            List<String[]> data = reader.readAll();
            System.out.println("Creating trie...");
            data.forEach(x -> ahoCorasick.insert(x[2].toLowerCase(Locale.ROOT)));
            ahoCorasick.insert("Harry Potter".toLowerCase(Locale.ROOT));
            System.out.println("Initializing Aho-Corasick links...");
            ahoCorasick.init();

            var names = ahoCorasick.analyzeString("Hey filme, describe me movie 43 please".toLowerCase(Locale.ROOT));

            String longestName = names.get(0);
            int longestLength = longestName.length();
            for (String s: names) {
                System.out.println(s);
                if (longestLength <= s.length()) {
                    longestLength = s.length();
                    longestName = s;
                }
            }
            System.out.println("The selected is: " + longestName);

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

    }
}
