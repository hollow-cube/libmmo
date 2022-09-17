package net.hollowcube.mql.foreign;

import net.hollowcube.mql.value.MqlCallable;
import net.hollowcube.mql.value.MqlValue;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.util.concurrent.AtomicDouble;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.truth.Truth.assertThat;

public class TestMqlForeignFunctions {

    private static final AtomicBoolean test1Called = new AtomicBoolean(false);

    public static void test1() {
        test1Called.set(true);
    }

    @Test
    public void emptyVoidFunction() throws Exception {
        Method method = getClass().getMethod("test1");
        MqlCallable function = MqlForeignFunctions.createForeign(method, null);
        assertThat(function.arity()).isEqualTo(0);
        assertThat(function.call(List.of())).isEqualTo(MqlValue.NULL);
        assertThat(test1Called.get()).isTrue();
    }

    private static final AtomicDouble test2Value = new AtomicDouble(0);

    public static void test2(double value) {
        test2Value.set(value);
    }

    @Test
    public void singleArgVoidFunction() throws Exception {
        Method method = getClass().getMethod("test2", double.class);
        MqlCallable function = MqlForeignFunctions.createForeign(method, null);
        MqlValue result = function.call(List.of(MqlValue.from(10.5)));

        assertThat(function.arity()).isEqualTo(1);
        assertThat(result).isEqualTo(MqlValue.NULL);
        assertThat(test2Value.get()).isEqualTo(10.5);
    }

    public static double test3() {
        return 10.5;
    }

    @Test
    public void emptyNonVoidFunction() throws Exception {
        Method method = getClass().getMethod("test3");
        MqlCallable function = MqlForeignFunctions.createForeign(method, null);
        MqlValue result = function.call(List.of());

        assertThat(function.arity()).isEqualTo(0);
        assertThat(result).isEqualTo(MqlValue.from(10.5));
    }

    public static double test4(double a, double b) {
        return a + b;
    }

    @Test
    public void multiParamNonVoidFunction() throws Exception {
        Method method = getClass().getMethod("test4", double.class, double.class);
        MqlCallable function = MqlForeignFunctions.createForeign(method, null);
        MqlValue result = function.call(List.of(MqlValue.from(10.5), MqlValue.from(20.5)));

        assertThat(function.arity()).isEqualTo(2);
        assertThat(result).isEqualTo(MqlValue.from(31));
    }
}
