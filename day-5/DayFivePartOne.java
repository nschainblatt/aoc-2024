import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DayFivePartOne {
	public static void getMiddleNumFromLineTest() {
		List<Integer> testList = List.of(1, 2, 3, 4, 5);
		int middleNum = DayFivePartOne.getMiddleNumFromLine(testList);
		assert (3 == middleNum);

		testList = List.of(1, 2, 3, 4);
		middleNum = DayFivePartOne.getMiddleNumFromLine(testList);
		assert (2 == middleNum);
	}

	public static void main(String[] args) {
		List<String> lines = getLinesFromInput("input");
		List<String> ruleLines = getRuleLinesFromLines(lines);
		List<List<Integer>> updateLines = getUpdateLinesFromLines(lines);
		Set<List<Integer>> ruleSet = getRuleSetFromRuleLines(ruleLines);
		// getMiddleNumFromLineTest();
		int sum = getSumFromInput(ruleSet, updateLines);
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

	public static Set<List<Integer>> getRuleSetFromRuleLines(List<String> ruleLines) {
		Set<List<Integer>> ruleSet = new HashSet<>();

		for (String line : ruleLines) {
			int left = Integer.parseInt(line.split("\\|")[0]);
			int right = Integer.parseInt(line.split("\\|")[1]);
			ruleSet.add(List.of(left, right));
		}

		return ruleSet;
	}

	public static int getSumFromInput(Set<List<Integer>> ruleSet, List<List<Integer>> updateLines) {
		int sum = 0;

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

			if (valid) {
				sum += getMiddleNumFromLine(line);
			}

		}

		return sum;
	}

	public static Integer getMiddleNumFromLine(List<Integer> line) {
		return line.get((line.size() - 1) / 2);
	}
}
