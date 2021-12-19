package me.itswagpvp.synccommands.bungeecord.firedEvents;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

@SuppressWarnings("unused")
public class ReceivedCommandEvent extends Event implements Cancellable {
    private final int commandId;
    private String command;
    private String sender;

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
