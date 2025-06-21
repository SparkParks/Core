package network.palace.core.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * The LockArmorStandCommand class provides functionality to toggle the lock state
 * of an Armor Stand within a specific range around the player. This lock state
 * determines whether players can edit the items on the Armor Stand.
 * <p>
 * This command identifies the closest Armor Stand to the player, within a
 * predefined distance, and toggles its editable lock state. The locking
 * mechanism is dependent on the server's Minecraft version, making use of
 * reflection to access and modify internal NMS fields.
 * <p>
 * The command is registered under the name "lockarmorstand" and can only be
 * executed by players with sufficient rank as specified in the command metadata.
 * <p>
 * Features:
 * - Finds the nearest Armor Stand to the player within two blocks.
 * - Toggles the locked/unlocked state of the Armor Stand.
 * - Sends feedback messages to the player regarding the Armor Stand's new state.
 * - Handles version-specific differences in NMS field names dynamically.
 * <p>
 * Exceptions:
 * - Catches and logs exceptions related to reflection, such as NoSuchFieldException
 *   and ClassNotFoundException, ensuring the stability of the command execution.
 * <p>
 * This command is typically used in custom gameplay mechanics for managing
 * or protecting Armor Stands in player-built environments.
 */
@CommandMeta(description = "Toggle whether players can edit the items on an armor stand", rank = Rank.CM)
public class LockArmorStandCommand extends CoreCommand {

    /**
     * Constructs an instance of the LockArmorStandCommand class.
     * <p>
     * This command is designed to manage the locking of armor stands in the application.
     * By setting the command name to "lockarmorstand", it allows for specific functionality
     * related to locking armor stands to be executed within the command framework.
     * <p>
     * This command inherits from the CoreCommand class, enabling seamless integration
     * with the command handling system and leveraging the base functionality provided.
     */
    public LockArmorStandCommand() {
        super("lockarmorstand");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Collection<ArmorStand> stands = player.getWorld().getEntitiesByClass(ArmorStand.class);
        Location loc = player.getLocation();

        ArmorStand closest = null;
        double distance = -1;
        for (ArmorStand stand : stands) {
            if (distance == -1 && stand.getLocation().distance(loc) <= 2) {
                closest = stand;
                distance = stand.getLocation().distance(loc);
            } else if (stand.getLocation().distance(loc) < distance) {
                closest = stand;
                distance = stand.getLocation().distance(loc);
            }
        }

        if (closest == null) {
            player.sendMessage(ChatColor.RED + "There are no ArmorStands within two blocks!");
            return;
        }

        try {
            String lockField;
            switch (Core.getMinecraftVersion()) {
                case "v1_13_R1":
                case "v1_13_R2":
                    lockField = "bH";
                    break;
                case "v1_12_R1":
                    lockField = "bB";
                    break;
                default:
                    lockField = "bA";
                    break;
            }
            Field f = Class.forName("net.minecraft.server." + Core.getMinecraftVersion() + ".EntityArmorStand")
                    .getDeclaredField(lockField);
            if (f != null) {
                f.setAccessible(true);
                Object craftStand = Class.forName("org.bukkit.craftbukkit." + Core.getMinecraftVersion() +
                        ".entity.CraftArmorStand").cast(closest);
                Object handle = craftStand.getClass().getDeclaredMethod("getHandle").invoke(craftStand);
                int i = (int) f.get(handle);
                if (i == 2096896) {
                    player.sendMessage(ChatColor.YELLOW + (closest.getCustomName() == null ? "The Armor Stand" : closest.getCustomName()) + ChatColor.GREEN + " has been unlocked!");
                    f.set(handle, 0);
                } else {
                    player.sendMessage(ChatColor.YELLOW + (closest.getCustomName() == null ? "The Armor Stand" : closest.getCustomName()) + ChatColor.GREEN + " has been locked!");
                    f.set(handle, 2096896);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
