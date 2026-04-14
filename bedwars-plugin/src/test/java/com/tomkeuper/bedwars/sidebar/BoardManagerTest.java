package com.tomkeuper.bedwars.sidebar;

import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardManagerTest {

    private BoardManager boardManager;

    @Mock
    private Player mockPlayer;

    @Mock
    private TabPlayer mockTabPlayer;

    @Mock
    private ScoreboardManager mockScoreboardManager;

    private UUID playerUUID = UUID.randomUUID();

    @BeforeEach
    void setUp() throws Exception {
        // Objenesis bypasses constructor avoiding static initializers issue
        Objenesis objenesis = new ObjenesisStd();
        boardManager = objenesis.newInstance(BoardManager.class);

        // Initialize maps that would normally be initialized in the field declaration
        Field tabPlayerCacheField = BoardManager.class.getDeclaredField("tabPlayerCache");
        tabPlayerCacheField.setAccessible(true);
        tabPlayerCacheField.set(boardManager, new ConcurrentHashMap<>());

        Field tabPlayersPrefixField = BoardManager.class.getDeclaredField("tabPlayersPrefix");
        tabPlayersPrefixField.setAccessible(true);
        tabPlayersPrefixField.set(boardManager, new HashMap<>());

        Field tabPlayersSuffixField = BoardManager.class.getDeclaredField("tabPlayersSuffix");
        tabPlayersSuffixField.setAccessible(true);
        tabPlayersSuffixField.set(boardManager, new HashMap<>());

        Field headPlayersPrefixField = BoardManager.class.getDeclaredField("headPlayersPrefix");
        headPlayersPrefixField.setAccessible(true);
        headPlayersPrefixField.set(boardManager, new HashMap<>());

        Field headPlayersSuffixField = BoardManager.class.getDeclaredField("headPlayersSuffix");
        headPlayersSuffixField.setAccessible(true);
        headPlayersSuffixField.set(boardManager, new HashMap<>());

        Field tabPlayersTitleField = BoardManager.class.getDeclaredField("tabPlayersTitle");
        tabPlayersTitleField.setAccessible(true);
        tabPlayersTitleField.set(boardManager, new HashMap<>());

        // Inject the mocked ScoreboardManager via reflection since it's a private static field
        Field scoreboardManagerField = BoardManager.class.getDeclaredField("scoreboardManager");
        scoreboardManagerField.setAccessible(true);
        scoreboardManagerField.set(null, mockScoreboardManager);
    }

    @AfterEach
    void tearDown() throws Exception {
        Field scoreboardManagerField = BoardManager.class.getDeclaredField("scoreboardManager");
        scoreboardManagerField.setAccessible(true);
        scoreboardManagerField.set(null, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCleanupPlayer_TabPlayerExists() throws Exception {
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);

        // Pre-populate the cache so getTabPlayer returns our mock
        Field tabPlayerCacheField = BoardManager.class.getDeclaredField("tabPlayerCache");
        tabPlayerCacheField.setAccessible(true);
        Map<UUID, TabPlayer> tabPlayerCache = (Map<UUID, TabPlayer>) tabPlayerCacheField.get(boardManager);
        tabPlayerCache.put(playerUUID, mockTabPlayer);

        Field tabPlayersPrefixField = BoardManager.class.getDeclaredField("tabPlayersPrefix");
        tabPlayersPrefixField.setAccessible(true);
        HashMap<TabPlayer, Integer> tabPlayersPrefix = (HashMap<TabPlayer, Integer>) tabPlayersPrefixField.get(boardManager);
        tabPlayersPrefix.put(mockTabPlayer, 1);

        Field tabPlayersSuffixField = BoardManager.class.getDeclaredField("tabPlayersSuffix");
        tabPlayersSuffixField.setAccessible(true);
        HashMap<TabPlayer, Integer> tabPlayersSuffix = (HashMap<TabPlayer, Integer>) tabPlayersSuffixField.get(boardManager);
        tabPlayersSuffix.put(mockTabPlayer, 2);

        Field headPlayersPrefixField = BoardManager.class.getDeclaredField("headPlayersPrefix");
        headPlayersPrefixField.setAccessible(true);
        HashMap<TabPlayer, Integer> headPlayersPrefix = (HashMap<TabPlayer, Integer>) headPlayersPrefixField.get(boardManager);
        headPlayersPrefix.put(mockTabPlayer, 3);

        Field headPlayersSuffixField = BoardManager.class.getDeclaredField("headPlayersSuffix");
        headPlayersSuffixField.setAccessible(true);
        HashMap<TabPlayer, Integer> headPlayersSuffix = (HashMap<TabPlayer, Integer>) headPlayersSuffixField.get(boardManager);
        headPlayersSuffix.put(mockTabPlayer, 4);

        Field tabPlayersTitleField = BoardManager.class.getDeclaredField("tabPlayersTitle");
        tabPlayersTitleField.setAccessible(true);
        HashMap<TabPlayer, Integer> tabPlayersTitle = (HashMap<TabPlayer, Integer>) tabPlayersTitleField.get(boardManager);
        tabPlayersTitle.put(mockTabPlayer, 5);

        // Verify maps contain the mockTabPlayer before calling cleanup
        assertTrue(tabPlayersPrefix.containsKey(mockTabPlayer));
        assertTrue(tabPlayersSuffix.containsKey(mockTabPlayer));
        assertTrue(headPlayersPrefix.containsKey(mockTabPlayer));
        assertTrue(headPlayersSuffix.containsKey(mockTabPlayer));
        assertTrue(tabPlayersTitle.containsKey(mockTabPlayer));

        // Call the method under test
        boardManager.cleanupPlayer(mockPlayer);

        // Verify maps no longer contain the mockTabPlayer
        assertFalse(tabPlayersPrefix.containsKey(mockTabPlayer), "tabPlayersPrefix should be cleared");
        assertFalse(tabPlayersSuffix.containsKey(mockTabPlayer), "tabPlayersSuffix should be cleared");
        assertFalse(headPlayersPrefix.containsKey(mockTabPlayer), "headPlayersPrefix should be cleared");
        assertFalse(headPlayersSuffix.containsKey(mockTabPlayer), "headPlayersSuffix should be cleared");
        assertFalse(tabPlayersTitle.containsKey(mockTabPlayer), "tabPlayersTitle should be cleared");

        // Verify scoreboard reset was called.
        // According to the code for cleanupPlayer, `scoreboardManager.resetScoreboard(tabPlayer);` IS called at line 497.
        verify(mockScoreboardManager, times(1)).resetScoreboard(mockTabPlayer);
    }

    @Test
    void testCleanupPlayer_TabPlayerDoesNotExist() throws Exception {
        BoardManager spyBoardManager = spy(boardManager);
        doReturn(null).when(spyBoardManager).getTabPlayer(mockPlayer);

        spyBoardManager.cleanupPlayer(mockPlayer);

        // Verify nothing was touched
        verify(mockScoreboardManager, never()).resetScoreboard(any());
    }
}
