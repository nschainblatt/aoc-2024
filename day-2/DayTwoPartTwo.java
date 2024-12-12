import java.util.*;
import java.io.*;

public class DayTwoPartTwo {
	public static void main(String[] args) throws Exception {
		List<List<Integer>> reports;

		try (FileReader fr = new FileReader("./input");
				BufferedReader br = new BufferedReader(fr)) {
			reports = br.lines().map(r -> Arrays.stream(r.split(" ")).map(Integer::valueOf).toList())
					.toList();
		}

		long numberOfSafeReports = reports.stream()
				.filter(r -> reportTest(r, 0))
				.count();

		System.out.println(numberOfSafeReports);
	}

	private static boolean reportTest(List<Integer> report, int faultCounter) {
		boolean increasing = false;
		for (int i = 1; i < report.size(); i++) {
			if (faultCounter > 1) {
				return false;
			}
			if (i == 1 && report.get(0) < report.get(i))
				increasing = true;

			if (!isLevelPairValid(report.get(i), report.get(i - 1), increasing)) {
				// increment fault counter, try removing each number in the report until it
				// passes or we reach the end and then return false
				faultCounter = faultCounter + 1;
				for (int j = 0; j < report.size(); j++) {
					List<Integer> modifiedReport = new ArrayList<>(report);
					modifiedReport.remove(j);
					boolean validAfterRemoval = singleTest(modifiedReport);
					if (validAfterRemoval) {
						return true;
					}
				}
				return false;
			}
		}

		return true;
	}

	private static boolean singleTest(List<Integer> report) {
		boolean increasing = false;
		for (int i = 1; i < report.size(); i++) {
			if (i == 1 && report.get(0) < report.get(i))
				increasing = true;
			if (!isLevelPairValid(report.get(i), report.get(i - 1), increasing))
				return false;
		}
		return true;
	}

	// Difference must be between 1 and 3 inclusive, and the level must contain the
	// same increasing boolean value (either still increasing or still decreasing)
	private static boolean isLevelPairValid(int currentNumber, int previousNumber, boolean increasing) {
		int difference = Math.abs(currentNumber - previousNumber);
		boolean validDifference = difference >= 1 && difference <= 3;
		boolean validIncrement = ((increasing && currentNumber > previousNumber)
				|| !increasing && currentNumber < previousNumber);
		return validDifference && validIncrement;
	}
}
