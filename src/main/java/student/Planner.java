package student;


import java.util.*;
import java.util.stream.Stream;

/**
 * The {@code Planner} class implements the {@code IPlanner} interface
 * and is responsible for filtering and sorting board games based on user-defined criteria.
 */
public class Planner implements IPlanner {
    /** The complete set of all available board games for filtering and sorting. */
    private final Set<BoardGame> allGames;

    /** The current set of filtered board games based on applied filters. */
    private Set<BoardGame> filteredGames;

    /**
     * Constructs a new Planner instance with a given set of board games.
     *
     * @param games The set of all board games available for filtering and sorting.
     */
    public Planner(Set<BoardGame> games) {
        this.allGames = new HashSet<>(games);
        this.filteredGames = new HashSet<>(games);
    }

    /**
     * Filters the board games based on a specified filter condition.
     *
     * @param filter A string representing the filter condition (e.g., "rating >= 4.5").
     * @return A stream of {@code BoardGame} objects that match the filter criteria.
     */
    @Override
    public Stream<BoardGame> filter(String filter) {
        if (filter.equalsIgnoreCase("all")) {
            return filteredGames.stream();
        }

        List<BoardGame> result = new ArrayList<>();

        for (BoardGame game : filteredGames) {
            if (applyFilter(game, filter)) {
                result.add(game);
            }
        }

        return result.stream();
    }

    /**
     * Filters the board games and sorts the results based on a specified field.
     *
     * @param filter A string representing the filter condition.
     * @param sortOn The field to sort the results on (e.g., rating, difficulty).
     * @return A stream of filtered and sorted {@code BoardGame} objects.
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn) {
        return filter(filter, sortOn, true);
    }

    /**
     * Filters the board games and sorts the results based on a specified field,
     * with an option to choose ascending or descending order.
     *
     * @param filter    A string representing the filter condition.
     * @param sortOn    The field to sort the results on.
     * @param ascending {@code true} for ascending order, {@code false} for descending order.
     * @return A stream of filtered and sorted {@code BoardGame} objects.
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn, boolean ascending) {
        List<BoardGame> result = new ArrayList<>();

        for (BoardGame game : filteredGames) {
            if (applyFilter(game, filter)) {
                result.add(game);
            }
        }

        result.sort(getComparator(sortOn, ascending));
        return result.stream();
    }

    /**
     * Resets the filtered games list to the original set of all games.
     */
    @Override
    public void reset() {
        this.filteredGames = new HashSet<>(allGames);
    }

    /**
     * Applies a filtering condition to determine if a board game matches the criteria.
     *
     * @param game   The board game to check.
     * @param filter The filtering condition string.
     * @return {@code true} if the game matches the filter, otherwise {@code false}.
     */
    private boolean applyFilter(BoardGame game, String filter) {
        if (filter.equalsIgnoreCase("all")) {
            return true;
        }

        if (filter.startsWith("name ==")) {
            String nameValue = filter.substring(8).trim();
            return game.getName().equalsIgnoreCase(nameValue);
        }

        if (filter.contains("=") && !filter.contains(">") && !filter.contains("<")) {
            String[] parts = filter.split("=");
            return game.getName().equalsIgnoreCase(parts[1].trim());
        } else if (filter.contains(">=")) {
            String[] parts = filter.split(">=");
            return getFieldValue(game, parts[0].trim()) >= Double.parseDouble(parts[1].trim());
        } else if (filter.contains("<=")) {
            String[] parts = filter.split("<=");
            return getFieldValue(game, parts[0].trim()) <= Double.parseDouble(parts[1].trim());
        } else if (filter.contains(">")) {
            String[] parts = filter.split(">");
            return getFieldValue(game, parts[0].trim()) > Double.parseDouble(parts[1].trim());
        } else if (filter.contains("<")) {
            String[] parts = filter.split("<");
            return getFieldValue(game, parts[0].trim()) < Double.parseDouble(parts[1].trim());
        } else if (filter.contains("!=")) {
            String[] parts = filter.split("!=");
            return getFieldValue(game, parts[0].trim()) != Double.parseDouble(parts[1].trim());
        } else if (filter.contains("~=")) {
            String[] parts = filter.split("~=");
            return Math.abs(getFieldValue(game, parts[0].trim()) - Double.parseDouble(parts[1].trim())) < 0.1;
        } else {
            throw new IllegalArgumentException("Invalid filter format");
        }
    }

    /**
     * Retrieves the numerical value of a given board game field.
     *
     * @param game  The board game object.
     * @param field The field to retrieve the value from (e.g., "rating", "difficulty").
     * @return The numerical value of the specified field.
     */
    private double getFieldValue(BoardGame game, String field) {
        switch (field.toLowerCase()) {
            case "minplayers": return game.getMinPlayers();
            case "maxplayers": return game.getMaxPlayers();
            case "minplaytime": return game.getMinPlayTime();
            case "maxplaytime": return game.getMaxPlayTime();
            case "difficulty": return game.getDifficulty();
            case "rating": return game.getRating();
            default: throw new IllegalArgumentException("Invalid field: " + field);
        }
    }

    /**
     * Retrieves a comparator for sorting board games based on a given field.
     *
     * @param sortOn    The field to sort by.
     * @param ascending {@code true} for ascending order, {@code false} for descending order.
     * @return A {@code Comparator} for sorting board games.
     */
    private Comparator<BoardGame> getComparator(GameData sortOn, boolean ascending) {
        Comparator<BoardGame> comparator;

        if (sortOn == GameData.RATING) {
            comparator = Comparator.comparingDouble(BoardGame::getRating);
        } else if (sortOn == GameData.DIFFICULTY) {
            comparator = Comparator.comparingDouble(BoardGame::getDifficulty);
        } else if (sortOn == GameData.MIN_PLAYERS) {
            comparator = Comparator.comparingInt(BoardGame::getMinPlayers);
        } else if (sortOn == GameData.MAX_PLAYERS) {
            comparator = Comparator.comparingInt(BoardGame::getMaxPlayers);
        } else if (sortOn == GameData.MIN_TIME) {
            comparator = Comparator.comparingInt(BoardGame::getMinPlayTime);
        } else if (sortOn == GameData.MAX_TIME) {
            comparator = Comparator.comparingInt(BoardGame::getMaxPlayTime);
        } else {
            throw new IllegalArgumentException("Invalid sorting field");
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return comparator;
    }
}
