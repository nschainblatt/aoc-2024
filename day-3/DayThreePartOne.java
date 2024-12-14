import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class DayThreePartOne {
	public static void main(String[] args) throws Exception {
		try (FileReader fr = new FileReader("./input");
				BufferedReader br = new BufferedReader(fr)) {
			long sum = br.lines().mapToLong(DayThreePartOne::lineSum).sum();
			System.out.println(sum);
		}

	}

	private static long lineSum(String line) {
		long sum = 0L;
		Pattern p = Pattern.compile("mul\\((\\d){1,3},(\\d){1,3}\\)");
		Matcher m = p.matcher(line);

		while (m.find()) {
			String mulOccurrence = m.group();
			int indexOfOpeningParenthesis = mulOccurrence.indexOf("(");
			int indexOfComma = mulOccurrence.indexOf(",");
			int firstNumber = Integer
					.parseInt(mulOccurrence.substring(indexOfOpeningParenthesis + 1, indexOfComma));
			int secondNumber = Integer.parseInt(
					mulOccurrence.substring(indexOfComma + 1, mulOccurrence.length() - 1));

			sum += firstNumber * secondNumber;
		}

		return sum;
	}
}
