package me.itswagpvp.synccommands.bungeecord.firedEvents;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@SuppressWarnings("unused")
public class SendCommandEvent extends Event implements Cancellable {
    private final CommandSender commandSender;
    private String command;
    private String sender;

    private boolean isCancelled;

    public SendCommandEvent (CommandSender commandSender, String command, String sender) {
        this.commandSender = commandSender;
        this.command = command;
        this.sender = sender;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    public final CommandSender getCommandSender() {
        return this.commandSender;
    }

    public String getCommand() {
        return this.command;
    }

    public String getSender() {
        return this.sender;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setSender (String sender) {
        this.sender = sender;
    }
}
