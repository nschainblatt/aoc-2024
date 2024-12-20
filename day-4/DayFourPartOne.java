import java.util.*;
import java.io.*;

public class DayFourPartOne {
	private static final String WORD = "XMAS";
	private static final char END_OF_WORD_CHARACTER = '\u0000';

	public static void main(String[] args) throws Exception {
		List<List<Character>> lines;

		try (FileReader fr = new FileReader("./input"); // 2434
				BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines()
					.map(line -> Arrays.stream(line.split(""))
							.map(c -> c.charAt(0)).toList())
					.toList();
		}

		long totalXmasCount = 0;
		for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
			List<Character> line = lines.get(lineIndex);
			for (int charIndex = 0; charIndex < lines.get(lineIndex).size(); charIndex++) {
				Character currentChar = line.get(charIndex);
				if (currentChar.equals(WORD.charAt(0))) {
					// Check all directions and count up all of the possible XMAS's that start with
					// this character

					// Check upwards
					if (foundUpwards(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}

					// Check downwards
					if (foundDownwards(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}

					// Check left
					if (foundRight(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}

					// Check right
					if (foundLeft(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}

					// Check up-right-diagonal
					if (foundUpRight(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}

					// Check up-left-diagonal
					if (foundUpLeft(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}

					// Check down-right-diagonal
					if (foundBottomRight(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}

					// Check down-left-diagonal
					if (foundBottomLeft(lineIndex, charIndex, lines)) {
						totalXmasCount++;
					}
				}
			}

		}

		System.out.println(totalXmasCount);
	}

	private static char getNextValidCharacter(char currentCharacter) {
		return switch (currentCharacter) {
			case 'X' -> 'M';
			case 'M' -> 'A';
			case 'A' -> 'S';
			case 'S' -> END_OF_WORD_CHARACTER;
			default -> throw new IllegalArgumentException("ILLEGAL CHARACTER: " + currentCharacter);
		};
	}

	private static boolean foundUpwards(int lineIndex, int charIndex, List<List<Character>> lines) {
		if (lineIndex < WORD.length() - 1) {
			// There aren't enough rows above to make up the word XMAS
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentLineIndex = lineIndex - 1; // Get the above line initially
		while (true) {
			currentChar = lines.get(currentLineIndex).get(charIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			// Reached the end of the word
			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentLineIndex--;
		}

		return true;
	}

	private static boolean foundDownwards(int lineIndex, int charIndex, List<List<Character>> lines) {
		if (lineIndex + WORD.length() - 1 > lines.size() - 1) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentLineIndex = lineIndex + 1; // Get the below line initially
		while (true) {
			currentChar = lines.get(currentLineIndex).get(charIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentLineIndex++;
		}

		return true;
	}

	private static boolean foundRight(int lineIndex, int charIndex, List<List<Character>> lines) {
		if (charIndex + WORD.length() - 1 > lines.get(lineIndex).size()) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentCharIndex = charIndex + 1;
		while (true) {
			currentChar = lines.get(lineIndex).get(currentCharIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentCharIndex++;
		}

		return true;
	}

	private static boolean foundLeft(int lineIndex, int charIndex, List<List<Character>> lines) {
		if (charIndex < WORD.length() - 1) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentCharIndex = charIndex - 1;
		while (true) {
			currentChar = lines.get(lineIndex).get(currentCharIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentCharIndex--;
		}

		return true;
	}

	private static boolean foundUpRight(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Ensure there is enough indexes to the right and up to make xmas
		if (charIndex + WORD.length() - 1 > lines.get(lineIndex).size() - 1 || lineIndex < WORD.length() - 1) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentCharIndex = charIndex + 1;
		int currentLineIndex = lineIndex - 1;
		while (true) {
			currentChar = lines.get(currentLineIndex).get(currentCharIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentCharIndex++;
			currentLineIndex--;
		}

		return true;
	}

	private static boolean foundUpLeft(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Ensure there is enough indexes to the left and up to make xmas
		if (charIndex < WORD.length() - 1 || lineIndex < WORD.length() - 1) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentCharIndex = charIndex - 1;
		int currentLineIndex = lineIndex - 1;
		while (true) {
			currentChar = lines.get(currentLineIndex).get(currentCharIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentCharIndex--;
			currentLineIndex--;
		}

		return true;
	}

	private static boolean foundBottomRight(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Ensure there is enough indexes to the right and down to make xmas
		if (charIndex + WORD.length() - 1 > lines.get(lineIndex).size() - 1
				|| lineIndex + WORD.length() - 1 > lines.size() - 1) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentCharIndex = charIndex + 1;
		int currentLineIndex = lineIndex + 1;
		while (true) {
			currentChar = lines.get(currentLineIndex).get(currentCharIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentCharIndex++;
			currentLineIndex++;
		}

		return true;
	}

	private static boolean foundBottomLeft(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Ensure there is enough indexes to the left and down to make xmas
		if (charIndex < WORD.length() - 1 || lineIndex + WORD.length() - 1 > lines.size() - 1) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentCharIndex = charIndex - 1;
		int currentLineIndex = lineIndex + 1;
		while (true) {
			currentChar = lines.get(currentLineIndex).get(currentCharIndex);
			if (nextValidCharacter != currentChar) {
				return false;
			}

			nextValidCharacter = getNextValidCharacter(currentChar);

			if (nextValidCharacter == END_OF_WORD_CHARACTER) {
				break;
			}

			currentCharIndex--;
			currentLineIndex++;
		}

		return true;
	}
}
