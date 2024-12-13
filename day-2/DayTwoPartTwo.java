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
				.filter(r -> originalReportTest(r, 0))
				.count();

		System.out.println(numberOfSafeReports);
	}

	private static boolean originalReportTest(List<Integer> report, int faultCounter) {
		if (report.size() < 2)
			return true;

		float signdiff = Math.signum(report.get(1) - report.get(0));
		for (int i = 1; i < report.size(); i++) {
			if (faultCounter > 1) {
				return false;
			}

			if (!isSafe(report.get(i), report.get(i - 1), signdiff)) {
				faultCounter++;
				for (int j = 0; j < report.size(); j++) {
					List<Integer> modifiedReport = new ArrayList<>(report);
					modifiedReport.remove(j);
					boolean validAfterRemoval = modifiedReportTest(modifiedReport);
					if (validAfterRemoval) {
						return true;
					}
				}
				return false; // by this time no removal made the report safe.
			}
		}

		return true;
	}

	private static boolean modifiedReportTest(List<Integer> report) {
		if (report.size() < 2)
			return true;
		float signdiff = Math.signum(report.get(1) - report.get(0));
		for (int i = 1; i < report.size(); i++) {
			if (!isSafe(report.get(i), report.get(i - 1), signdiff))
				return false;
		}
		return true;
	}

	private static boolean isSafe(int currentNumber, int previousNumber, float signdiff) {
		int absDifference = Math.abs(currentNumber - previousNumber);
		boolean validDifference = absDifference >= 1 && absDifference <= 3;
		boolean validSign = Math.signum(currentNumber - previousNumber) == Math.signum(signdiff);
		return validDifference && validSign;
	}
}
