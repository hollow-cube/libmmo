package net.hollowcube.mql.foreign;

import net.hollowcube.mql.runtime.MqlRuntimeError;
import net.hollowcube.mql.runtime.MqlScope;
import net.hollowcube.mql.value.MqlCallable;
import net.hollowcube.mql.value.MqlValue;
import net.hollowcube.util.StringUtil;
import net.minestom.server.utils.validate.Check;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MqlForeignFunctions {

    public static <T> @NotNull MqlScope create(@NotNull Class<T> type, @Nullable T instance) {
        // Construct a map with all static and non static @Query methods in the class
        Map<String, MqlCallable> functions = new HashMap<>();
        for (Method method : type.getMethods()) {
            Query annotation = method.getAnnotation(Query.class);
            if (annotation == null) continue;

            boolean isStatic = (method.getModifiers() & Modifier.STATIC) != 0;
            MqlCallable callable = createForeign(method, isStatic ? null : instance);

            String name = annotation.value();
            if (name.isEmpty()) name = StringUtil.camelCaseToSnakeCase(method.getName());

            functions.put(name, callable);
        }

        // Return the scope using the functions map
        return name -> {
            MqlCallable function = functions.get(name);
            if (function == null)
                throw new MqlRuntimeError("No such function: " + name);

            // 0 arg functions do not need an explicit call
            if (function.arity() == 0)
                return function.call(List.of());
            return function;
        };
    }

    /**
     * @param method The method to bind. Must be public, may be static.
     * @param bindTo The instance of the method's class to bind to. If the method is static, this must be null.
     * @return An {@link MqlCallable} representing an mql accessible function.
     *
     * @see <a href="https://github.com/Moulberry/Minestand/blob/master/src/main/java/net/gauntletmc/command/FastInvokerFactory.java">FastInvokerFactory</a>
     */
    public static @NotNull MqlCallable createForeign(@NotNull Method method, @UnknownNullability Object bindTo) {
        Check.argCondition((method.getModifiers() & Modifier.PUBLIC) == 0, "method must be public");
        Check.argCondition(bindTo != null && !method.getDeclaringClass().isInstance(bindTo), "bindTo must be an instance of the method class");

        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            Class<?>[] erasedTypes = new Class[method.getParameterCount()];
            Arrays.fill(erasedTypes, Object.class);
            Class<?> erasedReturnType = method.getReturnType();
            if (erasedReturnType != void.class) {
                erasedReturnType = Object.class;
            }

            Class<?>[] fixedTypes = new Class[method.getParameterCount()];
            int index = 0;
            for (Class<?> clazz : method.getParameterTypes()) {
                fixedTypes[index++] = convertPrimitive(clazz);
            }
            Class<?> fixedReturnType = method.getReturnType();
            if (fixedReturnType != void.class) {
                fixedReturnType = convertPrimitive(fixedReturnType);
            }

            boolean isVoid = erasedReturnType == void.class;
            boolean isStatic = (method.getModifiers() & Modifier.STATIC) != 0;

            CallSite callsite = LambdaMetafactory.metafactory(
                    lookup,
                    "accept" + method.getParameterCount() + (isVoid ? "V" : "R"),
                    MethodType.methodType(NConsumer.class, isStatic ? new Class[0] : new Class[]{method.getDeclaringClass()}),
                    MethodType.methodType(erasedReturnType, erasedTypes),
                    lookup.unreflect(method),
                    MethodType.methodType(fixedReturnType, fixedTypes)
            );

            var handle = (NConsumer) (isStatic ? callsite.getTarget().invoke() : callsite.getTarget().bindTo(bindTo).invoke());

            return new ForeignCallable(handle, fixedTypes, fixedReturnType);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> convertPrimitive(Class<?> in) {
        if (in == int.class) {
            return Integer.class;
        } else if (in == float.class) {
            return Float.class;
        } else if (in == boolean.class) {
            return Boolean.class;
        } else if (in == double.class) {
            return Double.class;
        } else if (in == long.class) {
            return Long.class;
        } else {
            return in;
        }
    }

    private record ForeignCallable(
            NConsumer handle,
            Class<?>[] parameterTypes,
            Class<?> returnType
    ) implements MqlCallable {

        @Override
        public int arity() {
            return parameterTypes.length;
        }

        @Override
        public @NotNull MqlValue call(@NotNull List<MqlValue> args) {
            if (args.size() != parameterTypes.length) {
                //todo mql exception
                throw new IllegalArgumentException("Expected " + parameterTypes.length + " arguments, got " + args.size());
            }

            Object[] javaArgs = new Object[args.size()];
            for (int i = 0; i < args.size(); i++) {
                javaArgs[i] = MqlForeignTypes.fromMql(args.get(i), parameterTypes[i]);
            }

            boolean isVoid = returnType == void.class;
            if (isVoid) {
                switch (args.size()) {
                    case 0 -> handle.accept0V();
                    case 1 -> handle.accept1V(javaArgs[0]);
                    case 2 -> handle.accept2V(javaArgs[0], javaArgs[1]);
                    case 3 -> handle.accept3V(javaArgs[0], javaArgs[1], javaArgs[2]);
                    case 4 -> handle.accept4V(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3]);
                    case 5 -> handle.accept5V(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4]);
                    case 6 -> handle.accept6V(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5]);
                    case 7 -> handle.accept7V(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5], javaArgs[6]);
                    case 8 -> handle.accept8V(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5], javaArgs[6], javaArgs[7]);
                    case 9 -> handle.accept9V(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5], javaArgs[6], javaArgs[7], javaArgs[8]);
                }
            } else {
                Object result = switch (args.size()) {
                    case 0 -> handle.accept0R();
                    case 1 -> handle.accept1R(javaArgs[0]);
                    case 2 -> handle.accept2R(javaArgs[0], javaArgs[1]);
                    case 3 -> handle.accept3R(javaArgs[0], javaArgs[1], javaArgs[2]);
                    case 4 -> handle.accept4R(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3]);
                    case 5 -> handle.accept5R(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4]);
                    case 6 -> handle.accept6R(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5]);
                    case 7 -> handle.accept7R(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5], javaArgs[6]);
                    case 8 -> handle.accept8R(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5], javaArgs[6], javaArgs[7]);
                    case 9 -> handle.accept9R(javaArgs[0], javaArgs[1], javaArgs[2], javaArgs[3], javaArgs[4], javaArgs[5], javaArgs[6], javaArgs[7], javaArgs[8]);
                    default -> throw new MqlRuntimeError("unreachable arg error");
                };

                return MqlForeignTypes.toMql(result);
            }

            return MqlValue.NULL;
        }
    }

    public interface NConsumer<R, P1, P2, P3, P4, P5, P6, P7, P8, P9> {

        R accept0R();
        R accept1R(P1 p1);
        R accept2R(P1 p1, P2 p2);
        R accept3R(P1 p1, P2 p2, P3 p3);
        R accept4R(P1 p1, P2 p2, P3 p3, P4 p4);
        R accept5R(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
        R accept6R(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
        R accept7R(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
        R accept8R(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
        R accept9R(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

        void accept0V();
        void accept1V(P1 p1);
        void accept2V(P1 p1, P2 p2);
        void accept3V(P1 p1, P2 p2, P3 p3);
        void accept4V(P1 p1, P2 p2, P3 p3, P4 p4);
        void accept5V(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);
        void accept6V(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6);
        void accept7V(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7);
        void accept8V(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8);
        void accept9V(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

    }
}
