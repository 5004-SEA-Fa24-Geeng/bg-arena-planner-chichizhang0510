package student;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The {@code GameList} class implements the {@code IGameList} interface and
 * provides functionality for managing a list of board games.
 * It allows adding, removing, listing, clearing, and saving games.
 */
public class GameList implements IGameList {

    /**
     * A collection of board games stored in a case-insensitive sorted set.
     */
    private Set<BoardGame> games;

    /**
     * Constructor for the GameList.
     */
    public GameList() {
        this.games = new TreeSet<>(Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Retrieves a sorted list of game names.
     *
     * @return A list of all game names in case-insensitive alphabetical order.
     */
    @Override
    public List<String> getGameNames() {
        return games.stream()
                .map(BoardGame::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    /**
     * Clears the list of games, removing all entries.
     */
    @Override
    public void clear() {
        games.clear();
    }

    /**
     * Returns the total number of games in the list.
     *
     * @return The number of games currently in the list.
     */
    @Override
    public int count() {
        return games.size();
    }

    /**
     * Saves the game list to a file.
     * Each game's name is written to a new line in the specified file.
     *
     * @param filename The name of the file to save the game list to.
     * @throws RuntimeException If an I/O error occurs while writing the file.
     */
    @Override
    public void saveGame(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String name : getGameNames()) {
                writer.write(name + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + filename, e);
        }
    }

    /**
     * Adds games to the list based on a filter condition.
     * If {@code str} equals "all", all filtered games are added.
     * Otherwise, only games matching the name specified in {@code str} are added.
     *
     * @param str      The filter condition ("all" or a specific game name).
     * @param filtered A stream of filtered {@code BoardGame} objects to add.
     * @throws IllegalArgumentException If an invalid format is encountered.
     */
    @Override
    public void addToList(String str, Stream<BoardGame> filtered) {
        if ("all".equalsIgnoreCase(str)) {
            games.addAll(filtered.collect(Collectors.toSet()));
        } else {
            filtered.forEach(game -> {
                if (game.getName().equalsIgnoreCase(str)) {
                    games.add(game);
                }
            });
        }
    }

    /**
     * Removes a game from the list by name.
     *
     * @param str The name of the game to remove (case-insensitive).
     * @throws IllegalArgumentException If the game is not found in the list.
     */
    @Override
    public void removeFromList(String str) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        Iterator<BoardGame> iterator = games.iterator();
        while (iterator.hasNext()) {
            BoardGame game = iterator.next();
            if (game.getName().equalsIgnoreCase(str)) {
                iterator.remove();
                return;
            }
        }
        throw new IllegalArgumentException("Game not found: " + str);
    }


}
