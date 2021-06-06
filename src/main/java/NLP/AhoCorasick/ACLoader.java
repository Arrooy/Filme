package NLP.AhoCorasick;

import NLP.Keywords;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import io.github.mightguy.spellcheck.symspell.common.SuggestionItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ACLoader {


    private static final String dataSourceExtension = "txt";

//    public static void main(String[] args) {
//
//        loadData();
//        new Keywords("res/keywords.txt");
//        AhoCorasick.getInstance().init();
//
//        ArrayList<ACResult> results = AhoCorasick.getInstance().analyzeString("Can you describe me the avengers?");
//        AhoCorasick.getInstance().processResults("Can you describe me the avengers?", results);
//
//        for (ACResult r: results) System.out.println(r);
//
//        System.out.println("Movie choice: " + AhoCorasick.getLongestFromType(results, ACNodeType.MOVIE));
//        System.out.println("Person choice: " + AhoCorasick.getLongestFromType(results, ACNodeType.PERSON));
//    }

    public static void loadMoviesFromCSV() {
        AhoCorasick ahoCorasick = AhoCorasick.getInstance();

        try (CSVReader reader = new CSVReader(new FileReader("res/IMDb_movies.csv"))) {
            System.out.println("Importing movies CSV...");
            List<String[]> data = reader.readAll();
            System.out.println("Creating trie...");

            ahoCorasick.setCurrentType(ACNodeType.MOVIE);
            data.forEach(x -> ahoCorasick.insert(x[2].toLowerCase(Locale.ROOT)));

            // Insert here the desired names that don't appear exactly on the CSV
            ahoCorasick.insert("Harry Potter".toLowerCase(Locale.ROOT));
            ahoCorasick.insert("Narnia".toLowerCase(Locale.ROOT));


        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    public static void loadPeopleFromCSV() {
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

    public static void loadFromTxt(String filename, ACNodeType nodeType) {
        AhoCorasick ahoCorasick = AhoCorasick.getInstance();

        try (BufferedReader reader = new BufferedReader(new FileReader("res/"+filename))) {
            System.out.println("Importing" + filename + "...");

            ahoCorasick.setCurrentType(nodeType);

            String line;
            while ((line = reader.readLine()) != null) {
                // process the line.
                ahoCorasick.insert(line.toLowerCase(Locale.ROOT));
            }

            if(nodeType.getType() == ACNodeType.MOVIE.getType()){
                // Insert here the desired names that don't appear exactly on the txt
                ahoCorasick.insert("Harry Potter".toLowerCase(Locale.ROOT));
                ahoCorasick.insert("Narnia".toLowerCase(Locale.ROOT));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ACLoader.translateCSVToTxt();
    }
    private static void translateCSVToTxt() {

        try (CSVReader reader = new CSVReader(new FileReader("res/IMDb_movies.csv"))) {
            saveData(reader,"IMDb_movies",false);
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        try (CSVReader reader = new CSVReader(new FileReader("res/IMDb_names.csv"))) {
            saveData(reader,"IMDb_names",true);
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private static void saveData(CSVReader reader, String name, boolean getTwoCols) throws IOException, CsvException {
        System.out.println("Importing movies CSV...");
        List<String[]> data = reader.readAll();

        FileWriter fw = new FileWriter("res/" + name + ".txt");
        PrintWriter writer = new PrintWriter(fw);

        data.forEach(x ->  {
            if(getTwoCols){
                writer.printf("%s\n%s\n", x[1].toLowerCase(Locale.ROOT),x[2].toLowerCase(Locale.ROOT));
            }else{
                writer.printf("%s\n", x[2].toLowerCase(Locale.ROOT));
            }
        });
    }

    public static void loadData() {
        if(dataSourceExtension.equals("txt")){
            ACLoader.loadFromTxt("IMDb_movies.txt",ACNodeType.MOVIE);
            ACLoader.loadFromTxt("IMDb_names.txt",ACNodeType.PERSON);
        }else{
            ACLoader.loadMoviesFromCSV();
            ACLoader.loadPeopleFromCSV();
        }
    }
}
