import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class DayEightPartTwo {

	public abstract static class Location {
		int lineIndex;
		int characterIndex;

		public Location(int lineIndex, int characterIndex) {
			this.lineIndex = lineIndex;
			this.characterIndex = characterIndex;

		}

		@Override
		public String toString() {
			return String.format("Line index: %d, Character index: %d", lineIndex, characterIndex);
		}

		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof Location)) {
				return false;
			}
			Location otherAntinodeLocation = (Location) other;
			return this.lineIndex == otherAntinodeLocation.lineIndex
					&& this.characterIndex == otherAntinodeLocation.characterIndex;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			int salt = 31;
			hash = lineIndex + (hash * salt); // 10 + 7*31
			hash = characterIndex + (hash * salt);// 50 +
			return hash;
		}
	}

	public static class AntennaLocation extends Location {
		public AntennaLocation(int lineIndex, int characterIndex) {
			super(lineIndex, characterIndex);
		}
	}

	public static class AntinodeLocation extends Location {
		public AntinodeLocation(int lineIndex, int characterIndex) {
			super(lineIndex, characterIndex);
		}
	}

	public static void testAntinodeHashCode() {
		AntinodeLocation v1 = new AntinodeLocation(10, 50);
		AntinodeLocation v2 = new AntinodeLocation(50, 10);
		assert v1.hashCode() != v2.hashCode();
		v1 = new AntinodeLocation(-10, 50);
		v2 = new AntinodeLocation(50, -10);
		assert v1.hashCode() != v2.hashCode();
		v1 = new AntinodeLocation(-10, -50);
		v2 = new AntinodeLocation(-50, -10);
		assert v1.hashCode() != v2.hashCode();
		v1 = new AntinodeLocation(10, -50);
		v2 = new AntinodeLocation(-50, 10);
		assert v1.hashCode() != v2.hashCode();
	}

	public static void testAntinodeEquals() {
		AntinodeLocation v1 = new AntinodeLocation(10, 50);
		AntinodeLocation v2 = new AntinodeLocation(10, 50);
		assert v1.equals(v2);

		v2 = null;
		assert !v1.equals(v2);

		v2 = new AntinodeLocation(50, 10);
		assert !v1.equals(v2);
	}

	public static void main(String args[]) {

		// Tests...
		testAntinodeHashCode();
		testAntinodeEquals();

		List<String> lines = new ArrayList<>();
		try (FileReader fr = new FileReader("./input"); BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines().toList();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		Set<AntinodeLocation> uniqueAntinodeLocations = new HashSet<>();
		Set<Character> uniqueTypesOfAntennas = getUniqueTypesOfAntennasFromInput(lines);

		System.out.println("Unique types of antennas: " + uniqueTypesOfAntennas);

		for (Character antennaType : uniqueTypesOfAntennas) {
			uniqueAntinodeLocations.addAll(getUniqueAntinodeLocationsFromAntennaType(lines, antennaType));
		}

		System.out.println(uniqueAntinodeLocations.size());

	}

	public static Set<AntinodeLocation> getUniqueAntinodeLocationsFromAntennaType(List<String> lines,
			Character antennaType) {

		Set<AntinodeLocation> uniqueAntinodeLocations = new HashSet<>();

		// outer loop finds each antenna to start from, giving each type of this antenna
		// a chance at the starting one
		// each antenna matching goes through the entire input searching for a second
		// pair, at a full iteration end, the search for the next first antenna
		// starts...
		for (int i = 0; i < lines.size(); i++) {
			String lineI = lines.get(i);
			for (int j = 0; j < lineI.length(); j++) {
				Character characterJ = lineI.charAt(j);

				if (characterJ.equals(antennaType)) {
					// first antenna found for pair
					AntennaLocation currentAntennaLocation = new AntennaLocation(i, j);
					System.out.println("First antenna found for a pair: " + currentAntennaLocation.toString());

					// find the second antenna starting at first antenna location
					for (int x = 0; x < lines.size(); x++) {
						String lineX = lines.get(x);
						for (int y = j + 1; y < lineX.length(); y++) {
							Character characterY = lineX.charAt(y);
							if (characterY.equals(antennaType)) {
								AntennaLocation otherAntennaLocation = new AntennaLocation(x, y);
								System.out.println("Second antenna found for a pair: " + otherAntennaLocation.toString());
								uniqueAntinodeLocations.addAll(
										getPossibleAntinodeLocationsFromAntennas(
												currentAntennaLocation,
												otherAntennaLocation,
												lines));
							}
						}
					}
				}
			}
		}

		return uniqueAntinodeLocations;

	}

	public static Set<AntinodeLocation> getPossibleAntinodeLocationsFromAntennas(AntennaLocation a1, AntennaLocation a2, List<String> lines) {
		Set<AntinodeLocation> locations = getAntinodeLocationsFromAntennas(a1, a2, lines);
		return locations;
	}

	public static Set<AntinodeLocation> getAntinodeLocationsFromAntennas(AntennaLocation a1, AntennaLocation a2, List<String> lines) {

		Set<AntinodeLocation> locations = new HashSet<>();

		// Add the antinode locations that are on top of the antenna pair:
		locations.add(new AntinodeLocation(a1.lineIndex, a1.characterIndex));
		locations.add(new AntinodeLocation(a2.lineIndex, a2.characterIndex));

		// DONT FORGET THE ANTINODES MUST BE INLINE WITH THE ANTENNAS
			// actually any two antinodes will always be inline on a 2d graph like this
		// ALWAYS SET THE DIFFERENCE FROM LEFT TO RIGHT TOP TO BOTTOM

		int verticalDifference = a1.lineIndex - a2.lineIndex;
		int horizontalDifference = a1.characterIndex - a2.characterIndex;

		// apply the original difference values to v1, then negate them and apply to v2
		AntinodeLocation currentLocationA1 = new AntinodeLocation(a1.lineIndex + verticalDifference, a1.characterIndex + horizontalDifference);  // keeping the original sign
		AntinodeLocation currentLocationA2 = new AntinodeLocation(a2.lineIndex - verticalDifference, a2.characterIndex - horizontalDifference);  // reversing the sign
		
		int antinodeCount = 1;
		while (isLocationInBounds(currentLocationA1, lines)) {
			locations.add(currentLocationA1);
			antinodeCount++;
			currentLocationA1 = new AntinodeLocation(a1.lineIndex + antinodeCount * verticalDifference, a1.characterIndex + antinodeCount * horizontalDifference);  // keeping the original sign
		}

		antinodeCount = 1;
		while (isLocationInBounds(currentLocationA2, lines)) {
			locations.add(currentLocationA2);
			antinodeCount++;
			currentLocationA2 = new AntinodeLocation(a2.lineIndex - antinodeCount * verticalDifference, a2.characterIndex - antinodeCount * horizontalDifference);  // reversing the sign
		}

		return locations;
	}

	public static boolean isLocationInBounds(AntinodeLocation location, List<String> lines) {
		if (lines == null || lines.isEmpty() || lines.get(0).length() == 0) {
			return false;
		}

		boolean inVerticalBounds = location.lineIndex >= 0 && location.lineIndex <= lines.size() - 1;
		boolean inHorizontalBounds = location.characterIndex >= 0 && location.characterIndex <= lines.get(0).length() - 1;

		return inVerticalBounds && inHorizontalBounds;
	}

	public static Set<Character> getUniqueTypesOfAntennasFromInput(List<String> inputLines) {

		Set<Character> uniqueTypesOfAntennas = new HashSet<>();

		for (String line : inputLines) {
			char[] characters = new char[line.length()];
			line.getChars(0, characters.length, characters, 0);
			for (char character : characters) {

				// Single lowercase letter, uppercase letter, or digit...
				if (Character.toString(character).matches("[a-zA-Z0-9]")
						&& !uniqueTypesOfAntennas.contains(character)) {
					uniqueTypesOfAntennas.add(character);
				}

			}

		}

		return uniqueTypesOfAntennas;

	}
}
