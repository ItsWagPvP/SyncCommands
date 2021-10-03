package me.itswagpvp.synccommands.bungeecord;

import me.itswagpvp.synccommands.bungeecord.commands.Sync;
import me.itswagpvp.synccommands.bungeecord.utils.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

public class SyncCommandsBungee extends Plugin {

    private static SyncCommandsBungee plugin;
    private String serverName;

    private List<String> serverList;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        long before = System.currentTimeMillis();

        sendConsoleMessage("&8+------------------------------------+");
        sendConsoleMessage("&r           &aSyncCommands");
        sendConsoleMessage("       &aBungeeCord version");
        sendConsoleMessage("");

        saveDefaultConfig();

        try {
            setupMySQL();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        serverName = plugin.getConfig().getString("ServerName");
        serverList = new Register(this).getServerList();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Sync(this));

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
        sendConsoleMessage("       &aBungeeCord version");
        sendConsoleMessage("");
        try {
            new Register(this).unregisterServer(getServerName());
            plugin.sendConsoleMessage("&f-> &7Closing database connection...");
            new MySQL(this).closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendConsoleMessage("&8+------------------------------------+");
        plugin = null;
    }

    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getConfig() {
        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    public String getMessage(String path) {
        return plugin.getConfig().getString(path).replaceAll("&", "§");
    }

    public void sendConsoleMessage(String coloredMessage) {
        plugin.getProxy().getConsole().sendMessage(
                coloredMessage.replaceAll("&", "§")
        );
    }

    public String getServerName() {
        return serverName;
    }

    private void setupMySQL() throws SQLException {
        sendConsoleMessage("&f-> &eLoading database...");
        // Commands database
        new MySQL(this).openConnection();
        new Checker(this).checkNewCommands();
        // Server list database
        new Register(this).createTable();
        new Register(this).registerServer();
    }
}
