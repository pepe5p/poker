package com.pkaras;

import com.pkaras.enums.PlayerAction;

import java.nio.channels.SocketChannel;

public class Player {
    final String name;
    final SocketChannel clientChannel;
    final Chips chips;
    Hand hand;
    boolean folded;
    boolean allIn;
    PlayerActionValue scheduledAction = null;
    record PlayerActionValue(PlayerAction action, Integer value) {}

    public Player(String name, Chips chips) {
        this(name, null, chips);
    }

    public Player(String name, SocketChannel clientChannel, Chips chips) {
        this.name = name;
        this.clientChannel = clientChannel;
        this.chips = chips;
        this.hand = new Hand();
        this.folded = true;
        this.allIn = false;
    }

    String setScheduledAction(PlayerAction action, String allArguments) {
        boolean emptyArguments = allArguments == null || allArguments.isEmpty();
        Integer value = emptyArguments ? null : Integer.parseInt(allArguments);
        scheduledAction = new PlayerActionValue(action, value);
        return "action scheduled " + action.actionName;
    }

    String nameString() {
        return "[" + name + "]";
    }

    @Override
    public String toString() {
        return nameString() + " " + chips.toString() + "\n";
    }
}
