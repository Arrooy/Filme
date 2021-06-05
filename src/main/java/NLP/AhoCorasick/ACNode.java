package NLP.AhoCorasick;

import java.util.ArrayList;
import java.util.HashMap;

public class ACNode {

    private final char value;
    private final HashMap<Character, ACNode> children;
    private ArrayList<ACNode> dictLinks;
    private ACNode failureLink;
    private ACNodeType type;
    private final boolean isRoot;
    private final ACNode father;

    public ACNode(char value, ACNode father, boolean isResult) {
        this.value = value;

        this.father = father;
        children = new HashMap<>();
        isRoot = false;
        if (isResult) type = AhoCorasick.getInstance().getCurrentType();
        else type = null;
    }

    public ACNode(char value, boolean isRoot) {
        this.value = value;

        children = new HashMap<>();
        this.isRoot = isRoot;
        type = null;
        father = null;
    }

    public void insert (String s) {
        if (s.isEmpty()) return;
        char firstChar = s.toCharArray()[0];

        ACNode next = children.get(firstChar);

        if (next == null) {
            ACNode newNode = new ACNode(firstChar, this, s.length() == 1);
            children.put(firstChar, newNode);
            newNode.insert(s.substring(1));
        } else {
            if (s.length() == 1) next.type = AhoCorasick.getInstance().getCurrentType();
            next.insert(s.substring(1));
        }
    }

    public void findFailureLink() {
        for (ACNode n: children.values()) n.findFailureLink();

        if (this.isRoot || father.isRoot) {
            this.failureLink = father;
            return;
        }

        String suffix = getSuffix().substring(1);
        ACNode link = null;

        while (link == null && !suffix.isEmpty()) {
            link = AhoCorasick.getInstance().getNode(suffix);
            suffix = suffix.substring(1);
        }

        if (link != null) this.failureLink = link;
        else this.failureLink = AhoCorasick.getInstance().getRoot();
    }

    public void findDictLinks() {
        for (ACNode n: children.values()) n.findDictLinks();
        dictLinks = new ArrayList<>();

        if (this.isRoot || father.isRoot) return;

        ACNode node = this.failureLink;

        if (node.isRoot) dictLinks.add(node);

        while (!node.isRoot) {
            if (node.type != null) dictLinks.add(node);
            node = node.failureLink;
        }
    }

    public String getSuffix() {
        if (father.isRoot) return "";
        else return father.getSuffix() + value;
    }

    public ACNode getChildren(Character c) {
        return children.get(c);
    }

    public char getValue() {
        return value;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public ArrayList<ACNode> getDictLinks() {
        return dictLinks;
    }

    public ACNode getFailureLink() {
        return failureLink;
    }

    public String getFullValue() {
        if (isRoot) return "";
        else return father.getFullValue() + value;
    }

    public ACNodeType getType() {
        return type;
    }
}
