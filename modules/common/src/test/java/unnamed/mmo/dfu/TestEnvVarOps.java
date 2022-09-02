package unnamed.mmo.dfu;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

public class TestEnvVarOps {

    @Test
    public void testBasicString() {
        record Config(String value) {}
        Codec<Config> codec = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("value").forGetter(Config::value)
        ).apply(i, Config::new));

        var ops = new MockEnvVarOps(Map.of(
            "VALUE", "foo"
        ));
        var result = ops.withDecoder(codec)
                .apply(new EnvVarOps.Path(""))
                .result()
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getFirst().value).isEqualTo("foo");
    }

    @Test
    public void testBasicInt() {
        record Config(int value) {}
        Codec<Config> codec = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("value").forGetter(Config::value)
        ).apply(i, Config::new));

        var ops = new MockEnvVarOps(Map.of(
            "VALUE", "1"
        ));
        var result = ops.withDecoder(codec)
                .apply(new EnvVarOps.Path(""))
                .result()
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getFirst().value).isEqualTo(1);
    }

    @Test
    public void testNestedValue() {
        record InnerConfig(String value) {}
        Codec<InnerConfig> innerCodec = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("value").forGetter(InnerConfig::value)
        ).apply(i, InnerConfig::new));
        record Config(InnerConfig inner) {}
        Codec<Config> codec = RecordCodecBuilder.create(i -> i.group(
                innerCodec.fieldOf("inner").forGetter(Config::inner)
        ).apply(i, Config::new));

        var ops = new MockEnvVarOps(Map.of(
            "INNER_VALUE", "foo"
        ));
        var result = ops.withDecoder(codec)
                .apply(new EnvVarOps.Path(""))
                .result()
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getFirst().inner.value).isEqualTo("foo");
    }

    @Test
    public void test2xNestedValue() {
        record InnerInnerConfig(String value) {}
        Codec<InnerInnerConfig> innerInnerCodec = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("value").forGetter(InnerInnerConfig::value)
        ).apply(i, InnerInnerConfig::new));
        record InnerConfig(InnerInnerConfig inner) {}
        Codec<InnerConfig> innerCodec = RecordCodecBuilder.create(i -> i.group(
                innerInnerCodec.fieldOf("inner").forGetter(InnerConfig::inner)
        ).apply(i, InnerConfig::new));
        record Config(InnerConfig inner) {}
        Codec<Config> codec = RecordCodecBuilder.create(i -> i.group(
                innerCodec.fieldOf("inner").forGetter(Config::inner)
        ).apply(i, Config::new));

        var ops = new MockEnvVarOps(Map.of(
            "INNER_INNER_VALUE", "foo"
        ));
        var result = ops.withDecoder(codec)
                .apply(new EnvVarOps.Path(""))
                .result()
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getFirst().inner.inner.value).isEqualTo("foo");
    }

    @Test
    public void testList() {
        record Config(List<String> value) {}
        Codec<Config> codec = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.listOf().fieldOf("value").forGetter(Config::value)
        ).apply(i, Config::new));

        var ops = new MockEnvVarOps(Map.of(
                "VALUE_0", "A",
                "VALUE_1", "B",
                "VALUE_2", "C"
        ));
        var result = ops.withDecoder(codec)
                .apply(new EnvVarOps.Path(""))
                .result()
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getFirst().value).containsExactly("A", "B", "C");
    }

    @Test
    public void testListOfObject() {
        record InnerConfig(String a, String b) {}
        Codec<InnerConfig> innerCodec = RecordCodecBuilder.create(i -> i.group(
                Codec.STRING.fieldOf("a").forGetter(InnerConfig::a),
                Codec.STRING.fieldOf("b").forGetter(InnerConfig::b)
        ).apply(i, InnerConfig::new));
        record Config(List<InnerConfig> value) {}
        Codec<Config> codec = RecordCodecBuilder.create(i -> i.group(
                innerCodec.listOf().fieldOf("value").forGetter(Config::value)
        ).apply(i, Config::new));

        var ops = new MockEnvVarOps(Map.of(
                "VALUE_0_A", "0 a",
                "VALUE_0_B", "0 b",
                "VALUE_1_A", "1 a",
                "VALUE_1_B", "1 b"
        ));
        var result = ops.withDecoder(codec)
                .apply(new EnvVarOps.Path(""))
                .result()
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getFirst().value).containsExactly(
                new InnerConfig("0 a", "0 b"),
                new InnerConfig("1 a", "1 b")
        );
    }




    private class MockEnvVarOps extends EnvVarOps {
        private final Map<String, String> env;

        private MockEnvVarOps(Map<String, String> env) {this.env = env;}

        @Override
        protected String get(Path path) {
            var value = env.get(path.path().toUpperCase(Locale.ROOT));
            return value == null ? "" : value;
        }

        @Override
        protected Collection<String> envKeys() {
            return env.keySet();
        }
    }
}
