package me.itswagpvp.synccommands.spigot.sync;

import me.itswagpvp.synccommands.spigot.SyncCommands;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySQL {

    private static final SyncCommands plugin = SyncCommands.getInstance();

    public void createTable() {
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
    }

    public void addCommand(String serverName, String command, String sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (
                    Connection connection = plugin.getHikari().getConnection();
                    PreparedStatement ps = connection.prepareStatement("REPLACE INTO SyncCommands (serverName,command,sender) VALUES(?,?,?)")
                    ){

                ps.setString(1, serverName);
                ps.setString(2, command);
                ps.setString(3, sender);

                ps.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }
}
