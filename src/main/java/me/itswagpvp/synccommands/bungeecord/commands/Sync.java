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

        String serverName = args[0];
        String execute = "";

        for (int i = 1; i < args.length; i++) execute = execute + " " + args[i];

        sender.sendMessage("§aSending command...");

        SendCommandEvent event = new SendCommandEvent(sender, execute.replaceFirst(" ", ""), serverName);
        plugin.getProxy().getPluginManager().callEvent(event);

        if (plugin.debugMode) {
            sender.sendMessage("§aServer: §7" + serverName);
            sender.sendMessage("§aCommand: §7" + execute.replaceFirst(" ", ""));
        }


        new MySQL(plugin).addCommand(serverName, execute.replaceFirst(" ", ""), plugin.getServerName());
    }
}
