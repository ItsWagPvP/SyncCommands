package me.itswagpvp.synccommands.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public class TabCompleterUtils implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("sync")) {
            int i = (args.length);
            switch (i) {
                case 1:
                    return new Register().getServerList();
                default:
                    return Arrays.asList("");
            }
        }
        return null;
    }
}
