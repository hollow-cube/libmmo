package net.hollowcube.mql.value;

public record MqlNumberValue(double value) implements MqlValue {

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
