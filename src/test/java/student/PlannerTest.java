package student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PlannerTest {
    private Planner planner;
    private Set<BoardGame> games;

    @BeforeEach
    void setUp() {
        games = Stream.of(
                new BoardGame("Apple", 1, 3, 4, 60, 30, 2.5, 1, 4.5, 1995),
                new BoardGame("Banana", 2, 2, 4, 45, 30, 1.8, 5, 4.3, 2017),
                new BoardGame("Orange", 3, 1, 5, 120, 90, 3.2, 10, 4.7, 2016)
        ).collect(Collectors.toSet());

        planner = new Planner(games);
    }

    @Test
    void testFilterByMinPlayers() {
        Stream<BoardGame> filtered = planner.filter("minPlayers>=3");
        assertEquals(1, filtered.count());
    }

    @Test
    void testFilterByRating() {
        Stream<BoardGame> filtered = planner.filter("rating>4.5");
        assertEquals(1, filtered.count());
    }

    @Test
    void testFilterByDifficulty() {
        Stream<BoardGame> filtered = planner.filter("difficulty<=2.0");
        assertEquals(1, filtered.count());
    }

    @Test
    void testFilterByMultipleConditions() {
        Stream<BoardGame> filtered = planner.filter("minPlayers>1, rating>=4.3");
        assertEquals(2, filtered.count());
    }

    @Test
    void testSortByRatingAscending() {
        Stream<BoardGame> sorted = planner.filter("all", GameData.RATING, true);
        BoardGame[] sortedGames = sorted.toArray(BoardGame[]::new);
        assertEquals("Banana", sortedGames[0].getName());
    }

    @Test
    void testSortByRatingDescending() {
        Stream<BoardGame> sorted = planner.filter("all", GameData.RATING, false);
        BoardGame[] sortedGames = sorted.toArray(BoardGame[]::new);
        assertEquals("Orange", sortedGames[0].getName());
    }

    @Test
    void testSortByMinPlayersAscending() {
        Stream<BoardGame> sorted = planner.filter("all", GameData.MIN_PLAYERS, true);
        BoardGame[] sortedGames = sorted.toArray(BoardGame[]::new);
        assertEquals("Orange", sortedGames[0].getName());
    }

    @Test
    void testSortByMinPlayersDescending() {
        Stream<BoardGame> sorted = planner.filter("all", GameData.MIN_PLAYERS, false);
        BoardGame[] sortedGames = sorted.toArray(BoardGame[]::new);
        assertEquals("Apple", sortedGames[0].getName());
    }

    @Test
    void reset() {
        planner.filter("minPlayers>=3");
        planner.reset();
        Stream<BoardGame> allGames = planner.filter("all");
        assertEquals(3, allGames.count());
    }
}
