package interpreter.value;

public class StringValue extends Value<String> {

    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
