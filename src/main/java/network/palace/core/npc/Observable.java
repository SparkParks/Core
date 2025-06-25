package network.palace.core.npc;

import com.google.common.collect.ImmutableSet;

/**
 * This interface defines a mechanism for managing a set of observers
 * and notifying them of changes or updates within a system. Observers can
 * be registered, unregistered, and queried through the provided methods.
 *
 * @param <T> the type of observers that can be registered with the observable
 */
public interface Observable<T> {
    /**
     * Registers an observer to the observable system.
     *
     * @param observer the observer to be registered; this object will be
     *                 notified of changes or updates in the observable system.
     */
    void registerObservable(T observer);

    /**
     * Unregisters an observer from the observable system.
     *
     * Once an observer is unregistered, it will no longer receive notifications or updates
     * from the observable system.
     *
     * @param observer the observer to be unregistered; this must be an instance of type {@code T}.
     *                 If the observer is not currently registered, invoking this method has no effect.
     */
    void unregisterObservable(T observer);

    /**
     * Retrieves the set of observers currently associated with the observable system.
     * Observers are entities that are notified of changes or updates within the system.
     *
     * @return an immutable set of observers of type {@code T}, representing the entities
     *         registered with the observable system.
     */
    ImmutableSet<T> getObservers();
}
