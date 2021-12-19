package me.itswagpvp.synccommands.spigot.utils;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Sounds {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    public void playErrorSound (CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return;
        }

        if (!plugin.getConfig().getBoolean("Sounds.Error.Enabled", true)) {
            return;
        }

        Sound sound = Sound.valueOf(plugin.getConfig().getString("Sounds.Error.Sound", "ENTITY_VILLAGER_NO"));
        Player p = (Player) sender;
        p.playSound(p.getLocation(), sound, 1.0F, 1.0F);

    }

    public void playSuccessSound(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return;
        }

        if (!plugin.getConfig().getBoolean("Sounds.Success.Enabled", true)) {
            return;
        }

        Sound sound = Sound.valueOf(plugin.getConfig().getString("Sounds.Success.Sound", "ENTITY_PLAYER_LEVELUP"));
        Player p = (Player) sender;
        p.playSound(p.getLocation(), sound, 1.0F, 1.0F);

    }
}
