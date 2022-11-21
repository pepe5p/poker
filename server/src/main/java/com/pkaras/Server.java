package com.pkaras;

import com.pkaras.enums.PlayerAction;
import com.pkaras.exceptions.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

class Server {
    static final int BUFFER_SIZE = 1024;
    static final String CLOSE_PHRASE = "2137";
    static boolean serverClosed = false;
    static ServerSocketChannel serverChannel;
    static Selector selector;
    static final Map<SocketChannel, ClientDetail> clients = new HashMap<>();
    static int clientsSequence = 0;
    static final Map<Integer, Game> games = new HashMap<>();
    static int gamesSequence = 0;

    record ClientDetail(SocketChannel clientChannel, String name, Game game, Player player) {}

    public static void main(String[] args) {
        try {
            Server.initialize();
            Server.handlingClientsLoop(ByteBuffer.allocate(BUFFER_SIZE));
            Server.closeConnection();
        }
        catch (IOException e) {
            Console.print(e.getMessage());
        }
    }

    private static void initialize() throws IOException {
        InetSocketAddress serverAddress = new InetSocketAddress(
            ConnectionConfig.serverAddress(),
            ConnectionConfig.PORT
        );
        Console.print(
            String.format(
                "\u001B[35mlistening to connections on %s:%d\u001B[0m",
                serverAddress.getHostName(),
                serverAddress.getPort()
            )
        );
        Server.selector = Selector.open();
        Server.serverChannel = ServerSocketChannel.open();
        Server.serverChannel.socket().bind(serverAddress);
        Server.serverChannel.configureBlocking(false);
        Server.serverChannel.register(Server.selector, Server.serverChannel.validOps(), null);
    }

    private static void handlingClientsLoop(ByteBuffer clientResponse) throws IOException {
        while (!serverClosed) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> i = selectedKeys.iterator();

            while (i.hasNext()) {
                SelectionKey key = i.next();
                if (key.isAcceptable()) {
                    Server.processAcceptEvent();
                } else if (key.isReadable()) {
                    Server.processReadEvent(
                        key,
                        clientResponse
                    );
                }
                i.remove();
            }
        }
    }

    private static void processAcceptEvent() throws IOException {
        clientsSequence += 1;
        SocketChannel clientChannel = Server.serverChannel.accept();
        clientChannel.configureBlocking(false);
        ClientDetail clientDetail = new ClientDetail(
            clientChannel,
            "user" + clientsSequence,
            null,
            null
        );
        clients.put(clientChannel, clientDetail);
        clientChannel.register(selector, SelectionKey.OP_READ);
        Console.print(
            String.format(
                "\u001B[35mclient accepted %s %s\u001B[0m",
                clientDetail.name,
                clientChannel.getRemoteAddress()
            )
        );
        Server.messageToOne(
            clientChannel,
            String.format(
                "connected to server as [%s]",
                clientDetail.name
            )
        );
    }

    private static void processReadEvent(
        SelectionKey key,
        ByteBuffer clientResponse
    ) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        try {
            clientChannel.read(clientResponse);
        }
        catch (SocketException e) {
            key.cancel();
            return;
        }
        Server.handleClientResponse(
            clients.get(clientChannel),
            clientResponse
        );
        clientResponse.clear();
        clientResponse.put(new byte[BUFFER_SIZE]);
        clientResponse.clear();
    }

    private static void handleClientResponse(
        ClientDetail clientDetail,
        ByteBuffer clientResponse
    ) {
        String data = new String(clientResponse.array()).trim();
        if (data.isEmpty()) return;
        String[] dataParts = data.split(" ", 2);
        PlayerAction action = PlayerAction.getByActionCommand(dataParts[0]);
        String allArguments = dataParts.length == 2 ? dataParts[1] : "";
        Console.print(
            String.format(
                "[%s] %s {%s}",
                clientDetail.name,
                action.actionName,
                allArguments
            )
        );
        try {
            Server.performAction(action, clientDetail, allArguments);
        }
        catch (PokerException e) {
            Server.messageToOne(clientDetail.clientChannel, e.getMessage());
        }
    }

    static void performAction(
        PlayerAction action,
        ClientDetail clientDetail,
        String allArguments
    ) throws PokerException {
        if (action.mustInGame && clientDetail.game == null) throw new PlayerInMenuException();
        if (action.mustInMenu && clientDetail.game != null) throw new PlayerInGameException();
        if (action.handledByGame) {
            if (clientDetail.game == null) throw new PlayerInMenuException();
            GameResponse gameResponse = clientDetail.game.performAction(action, clientDetail.player, allArguments);
            Server.sendGameResponse(clientDetail.game, gameResponse);
            return;
        }
        String[] arguments = new String[]{allArguments};
        if (action != PlayerAction.EMPTY) arguments = Server.splitArguments(allArguments, action);
        switch (action) {
            case CLOSE_SERVER -> Server.closeServer(arguments);
            case CREATE_GAME -> Server.createGame(clientDetail, arguments);
            case JOIN_GAME -> Server.joinGame(clientDetail, arguments);
            case QUIT_GAME -> Server.quitGame(clientDetail);
            default -> Server.messageToOne(clientDetail.clientChannel, "action not recognized");
        }
    }

    private static void sendGameResponse(Game game, GameResponse gameResponse) {
        if(!gameResponse.messageToGame().isEmpty()) {
            Server.messageToGame(game, gameResponse.messageToGame());
        }
        for (Map.Entry<Player, String> messageToPlayer : gameResponse.messageToPlayers().entrySet()) {
            if(messageToPlayer.getValue().isEmpty()) continue;
            Server.messageToOne(messageToPlayer.getKey().clientChannel, messageToPlayer.getValue());
        }
    }

    private static void closeServer(String[] arguments) {
        if (!arguments[0].equals(Server.CLOSE_PHRASE)) return;
        Server.serverClosed = true;
        Server.messageToAll("closing server");
    }

    private static void createGame(
        ClientDetail clientDetail,
        String[] arguments
    ) throws ShuffleIncompleteDeckException {
        gamesSequence += 1;
        int ante = Integer.parseInt(arguments[0]);
        int smallBlind = Integer.parseInt(arguments[1]);
        int bigBlind = smallBlind * 2;
        Game game = new Game(gamesSequence, ante, smallBlind);
        games.put(gamesSequence, game);
        String message = String.format(
            "game%s created with ante %d, small blind %d and big blind %d",
            gamesSequence,
            ante,
            smallBlind,
            bigBlind
        );
        messageToAll(message);
        joinGame(clientDetail, new String[]{String.valueOf(gamesSequence), arguments[2]});
    }

    private static void joinGame(ClientDetail clientDetail, String[] arguments) {
        int gameIndex = Integer.parseInt(arguments[0]);
        int chips = Integer.parseInt(arguments[1]);
        Game game;
        try {
            game = games.get(gameIndex);
        }
        catch (IndexOutOfBoundsException e) {
            String message = String.format(
                "there is no game%d",
                gameIndex
            );
            Server.messageToOne(clientDetail.clientChannel, message);
            return;
        }
        if (game == null) {
            String message = String.format(
                "there is no game%d",
                gameIndex
            );
            Server.messageToOne(clientDetail.clientChannel, message);
            return;
        }
        Player player = new Player(clientDetail.name, clientDetail.clientChannel, new Chips(chips));
        clients.replace(
            clientDetail.clientChannel,
            new ClientDetail(
                clientDetail.clientChannel,
                clientDetail.name,
                game,
                player
            )
        );
        game.enqueuePlayer(player);
        String message = String.format(
            "[%s] joined game%d with %d chips",
            clientDetail.name,
            gameIndex,
            chips
        );
        Server.messageToGame(game, message);
    }

    private static void quitGame(ClientDetail clientDetail) {
        if (clientDetail.player == clientDetail.game.owner) {
            String message = String.format(
                "finishing game because owner [%s] had left",
                clientDetail.name
            );
            clientDetail.game.gameStopped = true;
            Server.messageToGame(clientDetail.game, message);
            Server.messageToAll("game" + clientDetail.game.id + " finished");
            Server.kickEveryOneFromGame(clientDetail.game);
            games.remove(clientDetail.game.id);
        }
        else {
            Server.kickFromGame(clientDetail);
            String message = String.format(
                "[%s] left game%d",
                clientDetail.name,
                clientDetail.game.id
            );
            Server.messageToGame(clientDetail.game, message);
        }
    }

    private static void kickEveryOneFromGame(Game game) {
        clients.forEach(
            (clientChannel, clientDetail) -> {
                if (clientDetail.game != game) return;
                Server.kickFromGame(clientDetail);
            }
        );
    }

    private static void kickFromGame(ClientDetail clientDetail) {
        clientDetail.game.removePlayer(clientDetail.player);
        clients.replace(
            clientDetail.clientChannel,
            new ClientDetail(
                clientDetail.clientChannel,
                clientDetail.name,
                null,
                null
            )
        );
    }

    private static String[] splitArguments(
        String allArguments,
        PlayerAction action
    ) throws ImproperArgumentTypeException, ImproperArgumentsNumberException {
        String[] arguments = allArguments.split(" ");
        int argumentsLength = allArguments.equals("") ? 0 : arguments.length;
        if (argumentsLength != action.numberOfArguments) {
            throw new ImproperArgumentsNumberException(
                action.actionName,
                action.numberOfArguments,
                argumentsLength
            );
        }
        if (argumentsLength == 0) return arguments;
        for (String argument : arguments) {
            try {
                Integer.parseInt(argument);
            }
            catch (NumberFormatException e) {
                throw new ImproperArgumentTypeException(
                    action.actionName,
                    argument
                );
            }
        }
        return arguments;
    }

    private static void closeConnection() throws IOException {
        selector.close();
        serverChannel.close();
        Console.print("server closed");
    }

    private static void messageToAll(String message) {
        clients.keySet().forEach(
            clientChannel -> messageToOne(clientChannel, message, "(everyone) \u001B[32m")
        );
    }

    private static void messageToGame(Game game, String message) {
        clients.forEach(
            (clientChannel, clientDetail) -> {
                if (clientDetail.game == game) {
                    messageToOne(clientDetail.clientChannel, message, "(game) \u001B[34m");
                }
            }
        );
    }

    private static void messageToOne(SocketChannel clientChannel, String message) {
        Server.messageToOne(clientChannel, message, "(only you) \u001B[33m");
    }

    private static void messageToOne(SocketChannel clientChannel, String message, String color) {
        String coloredMessage = "> " + color + message + "\u001B[0m";
        try {
            ByteBuffer serverResponse = ByteBuffer.allocate(BUFFER_SIZE);
            serverResponse.put(coloredMessage.getBytes());
            serverResponse.flip();
            clientChannel.write(serverResponse);
        } catch (IOException e) {
            Console.print(e.getMessage());
        }
    }
}
