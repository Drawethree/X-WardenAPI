package dev.drawethree.xwarden.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Which thread a method has to be called from.
 *
 * <p>Two facts collide in an API over a Minecraft plugin: reads hit a database and block, and calls
 * into the server are only legal on the thread that ticks it. Getting either the wrong way round is
 * invisible in testing and catastrophic in production - a blocking read on the server thread stalls
 * the whole game, and a server call off it corrupts state in ways that surface much later.
 *
 * <p>So every method that has a requirement declares it here, and X-Warden enforces it: a call from
 * the wrong thread throws {@link IllegalStateException} immediately, naming the method and the
 * thread it wanted, rather than working nine times out of ten.
 *
 * <p>An addon doing work on a thread of its own - a web request, a socket - reaches the server
 * thread through {@link dev.drawethree.xwarden.api.addon.XWardenAddonContext#call} and leaves it
 * through {@link dev.drawethree.xwarden.api.addon.XWardenAddonContext#async}.
 *
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ThreadSafety {

    /**
     * The thread the annotated method must be called from.
     *
     * @return the requirement
     * @since 1.0.0
     */
    Requirement value();

    /**
     * The three answers to "which thread".
     *
     * @since 1.0.0
     */
    enum Requirement {

        /**
         * Must not be called from the server thread. It reads the database and will block for as
         * long as that takes.
         */
        OFF_PRIMARY,

        /**
         * Must be called from the server thread, because it touches the server. Use the bridge on
         * {@code XWardenAddonContext} to get there from a worker thread.
         */
        PRIMARY,

        /** Safe from anywhere: reads memory, touches neither the database nor the server. */
        ANY
    }
}
