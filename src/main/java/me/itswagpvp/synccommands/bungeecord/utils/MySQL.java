package me.itswagpvp.synccommands.bungeecord.utils;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private static SyncCommandsBungee plugin;

    public MySQL(SyncCommandsBungee plugin) {
        MySQL.plugin = plugin;
    }

    private static Connection connection;

    public void openConnection() {

        final String host = plugin.getConfig().getString("MySQL.IP", "localhost");
        final String port = plugin.getConfig().getString("MySQL.Port", "3306");
        final String database = plugin.getConfig().getString("MySQL.Database", "SyncCommandsPlus");
        final boolean autoReconnect = plugin.getConfig().getBoolean("MySQL.AutoReconnect", true);

        final String user = plugin.getConfig().getString("MySQL.User", "root");
        final String password = plugin.getConfig().getString("MySQL.Password", "qwerty");

        final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=" + autoReconnect + "&useSSL=false&characterEncoding=utf8";


            try {
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            createTable();

    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createTable() {
        Thread createTable = new Thread(() -> {
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
        });

        createTable.start();
    }

    public void addCommand(String serverName, String command) {
            Thread addCommand = new Thread(() -> {
                try (
                        PreparedStatement ps = connection.prepareStatement("REPLACE INTO SyncCommands (serverName,command) VALUES(?,?)")
                ) {

                    ps.setString(1, serverName);
                    ps.setString(2, command);

                    ps.execute();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });

            addCommand.start();
    }

    public Connection getConnection() {
        return connection;
    }
}
