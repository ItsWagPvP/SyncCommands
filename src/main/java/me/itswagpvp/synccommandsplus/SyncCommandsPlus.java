package me.itswagpvp.synccommandsplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SyncCommandsPlus extends JavaPlugin {

    private static SyncCommandsPlus plugin;
    private String serverName;

    @Override
    public void onEnable() {
        // Plugin startup logic
        long before = System.currentTimeMillis();
        plugin = this;
        saveDefaultConfig();

        serverName = plugin.getConfig().getString("ServerName");

        sendConsoleMessage("&e[SyncCommandsPlus] &7Registered server name: " + getServerName());
        sendConsoleMessage("&e[SyncCommandsPlus] &7Plugin loaded in " + (System.currentTimeMillis() - before) + "ms!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SyncCommandsPlus getInstance() {
        return plugin;
    }

    public String getServerName() {
        return serverName;
    }

    public void sendConsoleMessage(String coloredMessage) {
        Bukkit.getConsoleSender().sendMessage(
                coloredMessage.replaceAll("&", "§")
        );
    }
}
