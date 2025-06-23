package network.palace.core.messagequeue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;
import net.md_5.bungee.api.ChatColor;
import network.palace.core.Core;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.core.messagequeue.packets.*;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * The MessageHandler class is responsible for managing message queues, connections, and
 * message publishing or consuming. It provides functionality to initialize message clients,
 * handle message delivery, register consumers, and send messages to various exchange types.
 * This class also handles connection reinitialization on shutdown and error handling for
 * message processing.
 */
public class MessageHandler {
    /**
     * A constant representing the default AMQP.BasicProperties configured for JSON messages.
     * This property is pre-configured with the content encoding set to "application/json"
     * and can be used for message publication where JSON content is expected.
     * <p>
     * JSON_PROPS is immutable and ensures a consistent message property configuration
     * for JSON-based message communication within the system.
     */
    public static final AMQP.BasicProperties JSON_PROPS = new AMQP.BasicProperties.Builder().contentEncoding("application/json").build();

    /**
     * Holds the connection instance used specifically for publishing messages to a
     * message-oriented middleware or broker (e.g., RabbitMQ).
     * <p>
     * This connection is responsible for facilitating the publishing of messages
     * to a specified exchange or queue, as part of the messaging architecture.
     * It is initialized and managed by the MessageHandler class and provides the
     * underlying communication channel needed for message publishing operations.
     * <p>
     * The PUBLISHING_CONNECTION variable should not be directly modified or accessed
     * outside the MessageHandler's defined methods, ensuring encapsulation and
     * streamlined message handling.
     */
    public Connection PUBLISHING_CONNECTION, CONSUMING_CONNECTION;

    /**
     * A pre-initialized static instance of the MessageClient that is used for sending
     * messages to all active proxies. This instance facilitates communication via
     * a specific messaging exchange or queue dedicated to proxy-related operations.
     * <p>
     * ALL_PROXIES is intended to broadcast messages to all connected proxies in a messaging
     * system. It provides a standardized way to interact with proxy clients through messaging,
     * ensuring consistent and reliable communication across the network.
     */
    public MessageClient ALL_PROXIES, ALL_MC, STATISTICS, PROXY_DIRECT, MC_DIRECT, BOT;

    /**
     * A collection of persistent {@link MessageClient} instances, which are
     * designed for long-term use and correspond to permanent connections to
     * message exchanges or queues.
     * <p>
     * The keys in the map are unique identifiers (typically strings) for the clients,
     * and the values are {@link MessageClient} objects that manage communication
     * channels for handling messaging operations.
     * <p>
     * This map ensures that critical messaging clients remain accessible and reusable
     * throughout the lifecycle of the application, avoiding the need to repeatedly
     * create new connections for the same targets.
     */
    public final HashMap<String, MessageClient> permanentClients = new HashMap<>();

    /**
     * Represents a factory for establishing and managing connections to a messaging broker.
     * This variable provides a mechanism to create connections that facilitate communication
     * between components in a message-oriented architecture. It is used throughout the
     * {@code MessageHandler} class to manage connections required for message publishing
     * and consumption.
     * <p>
     * The {@code factory} is a central component for creating and configuring connection
     * channels to the messaging broker, ensuring efficient and consistent interaction
     * within the messaging system. It typically abstracts the underlying connection details,
     * providing a streamlined interface for establishing channels or connections.
     * <p>
     * This field is immutable and ensures thread-safe access across different operations
     * performed within the {@code MessageHandler}.
     */
    private final ConnectionFactory factory;

    /**
     * A mapping of all active communication channels managed by the MessageHandler,
     * where the key represents a unique channel name (as a String) and the value is
     * the corresponding Channel object.
     * <p>
     * This map is used to store and retrieve communication channels for various
     * message exchanges and operations within the messaging system. It facilitates
     * the efficient organization and lookup of Channel instances for handling
     * message publishing, consuming, or other communication tasks.
     * <p>
     * The use of a final HashMap ensures that the reference to the map cannot be
     * reassigned, although the contents of the map (entries it holds) can be modified.
     */
    private final HashMap<String, Channel> channels = new HashMap<>();

    /**
     * Constructs a new instance of the MessageHandler class and initializes connections
     * to a RabbitMQ message broker for both publishing and consuming purposes.
     * <p>
     * This constructor fetches RabbitMQ configuration values from a
     * predefined configuration file or section, including properties such as
     * virtual host, host, username, and password. It sets up a
     * {@link ConnectionFactory} object and establishes
     * two separate connections: one for publishing messages and another for
     * consuming messages.
     * <p>
     * Additionally, it registers shutdown listeners for both connections.
     * If either connection is closed unexpectedly, the shutdown listeners
     * attempt to reinitialize the connections. If the reinitialization fails,
     * an appropriate error message is logged.
     *
     * @throws IOException      if there is an error establishing a connection.
     * @throws TimeoutException if the connection attempt times out.
     */
    public MessageHandler() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        ConfigurationSection section = Core.getCoreConfig().getConfigurationSection("rabbitmq");
        factory.setVirtualHost(section.getString("virtualhost"));
        factory.setHost(section.getString("host"));
        factory.setUsername(section.getString("username"));
        factory.setPassword(section.getString("password"));

        PUBLISHING_CONNECTION = factory.newConnection();
        CONSUMING_CONNECTION = factory.newConnection();

        PUBLISHING_CONNECTION.addShutdownListener(e -> {
            Core.getInstance().getLogger().warning("Publishing connection has been closed - reinitializing!");
            try {
                PUBLISHING_CONNECTION = factory.newConnection();
            } catch (IOException | TimeoutException ioException) {
                Core.getInstance().getLogger().severe("Failed to reinitialize publishing connection!");
                ioException.printStackTrace();
            }
        });
        CONSUMING_CONNECTION.addShutdownListener(e -> {
            Core.getInstance().getLogger().warning("Consuming connection has been closed - reinitializing!");
            try {
                CONSUMING_CONNECTION = factory.newConnection();
            } catch (IOException | TimeoutException ioException) {
                Core.getInstance().getLogger().severe("Failed to reinitialize consuming connection!");
                ioException.printStackTrace();
            }
        });
    }

    /**
     * Initializes the message publishing queues and registers message consumers
     * for handling specific types of messages.
     * <p>
     * This method sets up several message publishing queues, such as `all_proxies`,
     * `all_mc`, `statistics`, `proxy_direct`, `mc_direct`, and `bot-networking`.
     * These queues are used for message routing between systems.
     * <p>
     * Additionally, it registers consumers to process messages received on
     * specific exchanges with defined routing keys. For the "all_mc" fanout exchange,
     * a consumer is registered to process incoming messages by parsing the delivery
     * and triggering an IncomingMessageEvent. Another consumer is registered for the
     * "mc_direct" exchange with a routing key defined as the instance name to handle
     * direct messaging and specific logic, such as playing sound on a mention packet.
     * <p>
     * If an error occurs during initialization or consumer registration, an error message
     * is logged, and the stack trace is printed for debugging purposes.
     *
     * @throws IOException if an error occurs creating the message publishing queues
     *                     or registering the consumers.
     * @throws TimeoutException if a timeout occurs during queue initialization or
     *                          consumer registration.
     */
    public void initialize() throws IOException, TimeoutException {
        try {
            // Initialize message clients for various queues and exchanges.

            // For all proxy-related messages
            ALL_PROXIES = new MessageClient(ConnectionType.PUBLISHING, "all_proxies", "fanout");
            // For messages broadcasted to all MC instances
            ALL_MC = new MessageClient(ConnectionType.PUBLISHING, "all_mc", "fanout");
            // For statistics-related messaging (uses default exchange type)
            STATISTICS = new MessageClient(ConnectionType.PUBLISHING, "statistics", true);
            // Direct messaging to proxy instances
            PROXY_DIRECT = new MessageClient(ConnectionType.PUBLISHING, "proxy_direct", "direct");
            // Direct messaging to specific MC instances
            MC_DIRECT = new MessageClient(ConnectionType.PUBLISHING, "mc_direct", "direct");
            // For bot networking messages
            BOT = new MessageClient(ConnectionType.PUBLISHING, "bot-networking", true);
        } catch (Exception e) {
            // Log and print the error if initialization fails.
            e.printStackTrace();
            Core.getInstance().getLogger().severe("There was an error initializing essential message publishing queues!");
        }

        // Define a do-nothing cancel callback for consumer registration
        CancelCallback doNothing = consumerTag -> {
        };

        // Register a consumer for the "all_mc" fanout exchange with no routing key.
        registerConsumer("all_mc", "fanout", "", (consumerTag, delivery) -> {
            try {
                // Parse the incoming message into a JsonObject
                JsonObject object = parseDelivery(delivery);
                Core.debugLog(object.toString()); // Log the object for debugging
                int id = object.get("id").getAsInt(); // Extract the message ID
                try {
                    // Trigger an IncomingMessageEvent for the parsed message
                    new IncomingMessageEvent(id, object).call();
                } catch (Exception e) {
                    // Log any errors during event processing
                    Core.logMessage("MessageHandler", "Error processing IncomingMessageEvent for incoming packet " + object.toString());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                // Handle general errors during delivery parsing
                handleError(consumerTag, delivery, e);
            }
        }, doNothing);

        // Register a consumer for the "mc_direct" exchange with instance-specific routing key.
        registerConsumer("mc_direct", "direct", Core.getInstanceName(), (consumerTag, delivery) -> {
            try {
                // Parse the incoming message
                JsonObject object = parseDelivery(delivery);
                Core.debugLog(object.toString());
                int id = object.get("id").getAsInt();
                try {
                    // Trigger IncomingMessageEvent for this direct message
                    new IncomingMessageEvent(id, object).call();
                } catch (Exception e) {
                    // Log error during event call
                    Core.logMessage("MessageHandler", "Error processing IncomingMessageEvent for incoming packet " + object.toString());
                    e.printStackTrace();
                }
                // Handle specific message IDs, e.g., ID 10 is for a mention packet
                switch (id) {
                    // Mention
                    case 10: {
                        MentionPacket packet = new MentionPacket(object);
                        CPlayer player = Core.getPlayerManager().getPlayer(packet.getUuid());
                        if (player != null)
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50F, 1F);
                    }
                }
            } catch (Exception e) {
                // Handle any error in message processing
                handleError(consumerTag, delivery, e);
            }
        }, doNothing);
    }

    /**
     * Handles errors that occur during the processing of a RabbitMQ message.
     * Logs detailed information about the error, including the consumer tag,
     * delivery envelope, and message body, for debugging purposes.
     * The stack trace of the exception is also printed.
     *
     * @param consumerTag the identifier of the consumer associated with the error
     * @param delivery the delivery object containing message properties such as envelope and body
     * @param e the exception that was caught and caused the error
     */
    public void handleError(String consumerTag, Delivery delivery, Exception e) {
        Core.getInstance().getLogger().severe("[MessageHandler] Error processing message: " + e.getClass().getName() + " - " + e.getMessage());
        Core.getInstance().getLogger().severe("consumerTag: " + consumerTag);
        Core.getInstance().getLogger().severe("envelope: " + delivery.getEnvelope().toString());
        Core.getInstance().getLogger().severe("body (bytes): " + Arrays.toString(delivery.getBody()));
        Core.getInstance().getLogger().severe("body (string): " + new String(delivery.getBody(), StandardCharsets.UTF_8));
        e.printStackTrace();
    }

    public JsonObject parseDelivery(Delivery delivery) {
        byte[] bytes = delivery.getBody();
        String s = new String(bytes, StandardCharsets.UTF_8);
        JsonObject object = (JsonObject) new JsonParser().parse(s);
        if (!object.has("id")) throw new IllegalArgumentException("Missing 'id' field from message packet");
        return object;
    }

    /**
     * Register a MessageQueue consumer
     *
     * @param exchange        the exchange name
     * @param exchangeType    the exchange type (i.e. fanout)
     * @param deliverCallback what to do when a message is received
     * @param cancelCallback  what to do when the consumer is closed
     * @return the queue name created (used to cancel the consumer)
     * @throws IOException      on IOException
     * @throws TimeoutException on TimeoutException
     */
    public String registerConsumer(String exchange, String exchangeType, String routingKey, DeliverCallback deliverCallback, CancelCallback cancelCallback) throws IOException, TimeoutException {
        Channel channel = CONSUMING_CONNECTION.createChannel();
        channel.exchangeDeclare(exchange, exchangeType);

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, routingKey);

        channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

        channels.put(queueName, channel);

        return queueName;
    }

    /**
     * Unregisters a consumer from the specified queue.
     * <br>
     * This method cancels the active consumer associated with the provided queue name,
     * and removes the corresponding channel from the internal channel map.
     *
     * @param queueName the name of the queue for which the consumer should be unregistered
     * @throws IOException if an error occurs while canceling the consumer
     */
    public void unregisterConsumer(String queueName) throws IOException {
        Channel channel = channels.remove(queueName);
        if (channel != null) {
            channel.basicCancel(queueName);
        }
    }

    /**
     * Shuts down the {@code MessageHandler}, closing all active resources such as connections,
     * channels, and statistics to ensure a clean termination. This method performs the
     * following actions:
     * <p>
     * - Closes the {@code ALL_PROXIES}, {@code ALL_MC}, {@code STATISTICS}, {@code PROXY_DIRECT},
     *   and {@code MC_DIRECT} connections, if they are not null.
     * - Iterates through all channels in the {@code channels} map and closes the underlying
     *   connections that are still open.
     * - Clears the {@code channels} map after closing all connections.
     * <p>
     * If any exceptions occur during the closing of these resources, they are caught and
     * their stack traces are printed to allow for debugging.
     * <p>
     * This method ensures that system resources are properly released in cases such as
     * shutdown or application exit.
     */
    public void shutdown() {
        if (ALL_PROXIES != null) {
            try {
                ALL_PROXIES.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ALL_MC != null) {
            try {
                ALL_MC.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (STATISTICS != null) {
            try {
                STATISTICS.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (PROXY_DIRECT != null) {
            try {
                PROXY_DIRECT.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (MC_DIRECT != null) {
            try {
                MC_DIRECT.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        channels.forEach((queueName, channel) -> {
            try {
                Connection connection = channel.getConnection();
                if (connection.isOpen()) connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        channels.clear();
    }

    /**
     * Sends a message using the specified packet and client.
     *
     * @param packet the {@link MQPacket} containing the message to be sent
     * @param client the {@link MessageClient} used to send the message
     * @throws IOException if an input/output error occurs during the message sending process
     */
    public void sendMessage(MQPacket packet, MessageClient client) throws IOException {
        sendMessage(packet, client, "");
    }

    /**
     * Sends a message using the specified packet, client, and routing key.
     *
     * @param packet     the {@link MQPacket} containing the message to be sent
     * @param client     the {@link MessageClient} used to send the message
     * @param routingKey the routing key used to route the message
     * @throws IOException if an input/output error occurs during the message sending process
     */
    public void sendMessage(MQPacket packet, MessageClient client, String routingKey) throws IOException {
        client.basicPublish(packet.toBytes(), routingKey);
    }

    /**
     * Sends a message to a specified exchange using the provided packet, exchange type,
     * and routing key.
     * <p>
     * This method creates a {@link MessageClient} configured for publishing, initializes
     * it with the specified exchange and exchange type, and publishes the given message
     * (converted from the {@link MQPacket} into a byte array) using the given routing key.
     * After publishing the message, the client connection is closed.
     *
     * @param packet       the {@link MQPacket} containing the message to be sent
     * @param exchange     the name of the exchange to which the message will be published
     * @param exchangeType the type of the exchange (e.g., "fanout", "direct", "topic")
     * @param routingKey   the routing key to route the message in the exchange
     * @throws Exception if an error occurs during the message sending process (e.g.,
     *                   connection issues or publishing errors)
     */
    public void sendMessage(MQPacket packet, String exchange, String exchangeType, String routingKey) throws Exception {
        MessageClient client = new MessageClient(ConnectionType.PUBLISHING, exchange, exchangeType);
        client.basicPublish(packet.toBytes(), routingKey);
        client.close();
    }

    /**
     * Sends a message to staff members with a specific rank or higher.
     * <p>
     * This method creates a {@link MessageByRankPacket} that formats the provided
     * message with a staff prefix and targets staff members with a rank of at least
     * {@link Rank#TRAINEE}. The message is then sent to all proxies in the network.
     *
     * @param message the message to be sent to staff members
     * @throws Exception if an error occurs while creating or sending the message packet
     */
    public void sendStaffMessage(String message) throws Exception {
        MessageByRankPacket packet = new MessageByRankPacket("[" + ChatColor.RED + "STAFF" +
                ChatColor.WHITE + "] " + message, Rank.TRAINEE, null, false, false);
        sendMessage(packet, ALL_PROXIES);
    }

    /**
     * Sends a message to a specific player identified by their UUID.
     * If the player is currently online, the message is delivered directly.
     * Otherwise, the message is wrapped in a packet and sent to all proxies.
     *
     * @param uuid    the unique identifier of the player to send the message to
     * @param message the message to be sent to the player
     * @throws Exception if an error occurs while sending the message or creating the packet
     */
    public void sendMessageToPlayer(UUID uuid, String message) throws Exception {
        sendMessageToPlayer(uuid, message, false);
    }

    /**
     * Sends a message to a specific player identified by their UUID. If the player
     * is currently online, the message is delivered directly using their player instance.
     * Otherwise, the message is encapsulated into a packet and broadcasted to all proxies
     * for delivery.
     *
     * @param uuid      the unique identifier of the player to whom the message should be sent
     * @param message   the message to be sent to the player
     * @param component if true, sends the message using a ComponentMessagePacket; otherwise,
     *                  uses a MessagePacket
     * @throws Exception if an error occurs while sending the message, creating the packet,
     *                   or during network operations
     */
    public void sendMessageToPlayer(UUID uuid, String message, boolean component) throws Exception {
        CPlayer player = Core.getPlayerManager().getPlayer(uuid);
        if (player != null) {
            player.sendMessage(message);
            return;
        }
        MQPacket packet;
        if (component) {
            packet = new ComponentMessagePacket(message, uuid);
        } else {
            packet = new MessagePacket(message, uuid);
        }
        sendMessage(packet, ALL_PROXIES);
    }

    /**
     * Sends a message directly to a specific proxy identified by its unique UUID.
     * This method uses the provided {@link MQPacket} to encapsulate the message
     * and routes it to the specified proxy using a direct exchange.
     *
     * @param packet      the {@link MQPacket} containing the message to be sent
     * @param targetProxy the {@link UUID} of the target proxy to which the message will be sent
     * @throws Exception if an error occurs while sending the message or during network operations
     */
    public void sendToProxy(MQPacket packet, UUID targetProxy) throws Exception {
        sendMessage(packet, PROXY_DIRECT, targetProxy.toString());
    }

    /**
     * Retrieves a connection based on the specified connection type.
     *
     * @param type the type of connection to retrieve; it can be PUBLISHING or CONSUMING
     * @return the connection that corresponds to the specified type; a new connection is returned for unsupported types
     * @throws IOException if an I/O error occurs during the creation of the connection
     * @throws TimeoutException if a timeout occurs while attempting to create the connection
     */
    public Connection getConnection(ConnectionType type) throws IOException, TimeoutException {
        switch (type) {
            case PUBLISHING:
                return PUBLISHING_CONNECTION;
            case CONSUMING:
                return CONSUMING_CONNECTION;
            default:
                return factory.newConnection();
        }
    }

    /**
     * Sends a direct message to a specified server using the provided MQPacket.
     *
     * @param packet the message packet to be sent
     * @param server the server identifier to which the message is sent
     * @throws IOException if an I/O error occurs while sending the message
     */
    public void sendDirectServerMessage(MQPacket packet, String server) throws IOException {
        sendMessage(packet, MC_DIRECT, server);
    }
}
