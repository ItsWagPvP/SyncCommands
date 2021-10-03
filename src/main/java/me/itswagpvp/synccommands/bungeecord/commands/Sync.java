package me.itswagpvp.synccommands.bungeecord.commands;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;
import me.itswagpvp.synccommands.bungeecord.utils.MySQL;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class Sync extends Command {


    private static SyncCommandsBungee plugin;
    public Sync(SyncCommandsBungee plugin) {
        super("sync");
        Sync.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("synccommandsplus.sync")) {
            sender.sendMessage(plugin.getMessage("Messages.NoPerms"));
            return;
        }

        if (args.length == 0 || args.length == 1) {
            sender.sendMessage(plugin.getMessage("Messages.InvalidCommand"));
            return;
        }

        String serverName = args[0];
        String execute = "";

        for (int i = 1; i < args.length; i++) {
            execute = execute + " " + args[i];
        }

        sender.sendMessage("§aSending command...");
        sender.sendMessage("§aServer: §7" + serverName);
        sender.sendMessage("§aCommand: §7" + execute.replaceFirst(" ", ""));

        new MySQL(plugin).addCommand(serverName, execute.replaceFirst(" ", ""));

        return;
    }
}
