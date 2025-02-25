package student;


import java.util.*;
import java.util.stream.Stream;

/**
 * The {@code Planner} class implements the {@code IPlanner} interface
 * and is responsible for filtering and sorting board games based on user-defined criteria.
 */
public class Planner implements IPlanner {
    /**
     * Constant string representing the keyword used to add or remove all filtered
     * games from the list.
     */
    private static final String ADD_ALL = "all";

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
        if (ADD_ALL.equalsIgnoreCase(filter)) {
            return filteredGames.stream();
        }

        return filteredGames.stream()
                .filter(game -> applyFilters(game, filter))
                .sorted(Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER));
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
        return filter(filter)
                .sorted(getComparator(sortOn, ascending));
    }

    /**
     * Resets the filtered games list to the original set of all games.
     */
    @Override
    public void reset() {
        this.filteredGames = new HashSet<>(allGames);
    }

    /**
     * Applies multiple filtering conditions to a given board game.
     * The filter string contains multiple conditions separated by commas,
     * and the game must satisfy all conditions to be included.
     *
     * @param game   The board game to check.
     * @param filter The filtering condition string.
     * @return {@code true} if the game matches all filters, otherwise {@code false}.
     */
    private boolean applyFilters(BoardGame game, String filter) {
        String[] conditions = filter.split(",");
        for (String condition : conditions) {
            if (!applyFilter(game, condition.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Applies a single filtering condition to a board game.
     * Supports numerical comparisons (>, <, >=, <=, ==, !=) and
     * string-based filtering for names (==, !=, ~=).
     *
     * @param game   The board game to check.
     * @param filter The filtering condition string.
     * @return {@code true} if the game matches the filter, otherwise {@code false}.
     * @throws IllegalArgumentException If the filter format is invalid.
     */
    private boolean applyFilter(BoardGame game, String filter) {
        filter = filter.trim();
        String[] operators = {">=", "<=", ">", "<", "==", "!=", "~="};

        for (String op : operators) {
            if (filter.contains(op)) {
                String[] parts = filter.split(op);
                String field = parts[0].trim();
                String value = parts[1].trim();

                // Handle name field separately (support lexicographical comparison)
                if (field.equalsIgnoreCase("name")) {
                    return switch (op) {
                        case "==" -> game.getName().equalsIgnoreCase(value);
                        case "!=" -> !game.getName().equalsIgnoreCase(value);
                        case "~=" -> game.getName().toLowerCase().contains(value.toLowerCase());
                        case ">" -> game.getName().compareToIgnoreCase(value) > 0;  // Lexicographical comparison
                        case "<" -> game.getName().compareToIgnoreCase(value) < 0;
                        case ">=" -> game.getName().compareToIgnoreCase(value) >= 0;
                        case "<=" -> game.getName().compareToIgnoreCase(value) <= 0;
                        default -> throw new IllegalArgumentException("Invalid operator for string field: " + op);
                    };
                }

                // Handle numeric comparisons
                double fieldValue = getFieldValue(game, field);
                double filterValue = Double.parseDouble(value);

                return switch (op) {
                    case ">" -> fieldValue > filterValue;
                    case "<" -> fieldValue < filterValue;
                    case ">=" -> fieldValue >= filterValue;
                    case "<=" -> fieldValue <= filterValue;
                    case "==" -> fieldValue == filterValue;
                    case "!=" -> fieldValue != filterValue;
                    default -> throw new IllegalArgumentException("Invalid operator: " + op);
                };
            }
        }

        throw new IllegalArgumentException("Invalid filter format: " + filter);
    }

    /**
     * Retrieves the numerical value of a given board game field.
     *
     * @param game  The board game object.
     * @param field The field to retrieve the value from (e.g., "rating", "difficulty").
     * @return The numerical value of the specified field.
     */
    private double getFieldValue(BoardGame game, String field) {
        switch (field.toLowerCase().replace("_", "")) {
            case "minplayers": return game.getMinPlayers();
            case "maxplayers": return game.getMaxPlayers();
            case "minplaytime": return game.getMinPlayTime();
            case "maxplaytime": return game.getMaxPlayTime();
            case "difficulty": return game.getDifficulty();
            case "rating": return game.getRating();
            case "rank": return game.getRank();
            case "yearpublished": return game.getYearPublished();
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
