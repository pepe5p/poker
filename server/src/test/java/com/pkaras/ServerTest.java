package com.pkaras;

import com.pkaras.enums.PlayerAction;
import com.pkaras.exceptions.PokerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.*;


class ServerTest {

    @BeforeAll
    static void beforeAllTests() {
        Server.serverChannel = mock(ServerSocketChannel.class);
        Server.selector = mock(Selector.class);
    }

    @DisplayName("Test Creating Game")
    @Test
    void testCreateGame() throws PokerException {
        Server.ClientDetail clientDetail = new Server.ClientDetail(
            mock(SocketChannel.class),
            "Jacuś",
            null,
            null
        );
        Server.performAction(
            PlayerAction.CREATE_GAME,
            clientDetail,
            "25 50 1000"
        );
        Assertions.assertFalse(Server.games.isEmpty());
        Game game = Server.games.get(1);
        Assertions.assertFalse(game.players.isEmpty());
    }

    @DisplayName("Test Joining Game")
    @Test
    void testJoinGame() throws PokerException {
        Game game = new Game(0, 0, 0);
        Server.games.put(0, game);
        Server.ClientDetail clientDetail = new Server.ClientDetail(
            mock(SocketChannel.class),
            "Jacuś",
            null,
            null
        );
        Server.performAction(
            PlayerAction.JOIN_GAME,
            clientDetail,
            game.id + " 1000"
        );
        Assertions.assertFalse(game.players.isEmpty());
        Assertions.assertNotNull(game.owner);
        Assertions.assertEquals(1000, game.players.get(0).chips.chipsInStack);
        Assertions.assertEquals(0, game.players.get(0).chips.chipsInGame);
    }

    @DisplayName("Test Quiting Game")
    @ParameterizedTest(name = "isOwner={0}")
    @ValueSource(booleans = {true, false})
    void testQuitGame(boolean isOwner) throws PokerException {
        Game game = new Game(0, 0, 0);
        Server.games.put(0, game);
        Player player = new Player("Jacuś", new Chips(0));
        game.players.add(player);
        if (isOwner) game.owner = player;
        Server.ClientDetail clientDetail = new Server.ClientDetail(
            mock(SocketChannel.class),
            player.name,
            game,
            player
        );
        Server.performAction(PlayerAction.QUIT_GAME, clientDetail, "");
        if (isOwner) Assertions.assertTrue(Server.games.isEmpty());
        else Assertions.assertTrue(game.players.isEmpty());
    }
}
