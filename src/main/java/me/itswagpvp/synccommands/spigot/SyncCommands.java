package me.itswagpvp.synccommands.spigot;

import me.itswagpvp.synccommands.spigot.commands.Sync;
import me.itswagpvp.synccommands.spigot.utils.Checker;
import me.itswagpvp.synccommands.spigot.utils.MySQL;
import me.itswagpvp.synccommands.spigot.utils.Register;
import me.itswagpvp.synccommands.spigot.utils.TabCompleterUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;

public final class SyncCommands extends JavaPlugin {

    private static SyncCommands plugin;
    private String serverName;

    private List<String> serverList;

    @Override
    public void onEnable() {
        // Plugin startup logic
        long before = System.currentTimeMillis();
        sendConsoleMessage("&8+------------------------------------+");
        sendConsoleMessage("&r           &aSyncCommands");
        sendConsoleMessage("");
        plugin = this;
        saveDefaultConfig();

        try {
            setupMySQL();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        serverName = plugin.getConfig().getString("ServerName");
        serverList = new Register().getServerList();

        loadCommands();

        sendConsoleMessage("");

        sendConsoleMessage("&f-> &7Registered server name: &c" + getServerName());
        sendConsoleMessage("&f");
        sendConsoleMessage("&f-> &aSuccessfully connected to:");

        for (String server : serverList) {
            sendConsoleMessage("&7   - " + server);
        }

        sendConsoleMessage("");
        sendConsoleMessage("&f-> &7Plugin loaded in " + (System.currentTimeMillis() - before) + "ms!");
        sendConsoleMessage("&8+------------------------------------+");

    }

    @Override
    public void onDisable() {
        sendConsoleMessage("&8+------------------------------------+");
        sendConsoleMessage("&r           &aSyncCommands");
        sendConsoleMessage("");
        try {
            new Register().unregisterServer(getServerName());
            plugin.sendConsoleMessage("&f-> &7Closing database connection...");
            new MySQL().closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendConsoleMessage("&8+------------------------------------+");
    }

    private void setupMySQL() throws SQLException {
        sendConsoleMessage("&f-> &eLoading database...");
        // Commands database
        new MySQL().openConnection();
        new Checker().checkNewCommands();
        // Server list database
        new Register().createTable();
        new Register().registerServer();
    }

    private void loadCommands() {
        sendConsoleMessage("&f-> &eLoading commands...");
        getCommand("sync").setExecutor(new Sync());
        getCommand("sync").setTabCompleter(new TabCompleterUtils());
    }

    public static SyncCommands getInstance() {
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

    public String getMessage(String path) {
        return plugin.getConfig().getString(path).replaceAll("&", "§");
    }

}
