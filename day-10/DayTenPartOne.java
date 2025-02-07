import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class DayTenPartOne {

	public static String TRAIL_HEAD = "0";

	public static List<List<String>> getLinesFromFilePath(String filePath) throws Exception {
		List<List<String>> lines = new ArrayList<>();
		try (FileReader fr = new FileReader(filePath);
				BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines().map(line -> Arrays.stream(line.split("")).toList()).toList();
		}
		return lines;
	}

	public static List<int[]> getAllTrainHeadIndices(List<List<String>> lines) {
		List<int[]> trainHeadIndices = new ArrayList<>();

		for (int i = 0; i < lines.size(); i++) {
			for (int j = 0; j < lines.get(i).size(); j++) {
				if (TRAIL_HEAD.equals(lines.get(i).get(j))) {
					trainHeadIndices.add(new int[] { i, j });
				}
			}
		}

		return trainHeadIndices; 
	}

	public static boolean isPositionInBounds(List<List<String>> lines, int[] position) {
		int lineIndex = position[0];
		int characterIndex = position[1];
		return lineIndex >= 0 && characterIndex >= 0
				&& (lineIndex < lines.size() && characterIndex < lines.get(lineIndex).size());
	}

	public static boolean isPositionValueOneGreater(List<List<String>> lines, int[] position, int previousNumber) {
		if (previousNumber == -1) {
			return true;
		}
		int newNumber = Integer.parseInt(lines.get(position[0]).get(position[1]));
		return newNumber == previousNumber + 1;
	}

	public static int[] getNewPositionInDirection(int[] position, Direction direction) {
		int lineIndex = position[0];
		int characterIndex = position[1];
		return new int[] { lineIndex + direction.indexChange[0],
				characterIndex + direction.indexChange[1] };
	}

	public static Direction reverseDirection(Direction direction) {
		return switch (direction) {
			case UP -> Direction.DOWN;
			case DOWN -> Direction.UP;
			case LEFT -> Direction.RIGHT;
			case RIGHT -> Direction.LEFT;
		};
	}

	public static int getNumberAtPosition(List<List<String>> lines, int[] position) {
		int lineIndex = position[0];
		int characterIndex = position[1];
		return Integer.parseInt(lines.get(lineIndex).get(characterIndex));
	}

	public static Set<Direction> getValidDirectionsFromPosition(List<List<String>> lines, int[] position) {
		int lineIndex = position[0];
		int characterIndex = position[1];

		if (!isPositionInBounds(lines, position)) {
			throw new IllegalStateException(
					"Position is not in bounds. LineIndex: " + lineIndex + ". CharacterIndex: "
							+ characterIndex);
		}

		Set<Direction> set = new HashSet<>();
		int numberAtOriginalPosition = Integer.parseInt(lines.get(position[0]).get(position[1]));
		for (Direction direction : Direction.values()) {

			int[] newPosition = getNewPositionInDirection(position, direction);
			// System.out.println("DEBUG: NEW POSITION BEFORE CHECKING: " + Arrays.toString(newPosition));

			if (!isPositionInBounds(lines, newPosition)) {
				continue;
			}

			// Check for valid increment
			int numberAtNewPosition = getNumberAtPosition(lines, newPosition);
			if (numberAtNewPosition != numberAtOriginalPosition + 1) {
				continue;
			}

			// Valid
			set.add(direction);
		}

		return set;
	}

	enum Direction {
		UP(new int[] { -1, 0 }),
		RIGHT(new int[] { 0, 1 }),
		DOWN(new int[] { 1, 0 }),
		LEFT(new int[] { 0, -1 });

		private int[] indexChange;

		private Direction(int[] indexChange) {
			this.indexChange = indexChange;
		}
	}

	public static class Path {
		public final int id;
		public final int[] position;
		public final Set<Direction> directions;

		public Path(int id, int[] position, Set<Direction> directions) {
			this.id = id;
			this.position = position;
			this.directions = directions;
		}

		public String toString() {
			return String.format("\n%s\n%s\n", Arrays.toString(position), directions.toString());
		}
	}

	public static class PathEnd {
		private final int pathId;
		private final List<Integer> pathEndLocations;

		public PathEnd(int pathId, List<Integer> pathEndLocations) {
			this.pathId = pathId;
			this.pathEndLocations = pathEndLocations;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.pathId, this.pathEndLocations);
		}

		@Override
		public boolean equals(Object other) {
			if (this == other)
				return true; // Same reference
			if (other == null)
				return false; // either or both null
			if (!(other instanceof PathEnd))
				return false; // different object type
			PathEnd otherPathEnd = (PathEnd) other; // same object type
			return this.pathId == otherPathEnd.pathId
					&& Objects.equals(this.pathEndLocations, otherPathEnd.pathEndLocations); // all
															// fields
															// equal
		}
	}

	public static List<List<String>> linesDeepCopy(List<List<String>> lines) {
		List<List<String>> copyLines = new ArrayList<>();
		for (List<String> line : lines) {
			List<String> copyCharacters = new ArrayList<>();
			for (String character : line) {
				copyCharacters.add(new String(character));
			}
			copyLines.add(copyCharacters);
		}
		return copyLines;

	}

	public static void printLines(List<List<String>> lines, int[] position) {
		List<List<String>> linesCopy = linesDeepCopy(lines);
		linesCopy.get(position[0]).set(position[1], "*");
		for (List<String> line : linesCopy) {
			System.out.println(line);
		}
	}

	public static void travelPath(Path path, Stack<Path> pathStack, Set<PathEnd> pathEndCount,
			List<List<String>> lines) {
		// System.out.println("CURRENT PATH: " + path);

		for (Direction direction : path.directions) {
			// System.out.println("New direction: " + direction);
			// NOTE: any direction inside the paths direction set has already been checked
			// to be valid, hence
			// why it was added to the set in the first place.
			int[] newPosition = getNewPositionInDirection(path.position, direction);
			int previousNumber = -1;
			// NOTE: First new position from above is already valid, this condition is for
			// future positions
			while (isPositionInBounds(lines, newPosition)
					&& isPositionValueOneGreater(lines, newPosition, previousNumber)) {
				int numberAtPosition = getNumberAtPosition(lines, newPosition);

				int[] previousPosition = getNewPositionInDirection(newPosition,
						reverseDirection(direction));
				int actualPreviousNumber = getNumberAtPosition(lines, previousPosition);
				if (numberAtPosition == 9 && (actualPreviousNumber == 8)) {
					pathEndCount.add(new PathEnd(path.id, List.of(newPosition[0], newPosition[1])));
				}

				// printLines(lines, newPosition);
				// System.out.println("Valid position: " + Arrays.toString(newPosition) + " - "
				// 		+ numberAtPosition);
				// System.out.println("Previous number: " + previousNumber);

				// Given the position and value are valid, get the new valid directions that we
				// can go to from here (left, right, up down)
				Set<Direction> newPositionValidDirections = getValidDirectionsFromPosition(lines,
						newPosition);

				// if (newPositionValidDirections.contains(direction)) {
				// newPositionValidDirections.remove(direction);
				// }

				// If there is a new path away from current position add to visit later
				if (!newPositionValidDirections.isEmpty()) {
					// NOTE: id's allow me to see if a sub-path has found the same trail end as a
					// sibling or parent path so i don't count it twice for sub or parent
					// other non related paths can reach the same train end and it will count
					// towards the total
					pathStack.push(new Path(path.id, newPosition, newPositionValidDirections));
				}

				// Get the next position to check
				// System.out.println("Getting new position in same direction\n");
				newPosition = getNewPositionInDirection(newPosition, direction);

				try {
					// Thread.sleep(1000);
				} catch (Exception ex) {

				}

				previousNumber = numberAtPosition;
			}
			// System.out.println("Invalid, moving to next direction, same path\n");

		}

		// System.out.println("DEBUG: done with this path, moving to the next one");

	}

	public static void main(String[] args) throws Exception {
		List<List<String>> lines = getLinesFromFilePath("input");
		Stack<Path> pathStack = new Stack<>();
		Set<PathEnd> pathEnds = new HashSet<>();

		int idIncrement = 0;

				List<int[]> positionOfTrailHeads = getAllTrainHeadIndices(lines);
	
				for (int[] positionOfTrailHead : positionOfTrailHeads) {

				if (positionOfTrailHead.length == 0) {
					break;
				}

				Set<Direction> validDirections = getValidDirectionsFromPosition(lines,
						positionOfTrailHead);
				pathStack.push(new Path(idIncrement++, positionOfTrailHead, validDirections));

				while (!pathStack.isEmpty()) {
					Path currentPath = pathStack.pop();
					travelPath(currentPath, pathStack, pathEnds, lines);
				}
				}


		// for (PathEnd end : pathEnds) {
		// 	System.out.println(end.pathId + " " + end.pathEndLocations + " " + getNumberAtPosition(lines, end.pathEndLocations.stream().mapToInt(Integer::intValue).toArray()));
		// }
		System.out.println(pathEnds.size());

	}
}
