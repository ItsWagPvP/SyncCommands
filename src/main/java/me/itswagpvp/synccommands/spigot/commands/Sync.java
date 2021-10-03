package me.itswagpvp.synccommands.spigot.commands;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import me.itswagpvp.synccommands.spigot.utils.MySQL;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Sync implements CommandExecutor {

    private final SyncCommands plugin = SyncCommands.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("synccommandsplus.sync")) {
            sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
            return true;
        }

        if (args.length == 0 || args.length == 1) {
            sender.sendMessage(plugin.getMessage("Messages.InvalidCommand"));
            return true;
        }

        String serverName = args[0];

        String execute = "";

        for (int i = 1; i < args.length; i++) {
            execute = execute + " " + args[i];
        }

        sender.sendMessage("§aSending command...");
        sender.sendMessage("§aServer: §7" + serverName);
        sender.sendMessage("§aCommand: §7" + execute.replaceFirst(" ", ""));

        new MySQL().addCommand(serverName, execute.replaceFirst(" ", ""));

        return true;
    }
}
