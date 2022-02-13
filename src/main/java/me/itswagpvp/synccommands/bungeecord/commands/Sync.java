package me.itswagpvp.synccommands.bungeecord.commands;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;
import me.itswagpvp.synccommands.bungeecord.utils.MySQL;
import me.itswagpvp.synccommands.bungeecord.firedEvents.SendCommandEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class Sync extends Command {

    private static SyncCommandsBungee plugin;
    public Sync(SyncCommandsBungee plugin) {
        super("bsync");
        Sync.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("synccommands.sync")) {
            sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
            return;
        }

        if (args.length == 0 || args.length == 1) {
            sender.sendMessage(plugin.getMessage("Messages.InvalidCommand"));
            return;
        }

        sender.sendMessage("§aSending command...");
        String execute = "";
        for (int i = 1; i < args.length; i++) execute = execute + " " + args[i];
        if (args[0].equals("*")) {
            for (String onlineServer : new me.itswagpvp.synccommands.bungeecord.utils.Register(plugin).getServerList()) {
                SendCommandEvent event = new SendCommandEvent(sender, execute.replaceFirst(" ", ""), onlineServer);
                plugin.getProxy().getPluginManager().callEvent(event);
                new MySQL(plugin).addCommand(onlineServer, execute.replaceFirst(" ", ""), plugin.getServerName());
            }

            if (plugin.debugMode) {
                sender.sendMessage("§aServer: §7ALL");
                sender.sendMessage("§aCommand: §7" + execute.replaceFirst(" ", ""));
            }

            return;
        }

        String serverName = args[0];

        for (int i = 1; i < args.length; i++) execute = execute + " " + args[i];

        SendCommandEvent event = new SendCommandEvent(sender, execute.replaceFirst(" ", ""), serverName);
        plugin.getProxy().getPluginManager().callEvent(event);

        if (plugin.debugMode) {
            sender.sendMessage("§aServer: §7" + serverName);
            sender.sendMessage("§aCommand: §7" + execute.replaceFirst(" ", ""));
        }

        new MySQL(plugin).addCommand(serverName, execute.replaceFirst(" ", ""), plugin.getServerName());
    }
}
