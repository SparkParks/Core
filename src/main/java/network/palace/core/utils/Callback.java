package network.palace.core.utils;

/**
 * Represents a generic callback interface that can be implemented to execute
 * custom logic when a specific task or process is completed.
 *
 * This interface is intended for use cases where an operation needs to notify
 * a listener or trigger custom logic upon its completion. The {@code finished}
 * method will be called to signify the conclusion of the associated task.
 */
public interface Callback {

    /**
     * Invoked to signal the completion of a task or process.
     *
     * Implementers of this method can define the actions to be
     * performed when the associated task has finished. This can
     * include cleanup operations, triggering subsequent actions,
     * or notifying other components.
     */
    void finished();
}
