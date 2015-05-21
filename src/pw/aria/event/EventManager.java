package pw.aria.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The main event manager. This class maintains a {@link java.util.Queue} of
 * {@link pw.aria.event.Listener}s that is iterated over whenever
 * {@link #push(Object)} is invoked. For each <tt>Listener</tt> in the
 * <tt>Queue</tt>, it checks if its <tt>type</tt> (really a
 * <tt>Class&lt;T&gt</tt>), and if it is, it pushes the event to it. After all
 * listeners have been used, {@link #push(Object)} returns the object that was
 * pushed.
 */
@SuppressWarnings("unused")
public class EventManager {
    /**
     * Whether pushing events is allowed. If this is <tt>false</tt>, then no
     * events can be pushed through the system, and the event currently being
     * fired will be immediately terminated.
     */
    private static volatile AtomicBoolean allowPush = new AtomicBoolean(true);

    /**
     * The {@link java.util.Queue} of {@link pw.aria.event.Listener}s. By
     * default, this is a {@link java.util.concurrent.ConcurrentLinkedQueue}.
     */
    private static final Queue<Listener<?>> listeners = new ConcurrentLinkedQueue<>();

    /**
     * Registers an arbitrary number of {@link pw.aria.event.Listener}s.
     *
     * @param listenerArg A vararg of <tt>Listeners</tt> to be registered.
     */
    public static synchronized void register(Listener<?>... listenerArg) {
        for(Listener listener : listenerArg) {
            if(!listeners.add(listener)) {
                System.out.println(String.format("Failed to add listener: %s", listener));
            }
        }
    }

    /**
     * Unregisters an arbitrary number of {@link pw.aria.event.Listener}s.
     *
     * @param listenerArg A vararg of <tt>Listeners</tt> to be unregistered.
     */
    public static synchronized void unregister(Listener<?>... listenerArg) {
        for(Listener listener : listenerArg) {
            if(!listeners.remove(listener)) {
                System.out.println(String.format("Failed to remove listener: %s", listener));
            }
        }
    }

    /**
     * Pushes an event through the "system" as described in the class' JavaDoc.
     *
     * @param object The event to push.
     * @param <T> The type of the event. Used for casting internally.
     * @return The event that was pushed.
     */
    @SuppressWarnings("unchecked")
    public static synchronized <T> T push(T object) {
        for(Listener e : listeners) {
            if(e.getType().equals(object.getClass())) {
                e.event(object);
            }
            if(!allowPush.get()) {
                return object;
            }
        }
        /*listeners.stream().filter(l -> l.getType().equals(object.getClass()))
                .forEach(l -> ((Listener<T>) l).event(object));*/
        return object;
    }

    /**
     * Unregisters all registered listeners. Note that this method will also
     * cause events to no longer be pushed until changed with
     * {@link #setPushState(boolean)}.
     */
    public static synchronized void unregisterAll() {
        listeners.clear();
    }

    /**
     * Sets whether or not events can be pushed.
     * @param b The new state to set
     * @return The newly set state
     */
    public static synchronized boolean setPushState(boolean b) {
        allowPush.set(b);
        return allowPush.get();
    }

    /**
     * Returns the current event push state
     * @return The current event push state
     */
    public static synchronized boolean getPushState() {
        return allowPush.get();
    }
}
