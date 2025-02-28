import java.io.*;
import java.util.*;

public class DayTwelvePartTwo {

  public static List<List<String>> getLinesFromInput(String filePath) {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      return br.lines()
          .map(line -> line.chars().mapToObj(i -> Character.toString((char) i)).toList())
          .toList();

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public record Location(int lineIndex, int characterIndex) {
    public Location applyDirection(Direction direction) {
      int newLineIndex = direction.lineIndexChange + this.lineIndex();
      int newCharacterIndex = direction.characterIndexChange + this.characterIndex();
      return new Location(newLineIndex, newCharacterIndex);
    }
  }

  // NOTE:
  // stack and set should be same size at all times
  public static class SetStack<T> {
    private final Set<T> set = new HashSet<>();
    private final Stack<T> stack = new Stack<>();

    public T pop() {
      if (stack.isEmpty()) {
        throw new EmptyStackException();
      }
      T item = stack.pop();
      set.remove(item);
      return item;
    }

    public boolean push(T item) {
      boolean success = set.add(item);
      if (success) {
        stack.push(item);
      }
      return success;
    }

    public boolean isEmpty() {
      return stack.isEmpty();
    }
  }

  public static class Region {
    public final String letter;
    public long area = 0L;
    public long perimeter = 0L;
    public final Set<Location> visited = new HashSet<>();

    public Region(String letter) {
      this.letter = letter;
    }

    public long getPrice() {
      return area * perimeter;
    }

    @Override
    public String toString() {
      return String.format("Region: %s | Area: %d | Perimeter: %d", letter, area, perimeter);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(letter)
          + Objects.hashCode(area)
          + Objects.hashCode(perimeter)
          + visited.hashCode();
    }

    @Override
    public boolean equals(Object other) {
      if (other == null) return false;
      if (!(other instanceof Region)) return false;
      Region otherRegion = (Region) other;
      if (letter == null && otherRegion.letter != null) return false;
      boolean letterEquals =
          (letter == null && otherRegion.letter == null) || letter.equals(otherRegion.letter);
      return letterEquals
          && area == otherRegion.area
          && perimeter == otherRegion.perimeter
          && visited.equals(otherRegion.visited);
    }
  }

  enum Direction {
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1);

    private int lineIndexChange;
    private int characterIndexChange;

    private Direction(int lineIndexChange, int characterIndexChange) {
      this.lineIndexChange = lineIndexChange;
      this.characterIndexChange = characterIndexChange;
    }
  }

  public static boolean inBounds(List<List<String>> lines, Location location) {
    return (0 <= location.lineIndex() && location.lineIndex() < lines.size())
        && (0 <= location.characterIndex()
            && location.characterIndex() < lines.get(location.lineIndex()).size());
  }

  public static Location getNeighborInDirection(
      List<List<String>> lines, String letter, Location location, Direction direction) {
    Location locationAbove = location.applyDirection(direction);
    if (inBounds(lines, locationAbove)
        && letter.equals(lines.get(locationAbove.lineIndex()).get(locationAbove.characterIndex))) {
      return locationAbove;
    }
    return null;
  }

  /**
   * Calculates an individual locations perimeter based on the letter, assumes the location for the
   * letter is valid and in bounds
   */
  public static int getPerimeterFromLocation(
      List<List<String>> lines, String letter, Location location) {

    // Look in all directions and add to the sub-perimeter the amount of locations
    // that are either out of bounds OR not the same letter, this way we know
    // that this location is an outside location of the region, and how many times
    // to add to the sub-perimeter

    int locationPerimeter = 0;

    // Check each direction to see if the current 'location' parameter is on the
    // edge of the current region.
    // For example, if the current region is only one plot then each direction will
    // have no neighbors which equals to a perimeter of 4.
    for (Direction direction : Direction.values()) {
      Location neighbor = getNeighborInDirection(lines, letter, location, direction);
      if (neighbor == null) {
        if (letter.equals("B")) {
          // System.out.printf(
          //     "Perimeter found | Letter: %s | Direction: %s | Location: %s\n",
          //     letter, direction.name(), location);
        }
        locationPerimeter++;
      }
    }

    return locationPerimeter;
  }

  // A straight fence of the Region
  public static class Side {

    public final Set<Location> locations = new HashSet<>();
  }

  public static int getSidesFromRegion(Region region) {
    int sides = 0;

    // TODO:
    //
    // Go over every plot in the region
    // For every plot in the region go in every direction
    //
    //
    // for each plot in the region, go in both of the axis directions, each axis will get its own potential side if one exists
    //
    // only increment the side count when the current side ends, the current side ends when a foreign plot is encounted in any direction or 
    // the foreigh plot is out of bounds.
    //
    //
    //

    for (Location location : region.visited) {

      // Go up & down

      // Go left & right

    }

    return sides;
  }

  public static Region getRegion(List<List<String>> lines, Location startingLocation) {
    String letter = lines.get(startingLocation.lineIndex()).get(startingLocation.characterIndex());
    Region currentRegion = new Region(letter);
    // System.out.println("Current Region: " + currentRegion.letter);
    SetStack<Location> neighbors = new SetStack<>();
    neighbors.push(startingLocation);

    while (!neighbors.isEmpty()) {
      Location current = neighbors.pop();
      currentRegion.visited.add(current);
      currentRegion.perimeter += getPerimeterFromLocation(lines, currentRegion.letter, current);

      // Look all directions for neighbors
      for (Direction direction : Direction.values()) {
        Location neighbor = getNeighborInDirection(lines, currentRegion.letter, current, direction);
        if (neighbor != null && !currentRegion.visited.contains(neighbor)) {
          neighbors.push(neighbor);
        }
      }
    }

    currentRegion.area = currentRegion.visited.size();

    // System.out.println(currentRegion.visited.size());
    // System.out.println(currentRegion);
    return currentRegion;
  }

  public static void main(String[] args) {
    List<List<String>> lines = getLinesFromInput("input");

    // To keep track of traversed regions
    Set<Region> regions = new HashSet<>();

    // To use to determine if a given location (in any region) has already been
    // visited.
    Set<Location> visitedLocations = new HashSet<>();

    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(i).size(); j++) {
        Location current = new Location(i, j);
        if (visitedLocations.contains(current)) {
          continue;
        }

        visitedLocations.add(current);
        // NOTE:
        // The first index/letter reached in a region will evaluate the entire region,
        // then subsequent iterations of this loop
        // will still be inside the region most likely (because were just doing a for every
        // character in line loop) but will be skipped because the
        // location will have already been marked as visited when the original region was completed.
        // This is expected.
        Region region = getRegion(lines, current);
        regions.add(region);
        visitedLocations.addAll(region.visited);
      }
    }

    // System.out.println("Region count: " + regions.size());
    long sum = 0L;
    for (Region region : regions) {
      sum += region.getPrice();
    }

    System.out.println(sum);
  }
}
