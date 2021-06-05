package NLP.AhoCorasick;

public enum ACNodeType {
    MOVIE(1),
    PERSON(2),
    OBJECT(3),
    ACTION(4),
    PRECEDENT(5),
    AFFIRMATION(6),
    NEGATION(7),
    HELLO(8),
    HELP(9),
    TIME(10),
    EXIT(11),
    HOW(12),
    WHO(13),
    ;

    private final int type;

    ACNodeType(int id) {
        this.type = id;
    }

    public int getType() {
        return type;
    }
}
