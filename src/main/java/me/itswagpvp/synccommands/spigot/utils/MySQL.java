package me.itswagpvp.synccommands.spigot.utils;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    private final String host = plugin.getConfig().getString("MySQL.IP", "localhost");
    private final String port = plugin.getConfig().getString("MySQL.Port", "3306");
    private final String database = plugin.getConfig().getString("MySQL.Database", "SyncCommandsPlus");
    private final boolean autoReconnect = plugin.getConfig().getBoolean("MySQL.AutoReconnect", true);

    private final String user = plugin.getConfig().getString("MySQL.User", "root");
    private final String password = plugin.getConfig().getString("MySQL.Password", "qwerty");

    private final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=" + autoReconnect + "&useSSL=false&characterEncoding=utf8";

    private static Connection connection;

    public void openConnection() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        createTable();
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE SyncCommands (id INT AUTO_INCREMENT PRIMARY KEY, " +
                "serverName VARCHAR(256) NOT NULL, " +
                "command VARCHAR(256) NOT NULL)";

        PreparedStatement statement;

        try {
            statement = connection.prepareStatement(sql);
            statement.execute();
        } catch (SQLException e) {

            if (!e.getMessage().contains("already exists")) e.printStackTrace();

        }
    }

    public void addCommand(String serverName, String command) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (
                    PreparedStatement ps = connection.prepareStatement("REPLACE INTO SyncCommands (serverName,command) VALUES(?,?)")
                    ){

                ps.setString(1, serverName);
                ps.setString(2, command);

                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public Connection getConnection() {
        return connection;
    }
}
