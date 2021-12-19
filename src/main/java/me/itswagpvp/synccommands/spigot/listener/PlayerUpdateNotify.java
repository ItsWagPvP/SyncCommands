package me.itswagpvp.synccommands.spigot.listener;

import me.itswagpvp.synccommands.general.updater.Updater;
import me.itswagpvp.synccommands.spigot.SyncCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerUpdateNotify implements Listener {

    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        if (SyncCommands.getInstance().updaterEnabled) {

            if (!event.getPlayer().hasPermission("synccommands.update")) {
                return;
            }

            final SyncCommands plugin = SyncCommands.getInstance();

            if (new Updater().isPluginOutdated(plugin.getDescription().getVersion())) {
                event.getPlayer().sendMessage("§d[SyncCommands] §7The plugin is outdated!");

                event.getPlayer().sendMessage(("§d[SyncCommands] §7You have §cv%this% §7in front of §av%latest%§7!")
                        .replace("%this%", plugin.getDescription().getVersion())
                        .replace("%latest%", "" + new Updater().getNewerVersion()));
            }
        }
    }
}
