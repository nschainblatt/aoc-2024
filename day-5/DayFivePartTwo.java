import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DayFivePartTwo {
	public static void getMiddleNumFromLineTest() {
		List<Integer> testList = List.of(1, 2, 3, 4, 5);
		int middleNum = DayFivePartTwo.getMiddleNumFromLine(testList);
		assert (3 == middleNum);

		testList = List.of(1, 2, 3, 4);
		middleNum = DayFivePartTwo.getMiddleNumFromLine(testList);
		assert (2 == middleNum);
	}

	public static void swap(ArrayList<Integer> line, int i, int j) {
		Integer itemp = line.get(i);
		Integer jtemp = line.get(j);
		line.remove(i);
		line.add(i, jtemp);
		line.remove(j);
		line.add(j, itemp);
	}

	public static void testSwapMethod() {
		ArrayList<Integer> testList = new ArrayList<>(List.of(1, 2, 3, 4, 5));
		System.out.println(testList);
		swap(testList, 2, 3);
		System.out.println(testList);
		assert (testList.equals(new ArrayList<Integer>(List.of(1, 2, 4, 3, 5))));
	}

	public static void main(String[] args) {
		List<String> lines = getLinesFromInput("input");
		List<String> ruleLines = getRuleLinesFromLines(lines);
		List<List<Integer>> updateLines = getUpdateLinesFromLines(lines);
		Set<List<Integer>> ruleSet = getRuleSetFromRuleLines(ruleLines);
		List<ArrayList<Integer>> incorrectUpdateLines = getIncorrectUpdateLinesFromUpdateLines(ruleSet,
				updateLines);
		System.out.println(incorrectUpdateLines);
		testSwapMethod();
		int sum = getSumFromCorrectedIncorrectLines(ruleSet, incorrectUpdateLines);
		System.out.println(sum);
	}

	public static List<String> getLinesFromInput(String fileName) {
		List<String> lines = new ArrayList<>();
		try (FileReader fr = new FileReader(fileName); BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines().toList();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
		return lines;
	}

	public static List<String> getRuleLinesFromLines(List<String> lines) {
		List<String> ruleLines = new ArrayList<>();

		for (String line : lines) {
			if (line.isBlank()) {
				break;
			}
			ruleLines.add(line);
		}

		return ruleLines;
	}

	public static List<List<Integer>> getUpdateLinesFromLines(List<String> lines) {
		List<List<Integer>> updateLines = new ArrayList<>();
		boolean start = false;
		for (String line : lines) {
			if (line.isBlank()) {
				start = true;
				continue;
			}

			if (!start) {
				continue;
			}
			updateLines.add(Arrays.stream(line.split(",")).mapToInt(Integer::parseInt).boxed().toList());
		}

		return updateLines;
	}

	public static List<ArrayList<Integer>> getIncorrectUpdateLinesFromUpdateLines(Set<List<Integer>> ruleSet,
			List<List<Integer>> updateLines) {
		List<ArrayList<Integer>> incorrectUpdateLines = new ArrayList<>();

		for (List<Integer> line : updateLines) {
			boolean valid = true;

			for (int i = 0; i < line.size(); i++) {
				if (!valid) {
					break;
				}

				for (int j = i + 1; j < line.size(); j++) {
					if (!ruleSet.contains(List.of(line.get(i), line.get(j)))) {
						valid = false;
						break;
					}

				}
			}

			if (!valid) {
				incorrectUpdateLines.add(new ArrayList<>(line));
			}

		}

		return incorrectUpdateLines;
	}

	public static Set<List<Integer>> getRuleSetFromRuleLines(List<String> ruleLines) {
		Set<List<Integer>> ruleSet = new HashSet<>();

		for (String line : ruleLines) {
			int left = Integer.parseInt(line.split("\\|")[0]);
			int right = Integer.parseInt(line.split("\\|")[1]);
			ruleSet.add(List.of(left, right));
		}

		return ruleSet;
	}

	public static int getSumFromCorrectedIncorrectLines(Set<List<Integer>> ruleSet,
			List<ArrayList<Integer>> incorrectUpdateLines) {
		int sum = 0;

		for (ArrayList<Integer> line : incorrectUpdateLines) {

			for (int i = 0; i < line.size(); i++) {

				// 97,75,47,29,13

				for (int j = i + 1; j < line.size(); j++) {
					while (!ruleSet.contains(List.of(line.get(i), line.get(j)))) {
						swap(line, i, j);
					}
				}
			}

			System.out.println(line);
			sum += getMiddleNumFromLine(line);

		}

		return sum;
	}

	public static Integer getMiddleNumFromLine(List<Integer> line) {
		return line.get((line.size() - 1) / 2);
	}
}
