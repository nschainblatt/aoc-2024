import java.util.*;
import java.io.*;

// Plan:
// Update the direction checkers to the new x-mas 'valid' way

// Find an M
// Check every diagonal direction
// Depending on the diagonal direction that has the first half of a possible match,
//	it will decide which indices to check next.
//	Example: if a first half is found going bottom-right, then we have to check
//		 either to the right two and the down-left, or we have to check
//		 two down and then up-right
//		 this is the case for any diagonal
//
//
// Then is two 'valid' halves are found in the X fashion, then we store those M characters in a used set, so we don't use them again. We don't store the other cahracters because the only ones that matter are M's. We don't have to worry about missing out on reused M's because when we encounter an M we check every direction.
// Meaning if we found an M, and a first half match in the down right direction, we would process that piece (looking ahead indecises) and then when done with that possible full match, we check the rest of the directions with that same M, therefore we won't need that M every again, and it wont confuse the algorithm down the line.
//
// We will also only use M's, meaning we will only go forward, not backwards. Because of this if we found a first half match in down-right, and we looked two to the right and its an S, that means we have to check two down from the M to find another M to start the other half with, otherwise its not a full match. 

public class DayFourPartTwo {
	private static final String WORD = "MAS";
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

		/*
		 * Algorithm should work as follows:
		 *
		 * Find a character matching the first character of the word MAS, so M.
		 * Check every possible diagonal direction starting with that character for a
		 * matching first half of the X
		 * For every direction with a half found starting with the first, look to the
		 * indices at
		 * the proper offset (depending on the direction) for the other matching half
		 * If a full match is found, check whether a combination of the first letter
		 * indices is used already, if so skip this one.
		 * This combination of indices is a Set of lists of indices, should only be the
		 * first M and the second M making up the X.
		 *
		 * TODO: refactor logic to find my simple bugs and errors having to do with
		 * checking the wrong direction or calculating an index incorrectly
		 * break things down into smaller problems and refactor into functions
		 * functions to determine index, functions to determine out of bounds
		 *
		 * NOTE: 
		 * i notice that there are times where i am not looking forward to the next session either with homework, work, or coding problems
		 * i think this stems from not having proper planning because i do enjoy those three areas most of the time
		 * i think that a certain problem cannot feel fun if it seems super daunting.
		 * we need to work on our problem solving skills where we analyze the problem, break down the problems, make a plan and execute
		 * we need to write better code that can be jumped back into easier instead of my solution to this problem which is probably uneccessarily complex.
		 * we should use our problem breakdown and plan to make functions that solve the problems instead of having giant functions that do a lot of things
		 *
		 *
		 *
		 *
		 *
		 * M.M..S.M.S
		 * .A....A.A.
		 * S.S..S.M.S
		 *
		 *
		 * ^^^ This is an example test case that fails, my current implementation only counts 2 for this when its really 3.
		 *	It does so because the two M's on the right have already been marked as used before checking the other directions within the same M's
		 *
		 *	Meaning when it gets to that first M' on the right it checks the first direction, and then for the other half, then marks as used before it checks
		 *	the other directions with the same M.
		 *
		 *	We need to not mark an M as used until all the directions are checked for that M.
		 */

		long totalXmasCount = 0;
		Set<Set<List<Integer>>> usedIndices = new HashSet<>();
		for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
			List<Character> line = lines.get(lineIndex);
			for (int charIndex = 0; charIndex < lines.get(lineIndex).size(); charIndex++) {
				Character currentChar = line.get(charIndex);
				if (currentChar.equals(WORD.charAt(0))) {

					List<Integer> firstHalfIndices = List.of(lineIndex, charIndex);
					List<Set<List<Integer>>> toMarkAsUsed = new ArrayList<>();

					if (foundUpRight(lineIndex, charIndex, lines)) {
						Set<List<Integer>> matchIndices = new HashSet<>();
						matchIndices.add(firstHalfIndices);
						List<Integer> secondHalfIndices = handleFoundUpRight(lineIndex,
								charIndex, lines);
						if (secondHalfIndices != null) {
							matchIndices.add(secondHalfIndices);
							if (!usedIndices.contains(matchIndices)) {
								toMarkAsUsed.add(matchIndices);
								// usedIndices.add(matchIndices);
								totalXmasCount++;
							}
						}
					}

					if (foundUpLeft(lineIndex, charIndex, lines)) {
						Set<List<Integer>> matchIndices = new HashSet<>();
						matchIndices.add(firstHalfIndices);
						List<Integer> secondHalfIndices = handleFoundUpLeft(lineIndex,
								charIndex, lines);
						if (secondHalfIndices != null) {
							matchIndices.add(secondHalfIndices);
							if (!usedIndices.contains(matchIndices)) {
								toMarkAsUsed.add(matchIndices);
								// usedIndices.add(matchIndices);
								totalXmasCount++;
							}
						}
					}

					if (foundDownRight(lineIndex, charIndex, lines)) {
						Set<List<Integer>> matchIndices = new HashSet<>();
						matchIndices.add(firstHalfIndices);
						List<Integer> secondHalfIndices = handleFoundDownRight(lineIndex,
								charIndex, lines);
						if (secondHalfIndices != null) {
							matchIndices.add(secondHalfIndices);
							if (!usedIndices.contains(matchIndices)) {
								toMarkAsUsed.add(matchIndices);
								// usedIndices.add(matchIndices);
								totalXmasCount++;
							}
						}
					}

					if (foundDownLeft(lineIndex, charIndex, lines)) {
						Set<List<Integer>> matchIndices = new HashSet<>();
						matchIndices.add(firstHalfIndices);
						List<Integer> secondHalfIndices = handleFoundDownLeft(lineIndex,
								charIndex, lines);
						if (secondHalfIndices != null) {
							matchIndices.add(secondHalfIndices);
							if (!usedIndices.contains(matchIndices)) {
								toMarkAsUsed.add(matchIndices);
								// usedIndices.add(matchIndices);
								totalXmasCount++;
							}
						}
					}
					usedIndices.addAll(toMarkAsUsed);
				}
			}

		}
		System.out.println(usedIndices);

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
		if (charIndex + WORD.length() - 1 > lines.get(lineIndex).size() - 1) {
			return false;
		}

		char currentChar = lines.get(lineIndex).get(charIndex);
		char nextValidCharacter = getNextValidCharacter(currentChar);
		int currentCharIndex = charIndex + 1;
		while (true) {
			System.out.println(charIndex);
			System.out.println(lineIndex);
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

	// Check the other half given we found another direction
	private static List<Integer> handleFoundUpRight(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Validate we should and can check two to the right index in bounds and valid
		// starting character
		List<Integer> secondHalfIndices = null;
		if (charIndex + (WORD.length() - 1) <= lines.get(lineIndex).size() - 1
				&& lines.get(lineIndex).get(charIndex + (WORD.length() - 1)).equals(WORD.charAt(0))) {

			// check up left
			if (foundUpLeft(lineIndex, charIndex + (WORD.length() - 1), lines)) {
				secondHalfIndices = List.of(lineIndex, charIndex + (WORD.length() - 1));
			}

			// if that fails validate we should check two up
		} else if (lineIndex >= WORD.length() - 1
				&& lines.get(lineIndex - (WORD.length() - 1)).get(charIndex).equals(WORD.charAt(0))) {
			// check down right
			if (foundDownRight(lineIndex - (WORD.length() - 1), charIndex, lines)) {
				secondHalfIndices = List.of(lineIndex - (WORD.length() - 1), charIndex);
			}

		}

		return secondHalfIndices;
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

	private static List<Integer> handleFoundUpLeft(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Validate we should and can check two to the left index in bounds and valid
		// starting character
		List<Integer> secondHalfIndices = null;
		if (charIndex >= (WORD.length() - 1)
				&& lines.get(lineIndex).get(charIndex - (WORD.length() - 1)).equals(WORD.charAt(0))) {

			// check down right
			if (foundUpRight(lineIndex, charIndex - (WORD.length() - 1), lines)) {
				secondHalfIndices = List.of(lineIndex, charIndex - (WORD.length() - 1));
			}

			// if that fails validate we should check two up
		} else if (lineIndex > (WORD.length() - 1)
				&& lines.get(lineIndex - (WORD.length() - 1)).get(charIndex).equals(WORD.charAt(0))) {
			// check down right
			if (foundDownLeft(lineIndex - (WORD.length() - 1), charIndex, lines)) {
				secondHalfIndices = List.of(lineIndex - (WORD.length() - 1), charIndex);
			}

		}

		return secondHalfIndices;
	}

	private static boolean foundDownRight(int lineIndex, int charIndex, List<List<Character>> lines) {
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

	private static List<Integer> handleFoundDownRight(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Validate we should and can check two to the right index in bounds and valid
		// starting character
		List<Integer> secondHalfIndices = null;
		if (charIndex + (WORD.length() - 1) <= lines.get(lineIndex).size() - 1
				&& lines.get(lineIndex).get(charIndex + (WORD.length() - 1)).equals(WORD.charAt(0))) {

			// check up left
			if (foundDownLeft(lineIndex, charIndex + (WORD.length() - 1), lines)) {
				secondHalfIndices = List.of(lineIndex, charIndex + (WORD.length() - 1));
			}

			// if that fails check two down
		} else if (lineIndex + (WORD.length() - 1) <= lines.size() - 1
				&& lines.get(lineIndex + (WORD.length() - 1)).get(charIndex).equals(WORD.charAt(0))) {
			// check down right
			if (foundUpRight(lineIndex + (WORD.length() - 1), charIndex, lines)) {
				secondHalfIndices = List.of(lineIndex + (WORD.length() - 1), charIndex);
			}

		}

		return secondHalfIndices;
	}

	private static boolean foundDownLeft(int lineIndex, int charIndex, List<List<Character>> lines) {
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

	private static List<Integer> handleFoundDownLeft(int lineIndex, int charIndex, List<List<Character>> lines) {
		// Validate we should and can check two to the left index in bounds and valid
		// starting character
		List<Integer> secondHalfIndices = null;
		if (charIndex >= (WORD.length() - 1)
				&& lines.get(lineIndex).get(charIndex - (WORD.length() - 1)).equals(WORD.charAt(0))) {

			// check down right
			if (foundDownRight(lineIndex, charIndex - (WORD.length() - 1), lines)) {
				secondHalfIndices = List.of(lineIndex, charIndex - (WORD.length() - 1));
			}

			return secondHalfIndices;
		}
		// if that fails validate we should check two down
		if (lineIndex + (WORD.length() - 1) <= lines.size() - 1
				&& lines.get(lineIndex + (WORD.length() - 1)).get(charIndex).equals(WORD.charAt(0))) {
			// check down right
			if (foundUpLeft(lineIndex + (WORD.length() - 1), charIndex, lines)) {
				secondHalfIndices = List.of(lineIndex + (WORD.length() - 1), charIndex);
			}

		}

		return secondHalfIndices;
	}
}
