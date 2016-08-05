package rupal.subscriber;

/**
 * A class holds constants.
 */
public final class Constants {
    /**
     * A memcache key for storing query result for recent messages.
     */
    public static final String MESSAGE_CACHE_KEY = "messageCache";
    /**
     * A base package name.
     */
    public static final String BASE_PACKAGE =
            "com.homedepot.sa.el.common.foundation.registry";

    /**
     * Prevents instantiation.
     */
    private Constants() {
    }
}