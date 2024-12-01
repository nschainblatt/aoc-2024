import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DayOnePartOne {
	public static void main(String[] args) {
		List<String> lines = new ArrayList<>();

		try (FileReader fr = new FileReader("./input"); BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines().toList();
		} catch (Exception ex) {
			System.exit(1);
		}

		PriorityQueue<Integer> leftQueue = new PriorityQueue<>();
		PriorityQueue<Integer> rightQueue = new PriorityQueue<>();

		for (String line : lines) {
			String[] halves = line.split("   ");
			leftQueue.offer(Integer.valueOf(halves[0]));
			rightQueue.offer(Integer.valueOf(halves[1]));
		}

		long sum = 0;
		while (!leftQueue.isEmpty()) {
			sum += Math.abs(leftQueue.poll() - rightQueue.poll());
		}

		System.out.println(sum);
	}

	private static void print(List<String> lines) {
		lines.forEach(System.out::println);
	}
}
