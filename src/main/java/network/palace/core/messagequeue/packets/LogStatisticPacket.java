package network.palace.core.messagequeue.packets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * The LogStatisticPacket class represents a packet used for logging statistical data.
 * It is a specific implementation of the MQPacket class, providing mechanisms to
 * handle measurement data, tags, and fields.
 * <p>
 * This packet uses JSON objects to encapsulate and serialize measurement data,
 * which consists of three main components:
 * - measurement: A string representing the name or type of measurement.
 * - tags: A map containing key-value pairs representing metadata or context about the measurement.
 * - fields: A map containing key-value pairs representing the actual data or metrics of the measurement.
 * <p>
 * The class provides two constructors: one for initializing the packet from a JSON object,
 * and another for manually creating the packet by providing measurement, tags, and fields.
 */
@Getter
public class LogStatisticPacket extends MQPacket {
    /**
     * Represents the measurement identifier for a logging statistic packet.
     * The value of this variable is the fundamental identifier used in the
     * context of logging and analytics to specify the type or category of measurement
     * associated with this packet.
     * <p>
     * This field is immutable and should be assigned a meaningful identifier
     * that aligns with the logging or monitoring system. Typically, it may
     * represent a metric name or event type being tracked in the system.
     */
    private final String measurement;

    /**
     * A collection of key-value pairs representing metadata associated with a log statistic packet.
     * The keys are unique strings identifying the metadata, and the values are objects of any type
     * that hold the associated data.
     * <p>
     * This field is immutable and holds metadata that can be used for categorization, filtering,
     * or any additional information relevant to the log statistics being captured.
     */
    private final HashMap<String, Object> tags;

    /**
     * A map containing key-value pairs that represent specific statistical fields
     * related to a given measurement in a logging context. Each key is a string
     * representing the name of the field, and each value is an object representing
     * its corresponding data.
     * <p>
     * This field is immutable and stores the main data associated with the measurement,
     * which can later be serialized or processed as needed.
     */
    private final HashMap<String, Object> fields;

    /**
     * Constructs a LogStatisticPacket with the specified JSON object.
     * This constructor initializes the packet with the measurement,
     * tags, and fields extracted from the given JSON object.
     *
     * @param object the JSON object containing the measurement, tags, and fields data for this packet
     *               - "measurement": a string specifying the name of the measurement
     *               - "tags": a JSON array of key-value pairs representing tags where
     *                 each element is a JSON object with keys "key" and "value"
     *               - "fields": a JSON array of key-value pairs where each
     *                 element is a JSON object with "key" as string and "value" as boolean, string, or number
     */
    public LogStatisticPacket(JsonObject object) {
        super(PacketID.Global.LOG_STATISTIC.getId(), object);
        this.measurement = object.get("measurement").getAsString();
        this.tags = new HashMap<>();
        JsonArray tags = object.getAsJsonArray("tags");
        for (JsonElement e : tags) {
            JsonObject o = (JsonObject) e;
            this.tags.put(o.get("key").getAsString(), o.get("value").getAsString());
        }
        this.fields = new HashMap<>();
        JsonArray fields = object.getAsJsonArray("fields");
        for (JsonElement e : fields) {
            JsonObject o = (JsonObject) e;
            JsonPrimitive primitive = o.get("value").getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                this.fields.put(o.get("key").getAsString(), o.get("value").getAsBoolean());
            } else if (primitive.isNumber()) {
                this.fields.put(o.get("key").getAsString(), o.get("value").getAsFloat());
            } else if (primitive.isString()) {
                this.fields.put(o.get("key").getAsString(), o.get("value").getAsString());
            }
        }
    }

    /**
     * Constructs a LogStatisticPacket with the specified measurement, tags, and fields.
     * This packet is used to log statistical data, encapsulating measurement information,
     * tags for categorization, and fields containing the data points.
     *
     * @param measurement the name of the measurement to be logged
     * @param tags a map of key-value pairs representing tag information, used for categorization
     * @param fields a map of key-value pairs representing field data points to be logged
     */
    public LogStatisticPacket(String measurement, HashMap<String, Object> tags, HashMap<String, Object> fields) {
        super(PacketID.Global.LOG_STATISTIC.getId(), null);
        this.measurement = measurement;
        this.tags = tags;
        this.fields = fields;
    }

    /**
     * Generates a JSON representation of this LogStatisticPacket.
     * <p>
     * The JSON object includes a "measurement" property, as well as "tags" and "fields"
     * arrays. Each element in the "tags" or "fields" arrays is a JSON object
     * with "key" and "value" properties, where "value" can be a string, number, or
     * boolean depending on the type of the corresponding data.
     * <p>
     * If an exception occurs during the creation of the JSON object, it will return null.
     *
     * @return a JsonObject representing the LogStatisticPacket or null if an exception occurs
     */
    @Override
    public JsonObject getJSON() {
        JsonObject object = getBaseJSON();
        try {
            object.addProperty("measurement", measurement);
            JsonArray tags = new JsonArray();
            for (Map.Entry<String, Object> entry : this.tags.entrySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("key", entry.getKey());
                Object value = entry.getValue();
                if (value instanceof Number) {
                    obj.addProperty("value", (Number) value);
                } else if (value instanceof Boolean) {
                    obj.addProperty("value", (Boolean) value);
                } else if (value instanceof String) {
                    obj.addProperty("value", (String) value);
                }
                tags.add(obj);
            }
            object.add("tags", tags);
            JsonArray fields = new JsonArray();
            for (Map.Entry<String, Object> entry : this.fields.entrySet()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("key", entry.getKey());
                Object value = entry.getValue();
                if (value instanceof Number) {
                    obj.addProperty("value", (Number) value);
                } else if (value instanceof Boolean) {
                    obj.addProperty("value", (Boolean) value);
                } else if (value instanceof String) {
                    obj.addProperty("value", (String) value);
                }
                fields.add(obj);
            }
            object.add("fields", fields);
        } catch (Exception e) {
            return null;
        }
        return object;
    }
}
