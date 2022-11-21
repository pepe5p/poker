package com.pkaras.enums;

import java.util.HashMap;
import java.util.Map;

public enum PlayerAction {
    CLOSE_SERVER("close server", "close-server", 1, false, false, false),
    CREATE_GAME("create game", "create", 3, true, false, false),
    JOIN_GAME("join game", "join", 2, true, false, false),

    START_GAME("start game", "start", 0, false, true, true),
    QUIT_GAME("quit game", "quit", 0, false, true, false),

    CALL("call", "call", 0, false, true, true),
    RAISE("raise", "raise", 1, false, true, true),
    FOLD("fold", "fold", 0, false, true, true),
    ALL_IN("all-in", "all-in", 0, false, true, true),
    CLEAR_ACTION("clear scheduled action", "clear", 0, false, true, true),
    CHECK_STATUS("check game status", "status", 0, false, true, true),

    EMPTY("empty action", "", 0, false, false, false);

    public final String actionName;
    public final String actionCommand;
    public final int numberOfArguments;
    public final boolean mustInMenu;
    public final boolean mustInGame;
    public final boolean handledByGame;

    PlayerAction(
        final String actionName,
        final String actionCommand,
        final int numberOfArguments,
        final boolean mustInMenu,
        final boolean mustInGame,
        final boolean handledByGame
    ) {
        this.actionName = actionName;
        this.actionCommand = actionCommand;
        this.numberOfArguments = numberOfArguments;
        this.mustInMenu = mustInMenu;
        this.mustInGame = mustInGame;
        this.handledByGame = handledByGame;
    }
    private static final Map<String, PlayerAction> map;
    static {
        map = new HashMap<>();
        for (PlayerAction playerAction : PlayerAction.values()) {
            map.put(playerAction.actionCommand, playerAction);
        }
    }
    public static PlayerAction getByActionCommand(String actionCommand) {
        PlayerAction action = map.get(actionCommand);
        if (action == null) return EMPTY;
        return action;
    }
}
