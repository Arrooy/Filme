package NLP.Aho_Corasick;

import java.util.ArrayList;

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

    // Accés singleton a la instància de la classe
    private static AhoCorasick singleton;
    public static AhoCorasick getInstance() {
        if (singleton == null) singleton = new AhoCorasick();
        return singleton;
    }

    // Constructor
    public AhoCorasick() {
        root = new ACNode('\0', true);
    }

    /**
     * Funció per inserir un nou valor al sistema. El cost de la inserció és de N,
     * on N és la mida de la cadena a inserir. El que realitza consisteix en fer servir
     * els caràcters de la cadena per anar seguint el Trie, i, si no existeix un node determinat
     * el crea. Finalment marca el node equivalent a l'últim caràcter com a node que conté un valor.
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
     * @return root
     */
    public ACNode getRoot() {
        return root;
    }

    /**
     * Funció que a partir d'una cadena d'entrada determina quina és la llista de totes les
     * coincidències trobades a partir del seu diccionari.
     * @param value Cadena a analitzar
     * @return Llista de paraules del diccionari que s'ha trobat a la cadena d'entrada
     */
    public ArrayList<String> analyzeString(String value) {
        ArrayList<ACNode> resultsRaw = new ArrayList<>();
        ACNode current = root;

        for (char c: value.toCharArray()) {
            ACNode next = current.getChildren(c);

            if (next != null) {
                current = next;
                if (current.getValue() == c && current.isResult()) {
                    String name = current.getFullValue();
                    resultsRaw.add(current);
                }
                for (ACNode n: current.getDictLinks()) if (n.getValue() == c) {
                    String name = n.getFullValue();
                    resultsRaw.add(n);
                }
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

        ArrayList<String> results = new ArrayList<>();
        for (ACNode n: resultsRaw) results.add(n.getFullValue());
        return results;
    }
}
