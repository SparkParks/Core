package network.palace.core.message;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a formatted message that supports various text and interaction
 * styles such as colors, hover actions, and click actions. The message can
 * be built piece by piece and sent to players in Minecraft.
 * <p>
 * This class provides a fluent API for creating complex formatted messages
 * by chaining multiple methods.
 */
public class FormattedMessage {

    /**
     * A list that stores individual sections of a formatted message.
     * Each section, represented by a {@link MessageSection}, can have its own text, color,
     * style, click actions, and hover actions.
     * <p>
     * This variable underpins the functionality of the {@code FormattedMessage} class, allowing
     * for dynamic customization and rendering of complex messages composed of multiple sections
     * with distinct attributes.
     */
    private final List<MessageSection> messageSections;

    /**
     * Constructs a new FormattedMessage instance with the specified initial text.
     *
     * @param firstPartText the initial text that forms the first part of the message
     */
    public FormattedMessage(final String firstPartText) {
        messageSections = new ArrayList<>();
        messageSections.add(new MessageSection(firstPartText));
    }

    /**
     * Sets the color for the latest section of the message.
     *
     * @param color the {@link ChatColor} to apply to the latest message section
     * @return the updated {@link FormattedMessage} instance
     * @throws IllegalArgumentException if the provided {@link ChatColor} is not a valid color
     */
    public FormattedMessage color(final ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(color.name() + " is not a color");
        }
        latest().color = color;
        return this;
    }

    /**
     * Applies one or more formatting styles to the latest section of the message.
     * <p>
     * Each specified style must be a formatting code provided by {@link ChatColor},
     * and it must represent a text style (such as bold or italic). If any of the
     * provided styles do not represent valid formats, an {@link IllegalArgumentException}
     * will be thrown.
     *
     * @param styles the array of {@link ChatColor} styles to apply to the latest message section
     * @return the updated {@link FormattedMessage} instance
     * @throws IllegalArgumentException if any {@link ChatColor} in the provided array is not a valid format style
     */
    public FormattedMessage style(final ChatColor... styles) {
        for (final ChatColor style : styles) {
            if (!style.isFormat()) {
                throw new IllegalArgumentException(style.name() + " is not a style");
            }
        }
        latest().styles = styles;
        return this;
    }

    /**
     * Adds a file interaction behavior to the latest section of the message.
     * When the user clicks on the message, it will trigger an action to open the specified file.
     *
     * @param path the file path to associate with the message section's click action
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage file(final String path) {
        onClick("open_file", path);
        return this;
    }

    /**
     * Adds a clickable link to the latest section of the message.
     * When clicked, the provided URL will be opened.
     *
     * @param url the URL to associate with the message section's click action
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage link(final String url) {
        onClick("open_url", url);
        return this;
    }

    /**
     * Adds a "suggest_command" click action to the latest section of the message.
     * When the user clicks on the message, it populates their chat input with the provided command.
     *
     * @param command the command to be suggested when the section is clicked
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage suggest(final String command) {
        onClick("suggest_command", command);
        return this;
    }

    /**
     * Adds a "run_command" click action to the latest section of the message.
     * When the user clicks on the message, the specified command will be executed.
     *
     * @param command the command to be executed when the section is clicked
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage command(final String command) {
        onClick("run_command", command);
        return this;
    }

    /**
     * Adds a hover tooltip that displays an achievement description to the latest section of the message.
     * The tooltip is linked to the achievement specified by the given name.
     *
     * @param name the name of the achievement for which the tooltip will be displayed
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage achievementTooltip(final String name) {
        onHover("show_achievement", "achievement." + name);
        return this;
    }

    /**
     * Adds a hover tooltip action to the latest section of the message, displaying
     * item details based on the provided JSON string. The JSON string must represent
     * the item in a format compatible with the tooltip system.
     *
     * @param itemJSON a JSON string that represents the item, containing its properties
     *                 and details to be shown in the tooltip
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage itemTooltip(final String itemJSON) {
        onHover("show_item", itemJSON);
        return this;
    }

    /**
     * Adds a multiline hover tooltip to the latest section of the message.
     * The tooltip displays the specified name and additional lines of text.
     *
     * @param name  the main title or name to display in the tooltip
     * @param lines additional lines of text to display in the tooltip
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage multilineTooltip(final String name, final String... lines) {
        ItemStack stack = ItemUtil.create(Material.BOOK, 1, name, Arrays.asList(lines));
        return itemTooltip(stack);
    }

    /**
     * Adds a hover tooltip to the latest section of the message.
     * The tooltip displays the specified lines of text, with each line on a new row.
     *
     * @param lines the list of strings to be displayed as lines in the tooltip
     * @return the updated {@code FormattedMessage} instance
     */
    public FormattedMessage tooltip(final List<String> lines) {
        return tooltip((String[]) lines.toArray());
    }

    /**
     * Adds a hover tooltip to the latest section of the message.
     * The tooltip displays the specified lines of text, with each line on a new row.
     *
     * @param lines the array of strings to be displayed as lines in the tooltip
     * @return the updated {@code FormattedMessage} instance
     */
    public FormattedMessage tooltip(final String... lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            builder.append(lines[i]);
            if (i < lines.length - 1) {
                builder.append('\n');
            }
        }
        tooltip(builder.toString());
        return this;
    }

    /**
     * Adds a hover tooltip action to the latest section of the message, displaying
     * item details based on the provided {@link ItemStack}. The item information
     * is processed and converted into a format compatible with the tooltip system.
     *
     * @param itemStack the {@link ItemStack} instance containing the details
     *                  of the item to be displayed in the tooltip
     * @return the updated {@link FormattedMessage} instance
     */
    public FormattedMessage itemTooltip(final ItemStack itemStack) {
        try {
            return itemTooltip(ItemUtil.getFriendlyNBT(itemStack));
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    /**
     * Adds a hover tooltip to the latest section of the message.
     * The tooltip displays the specified line of text as hover text.
     *
     * @param line the single string to be displayed as the tooltip text
     * @return the updated {@code FormattedMessage} instance
     */
    public FormattedMessage tooltip(final String line) {
        onHover("show_text", line);
        return this;
    }

    /**
     * Adds a new section with the specified message text to the formatted message.
     *
     * @param message the text to add as the next section of the formatted message
     * @return the updated {@code FormattedMessage} instance
     */
    public FormattedMessage then(final String message) {
        messageSections.add(new MessageSection(message));
        return this;
    }

    /**
     * Converts the formatted message into a user-readable string by combining
     * the friendly string representation of each message section.
     * If the message consists of only one section, the friendly string of
     * the latest section is returned directly.
     *
     * @return a user-friendly string representation of the formatted message
     */
    public String toFriendlyString() {
        StringBuilder builder = new StringBuilder();
        if (messageSections.size() != 1) {
            for (final MessageSection part : messageSections) {
                builder.append(part.getFriendlyString());
            }
        } else {
            builder.append(latest().getFriendlyString());
        }
        return builder.toString();
    }

    /**
     * Converts the current formatted message into its JSON representation.
     * The JSON structure includes both basic text and any additional metadata,
     * such as style, color, and actions for click or hover events.
     * If the message contains only one section, it directly converts
     * the latest message section into a JSON object. For multiple sections,
     * it generates a JSON object with a base text and an array of additional
     * elements.
     *
     * @return the JSON string representation of the formatted message
     */
    @SuppressWarnings("unchecked")
    public String toJSONString() {
        JSONObject json = new JSONObject();
        if (messageSections.size() != 1) {
            json.put("text", "");
            if (!json.containsKey("extra")) {
                json.put("extra", new JSONArray());
            }
            JSONArray extra = (JSONArray) json.get("extra");
            for (final MessageSection part : messageSections) {
                extra.add(part.getJsonObject());
            }
        } else {
            json = latest().getJsonObject();
        }
        return json.toString();
    }

    /**
     * Sends a fully formatted chat message to the specified player.
     * The message is constructed using the current state of the
     * {@code FormattedMessage} and is sent as a chat packet.
     *
     * @param player the player to whom the chat message will be sent
     */
    public void send(Player player) {
        PacketContainer chatMessage = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
        String json = toJSONString();
        chatMessage.getChatComponents().write(0, WrappedChatComponent.fromJson(json));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, chatMessage);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a fully formatted chat message to the specified player.
     * The message is constructed using the current state of the {@code FormattedMessage}
     * and is sent as a chat packet.
     *
     * @param player the {@code CPlayer} object representing the player to whom the chat message will be sent
     */
    public void send(CPlayer player) {
        send(player.getBukkitPlayer());
    }

    /**
     * Retrieves the latest message section from the list of message sections.
     *
     * @return the most recent {@code MessageSection} in the {@code messageSections} list
     */
    private MessageSection latest() {
        return messageSections.get(messageSections.size() - 1);
    }

    /**
     * Sets a click action for the latest message section using the specified name and data.
     * This method modifies the latest {@code MessageSection} to include the given
     * click action name and data, enabling click interaction functionality.
     *
     * @param name the name of the click action to associate with the latest message section
     * @param data the data to associate with the click action name
     */
    private void onClick(final String name, final String data) {
        final MessageSection latest = latest();
        latest.clickActionName = name;
        latest.clickActionData = data;
    }

    /**
     * Sets a hover action for the latest message section using the specified name and data.
     * This method modifies the latest {@code MessageSection} to include the given
     * hover action name and data, enabling hover interaction functionality.
     *
     * @param name the name of the hover action to associate with the latest message section
     * @param data the data to associate with the hover action name
     */
    private void onHover(final String name, final String data) {
        final MessageSection latest = latest();
        latest.hoverActionName = name;
        latest.hoverActionData = data;
    }
}
