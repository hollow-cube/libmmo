package net.hollowcube.mql.runtime;

import net.hollowcube.mql.foreign.MqlForeignFunctions;
import net.hollowcube.mql.foreign.Query;

import java.util.concurrent.ThreadLocalRandom;

public class MqlMath {

    public static final MqlScope INSTANCE = MqlForeignFunctions.create(MqlMath.class, null);

    private MqlMath() {}

    /** Absolute value of value */
    @Query
    public static double abs(double value) {
        return Math.abs(value);
    }

    /** arccos of value */
    @Query
    public static double acos(double value) {
        return Math.acos(value);
    }

    /** arcsin of value */
    @Query
    public static double asin(double value) {
        return Math.asin(value);
    }

    /** arctan of value */
    @Query
    public static double atan(double value) {
        return Math.atan(value);
    }

    /** arctan of y/x. NOTE: the order of arguments! */
    @Query
    public static double atan2(double y, double x) {
        return Math.atan2(y, x);
    }

    /** Round value up to nearest integral number */
    @Query
    public static double ceil(double value) {
        return Math.ceil(value);
    }

    /** Clamp value to between min and max inclusive */
    @Query
    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /** Cosine (in degrees) of value */
    @Query
    public static double cos(double value) {
        return Math.cos(value);
    }

    /** Returns the sum of 'num' random numbers, each with a value from low to high. Note: the generated random numbers are not integers like normal dice. For that, use math.die_roll_integer. */
    @Query
    public static double dieRoll(double num, double low, double high) {
        double total = 0;
        for (int i = 0; i < num; i++)
            total += random(low, high);
        return total;
    }

    /** Returns the sum of 'num' random integer numbers, each with a value from low to high. Note: the generated random numbers are integers like normal dice. */
    @Query
    public static double dieRollInteger(double num, double low, double high) {
        double total = 0;
        for (int i = 0; i < num; i++)
            total += randomInteger(low, high);
        return total;
    }

    /** Calculates e to the value 'nth' power */
    @Query
    public static double exp(double value) {
        return Math.exp(value);
    }

    /** Round value down to nearest integral number */
    @Query
    public static double floor(double value) {
        return Math.floor(value);
    }

    /** Useful for simple smooth curve interpolation using one of the Hermite Basis functions: 3t^2 - 2t^3. Note that while any valid float is a valid input, this function works best in the range [0,1]. */
    @Query
    public static double hermiteBlend(double value) {
        //todo: implement me
        throw new MqlRuntimeError("hermite_blend not implemented");
    }

    /** Lerp from start to end via zeroToOne */
    @Query
    public static double lerp(double start, double end, double zeroToOne) {
        //todo test me
        zeroToOne = clamp(zeroToOne, 0, 1);
        return start * zeroToOne + end * (1D - zeroToOne);
    }

    /** Lerp the shortest direction around a circle from start degrees to end degrees via zeroToOne */
    @Query
    public static double lerprotate(double start, double end, double zeroToOne) {
        //todo test me
        zeroToOne = clamp(zeroToOne, 0, 1);
        double diff = end - start;
        if (diff > 180) diff -= 360;
        else if (diff < -180) diff += 360;
        return start + diff * zeroToOne;
    }

    /** Natural logarithm of value */
    @Query
    public static double ln(double value) {
        return Math.log(value);
    }

    /** Return highest value of A or B */
    @Query
    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    /** Return lowest value of A or B */
    @Query
    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    /** Minimize angle magnitude (in degrees) into the range [-180, 180) */
    @Query
    public static double minAngle(double value) {
        //todo: implement me
        throw new MqlRuntimeError("hermite_blend not implemented");
    }

    /** Return the remainder of value / denominator */
    @Query
    public static double mod(double value, double denominator) {
        return value % denominator;
    }

    /** Returns the float representation of the constant pi. */
    @Query
    public static double pi() {
        return Math.PI;
    }

    /** Elevates base to the exponent'th power */
    @Query
    public static double pow(double base, double exponent) {
        return Math.pow(base, exponent);
    }

    /**
     * Random value between low (inclusive) and high (exclusive)
     * <p>
     * Note: The original molang spec says that the range is inclusive, but this high end is exclusive.
     */
    @Query
    public static double random(double low, double high) {
        return ThreadLocalRandom.current().nextDouble(low, high);
    }

    /** Random integer value between low and high (inclusive) */
    @Query
    public static double randomInteger(double low, double high) {
        return ThreadLocalRandom.current().nextInt((int) low, (int) high + 1);
    }

    /** Round value to nearest integral number */
    @Query
    public static double round(double value) {
        return Math.round(value);
    }

    /** Sine (in degrees) of value */
    @Query
    public static double sin(double value) {
        return Math.sin(value);
    }

    /** Square root of value */
    @Query
    public static double sqrt(double value) {
        return Math.sqrt(value);
    }

    /** Round value towards zero */
    @Query
    public static double trunc(double value) {
        return value < 0 ? Math.ceil(value) : Math.floor(value);
    }

}
