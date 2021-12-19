package me.itswagpvp.synccommands.spigot.log;

import me.itswagpvp.synccommands.spigot.SyncCommands;

public class ConsoleLogger {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    public void logCommandReceived(int commandId, String command, String sender) {
        if (plugin.debugMode) {
            plugin.sendConsoleMessage("[SyncCommands] Received command with ID = " + commandId + " from " + sender);
        }

        new FileLogger().createLog(commandId, command, sender);
    }
}
