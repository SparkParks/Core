package network.palace.core.commands.disabled;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code PrefixCommandListener} class implements the {@link Listener} interface and provides
 * functionalities to handle command preprocessing and tab completion events in a Minecraft server.
 * <p>
 * This class restricts players below a certain rank from using specific commands based on their
 * prefixes (e.g., "/minecraft:", "/bukkit:") or custom blocked completions. It processes player
 * commands and filter results provided in tab completions.
 * <p>
 * Events handled by this listener include:
 * 1. {@link PlayerCommandPreprocessEvent}: This event is used to intercept and conditionally cancel
 *    commands based on the player's rank and specified command prefixes.
 * 2. {@link TabCompleteEvent}: This event is used to filter and control the command suggestions
 *    displayed to players, blocking access to commands with blocked prefixes or plugins.
 * <p>
 * Commands and completions starting with specific prefixes or meant for blocked plugins are
 * disabled for players with a rank lower than a predefined level.
 */
public class PrefixCommandListener implements Listener {
    /**
     * A static list of string prefixes representing command completions that are blocked.
     * <p>
     * The `blockedCompletions` variable contains a set of strings, each representing the
     * prefixes of commands that are restricted or disabled from being used or suggested
     * within the application. This is typically used to prevent players from executing certain
     * commands or accessing specific functionality by filtering command inputs or suggestions.
     * <p>
     * Example prefixes include commonly restricted namespaces or tools like "/minecraft:" and "//".
     */
    private static List<String> blockedCompletions = new ArrayList<>(Arrays.asList("/minecraft:", "/bukkit:", "/worldedit",
            "/ncp", "/nocheatplus", "//"));

    /**
     * Handles the {@link PlayerCommandPreprocessEvent} to intercept and restrict the use of certain commands
     * based on the player's rank.
     *
     * @param event the {@link PlayerCommandPreprocessEvent} triggered when a player attempts to execute a command
     */
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/minecraft:") || event.getMessage().startsWith("/bukkit:")) {
            CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
            if (player == null || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
                event.getPlayer().sendMessage(ChatColor.RED + "Disabled");
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handles the {@link TabCompleteEvent} to filter and modify tab completion suggestions
     * based on the player's rank and blocked entries.
     *
     * @param event the {@link TabCompleteEvent} triggered when a player attempts to use tab completion
     */
    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        List<String> newCompletions = new ArrayList<>();
        List<String> completions = event.getCompletions();
        if (event.getSender() instanceof Player) {
            CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getSender());
            if (player == null || player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
                for (String completion : completions) {
                    boolean block = false;
                    for (String s : blockedCompletions) {
                        if (completion.startsWith(s)) {
                            block = true;
                            break;
                        }
                    }

                    if (!block && completion.contains(":")) {
                        int start = completion.startsWith("/") ? 1 : 0;
                        String prefix = completion.substring(start, completion.indexOf(":")).toLowerCase();
                        String suffix = completion.substring(completion.indexOf(":") + 1);
                        if (!prefix.contains(" ")) {
                            for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                                if (p.getName().equalsIgnoreCase(prefix)) {
                                    blockedCompletions.add((start == 1 ? "/" : "") + prefix);
                                    block = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (block) continue;

                    newCompletions.add(completion);
                }
                newCompletions.sort(String.CASE_INSENSITIVE_ORDER);
                event.setCompletions(newCompletions);
            }
        }
    }
}
