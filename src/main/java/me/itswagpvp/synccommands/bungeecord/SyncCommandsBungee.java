package me.itswagpvp.synccommands.bungeecord;

import com.zaxxer.hikari.HikariDataSource;
import me.itswagpvp.synccommands.bungeecord.commands.Main;
import me.itswagpvp.synccommands.bungeecord.commands.Sync;
import me.itswagpvp.synccommands.bungeecord.utils.*;
import me.itswagpvp.synccommands.general.metrics.BungeeCordMetrics;
import me.itswagpvp.synccommands.general.updater.Updater;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SyncCommandsBungee extends Plugin {

    private static SyncCommandsBungee plugin;
    private HikariDataSource hikari;

    private String serverName;

    private boolean disabled = false;
    private boolean updaterEnabled = false;
    public boolean debugMode = false;

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
        saveMessagesConfig();

        if (getConfig().getString("MySQL.IP").equals("")
                || getConfig().getString("MySQL.Port").equals("")
                || getConfig().getString("MySQL.Database").equals("")
                || getConfig().getString("MySQL.User").equals("")
                || getConfig().getString("MySQL.Password").equals("")) {
            sendConsoleMessage("&cThe plugin can't work without a MySQL connection, disabling...");
            sendConsoleMessage("&8+------------------------------------+");
            getProxy().getPluginManager().getPlugin("SyncCommands").onDisable();
            return;
        }

        setupMySQL();

        setServerName(plugin.getConfig().getString("ServerName"));

        sendConsoleMessage("&f-> &eLoading commands...");
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Sync(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Main(this));

        sendConsoleMessage("");

        sendConsoleMessage("&f-> &7Registered server name: &c" + getServerName());
        sendConsoleMessage("&f");

        sendConsoleMessage("");

        loadMetrics();

        sendConsoleMessage("&f-> &7Plugin loaded in " + (System.currentTimeMillis() - before) + "ms!");
        sendConsoleMessage("&8+------------------------------------+");

        if (getConfig().getBoolean("Debug-Mode", false)) debugMode = true;
        if (getConfig().getBoolean("Updater", true)) updaterEnabled = true;

        // UPDATER
        if (updaterEnabled) {

            if (new Updater().isPluginOutdated(plugin.getDescription().getVersion())) {
                sendConsoleMessage("&7[SyncCommands] &7Your plugin is outdated!");

                sendConsoleMessage(("&7[SyncCommands] &7You have &cv%this% &7in front of &av%latest%&7!")
                        .replace("%this%", plugin.getDescription().getVersion())
                        .replace("%latest%", "" + new Updater().getNewerVersion()));

            }
        }
    }

    @Override
    public void onDisable() {
        if (!disabled) {
            disabled = true;
        } else {
            return;
        }

        sendConsoleMessage("&8+------------------------------------+");
        sendConsoleMessage("&r           &aSyncCommands");
        sendConsoleMessage("       &aBungeeCord version");
        sendConsoleMessage("");
        plugin.sendConsoleMessage("&f-> &7Closing database connection...");

        if (hikari != null)  {
            Checker.loop.stop();
            new Register(this).unregisterServer(getServerName());
            hikari.close();
        }

        sendConsoleMessage("&8+------------------------------------+");
    }

    public void saveDefaultConfig() {
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

    public void saveMessagesConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "messages.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("messages.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Configuration getMessagesConfig() {
        Configuration configuration = null;
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    public String getMessage(String path) {
        return plugin.getMessagesConfig().getString(path).replaceAll("&", "§");
    }

    public void sendConsoleMessage(String coloredMessage) {
        plugin.getProxy().getConsole().sendMessage(
                coloredMessage.replaceAll("&", "§")
        );
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String newName) {
        this.serverName = newName;
    }

    private void setupMySQL() {
        sendConsoleMessage("&f-> &eLoading database...");

        final String host = plugin.getConfig().getString("MySQL.IP");
        final String port = plugin.getConfig().getString("MySQL.Port");
        final String database = plugin.getConfig().getString("MySQL.Database");
        final boolean autoReconnect = plugin.getConfig().getBoolean("MySQL.AutoReconnect", true);

        final String user = plugin.getConfig().getString("MySQL.User");
        final String password = plugin.getConfig().getString("MySQL.Password");

        final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=" + autoReconnect + "&useSSL=false&characterEncoding=utf8";

        hikari = new HikariDataSource();
        hikari.setJdbcUrl(url);

        hikari.addDataSourceProperty("serverName", host);
        hikari.addDataSourceProperty("port", port);
        hikari.addDataSourceProperty("databaseName", database);
        hikari.addDataSourceProperty("user", user);
        hikari.addDataSourceProperty("password", password);

        new MySQL(this).createTable();

        new Checker(this).checkNewCommands();
        new Register(this).createTable();
        new Register(this).registerServer();
    }

    private void loadMetrics() {
        sendConsoleMessage("&f-> &7Loading metrics...");
        try {
            new BungeeCordMetrics(plugin, 12966);
        } catch (Exception e) {
            sendConsoleMessage("&f-> &7Error loading metrics: " + e.getMessage());
        }
    }

    public HikariDataSource getHikari() {
        return hikari;
    }
}
