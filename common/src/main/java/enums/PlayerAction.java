package enums;

public enum PlayerAction {
    CREATE_GAME("Create Game", "create"),
    JOIN_GAME("Join Game", "join"),
    START_GAME("Start Game", "start"),
    QUIT_GAME("Quit Game", "quit"),

    CALL("Call", "call"),
    RAISE("Raise", "raise"),
    FOLD("Fold", "fold");

    public final String actionName;
    public final String actionCommand;

    PlayerAction(
            final String actionName,
            final String actionCommand
    ) {
        this.actionName = actionName;
        this.actionCommand = actionCommand;
    }
}
