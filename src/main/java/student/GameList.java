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
     * Otherwise, games matching the name or a range selection will be added.
     *
     * @param str      The filter condition ("all", a specific game name, a number index, or a range).
     * @param filtered A stream of filtered {@code BoardGame} objects to add.
     * @throws IllegalArgumentException If an invalid format is encountered.
     */
    @Override
    public void addToList(String str, Stream<BoardGame> filtered) throws IllegalArgumentException {
        List<BoardGame> filteredList = filtered.sorted(Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        if (ADD_ALL.equalsIgnoreCase(str)) {
            games.addAll(filteredList);
            return;
        }

        try {
            if (str.matches("\\d+")) {
                int index = Integer.parseInt(str) - 1;
                if (index < 0 || index >= filteredList.size()) {
                    throw new IllegalArgumentException("Index out of range: " + str);
                }
                games.add(filteredList.get(index));
            } else if (str.matches("\\d+-\\d+")) {
                String[] range = str.split("-");
                int start = Integer.parseInt(range[0]) - 1;
                int end = Integer.parseInt(range[1]) - 1;

                if (start < 0 || end >= filteredList.size() || start > end) {
                    throw new IllegalArgumentException("Invalid range: " + str);
                }

                games.addAll(filteredList.subList(start, end + 1));
            } else {
                Optional<BoardGame> game = filteredList.stream()
                        .filter(g -> g.getName().equalsIgnoreCase(str))
                        .findFirst();
                game.ifPresent(games::add);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format: " + str);
        }
    }

    /**
     * Removes games from the list based on a filter condition.
     * If {@code str} equals "all", clears the list.
     * Otherwise, games matching the name or a range selection will be removed.
     *
     * @param str The filter condition ("all", a specific game name, a number index, or a range).
     * @throws IllegalArgumentException If an invalid format is encountered.
     */
    @Override
    public void removeFromList(String str) throws IllegalArgumentException {
        if (ADD_ALL.equalsIgnoreCase(str)) {
            clear();
            return;
        }

        List<String> gameNames = getGameNames();

        try {
            if (str.matches("\\d+")) {
                int index = Integer.parseInt(str) - 1;
                if (index < 0 || index >= gameNames.size()) {
                    throw new IllegalArgumentException("Index out of range: " + str);
                }
                removeGameByName(gameNames.get(index));
            } else if (str.matches("\\d+-\\d+")) {
                String[] range = str.split("-");
                int start = Integer.parseInt(range[0]) - 1;
                int end = Integer.parseInt(range[1]) - 1;

                if (start < 0 || end >= gameNames.size() || start > end) {
                    throw new IllegalArgumentException("Invalid range: " + str);
                }

                for (int i = start; i <= end; i++) {
                    removeGameByName(gameNames.get(i));
                }
            } else {
                removeGameByName(str);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format: " + str);
        }
    }

    /**
     * Removes a game from the list by its name.
     * This method iterates through the set of games and removes the game if the name matches.
     * If no match is found, an exception is thrown.
     *
     * @param name The name of the game to remove.
     * @throws IllegalArgumentException If the game is not found.
     */
    private void removeGameByName(String name) {
        Iterator<BoardGame> iterator = games.iterator();
        while (iterator.hasNext()) {
            BoardGame game = iterator.next();
            if (game.getName().equalsIgnoreCase(name)) {
                iterator.remove();
                return;
            }
        }
        throw new IllegalArgumentException("Game not found: " + name);
    }
}