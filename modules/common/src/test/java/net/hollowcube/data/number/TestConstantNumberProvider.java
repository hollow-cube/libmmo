package net.hollowcube.data.number;

import net.hollowcube.data.NumberSource;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import static com.google.common.truth.Truth.assertThat;

public class TestConstantNumberProvider {

    @Test
    public void testHappyCase() {
        var source = NumberSource.constant(0);
        var provider = new ConstantNumberProvider(1.0);

        assertThat(provider.nextLong(source)).isEqualTo(1);
    }

    @Test
    public void testIgnoresSource() {
        var source = new NumberSource() {
            @Override
            public double random() {
                throw new AssertionFailedError("Should not be called.");
            }
        };
        var provider = new ConstantNumberProvider(1.0);

        provider.nextLong(source);
        provider.nextDouble(source);
    }

}
