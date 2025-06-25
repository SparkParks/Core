package network.palace.core.npc;

import com.comphenix.protocol.wrappers.Vector3F;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.*;

/**
 * The ProtocolLibSerializers class serves as a utility for creating
 * WrappedDataWatcherObject instances with appropriate serializers
 * based on different data types. This class abstracts the process
 * of retrieving the correct serializers from the underlying registry
 * and constructing watcher objects for use in data packets.
 */
public class ProtocolLibSerializers {

    /**
     * Retrieves a {@code WrappedDataWatcherObject} that refers to a byte value
     * at the specified index. This method uses the appropriate serializer for byte
     * data from the registry to create the watcher object.
     *
     * @param index the index of the data watcher object, determining its position
     *              or identifier in the list of data watchers.
     * @return a {@code WrappedDataWatcherObject} configured to handle byte data
     *         at the specified index.
     */
    public static WrappedDataWatcherObject getByte(int index) {
        Serializer bs = Registry.get(Byte.class);
        return new WrappedDataWatcherObject(index, bs);
    }

    /**
     * Retrieves a {@code WrappedDataWatcherObject} configured to handle boolean data
     * at the specified index. This method uses the appropriate serializer for
     * boolean data from the registry to create the watcher object.
     *
     * @param index the index of the data watcher object, determining its position
     *              or identifier in the list of data watchers.
     * @return a {@code WrappedDataWatcherObject} configured to handle boolean data
     *         at the specified index.
     */
    public static WrappedDataWatcherObject getBoolean(int index) {
        Serializer bs = Registry.get(Boolean.class);
        return new WrappedDataWatcherObject(index, bs);
    }

    /**
     * Retrieves a {@code WrappedDataWatcherObject} configured to handle float data
     * at the specified index. This method uses the appropriate serializer for
     * float data from the registry to create the watcher object.
     *
     * @param index the index of the data watcher object, determining its position
     *              or identifier in the list of data watchers.
     * @return a {@code WrappedDataWatcherObject} configured to handle float data
     *         at the specified index.
     */
    public static WrappedDataWatcherObject getFloat(int index) {
        Serializer fs = Registry.get(Float.class);
        return new WrappedDataWatcherObject(index, fs);
    }

    /**
     * Retrieves a {@code WrappedDataWatcherObject} configured to handle string data
     * at the specified index. This method uses the appropriate serializer for
     * string data from the registry to create the watcher object.
     *
     * @param index the index of the data watcher object, determining its position
     *              or identifier in the list of data watchers.
     * @return a {@code WrappedDataWatcherObject} configured to handle string data
     *         at the specified index.
     */
    public static WrappedDataWatcherObject getString(int index) {
        Serializer ss = Registry.get(String.class);
        return new WrappedDataWatcherObject(index, ss);
    }

    /**
     * Retrieves a {@code WrappedDataWatcherObject} configured to handle {@code Vector3F} data
     * at the specified index. This method uses the appropriate serializer for {@code Vector3F}
     * data from the registry to create the watcher object.
     *
     * @param index the index of the data watcher object, determining its position
     *              or identifier in the list of data watchers.
     * @return a {@code WrappedDataWatcherObject} configured to handle {@code Vector3F}
     *         data at the specified index.
     */
    public static WrappedDataWatcherObject getVector3F(int index) {
        Serializer vs = Registry.get(Vector3F.getMinecraftClass());
        return new WrappedDataWatcherObject(index, vs);
    }
}
