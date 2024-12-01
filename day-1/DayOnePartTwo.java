import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayOnePartTwo {
	public static void main(String[] args) {
		List<String> lines = new ArrayList<>();

		try (FileReader fr = new FileReader("./input"); BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines().toList();
		} catch (Exception ex) {
			System.exit(1);
		}

		List<Integer> leftNumbers = new ArrayList<>();
		Map<Integer, Integer> rightNumbersCount = new HashMap<>();

		for (String line : lines) {
			String[] halves = line.split("   ");
			leftNumbers.add(Integer.valueOf(halves[0]));
			rightNumbersCount.merge(Integer.valueOf(halves[1]), 1, (v, d) -> v + d);
		}

		long sum = 0;
		for (Integer leftNum : leftNumbers) {
			sum += leftNum * rightNumbersCount.getOrDefault(leftNum, 0);

		}

		System.out.println(sum);
	}

	private static void print(List<String> lines) {
		lines.forEach(System.out::println);
	}
}
