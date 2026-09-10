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
 * the wrong thread throws immediately rather than working nine times out of ten.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ThreadSafety {

    Requirement value();

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
