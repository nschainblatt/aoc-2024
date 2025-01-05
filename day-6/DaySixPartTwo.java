import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class DaySixPartTwo {

	enum Direction {
		UP("^"), RIGHT(">"), DOWN("v"), LEFT("<");

		public final String guard;

		Direction(String guard) {
			this.guard = guard;
		}
	}

	// optimize the indices we are placing the obstacles that ones the guard is
	// actually going to cross: DONE
	//
	// i think the loop were stuck in right now is the placed obstacle caused a loop
	// to start with a different existing object, because no loop existed in part
	// one, but the placed obstacle caused a different path to be
	// taken which caused a loop to start between existng objects.
	//
	// so we have to attatch state to every object encountered new and existing.
	// or we have to memorize paths to see if we've taken this path more than once.
	//
	// first is probably the way to go?
	// to attach state we can create a map where the indices of the object is the
	// key
	// and the value can be
	// an object that holds the number of times we've encountered that object and
	// the previous direction encountered
	//
	//

	public static void main(String[] args) {
		List<List<String>> originalLines = new ArrayList<>();
		try (FileReader fr = new FileReader("./input"); BufferedReader br = new BufferedReader(fr)) {
			originalLines = br.lines()
					.map(line -> Arrays.stream(line.split("")).collect(Collectors.toList()))
					.toList();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		System.out.println("START:");

		int obstacleLoopCounter = 0;

		Set<List<Integer>> guardPath = getGuardPath(deepcopy(originalLines));

		for (List<Integer> indices : guardPath) {
			List<List<String>> lines = deepcopy(originalLines);
			List<Integer> startingPosition = getStartingPositionFromLines(lines);
			Set<List<Integer>> positionSet = new HashSet<>();
			int totalPositionsVisited = 0;
			List<Integer> currentPosition = startingPosition;
			Direction currentDirection = Direction.UP;

			Integer lineIndex = indices.get(0);
			Integer charIndex = indices.get(1);

			char character = lines.get(lineIndex).get(charIndex).charAt(0);

			if (isCharacterUsed(character)) {
				continue;
			}

			List<Integer> obstaclePosition = List.of(lineIndex, charIndex);
			placeCharacterAtPosition(lines, obstaclePosition, "O");
			Map<List<Integer>, ObjectData> objectMap = findAllObjectsFromInput(originalLines);
			objectMap.put(obstaclePosition, new ObjectData());

			// This counter is for the 'O' obstacle, not any existing '#'
			// int encounteredObstacleCounter = 0;
			// Direction encounteredObstacleDirection = null;

			while (positionIsValid(lines, currentPosition)) {
				positionSet.add(currentPosition);
				totalPositionsVisited++;

				// printMap(lines, obstacleLoopCounter);
				// try {
				// Thread.sleep(250);
				// } catch (InterruptedException ex) {
				// }

				// if (encounteredObstacleCounter >= 2) {
				// obstacleLoopCounter++;
				// break;
				// }

				if (currentDirection.equals(Direction.UP)) {
					// Check up for # OR out of bound
					List<Integer> positionOfCharUp = getPositionAbove(currentPosition);
					Character charUp = getCharUp(lines, currentPosition);
					if (charUp == null) {
						// out of bounds
						break; // break because guard left area, so we just count up
							// whats in the set
							// now and we have our final answer
					} else if (charUp.equals('#') || charUp.equals('O')) {

						if (handleEncounteredObstacle(objectMap, positionOfCharUp,
								currentDirection)) {
							obstacleLoopCounter++;
							break;
						}

						currentDirection = Direction.RIGHT;
						continue; // no need to increment, we want to check first, which
								// happens in the
								// next iteration in the correct ifelse branch
								// condition
					} else {
						// Move up since valid and no wall encountered
						currentPosition = move(lines, currentDirection.guard,
								getPositionAbove(currentPosition),
								currentPosition);
					}
				} else if (currentDirection.equals(Direction.RIGHT)) {
					List<Integer> positionOfCharRight = getPositionToRight(currentPosition);
					Character charRight = getCharRight(lines, currentPosition);
					if (charRight == null) {
						break;
					} else if (charRight.equals('#') || charRight.equals('O')) {

						if (handleEncounteredObstacle(objectMap, positionOfCharRight,
								currentDirection)) {
							obstacleLoopCounter++;
							break;
						}

						currentDirection = Direction.DOWN;
						continue;
					} else {
						// Move right
						currentPosition = move(lines, currentDirection.guard,
								getPositionToRight(currentPosition),
								currentPosition);
					}
				} else if (currentDirection.equals(Direction.DOWN)) {
					List<Integer> positionOfCharDown = getPositionBelow(currentPosition);
					Character charDown = getCharDown(lines, currentPosition);
					if (charDown == null) {
						break;
					} else if (charDown.equals('#') || charDown.equals('O')) {

						if (handleEncounteredObstacle(objectMap, positionOfCharDown,
								currentDirection)) {
							obstacleLoopCounter++;
							break;
						}

						currentDirection = Direction.LEFT;
						continue;
					} else {
						// Move down, already validated
						currentPosition = move(lines, currentDirection.guard,
								getPositionBelow(currentPosition),
								currentPosition);
					}
				} else if (currentDirection.equals(Direction.LEFT)) {
					List<Integer> positionOfCharLeft = getPositionToLeft(currentPosition);
					Character charLeft = getCharLeft(lines, currentPosition);
					if (charLeft == null) {
						break;
					} else if (charLeft.equals('#') || charLeft.equals('O')) {

						if (handleEncounteredObstacle(objectMap, positionOfCharLeft,
								currentDirection)) {
							obstacleLoopCounter++;
							break;
						}

						currentDirection = Direction.UP;
						continue;
					} else {
						// Move left, we've already validated we can
						currentPosition = move(lines, currentDirection.guard,
								getPositionToLeft(currentPosition),
								currentPosition);
					}
				} else {
					throw new IllegalStateException("We shouldn't have gotten here");
				}

			}

			objectMap.remove(obstaclePosition);
			placeCharacterAtPosition(lines, obstaclePosition, ".");

		}

		System.out.println(obstacleLoopCounter);
	}

	public static class ObjectData {
		int encounteredObjectCount = 0;
		Direction encounteredObjectDirection = null;
	}

	public static Map<List<Integer>, ObjectData> findAllObjectsFromInput(List<List<String>> lines) {

		Map<List<Integer>, ObjectData> objectMap = new HashMap<>();

		for (int i = 0; i < lines.size(); i++) {

			for (int j = 0; j < lines.get(i).size(); j++) {

				if (lines.get(i).get(j).equals("#")) {
					objectMap.put(List.of(i, j), new ObjectData());
				}

			}

		}

		return objectMap;
	}

	public static Set<List<Integer>> getGuardPath(List<List<String>> lines) {
		List<Integer> startingPosition = getStartingPositionFromLines(lines);
		Set<List<Integer>> positionSet = new HashSet<>();
		List<Integer> currentPosition = startingPosition;
		Direction currentDirection = Direction.UP;

		while (positionIsValid(lines, currentPosition)) {
			positionSet.add(currentPosition);
			if (currentDirection.equals(Direction.UP)) {
				// Check up for # OR out of bound
				Character charUp = getCharUp(lines, currentPosition);
				if (charUp == null) {
					// out of bounds
					break; // break because guard left area, so we just count up whats in the set
						// now and we have our final answer
				} else if (charUp.equals('#')) {
					currentDirection = Direction.RIGHT;
					continue; // no need to increment, we want to check first, which happens in the
							// next iteration in the correct ifelse branch condition
				} else {
					// Move up since valid and no wall encountered
					currentPosition = move(lines, currentDirection.guard,
							getPositionAbove(currentPosition),
							currentPosition);
				}
			} else if (currentDirection.equals(Direction.RIGHT)) {
				Character charRight = getCharRight(lines, currentPosition);
				if (charRight == null) {
					break;
				} else if (charRight.equals('#')) {
					currentDirection = Direction.DOWN;
					continue;
				} else {
					// Move right
					currentPosition = move(lines, currentDirection.guard,
							getPositionToRight(currentPosition),
							currentPosition);
				}
			} else if (currentDirection.equals(Direction.DOWN)) {
				Character charDown = getCharDown(lines, currentPosition);
				if (charDown == null) {
					break;
				} else if (charDown.equals('#')) {
					currentDirection = Direction.LEFT;
					continue;
				} else {
					// Move down, already validated
					currentPosition = move(lines, currentDirection.guard,
							getPositionBelow(currentPosition),
							currentPosition);
				}
			} else if (currentDirection.equals(Direction.LEFT)) {
				Character charLeft = getCharLeft(lines, currentPosition);
				if (charLeft == null) {
					break;
				} else if (charLeft.equals('#')) {
					currentDirection = Direction.UP;
					continue;
				} else {
					// Move left, we've already validated we can
					currentPosition = move(lines, currentDirection.guard,
							getPositionToLeft(currentPosition),
							currentPosition);
				}
			} else {
				throw new IllegalStateException("We shouldn't have gotten here");
			}

		}

		return positionSet;
	}

	public static boolean handleEncounteredObstacle(Map<List<Integer>, ObjectData> objectMap,
			List<Integer> positionOfCharacter, Direction currentDirection) {
		ObjectData data = objectMap.get(positionOfCharacter);
		if (data == null) {
			throw new NoSuchElementException(String.format("No object data found for indices: %d, %d",
					positionOfCharacter.get(0), positionOfCharacter.get(1)));
		}

		if (data.encounteredObjectDirection == null) {
			data.encounteredObjectDirection = currentDirection;
			data.encounteredObjectCount++;
			return false; // need another hit to be considered a loop

		} else if (data.encounteredObjectDirection.equals(currentDirection)) {
			// System.out.println(data.encounteredObjectCount);

			// We've hit this object at this direction before, meaning we've reached a loop,
			// because this is the second time we've hit this at the same direction
			return true;
		}

		// Encountered at different angle
		return false;
	}

	public static List<List<String>> deepcopy(List<List<String>> lines) {
		List<List<String>> copyList = new ArrayList<>();

		for (List<String> line : lines) {
			List<String> innerCopyList = new ArrayList<>();
			for (String character : line) {
				innerCopyList.add(new String(character));
			}
			copyList.add(innerCopyList);
		}

		return copyList;
	}

	public static boolean isCharacterUsed(char character) {
		return character == '#' || character == 'O' || character == '^' || character == 'v' || character == '<'
				|| character == '>';
	}

	public static void placeCharacterAtPosition(List<List<String>> lines, List<Integer> position,
			String character) {
		lines.get(position.get(0)).set(position.get(1), character);
	}

	public static void printMap(List<List<String>> lines, int totalLoopsCreated) {
		// Clear screen (works in linux at least idk about anything else rn)
		System.out.print("\033[H\033[2J");
		System.out.flush();
		int index = 0;
		for (List<String> line : lines) {
			for (String character : line) {
				System.out.print(character);
			}
			System.out.print("  " + (index + 1));
			System.out.println();
			index++;
		}

		System.out.println();
		// System.out.println("Unique Positions Visited: " + uniquePositionsVisited);
		// System.out.println("Total Positions Visited: " + totalPositionsVisited);
		System.out.println("Total loops created: " + totalLoopsCreated);
		System.out.println("----------");
		System.out.println();
	}

	public static void printLoops(List<Integer> indices, int loops) {
		System.out.print("\033[H\033[2J");
		System.out.flush();

		System.out.printf("Current indices: %d\t%d\n", indices.get(0), indices.get(1));
		System.out.printf("Obstacle loops created: %d\n", loops);
	}

	public static List<Integer> getPositionAbove(List<Integer> currentPosition) {
		return List.of(currentPosition.get(0) - 1, currentPosition.get(1));
	}

	public static List<Integer> getPositionToRight(List<Integer> currentPosition) {
		return List.of(currentPosition.get(0), currentPosition.get(1) + 1);
	}

	public static List<Integer> getPositionBelow(List<Integer> currentPosition) {
		return List.of(currentPosition.get(0) + 1, currentPosition.get(1));
	}

	public static List<Integer> getPositionToLeft(List<Integer> currentPosition) {
		return List.of(currentPosition.get(0), currentPosition.get(1) - 1);
	}

	/**
	 * Moves the guard character to the currentPosition, removes the guard character
	 * from the previousPosition
	 */
	public static List<Integer> move(List<List<String>> lines, String guard, List<Integer> currentPosition,
			List<Integer> previousPosition) {

		// Given the guard character is at the previous position lets set it to a '.'
		lines.get(previousPosition.get(0)).set(previousPosition.get(1), ".");

		// Given the current position is in bounds and not a wall lets set it to a guard
		lines.get(currentPosition.get(0)).set(currentPosition.get(1), guard);

		return currentPosition;
	}

	public static Character getCharUp(List<List<String>> lines, List<Integer> currentPosition) {
		int lineIndex = currentPosition.get(0) - 1; // Go up one
		int characterIndex = currentPosition.get(1);
		List<Integer> upPosition = List.of(lineIndex, characterIndex);
		boolean valid = positionIsValid(lines, upPosition);
		if (!valid) {
			return null;
		}
		return lines.get(lineIndex).get(characterIndex).charAt(0);
	}

	public static Character getCharRight(List<List<String>> lines, List<Integer> currentPosition) {
		int lineIndex = currentPosition.get(0);
		int characterIndex = currentPosition.get(1) + 1;
		List<Integer> rightPosition = List.of(lineIndex, characterIndex);
		boolean valid = positionIsValid(lines, rightPosition);
		if (!valid) {
			return null;
		}
		return lines.get(lineIndex).get(characterIndex).charAt(0);
	}

	public static Character getCharDown(List<List<String>> lines, List<Integer> currentPosition) {
		int lineIndex = currentPosition.get(0) + 1;
		int characterIndex = currentPosition.get(1);
		List<Integer> downPosition = List.of(lineIndex, characterIndex);
		boolean valid = positionIsValid(lines, downPosition);
		if (!valid) {
			return null;
		}
		return lines.get(lineIndex).get(characterIndex).charAt(0);
	}

	public static Character getCharLeft(List<List<String>> lines, List<Integer> currentPosition) {
		int lineIndex = currentPosition.get(0);
		int characterIndex = currentPosition.get(1) - 1;
		List<Integer> leftPosition = List.of(lineIndex, characterIndex);
		boolean valid = positionIsValid(lines, leftPosition);
		if (!valid) {
			return null;
		}
		return lines.get(lineIndex).get(characterIndex).charAt(0);
	}

	public static List<Integer> getStartingPositionFromLines(List<List<String>> lines) {
		for (int i = 0; i < lines.size(); i++) {
			for (int j = 0; j < lines.get(i).size(); j++) {

				char character = lines.get(i).get(j).charAt(0);
				if (character == '^') {
					return List.of(i, j);
				}
			}
		}

		throw new NoSuchElementException();
	}

	public static boolean positionIsValid(List<List<String>> lines, List<Integer> position) {
		int lineIndex = position.get(0);
		int characterIndex = position.get(1);
		boolean validVertical = lineIndex >= 0 && lineIndex <= (lines.size() - 1);
		if (!validVertical)
			return false;
		boolean validHorizontal = characterIndex >= 0 && characterIndex <= (lines.get(lineIndex).size() - 1);
		if (!validHorizontal)
			return false;
		return true;
	}

}
