import java.util.*;
import java.io.*;

public class DayTwoPartOne {
	public static void main(String[] args) throws Exception {
		List<List<Integer>> reports;

		try (FileReader fr = new FileReader("./input");
				BufferedReader br = new BufferedReader(fr)) {
			reports = br.lines().map(r -> Arrays.stream(r.split(" ")).map(Integer::valueOf).toList())
					.toList();
		}

		long numberOfSafeReports = reports.stream()
				.filter(DayTwoPartOne::reportTest)
				.count();

		System.out.println(numberOfSafeReports);
	}

	private static boolean reportTest(List<Integer> report) {
		boolean increasing = false;
		for (int i = 1; i < report.size(); i++) {
			if (i == 1 && report.get(i - 1) < report.get(i)) {
				increasing = true;
			}

			// Ensure increasing or decreasing still
			if ((increasing && report.get(i) < report.get(i - 1))
					|| !increasing && report.get(i) > report.get(i - 1)) {
				return false;
			}

			// Check difference
			if (!isDifferenceValid(report.get(i - 1), report.get(i))) {
				return false;
			}

		}
		return true;
	}

	private static boolean isDifferenceValid(int v1, int v2) {
		return Math.abs(v1 - v2) >= 1 && Math.abs(v1 - v2) <= 3 && v1 != v2;
	}
}
