package me.itswagpvp.synccommands.spigot.utils;

import me.itswagpvp.synccommands.spigot.sync.Register;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TabCompleterUtils implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("synccommands")) {
            int i = (args.length);
            if (i == 1) {
                return Arrays.asList("discord", "debug", "update", "servers", "reload");
            }
            return Collections.singletonList("");
        }

        if (command.getName().equalsIgnoreCase("sync")) {
            int i = (args.length);
            if (i == 1) {
                return new Register().getServerList();
            }
            return Collections.singletonList("");
        }
        return null;
    }
}
