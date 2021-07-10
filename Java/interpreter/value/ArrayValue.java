package interpreter.value;

import java.util.Vector;

public class ArrayValue extends Value<Vector<? extends Value<?>>> {

    private Vector<? extends Value<?>> value;

    public ArrayValue(Vector<? extends Value<?>> value) {
        this.value = value;
    }

    public Vector<? extends Value<?>> value() {
        return value;
    }

}
