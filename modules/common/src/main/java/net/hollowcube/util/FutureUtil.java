package net.hollowcube.util;

import org.jetbrains.annotations.Contract;

public class FutureUtil {

    @Contract("_ -> null")
    @SuppressWarnings("TypeParameterUnusedInFormals")
    public static <T> T handleException(Throwable throwable) {
        //todo log to sentry or something
        throwable.printStackTrace();
        return null;
    }

}
