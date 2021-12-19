package me.itswagpvp.synccommands.spigot.firedEvents;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class ReceivedCommandEvent extends Event implements Cancellable {
    private final int commandId;
    private String command;
    private String sender;

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public ReceivedCommandEvent (int commandId, String command, String sender) {
        this.commandId = commandId;
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

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public int getCommandId() {
        return this.commandId;
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
