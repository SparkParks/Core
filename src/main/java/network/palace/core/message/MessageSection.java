package network.palace.core.message;

import org.bukkit.ChatColor;
import org.json.simple.JSONObject;

import java.util.Arrays;

/**
 * Represents a section of a message with customizable text, color, styles, and actions.
 * This class provides methods to generate text in a readable format as well as in a JSON structure
 * for further processing or integration with other systems.
 */
public class MessageSection {

    /**
     * The main text content of the message section. This is the core string that
     * will be displayed or processed. It can be further customized with colors,
     * styles, and interactive actions defined in the same class.
     * <p>
     * This variable is immutable and must be initialized through the constructor.
     */
    public final String text;

    /**
     * Defines the color of the text in this message section.
     * The color is represented by a {@link ChatColor} value and can be used
     * to customize the display appearance of the text.
     * <p>
     * By default, this value is set to {@code null}, which indicates that
     * no specific color is applied to the text. If a color is assigned, it
     * will be used when generating the textual or JSON representation of
     * this message section.
     */
    public ChatColor color = null;

    /**
     * Specifies the set of styles applied to the text in this message section.
     * Each style is represented by a {@link ChatColor} value, which defines
     * formatting options such as bold, italic, or underlined text.
     * <p>
     * When rendering the textual or JSON representation of the message section,
     * these styles are included to ensure the appearance matches the desired
     * formatting.
     * <p>
     * By default, this value is {@code null}, meaning no styling is applied.
     * If styles are defined, they are applied in addition to the text color,
     * if any, specified in the same class.
     */
    public ChatColor[] styles = null;

    /**
     * Represents the name of the action triggered when the element is clicked.
     * <p>
     * This variable is used to store the identifier or type of the click action
     * associated with the object. It is typically paired with additional data or
     * configurations to define the behavior or effect of the action when activated.
     * <p>
     * Default value is null, which indicates no click action is associated by default.
     */
    public String clickActionName = null;

    /**
     * Represents the data associated with a click action in a message section.
     * Typically used to define additional information or parameters needed
     * for the click action when a user interacts with a message element.
     */
    public String clickActionData = null;

    /**
     * Represents the name of the hover action associated with a message section.
     * This variable is used to define the type of action that will be triggered
     * when the user hovers over the corresponding message section.
     */
    public String hoverActionName = null;

    /**
     * Stores the data associated with the hover action in a message, typically used in a graphical user interface
     * or messaging system to display additional information when hovering over an element.
     * <p>
     * This variable is part of the MessageSection class and allows customization of the hover interaction by holding
     * relevant data that can be interpreted by the system to display appropriate content.
     */
    public String hoverActionData = null;


    /**
     * Constructs a MessageSection instance with the specified text.
     *
     * @param text the text content of this MessageSection
     */
    public MessageSection(final String text) {
        this.text = text;
    }

    /**
     * Constructs a friendly string representation of the MessageSection by combining
     * the color, styles, and text if they are present.
     *
     * @return a StringBuilder containing the combined friendly string representation
     *         of the MessageSection.
     */
    public StringBuilder getFriendlyString() {
        StringBuilder builder = new StringBuilder();
        if (color != null) {
            builder.append(color);
        }
        if (styles != null) {
            Arrays.asList(styles).forEach(builder::append);
        }
        builder.append(text);
        return builder;
    }

    /**
     * Constructs a JSON representation of the MessageSection object.
     * The method compiles the text content, color, styles, click events,
     * and hover events into a structured JSONObject.
     *
     * @return a JSONObject representing the current state of the MessageSection,
     * including text, color, styles, click events, and hover events if they are present.
     */
    public JSONObject getJsonObject() {
        JSONObject json = new JSONObject();
        json.put("text", text);
        if (color != null) {
            json.put("color", color.name().toLowerCase());
        }
        if (styles != null) {
            for (final ChatColor style : styles) {
                json.put(style == ChatColor.UNDERLINE ? "underlined" : style.name().toLowerCase(), true);
            }
        }
        if (clickActionName != null && clickActionData != null) {
            JSONObject clickEvent = new JSONObject();
            clickEvent.put("action", clickActionName);
            clickEvent.put("value", clickActionData);
            json.put("clickEvent", clickEvent);
        }
        if (hoverActionName != null && hoverActionData != null) {
            JSONObject hoverEvent = new JSONObject();
            hoverEvent.put("action", hoverActionName);
            hoverEvent.put("value", hoverActionData);
            json.put("hoverEvent", hoverEvent);
        }
        return json;
    }
}
