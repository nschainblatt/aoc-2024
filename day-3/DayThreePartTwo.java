import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class DayThreePartTwo {
	private static boolean enabled = true;

	public static void main(String[] args) throws Exception {
		try (FileReader fr = new FileReader("./input");
				BufferedReader br = new BufferedReader(fr)) {
			long sum = br.lines().mapToLong(DayThreePartTwo::lineSum).sum();
			System.out.println(sum);
		}

	}

	private static long lineSum(String line) {
		long sum = 0L;
		Pattern p = Pattern.compile("(mul\\((\\d){1,3},(\\d){1,3}\\))|(do\\(\\))|(don\\'t\\(\\))");
		Matcher m = p.matcher(line);

		while (m.find()) {
			String mulOccurrence = m.group();
			if (mulOccurrence.matches("mul\\((\\d){1,3},(\\d){1,3}\\)") && enabled) {
				int indexOfOpeningParenthesis = mulOccurrence.indexOf("(");
				int indexOfComma = mulOccurrence.indexOf(",");
				int firstNumber = Integer
						.parseInt(mulOccurrence.substring(indexOfOpeningParenthesis + 1,
								indexOfComma));
				int secondNumber = Integer.parseInt(
						mulOccurrence.substring(indexOfComma + 1, mulOccurrence.length() - 1));

				sum += firstNumber * secondNumber;

			} else if (mulOccurrence.matches("do\\(\\)")) {
				enabled = true;

			} else {
				enabled = false;
			}
		}

		return sum;
	}
}
