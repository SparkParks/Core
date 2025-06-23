package network.palace.core.messagequeue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import lombok.Getter;
import network.palace.core.Core;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * MessageClient is a utility class designed to facilitate communication
 * with a messaging broker. It encapsulates the creation and management of
 * message exchanges or queues, and allows for the publishing of messages.
 * <p>
 * Instances of MessageClient are initialized with a specific connection type
 * along with the configuration details for either an exchange or a queue.
 * The client handles the declaration of exchanges or queues as required.
 */
@Getter
public class MessageClient {
    /**
     * Represents the communication channel used by the MessageClient to interact
     * with a message broker. This channel is responsible for facilitating
     * message publishing and other direct interactions with the broker, such as
     * declaring exchanges or queues.
     * <p>
     * The channel is initialized during the creation of a MessageClient instance
     * based on the specified ConnectionType and configuration. It is a core component
     * for performing AMQP operations, including message publishing and resource
     * declaration.
     * <p>
     * This variable is immutable and ensures a consistent connection channel
     * throughout the lifespan of the MessageClient instance.
     */
    private final Channel channel;

    /**
     * Represents the name of the exchange or queue used by the MessageClient to
     * facilitate communication with a message broker. This variable is initialized
     * based on the type of resource (exchange or queue) specified during the creation
     * of the MessageClient instance.
     * <p>
     * In cases where the MessageClient is configured for publishing messages to an
     * exchange, this variable holds the name of the exchange being declared and used.
     * Conversely, if the MessageClient is configured to interact with a queue, it holds
     * the name of the queue being declared and utilized.
     * <p>
     * This variable is immutable and remains constant throughout the lifecycle of the
     * MessageClient instance, ensuring consistent identification of the target
     * exchange or queue.
     */
    private final String name;

    /**
     * Indicates whether the client is operating with a queue-based mechanism.
     * This flag determines the mode of communication, specifying if the
     * current interaction is tied to a queue.
     * <p>
     * Typically, this is used to differentiate between working with a queue
     * versus publishing to an exchange.
     */
    private final boolean queue;

    /**
     * Initializes a MessageClient to connect to a message exchange with a specified exchange type.
     * Sets up the necessary channel and declares the exchange.
     *
     * @param type the type of connection indicating how the client interacts with the messaging system (e.g., PUBLISHING, CONSUMING, OTHER)
     * @param exchange the name of the exchange to connect to
     * @param exchangeType the type of the exchange to declare (e.g., direct, topic, fanout)
     * @throws Exception if the connection setup or exchange declaration fails
     */
    public MessageClient(ConnectionType type, String exchange, String exchangeType) throws Exception {
        queue = false;
        this.channel = Core.getMessageHandler().getConnection(type).createChannel();
        this.name = exchange;
        channel.exchangeDeclare(exchange, exchangeType);
    }

    /**
     * Initializes a MessageClient to connect to a specific queue with the given parameters.
     * Sets up the necessary channel and declares the queue.
     *
     * @param type the type of connection indicating how the client interacts with the messaging system (e.g., PUBLISHING, CONSUMING, OTHER)
     * @param queueName the name of the queue to connect to
     * @param durable specifies whether the queue should be durable (survive a broker restart)
     * @throws Exception if the connection setup or queue declaration fails
     */
    public MessageClient(ConnectionType type, String queueName, boolean durable) throws Exception {
        queue = true;
        this.channel = Core.getMessageHandler().getConnection(type).createChannel();
        this.name = queueName;
        // queueName, durable, exclusive, autoDelete, args
        channel.queueDeclare(queueName, durable, false, false, null);
    }

    /**
     * Publishes a message to a default routing key with default message properties.
     *
     * @param bytes the byte array representing the message content to be published
     * @throws IOException if there is an error during the publishing process
     */
    public void basicPublish(byte[] bytes) throws IOException {
        basicPublish(bytes, "");
    }

    /**
     * Publishes a message to a specified routing key using default message properties.
     * It determines the appropriate publishing configuration based on whether the
     * client is connected to a queue or an exchange.
     *
     * @param bytes the byte array representing the message content to be published
     * @param routingKey the routing key to which the message should be published
     * @throws IOException if there is an error during the publishing process
     */
    public void basicPublish(byte[] bytes, String routingKey) throws IOException {
        basicPublish(bytes, routingKey, MessageHandler.JSON_PROPS);
    }

    /**
     * Publishes a message to a messaging system with the specified routing key and additional message properties.
     * The method determines whether to publish to an exchange or a queue based on the configuration of the MessageClient.
     *
     * @param bytes the byte array representing the content of the message to be published
     * @param routingKey the routing key used to route the message to the appropriate queue or exchange
     * @param props the AMQP.BasicProperties object containing additional message properties such as headers, content type, etc.
     * @throws IOException if an error occurs while publishing the message
     */
    public void basicPublish(byte[] bytes, String routingKey, AMQP.BasicProperties props) throws IOException {
        if (queue) {
            channel.basicPublish(routingKey, name, props, bytes);
        } else {
            channel.basicPublish(name, routingKey, props, bytes);
        }
    }

    /**
     * Closes the currently active channel associated with the MessageClient.
     * This method should be called to release resources and properly terminate
     * the connection to the messaging system.
     *
     * @throws IOException if an I/O error occurs while closing the channel
     * @throws TimeoutException if the operation times out while closing the channel
     */
    public void close() throws IOException, TimeoutException {
        channel.close();
    }
}
