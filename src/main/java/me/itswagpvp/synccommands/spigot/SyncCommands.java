package me.itswagpvp.synccommands.spigot;

import com.zaxxer.hikari.HikariDataSource;
import me.itswagpvp.synccommands.general.metrics.SpigotMetrics;
import me.itswagpvp.synccommands.general.updater.Updater;
import me.itswagpvp.synccommands.spigot.commands.Main;
import me.itswagpvp.synccommands.spigot.commands.Sync;
import me.itswagpvp.synccommands.spigot.listener.PlayerUpdateNotify;
import me.itswagpvp.synccommands.spigot.log.FileLogger;
import me.itswagpvp.synccommands.spigot.sync.MySQL;
import me.itswagpvp.synccommands.spigot.sync.Checker;
import me.itswagpvp.synccommands.spigot.sync.Register;
import me.itswagpvp.synccommands.spigot.utils.TabCompleterUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class SyncCommands extends JavaPlugin {

    private static SyncCommands plugin;
    private String serverName;
    private HikariDataSource hikari;

    public boolean debugMode = false;
    public boolean updaterEnabled = false;

    private FileConfiguration messagesConfig;
    private File messagesFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        long before = System.currentTimeMillis();
        plugin = this;

        sendConsoleMessage("&8+------------------------------------+");
        sendConsoleMessage("&r           &aSyncCommands");
        sendConsoleMessage("          &aSpigot version");
        sendConsoleMessage("");

        saveDefaultConfig();
        createMessagesConfig();

        if (getConfig().getBoolean("Debug-Mode", false)) debugMode = true;
        if (getConfig().getBoolean("Log", true)) new FileLogger().createLogConfig();
        if (getConfig().getBoolean("Updater", true)) updaterEnabled = true;

        if (getConfig().getString("MySQL.IP").equals("")
                || getConfig().getString("MySQL.Port").equals("")
                || getConfig().getString("MySQL.Database").equals("")
                || getConfig().getString("MySQL.User").equals("")
                || getConfig().getString("MySQL.Password").equals("")) {
            sendConsoleMessage("&cThe plugin can't work without a MySQL connection, disabling...");
            sendConsoleMessage("&8+------------------------------------+");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }

        setupMySQL();

        setServerName(plugin.getConfig().getString("ServerName"));

        loadCommands();

        sendConsoleMessage("");

        sendConsoleMessage("&f-> &7Registered server name: &c" + getServerName());

        if (new Register().getServerList().size() != 0) {
            sendConsoleMessage("&f-> &aSuccessfully connected to:");
            for (String server : new Register().getServerList()) sendConsoleMessage("&7   - " + server);
        } else {
            sendConsoleMessage("&f-> &aNo servers connected...");
        }

        sendConsoleMessage("");

        loadMetrics();

        if (!plugin.getDescription().getVersion().equals(plugin.getConfig().getString("Version"))) {
            sendConsoleMessage("&f-> &eYour config.yml is outdated!");
        }

        if (!plugin.getDescription().getVersion().equals(getMessagesVersion())) {
            sendConsoleMessage("&f-> &eYour messages.yml is outdated!");
        }

        sendConsoleMessage("");

        sendConsoleMessage("&f-> &7Plugin loaded in " + (System.currentTimeMillis() - before) + "ms!");
        sendConsoleMessage("&8+------------------------------------+");

        if (updaterEnabled) {

            if (new Updater().isPluginOutdated(plugin.getDescription().getVersion())) {
                sendConsoleMessage("&7[SyncCommands] &7The plugin is outdated!");

                sendConsoleMessage(("&7[SyncCommands] &7You have &cv%this% &7in front of &av%latest%&7!")
                        .replace("%this%", plugin.getDescription().getVersion())
                        .replace("%latest%", "" + new Updater().getNewerVersion()));
            }
        }

        Bukkit.getPluginManager().registerEvents(new PlayerUpdateNotify(), plugin);

    }

    @Override
    public void onDisable() {
        sendConsoleMessage("&8+------------------------------------+");
        sendConsoleMessage("&r           &aSyncCommands");
        sendConsoleMessage("");
        plugin.sendConsoleMessage("&f-> &7Closing database connection...");

        if (hikari != null) {
            new Register().unregisterServer(getServerName());
            hikari.close();
        }

        sendConsoleMessage("&8+------------------------------------+");
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

        new MySQL().createTable();
        new Checker().checkNewCommands();
        new Register().createTable();
        new Register().registerServer();

    }

    private void loadCommands() {
        sendConsoleMessage("&f-> &eLoading commands...");
        getCommand("sync").setExecutor(new Sync());
        getCommand("sync").setTabCompleter(new TabCompleterUtils());

        getCommand("synccommands").setExecutor(new Main());
        getCommand("synccommands").setTabCompleter(new TabCompleterUtils());
    }

    private void loadMetrics() {
        sendConsoleMessage("&f-> &7Loading metrics...");
        try {
            new SpigotMetrics(plugin, 12964);
        } catch (Exception e) {
            sendConsoleMessage("&f-> &7Error loading metrics: " + e.getMessage());
        }
    }

    public static SyncCommands getInstance() {
        return plugin;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String newName) {
        this.serverName = newName;
    }

    public void sendConsoleMessage(String coloredMessage) {
        Bukkit.getConsoleSender().sendMessage(
                coloredMessage.replaceAll("&", "§")
        );
    }

    public String getMessage(String path) {
        if (getMessagesConfig().get(path) == null) return path;
        return getMessagesConfig().getString(path).replaceAll("&", "§");
    }

    public String getMessagesVersion() {
        if (getMessagesConfig().get("Version") == null) return "Error";
        return getMessagesConfig().getString("Version");
    }

    public HikariDataSource getHikari() {
        return hikari;
    }

    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }

    public void createMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig= new YamlConfiguration();
        try {
            messagesConfig.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void loadMessagesConfig() {
        messagesConfig= new YamlConfiguration();
        try {
            messagesConfig.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
