package me.itswagpvp.synccommands.spigot.commands;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import me.itswagpvp.synccommands.spigot.firedEvents.SendCommandEvent;
import me.itswagpvp.synccommands.spigot.sync.MySQL;
import me.itswagpvp.synccommands.spigot.sync.Register;
import me.itswagpvp.synccommands.spigot.utils.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Sync implements CommandExecutor {

    private final SyncCommands plugin = SyncCommands.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("synccommands.sync")) {
            sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
            return true;
        }

        if (args.length == 0 || args.length == 1) {
            sender.sendMessage(plugin.getMessage("Messages.InvalidCommand"));
            return true;
        }

        String execute = "";
        sender.sendMessage("§aSending command...");

        for (int i = 1; i < args.length; i++) execute = execute + " " + args[i];
        if (args[0].equals("*")) {
            for (String onlineServer : new Register().getServerList()) {
                SendCommandEvent event = new SendCommandEvent(sender, execute.replaceFirst(" ", ""), onlineServer);
                plugin.getServer().getPluginManager().callEvent(event);

                new MySQL().addCommand(onlineServer, execute.replaceFirst(" ", ""), plugin.getServerName());

            }

            new Sounds().playSuccessSound(sender);
            if (plugin.debugMode) {
                sender.sendMessage("§aServer: §7ALL");
                sender.sendMessage("§aCommand: §7" + execute.replaceFirst(" ", ""));
            }
        }

        String serverName = args[0];

        SendCommandEvent event = new SendCommandEvent(sender, execute.replaceFirst(" ", ""), serverName);
        plugin.getServer().getPluginManager().callEvent(event);

        if (plugin.debugMode) {
            sender.sendMessage("§aServer: §7" + serverName);
            sender.sendMessage("§aCommand: §7" + execute.replaceFirst(" ", ""));
        }

        new Sounds().playSuccessSound(sender);
        new MySQL().addCommand(serverName, execute.replaceFirst(" ", ""), plugin.getServerName());

        return true;
    }
}
