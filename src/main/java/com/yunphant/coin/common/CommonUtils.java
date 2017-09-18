package com.yunphant.coin.common;

import org.slf4j.Logger;

/**
 * The type Common utils.
 */
public class CommonUtils {
    /**
     * Log error.
     *
     * @param logger  the logger
     * @param message the message
     */
    public static void logError(Logger logger, String message) {
        logger.error(message);
        throw new YunphantCoinException(message);
    }

    /**
     * Log error.
     *
     * @param logger    the logger
     * @param message   the message
     * @param throwable the throwable
     */
    public static void logError(Logger logger, String message, Throwable throwable ) {
        logger.error(message,throwable);
        throw new YunphantCoinException(message,throwable);
    }
}
