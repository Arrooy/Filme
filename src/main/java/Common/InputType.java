package Common;

import java.util.LinkedList;

public enum InputType {
    BASE(0),
    AFFIRMATIVE(2),
    NEGATIVE(4),
    EXIT(8),
    HELLO(16),
    HELP(32),
    TIME(64),
    HOW(128),
    WHO(256);

    public final int v;

    InputType(int v) {
        this.v = v;
    }
}
