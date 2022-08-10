package unnamed.mmo.data;

import org.jetbrains.annotations.NotNull;

/**
 * A source of numbers, implementations decide where the numbers come from
 */
public interface NumberSource {

    static @NotNull NumberSource constant(double value) {
        return () -> value;
    }


    double random();

}
