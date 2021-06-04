package Common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public enum InputType {
    BASE(0),
    AFFIRMATIVE(2),
    NEGATIVE(4),
    EXIT(8),
    HELLO(16),
    HELP(32),
    TIME(64);

    static Map<Integer, InputType> map = new HashMap<>();

    static {
        for (InputType catalog : InputType.values()) {
            map.put(catalog.value, catalog);
        }
    }

    private final int value;

    InputType(int value) {
        this.value = value;
    }

    public LinkedList<InputType> decompose(){
        LinkedList<InputType> inputs = new LinkedList<>();
        int editedVal = value;
        System.out.println("VAl is " + value);

        do{
            int usedPower = 2;
            for (InputType it : InputType.values()) {
                if(it.value == editedVal) {
                    inputs.add(it);
                    usedPower = it.value;
                    break;
                }
            }
            if(editedVal != 0)
                editedVal/=usedPower;
        }while (editedVal != 0);
        return inputs;
    }

    public InputType add(InputType exit) {
        return map.get(exit.value + value);
    }
}
