import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class DaySevenPartOne {

	enum Operator {
		ADD, MULTIPLY
	}

	public static void main(String args[]) {
		List<String> lines = new ArrayList<>();
		try (FileReader fr = new FileReader("./input"); BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines().toList();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		long totalSum = 0;
		for (String line : lines) {
			long testValue = Long.parseLong(line.split(": ")[0]);
			Integer[] operands = Arrays.stream(line.split(": ")[1].split(" ")).mapToInt(Integer::parseInt).boxed().toList().toArray(new Integer[0]);

			String version = null;
			for (int i = 0; i < Math.pow(Operator.values().length, operands.length - 1); i++) {
				version = Integer.toBinaryString(i);
				int numberOfSpacesForOperators = operands.length - 1;
				int width = numberOfSpacesForOperators - version.length();
				String binary = "0".repeat(width).concat(version);
				Operator[] operators = mapBinaryToOperators(binary);
				long sum = applyOperatorsToOperands(operators, operands);
				if (sum == testValue) {
					totalSum += sum;
					break;
				}

				if (totalSum < 0 || sum < 0) {
					System.out.println(line);
					throw new IllegalArgumentException("Overflow");
				}
			}

		}


		System.out.println(totalSum);
	}

	/** Maps a string like 001 to ['+', '+', '*'] */
	public static Operator[] mapBinaryToOperators(String binaryString) {
		char[] characters = new char[binaryString.length()];
		binaryString.getChars(0, binaryString.length(), characters, 0);

		Operator[] operators = new Operator[binaryString.length()];

		int index = 0;
		for (char bit : characters) {
			if (bit == '0') {
				operators[index] = Operator.ADD;
			} else {
				operators[index] = Operator.MULTIPLY;
			}
			index++;
		}

		return operators;
	} 

	public static long applyOperatorsToOperands(Operator[] operators, Integer[] operands) {
		if (operands.length < 2) {
			throw new IllegalArgumentException("At least two operands are required");
		}
		if (operators.length < 1) {
			throw new IllegalArgumentException("At least one operator is required");
		}

		long sum = operands[0];
		for (int i = 1; i < operands.length; i++) {
			
			int operand = operands[i];
			Operator operator = operators[i - 1];
			if (Operator.ADD.equals(operator)) {
				sum = sum + operand;
			} else {
				sum = sum * operand;
			}
		}


		return sum;
	}

}
