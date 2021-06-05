package NLP.AhoCorasick;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ACLoader {

    public static void main(String[] args) {
        ACLoader.loadMovies();
        ACLoader.loadPeople();
        AhoCorasick.getInstance().init();

        ArrayList<ACResult> results = AhoCorasick.getInstance().analyzeString("Hey filme, does johnny depp and scarlett johansson appear in movie 43 please");
        AhoCorasick.getInstance().processResults(results);

        for (ACResult r: results) System.out.println(r);

        System.out.println("Movie choice: " + AhoCorasick.getLongestFromType(results, ACNodeType.MOVIE));
        System.out.println("Person choice: " + AhoCorasick.getLongestFromType(results, ACNodeType.PERSON));
    }

    public static void loadMovies() {
        AhoCorasick ahoCorasick = AhoCorasick.getInstance();

        try (CSVReader reader = new CSVReader(new FileReader("res/IMDb_movies.csv"))) {
            System.out.println("Importing movies CSV...");
            List<String[]> data = reader.readAll();
            System.out.println("Creating trie...");

            ahoCorasick.setCurrentType(ACNodeType.MOVIE);
            data.forEach(x -> ahoCorasick.insert(x[2].toLowerCase(Locale.ROOT)));

            // Insert here the desired names that don't appear exactly on the CSV
            ahoCorasick.insert("Harry Potter".toLowerCase(Locale.ROOT));

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public static void loadPeople() {
        AhoCorasick ahoCorasick = AhoCorasick.getInstance();

        try (CSVReader reader = new CSVReader(new FileReader("res/IMDb_names.csv"))) {
            System.out.println("Importing names CSV...");
            List<String[]> data = reader.readAll();
            System.out.println("Creating trie...");

            ahoCorasick.setCurrentType(ACNodeType.PERSON);
            data.forEach(x -> {
                ahoCorasick.insert(x[1].toLowerCase(Locale.ROOT));
                ahoCorasick.insert(x[2].toLowerCase(Locale.ROOT));
            });

            // Insert here the desired names that don't appear exactly on the CSV
            // ahoCorasick.insert("Harry Potter".toLowerCase(Locale.ROOT));

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }
}
