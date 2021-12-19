package me.itswagpvp.synccommands.bungeecord.utils;

import me.itswagpvp.synccommands.bungeecord.SyncCommandsBungee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private static SyncCommandsBungee plugin;

    public MySQL(SyncCommandsBungee plugin) {
        MySQL.plugin = plugin;
    }

    public void createTable() {
        Thread createTable = new Thread(() -> {
            String sql = "CREATE TABLE SyncCommands (id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "serverName VARCHAR(256) NOT NULL, " +
                    "command VARCHAR(256) NOT NULL, " +
                    "sender VARCHAR(256) NOT NULL)";

            PreparedStatement statement;
            try (
                    Connection connection = plugin.getHikari().getConnection()
                    ){

                statement = connection.prepareStatement(sql);
                statement.execute();
            } catch (SQLException e) {

                if (!e.getMessage().contains("already exists")) e.printStackTrace();

            }
        });

        createTable.start();
    }

    public void addCommand(String serverName, String command, String sender) {
        Thread addCommand = new Thread(() -> {
            try (
                    Connection connection = plugin.getHikari().getConnection();
                    PreparedStatement ps = connection.prepareStatement("REPLACE INTO SyncCommands (serverName,command,sender) VALUES(?,?,?)")
            ) {

                ps.setString(1, serverName);
                ps.setString(2, command);
                ps.setString(3, sender);

                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

        addCommand.start();
    }
}
