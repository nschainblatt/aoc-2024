import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DayElevenPartTwo {

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

  public static class MyThread extends Thread {

    private final long stone;
    private final int timesToBlink;
    public long count;
    public Set<Long> blinkTracker = new HashSet<>();

    public MyThread(long stone, int timesToBlink) {
      this.stone = stone;
      this.timesToBlink = timesToBlink;
    }

    @Override
    public void run() {
      // System.out.println("Thread: " + Thread.currentThread().getId());
      this.count = blinkNTimes(stone, timesToBlink, this);
    }
  }

  public static long blinkNTimes(List<Long> originalStones, int n) {
    long count = 0;

    List<MyThread> threads = new ArrayList<>();
    for (long stone : originalStones) {
      MyThread thread = new MyThread(stone, n);
      thread.start();
      System.out.println("Thread: " + thread.getId() + " has started processing it's original stone");
      threads.add(thread);
    }

    try {
      for (MyThread thread : threads) {
        thread.join();
        System.out.println("Thread: " + thread.getId() + " has finished it's original stone!");
        count += thread.count;
      }
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }

    return count;
  }

  public static long blinkNTimes(long stone, int timesToBlink, MyThread thread) {

    if (timesToBlink == 0) {
      return 1L;
    }

    if(thread.blinkTracker.add((long) timesToBlink) && thread.getId() == 33) {
      System.out.printf("THREAD ID: %d | ORIGINAL STONE: %d | STONE: %d | TIMES TO BLINK COUNTER: %d \n", thread.getId(), thread.stone, stone, timesToBlink);
    }
    if(timesToBlink == 1 && thread.getId() == 33) {
      System.out.println(
        "last blink: " + stone
      );
    }

    List<Long> newStones = getStonesFromStone(stone);
    long count = 0;
    for (long newStone : newStones) {
      count += blinkNTimes(newStone, timesToBlink - 1, thread);
    }

    return count;
  }

  public static void main(String[] args) {
    List<Long> originalStones = getNumbersFromInput("./input");
    System.out.println(blinkNTimes(originalStones, 5));
  }
}
