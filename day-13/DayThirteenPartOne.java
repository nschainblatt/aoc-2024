import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DayThirteenPartOne {

  public record Coordinate(int x, int y) {}

  public static class Button {
    public final String label;
    public final int xDifference;
    public final int yDifference;

    public Button(String label, int xDifference, int yDifference) {
      this.label = label;
      this.xDifference = xDifference;
      this.yDifference = yDifference;
    }

    @Override
    public String toString() {
      return String.format("%s: X+%d, Y+%d", label, xDifference, yDifference);
    }
  }

  public static class ClawMachine {
    public Button buttonA;
    public Button buttonB;
    public Coordinate prizeLocation;

    public boolean isEmpty() {
      return buttonA == null && buttonB == null && prizeLocation == null;
    }

    @Override
    public String toString() {
      return String.format(
          "%s\n%s\nPrize: X=%d, Y=%d", buttonA, buttonB, prizeLocation.x(), prizeLocation.y());
    }
  }

  public static List<String> getLinesFromInput(String filePath) {
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      return br.lines().toList();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static List<ClawMachine> getClawMachinesFromLines(List<String> lines) {
    List<ClawMachine> clawMachines = new ArrayList<>();

    int machineLine = 1;
    ClawMachine current = new ClawMachine();
    for (String line : lines) {
      if (line.isEmpty()) {
        machineLine = 1;
        if (!current.isEmpty()) {
          clawMachines.add(current);
        } else {
          System.out.println("Claw is empty");
        }
        current = new ClawMachine();
        continue;
      }
      String[] lineParts = line.split(":");
      if (machineLine == 1 || machineLine == 2) {
        String buttonLabel = lineParts[0];
        int firstPlus =
            lineParts[1].indexOf('+') + 1; // Adding one to get the index of the first digit
        int firstComma = lineParts[1].indexOf(',');
        int xDifference = Integer.parseInt(lineParts[1].substring(firstPlus, firstComma));
        int yDifference =
            Integer.parseInt(lineParts[1].substring(lineParts[1].indexOf('+', firstPlus + 1)));
        Button button = new Button(lineParts[0], xDifference, yDifference);
        if (button.label.endsWith("A")) {
          current.buttonA = button;
        } else {
          current.buttonB = button;
        }

      } else {
        int firstEquals =
            lineParts[1].indexOf('=') + 1; // Adding one to get the index of the first digit
        int firstComma = lineParts[1].indexOf(',');
        int x = Integer.parseInt(lineParts[1].substring(firstEquals, firstComma));
        int y =
            Integer.parseInt(
                lineParts[1].substring(lineParts[1].indexOf('=', firstEquals + 1) + 1));
        current.prizeLocation = new Coordinate(x, y);
      }
      machineLine++;
    }
    clawMachines.add(current);

    return clawMachines;
  }

  public static void main(String[] args) {
    List<String> lines = getLinesFromInput("sampleInput");
    List<ClawMachine> clawMachines = getClawMachinesFromLines(lines);
    for (ClawMachine claw : clawMachines) {
      System.out.println(claw + "\n");
    }
  }
}
