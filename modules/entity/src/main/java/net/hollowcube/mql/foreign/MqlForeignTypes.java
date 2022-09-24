package net.hollowcube.mql.foreign;

import net.hollowcube.mql.value.MqlNumberValue;
import net.hollowcube.mql.value.MqlValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Conversion utility between java types and MQL types.
 * <p>
 * todo not sure if other types should be convertable. I think just doubles is fine.
 */
public class MqlForeignTypes {

    public static @NotNull MqlValue toMql(@UnknownNullability Object javaValue) {
        if (javaValue == null) return MqlValue.NULL;
        if (javaValue instanceof MqlValue value)
            return value;
        if (javaValue instanceof Double value)
            return new MqlNumberValue(value);
//        if (javaValue instanceof Float value)
//            return new MqlNumberValue(value);
//        if (javaValue instanceof Long value)
//            return new MqlNumberValue(value);
//        if (javaValue instanceof Integer value)
//            return new MqlNumberValue(value);
//        if (javaValue instanceof Short value)
//            return new MqlNumberValue(value);
//        if (javaValue instanceof Byte value)
//            return new MqlNumberValue(value);
        if (javaValue instanceof Boolean value)
            return MqlValue.from(value);
        throw new RuntimeException("cannot convert " + javaValue.getClass().getSimpleName() + " to mql value");
    }

    public static @UnknownNullability Object fromMql(@NotNull MqlValue value, @NotNull Class<?> targetType) {
        if (value instanceof MqlNumberValue numberValue) {
            if (Double.class.equals(targetType) || double.class.equals(targetType)) {
                return numberValue.value();
//            } else if (Float.class.equals(targetType) || float.class.equals(targetType)) {
//                return (float) numberValue.value();
//            } else if (Long.class.equals(targetType) || long.class.equals(targetType)) {
//                return (long) numberValue.value();
//            } else if (Integer.class.equals(targetType) || int.class.equals(targetType)) {
//                return (int) numberValue.value();
//            } else if (Short.class.equals(targetType) || short.class.equals(targetType)) {
//                return (short) numberValue.value();
//            } else if (Byte.class.equals(targetType) || byte.class.equals(targetType)) {
//                return (byte) numberValue.value();
            } else if (Boolean.class.equals(targetType) || boolean.class.equals(targetType)) {
                return numberValue.value() != 0;
            }
            throw new RuntimeException("cannot convert number " + targetType.getSimpleName());
        }
        throw new RuntimeException("cannot convert " + value.getClass().getSimpleName() + " to " + targetType.getSimpleName());
    }

}
