package NLP.AhoCorasick;

import io.github.mightguy.spellcheck.symspell.common.WeightedDamerauLevenshteinDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Classe que implementa l'algoritme d'Aho-Corasick. Aquest està basat en l'estructura
 * de dades Trie per tal de trobar totes les paraules d'un diccionari en una cadena donada
 * amb un cost constant de N (on N és la mida de la cadena d'entrada). Per tant, la cerca de
 * les coincidències depén únicament de la mida de la cadena d'entrada i no de la mida
 * del propi diccionari
 */
public class AhoCorasick {

    // Node del que deriva l'estructura Trie
    private final ACNode root;

    private ACNodeType currentType;

    // Accés singleton a la instància de la classe
    private static AhoCorasick singleton;

    public static AhoCorasick getInstance() {
        if (singleton == null) singleton = new AhoCorasick();
        return singleton;
    }

    // Constructor
    public AhoCorasick() {
        root = new ACNode('\0', true);
        currentType = null;
    }

    /**
     * Funció per inserir un nou valor al sistema. El cost de la inserció és de N,
     * on N és la mida de la cadena a inserir. El que realitza consisteix en fer servir
     * els caràcters de la cadena per anar seguint el Trie, i, si no existeix un node determinat
     * el crea. Finalment marca el node equivalent a l'últim caràcter com a node que conté un valor.
     *
     * @param value Nou valor a inserir a l'estructura
     */
    public void insert(String value) {
        root.insert(value);
    }

    /**
     * Una vegada inserides totes les paraules al Trie cal cridar aquesta funció. Aquesta analitza
     * el Trie i defineix quins són els failure link i el dictionary link. Els quals serveixen per
     * detectar què fer en cas d'error i com detectar paraules anidades en altres paraules respectivament.
     */
    public void init() {
        root.findFailureLink();
        root.findDictLinks();
    }

    /**
     * Busca un node en el Trie equivalent al valor indicat.
     *
     * @param value Valor del node a buscar
     * @return Node que representa a value
     */
    public ACNode getNode(String value) {
        ACNode node = root;
        while (!value.isEmpty()) {
            char nc = value.toCharArray()[0];
            value = value.substring(1);
            node = node.getChildren(nc);
            if (node == null) return null;
        }
        return node;
    }

    /**
     * Getter del root
     *
     * @return root
     */
    public ACNode getRoot() {
        return root;
    }

    /**
     * Funció que a partir d'una cadena d'entrada determina quina és la llista de totes les
     * coincidències trobades a partir del seu diccionari.
     *
     * @param value Cadena a analitzar
     * @return Llista de paraules del diccionari que s'ha trobat a la cadena d'entrada
     */
    public ArrayList<ACResult> analyzeString(String value) {
        value = value.toLowerCase(Locale.ROOT);
        ArrayList<ACNode> resultsRaw = new ArrayList<>();
        ACNode current = root;

        for (char c : value.toCharArray()) {
            ACNode next = current.getChildren(c);

            if (next != null) {
                current = next;
                if (current.getValue() == c && current.getType() != null) resultsRaw.add(current);
                for (ACNode n : current.getDictLinks()) if (n.getValue() == c) resultsRaw.add(n);

            } else {
                while (!current.isRoot()) {
                    current = current.getFailureLink();
                    next = current.getChildren(c);
                    if (next != null) {
                        current = next;
                        break;
                    }
                }
            }
        }

        ArrayList<ACResult> results = new ArrayList<>();
        for (ACNode n : resultsRaw) {
            results.add(new ACResult(n.getType(), n.getFullValue()));
        }
        return results;
    }

    public ACNodeType getCurrentType() {
        return currentType;
    }

    public void setCurrentType(ACNodeType currentType) {
        this.currentType = currentType;
    }

    public static String getLongestFromType(ArrayList<ACResult> values, ACNodeType type) {
        String longestName = null;
        int longestLength = -1;
        for (ACResult r : values) {
            if (r.getType() == type && longestLength <= r.getValue().length()) {
                longestLength = r.getValue().length();
                longestName = r.getValue();
            }
        }
        return longestName;
    }

    public static ArrayList<String> getAllFromType(ArrayList<ACResult> values, ACNodeType type) {
        ArrayList<String> results = new ArrayList<>();
        for (ACResult r : values) {
            if (r.getType().getType() == type.getType()) {
                results.add(r.getValue());
            }
        }
        return results;
    }

    public void processResults(String input, ArrayList<ACResult> values) {
        input = input.toLowerCase(Locale.ROOT);
        for (int i = values.size() - 1; i >= 0; i--) {
            ACResult r = values.get(i);

            // Eliminació de substrings.
            boolean isSubstring = false;
            for (ACResult c : values) {
                if (r != c && c.getValue().contains(r.getValue())) {
                    isSubstring = true;
                    break;
                }
            }

            if (isSubstring) {
//                System.out.println("Removing " + r.getValue());
                values.remove(i);
                continue;
            }

            String[] words = input.split("\\P{L}+");
            String[] resultWords = r.getValue().split("\\P{L}+");
            boolean remove = false;

            // Si estem parlant d'una sola paraula:
            if(resultWords.length == 1){
                // Eliminaciío de matches que tinguin una distancia major a 1.
                WeightedDamerauLevenshteinDistance damerauLevenshteinDistance =
                        new WeightedDamerauLevenshteinDistance(1,1,1,1,
                                null);

                remove = true;
                for (String source : words){
                    if(damerauLevenshteinDistance.getDistance(source,resultWords[0]) <= 1){
                        remove = false;
                        break;
                    }
                }

           }else {
                remove = !Arrays.asList(words).containsAll(Arrays.asList(resultWords));
            }

//
//            System.out.println("Words is " + Arrays.toString(words));
//            System.out.println("resultWords is " + Arrays.toString(resultWords));



//            System.out.println("Checked that " + r.getValue() + (!remove ? " is a word." : " not a word"));
            if (remove) values.remove(i);
        }
    }

    public static boolean existsType(ArrayList<ACResult> values, ACNodeType type) {
        boolean ok = false;
        for (ACResult r : values)
            if (r.getType().getType() == type.getType()) {
                ok = true;
                break;
            }
        return ok;
    }
}
