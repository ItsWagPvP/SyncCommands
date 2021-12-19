package me.itswagpvp.synccommands.bungeecord.commands;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;
import me.itswagpvp.synccommands.bungeecord.utils.Register;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class Main extends Command {
    private static SyncCommandsBungee plugin;
    public Main(SyncCommandsBungee plugin) {
        super("synccommands");
        Main.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§a§lSyncCommands §7v" + plugin.getDescription().getVersion() + " §7made by §a§l_ItsWagPvP §7with <3 §e(BungeeCord)");
            if (sender.hasPermission("synccommands.discord")) {
                sender.sendMessage("§7For support do /synccommands help");
            }

            return;
        }

        if (args.length == 1) {

            // /synccommands discord
            if (args[0].equalsIgnoreCase("discord")) {
                if (!sender.hasPermission("synccommands.discord")) {
                    sender.sendMessage(plugin.getMessage("NoPerms"));
                    return;
                }

                sender.sendMessage("§a§lSyncCommands §7discord link:");
                sender.sendMessage("§9https://discord.io/wagsupport");
                return;
            }

            // /synccommands debug
            if (args[0].equalsIgnoreCase("debug")) {
                plugin.sendConsoleMessage("&8+------------------------------------+");
                plugin.sendConsoleMessage("&r           &aSyncCommands");
                plugin.sendConsoleMessage("&r              &aDebug");
                plugin.sendConsoleMessage("");
                plugin.sendConsoleMessage("&f-> &7Server software: &6" + plugin.getProxy().getName());
                plugin.sendConsoleMessage("&f-> &7Software version: &6" + plugin.getProxy().getVersion());
                plugin.sendConsoleMessage("");
                plugin.sendConsoleMessage("&f-> &7Version of the plugin: &e" + plugin.getDescription().getVersion());
                plugin.sendConsoleMessage("&f-> &7Version of the config: &e" + plugin.getConfig().get("Version"));
                plugin.sendConsoleMessage("");
                plugin.sendConsoleMessage("&f-> &7Numbers of server connected: " + new Register(plugin).getServerList().size());
                plugin.sendConsoleMessage("&8+------------------------------------+");
                return;
            }

            // /synccommands server
            if (args[0].equalsIgnoreCase("servers")) {
                if (!sender.hasPermission("synccommands.servers")) {
                    sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
                    return;
                }

                sender.sendMessage("§a§lSyncCommands §7connected servers: §e(Now connected with " + plugin.getServerName() + ")");
                for (String server : new me.itswagpvp.synccommands.bungeecord.utils.Register(plugin).getServerList()) sender.sendMessage("§7- " + server);
                return;
            }

            // /synccommands reload
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("synccommands.reload")) {
                    sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
                    return;
                }

                long before = System.currentTimeMillis();

                plugin.saveDefaultConfig();
                plugin.saveMessagesConfig();

                sender.sendMessage("§aPlugin reloaded in " + (System.currentTimeMillis() - before) + " ms!");

                return;
            }

            sender.sendMessage("§cInvalid args! use /synccommands <discord/debug/servers/reload>");
        }
    }
}
