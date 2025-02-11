import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DayElevenPartOne {

  public record StoneHalves(long left, long right) {
    @Override
    public String toString() {
      return String.format("Left: %d, Right: %d", left, right);
    }
  }

  public static List<Long> getNumbersFromInput(String pathToInput) {
    try (BufferedReader br = new BufferedReader(new FileReader(pathToInput))) {
      return Arrays.stream(br.readLine().split(" ")).mapToLong(Long::parseLong).boxed().toList();
    } catch (IOException ex) {
      ex.printStackTrace();
      throw new RuntimeException(ex);
    }
  }

  public static int getNumberOfDigits(long stone) {
    return (int) Math.floor(Math.log10(stone)) + 1;
  }

  public static StoneHalves splitStoneInHalf(long stone, int stoneDigits) {
    long denominator = (long) Math.pow(10, stoneDigits / 2);
    long left = stone / denominator;
    long right = stone % denominator;
    return new StoneHalves(left, right);
  }

  public static boolean isEven(int n) {
    return n % 2 == 0;
  }

  public static List<Long> getStonesFromStone(long n) {
    if (n == 0) {
      return List.of(1L);
    }

    int nDigitCount = getNumberOfDigits(n);
    if (isEven(nDigitCount)) {
      StoneHalves halves = splitStoneInHalf(n, nDigitCount);
      return List.of(halves.left(), halves.right());
    }

    return List.of(n * 2024L);
  }

  public static List<Long> blinkTwentyFiveTimes(List<Long> originalStones) {
    List<Long> nextStones = originalStones;
    for (int i = 0; i < 25; i++) {
      List<Long> currentStones = new ArrayList<>();

      for (Long stone : nextStones) {
        currentStones.addAll(getStonesFromStone(stone));
      }

      nextStones = currentStones;
    }

    return nextStones;
  }

  public static void main(String[] args) {
    List<Long> originalStones = getNumbersFromInput("./input");
    List<Long> finalStones = blinkTwentyFiveTimes(originalStones);
    System.out.println(finalStones.size());
  }
}
