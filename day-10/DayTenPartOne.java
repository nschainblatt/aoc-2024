import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class DayTenPartOne {

  public static String TRAIL_HEAD = "0";

  public static List<List<String>> getLinesFromFilePath(String filePath) throws Exception {
    List<List<String>> lines = new ArrayList<>();
    try (FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr)) {
      lines = br.lines().map(line -> Arrays.stream(line.split("")).toList()).toList();
    }
    return lines;
  }

  public static List<int[]> getAllTrainHeadIndices(List<List<String>> lines) {
    List<int[]> trainHeadIndices = new ArrayList<>();

    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(i).size(); j++) {
        if (TRAIL_HEAD.equals(lines.get(i).get(j))) {
          trainHeadIndices.add(new int[] {i, j});
        }
      }
    }

    return trainHeadIndices;
  }

  public static boolean isPositionInBounds(List<List<String>> lines, int[] position) {
    int lineIndex = position[0];
    int characterIndex = position[1];
    return lineIndex >= 0
        && characterIndex >= 0
        && (lineIndex < lines.size() && characterIndex < lines.get(lineIndex).size());
  }

  public static boolean isPositionValueOneGreater(
      List<List<String>> lines, int[] position, int previousNumber) {
    if (previousNumber == -1) {
      return true;
    }
    int newNumber = getNumberAtPosition(lines, position);
    return newNumber == previousNumber + 1;
  }

  public static int[] getNewPositionInDirection(int[] position, Direction direction) {
    int lineIndex = position[0];
    int characterIndex = position[1];
    return new int[] {
      lineIndex + direction.indexChange[0], characterIndex + direction.indexChange[1]
    };
  }

  public static Direction reverseDirection(Direction direction) {
    return switch (direction) {
      case UP -> Direction.DOWN;
      case DOWN -> Direction.UP;
      case LEFT -> Direction.RIGHT;
      case RIGHT -> Direction.LEFT;
    };
  }

  public static int getNumberAtPosition(List<List<String>> lines, int[] position) {
    int lineIndex = position[0];
    int characterIndex = position[1];
    return Integer.parseInt(lines.get(lineIndex).get(characterIndex));
  }

  /**
   * Returns a set of valid directions that can be taken from the given position, these directions
   * are guranteed to have at least 1 valid increment in that direction
   */
  public static Set<Direction> getValidDirectionsFromPosition(
      List<List<String>> lines, int[] position) {
    Set<Direction> set = new HashSet<>();
    int numberAtOriginalPosition = getNumberAtPosition(lines, position);
    for (Direction direction : Direction.values()) {

      int[] newPosition = getNewPositionInDirection(position, direction);

      if (!isPositionValid(lines, newPosition, numberAtOriginalPosition)) {
        continue;
      }

      // Valid
      set.add(direction);
    }

    return set;
  }

  enum Direction {
    UP(new int[] {-1, 0}),
    RIGHT(new int[] {0, 1}),
    DOWN(new int[] {1, 0}),
    LEFT(new int[] {0, -1});

    private int[] indexChange;

    private Direction(int[] indexChange) {
      this.indexChange = indexChange;
    }
  }

  public static class Path {
    public final int id;
    public final int[] position;
    public final Set<Direction> directions;

    public Path(int id, int[] position, Set<Direction> directions) {
      this.id = id;
      this.position = position;
      this.directions = directions;
    }

    public String toString() {
      return String.format("\n%s\n%s\n", Arrays.toString(position), directions.toString());
    }
  }

  public static class PathEnd {
    private final int pathId;
    private final List<Integer> pathEndLocations;

    public PathEnd(int pathId, List<Integer> pathEndLocations) {
      this.pathId = pathId;
      this.pathEndLocations = pathEndLocations;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.pathId, this.pathEndLocations);
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) return true;
      if (other == null) return false;
      if (!(other instanceof PathEnd)) return false;
      PathEnd otherPathEnd = (PathEnd) other;
      return this.pathId == otherPathEnd.pathId
          && Objects.equals(this.pathEndLocations, otherPathEnd.pathEndLocations);
    }
  }

  public static List<List<String>> linesDeepCopy(List<List<String>> lines) {
    List<List<String>> copyLines = new ArrayList<>();
    for (List<String> line : lines) {
      List<String> copyCharacters = new ArrayList<>();
      for (String character : line) {
        copyCharacters.add(new String(character));
      }
      copyLines.add(copyCharacters);
    }
    return copyLines;
  }

  public static void printLines(List<List<String>> lines, int[] position) {
    List<List<String>> linesCopy = linesDeepCopy(lines);
    linesCopy.get(position[0]).set(position[1], "*");
    for (List<String> line : linesCopy) {
      System.out.println(line);
    }
  }

  public static boolean isPositionValid(
      List<List<String>> lines, int[] newPosition, int previousNumber) {
    return isPositionInBounds(lines, newPosition)
        && isPositionValueOneGreater(lines, newPosition, previousNumber);
  }

  public static boolean hasTrailEndedSuccessfully(
      List<List<String>> lines,
      int currentNumber,
      int[] currentPosition,
      Direction currentDirection) {
    int[] previousPosition =
        getNewPositionInDirection(currentPosition, reverseDirection(currentDirection));
    int previousNumber = getNumberAtPosition(lines, previousPosition);
    return currentNumber == 9 && previousNumber == 8;
  }

  public static void travelPath(
      Path path, Stack<Path> pathStack, Set<PathEnd> pathEndCount, List<List<String>> lines) {

    for (Direction direction : path.directions) {
      // NOTE:
      // Any direction inside the paths directions set has already been checked
      // to be valid (in bounds and valid incrememnt). This means that the first
      // position in this direction is valid.
      int[] newPosition = getNewPositionInDirection(path.position, direction);
      int previousNumber = -1;
      while (isPositionValid(lines, newPosition, previousNumber)) {
        int numberAtPosition = getNumberAtPosition(lines, newPosition);

        if (hasTrailEndedSuccessfully(lines, numberAtPosition, newPosition, direction)) {
          pathEndCount.add(new PathEnd(path.id, List.of(newPosition[0], newPosition[1])));
        }

        // Given the position and value are valid, get the new valid directions that we
        // can go to from here (left, right, up down)
        Set<Direction> newPositionValidDirections =
            getValidDirectionsFromPosition(lines, newPosition);

        // If there is a new path away from current position add to visit later
        if (!newPositionValidDirections.isEmpty()) {
          pathStack.push(new Path(path.id, newPosition, newPositionValidDirections));
        }

        newPosition = getNewPositionInDirection(newPosition, direction);

        try {
          // NOTE:
          // For viewing the printed lines like a maze
          // printLines(lines, newPosition);
          // Thread.sleep(1000);
        } catch (Exception ex) {

        }

        previousNumber = numberAtPosition;
      }
    }
  }

  public static void main(String[] args) throws Exception {
    List<List<String>> lines = getLinesFromFilePath("input");
    Stack<Path> pathStack = new Stack<>();
    Set<PathEnd> pathEnds = new HashSet<>();

    // NOTE:
    // All original trail heads and sub-trails created from this trail will
    // contain the same id allowing the count to be correct for multiple of the same
    // trails ending at the same trail end
    int idIncrement = 0;

    List<int[]> positionOfTrailHeads = getAllTrainHeadIndices(lines);

    for (int[] positionOfTrailHead : positionOfTrailHeads) {

      Set<Direction> validDirections = getValidDirectionsFromPosition(lines, positionOfTrailHead);
      pathStack.push(new Path(idIncrement++, positionOfTrailHead, validDirections));

      while (!pathStack.isEmpty()) {
        Path currentPath = pathStack.pop();
        travelPath(currentPath, pathStack, pathEnds, lines);
      }
    }

    System.out.println(pathEnds.size());
  }
}
