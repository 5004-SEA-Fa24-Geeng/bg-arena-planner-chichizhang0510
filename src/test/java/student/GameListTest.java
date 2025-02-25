package student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class GameListTest {
    private GameList gameList;
    private Stream<BoardGame> games;

    @BeforeEach
    void setUp() {
        gameList = new GameList();
        games = Stream.of(
                new BoardGame("Apple", 1, 3, 4, 60, 30, 2.5, 1, 4.5, 1995),
                new BoardGame("Banana", 2, 2, 4, 45, 30, 1.8, 5, 4.3, 2017),
                new BoardGame("Orange", 3, 1, 5, 120, 90, 3.2, 10, 4.7, 2016)
        );
    }

    @Test
    void getGameNames() {
        gameList.addToList("all", games);
        List<String> gameNames = gameList.getGameNames();
        assertEquals(3, gameNames.size());
        assertEquals(List.of("Apple", "Banana", "Orange"), gameNames);
    }

    @Test
    void clear() {
        gameList.addToList("all", games);
        gameList.clear();
        assertEquals(0, gameList.count());
    }

    @Test
    void count() {
        assertEquals(0, gameList.count());
        gameList.addToList("Apple", Stream.of(new BoardGame("Apple", 1, 3, 4, 60, 30, 2.5, 1, 4.5, 1995)));
        assertEquals(1, gameList.count());
    }

    @Test
    void saveGame() throws IOException {
        gameList.addToList("all", games);
        String filename = "test_game_list.txt";
        gameList.saveGame(filename);

        File file = new File(filename);
        assertTrue(file.exists());

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(List.of("Apple", "Banana", "Orange"), lines);

        file.delete();
    }

    @Test
    void addToListByIndex() {
        gameList.addToList("1", games);
        assertEquals(1, gameList.count());
        assertEquals(List.of("Apple"), gameList.getGameNames());
    }

    @Test
    void addToListByRange() {
        gameList.addToList("1-2", games);
        assertEquals(2, gameList.count());
        assertTrue(gameList.getGameNames().containsAll(List.of("Apple", "Banana")));
    }

    @Test
    void removeFromListByName() {
        gameList.addToList("all", games);
        gameList.removeFromList("Apple");
        assertEquals(2, gameList.count());
        assertFalse(gameList.getGameNames().contains("Apple"));
    }

    @Test
    void removeFromListByIndex() {
        gameList.addToList("all", games);
        gameList.removeFromList("1");
        assertEquals(2, gameList.count());
        assertFalse(gameList.getGameNames().contains("Apple"));
    }

    @Test
    void removeFromListByRange() {
        gameList.addToList("all", games);
        gameList.removeFromList("1-2");
        assertEquals(1, gameList.count());
        assertTrue(gameList.getGameNames().contains("Orange"));
    }
}
