package me.itswagpvp.synccommands.spigot.commands;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import me.itswagpvp.synccommands.spigot.log.FileLogger;
import me.itswagpvp.synccommands.spigot.sync.Register;
import me.itswagpvp.synccommands.spigot.utils.MessagesUtils;
import me.itswagpvp.synccommands.spigot.utils.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Main implements CommandExecutor {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("§a§lSyncCommands §7v" + plugin.getDescription().getVersion() + " §7made by §a§l_ItsWagPvP §7with <3 §e(Spigot)");
            if (sender.hasPermission("synccommands.discord")) {
                sender.sendMessage("§7For support do /synccommands help");
            }

            new Sounds().playSuccessSound(sender);
            return true;
        }

        if (args.length == 1) {

            // /synccommands discord
            if (args[0].equalsIgnoreCase("discord")) {
                if (!sender.hasPermission("synccommands.discord")) {
                    sender.sendMessage(plugin.getMessage("NoPerms"));
                    new Sounds().playErrorSound(sender);
                    return true;
                }

                sender.sendMessage("§a§lSyncCommands §7discord link:");
                sender.sendMessage("§9https://discord.io/wagsupport");
                new Sounds().playSuccessSound(sender);
                return true;
            }

            // /synccommands debug
            if (args[0].equalsIgnoreCase("debug")) {
                plugin.sendConsoleMessage("&8+------------------------------------+");
                plugin.sendConsoleMessage("&r           &aSyncCommands");
                plugin.sendConsoleMessage("&r              &aDebug");
                plugin.sendConsoleMessage("");
                plugin.sendConsoleMessage("&f-> &7MC-Version of the server: &c" + Bukkit.getBukkitVersion());
                plugin.sendConsoleMessage("&f-> &7Server software: &6" + Bukkit.getName());
                plugin.sendConsoleMessage("&f-> &7Software version: &6" + Bukkit.getVersion());
                plugin.sendConsoleMessage("");
                plugin.sendConsoleMessage("&f-> &7Version of the plugin: &e" + plugin.getDescription().getVersion());
                plugin.sendConsoleMessage("&f-> &7config.yml version: &e" + plugin.getConfig().getString("Version"));
                plugin.sendConsoleMessage("&f-> &7messages.yml version: &e" + plugin.getMessagesVersion());
                plugin.sendConsoleMessage("");
                plugin.sendConsoleMessage("&f-> &7Numbers of server connected: " + new Register().getServerList().size());
                plugin.sendConsoleMessage("&8+------------------------------------+");
                return true;
            }

            // /synccommands server
            if (args[0].equalsIgnoreCase("servers")) {
                if (!sender.hasPermission("synccommands.servers")) {
                    sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
                    return true;
                }
                sender.sendMessage("§a§lSyncCommands §7connected servers: §e(Now connected with " + plugin.getServerName() + ")");
                for (String server : new Register().getServerList()) sender.sendMessage("§7- " + server);
                new Sounds().playSuccessSound(sender);
                return true;
            }

            // /synccommands reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("synccommands.reload")) {
                    sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
                    return true;
                }

                long before = System.currentTimeMillis();
                plugin.saveDefaultConfig();
                new FileLogger().createLogConfig();
                new MessagesUtils().createMessagesConfig();
                new MessagesUtils().reloadMessagesConfig();

                sender.sendMessage("§aPlugin reloaded in " + (System.currentTimeMillis() - before) + " ms!");
                new Sounds().playSuccessSound(sender);

                return true;
            }

            sender.sendMessage("§cInvalid args! use /synccommands <discord/debug/servers/reload>");
            new Sounds().playErrorSound(sender);

            return true;

        }

        return true;

    }
}
