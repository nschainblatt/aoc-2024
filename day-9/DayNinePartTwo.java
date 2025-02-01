
import java.util.*;
import java.io.*;

public class DayNinePartTwo {

	public enum StorageType {
		FILE, FREE_SPACE
	}

	public static class StorageObject {
		final Optional<Integer> id;
		final StorageType type;
		final int blocks;

		public StorageObject(Integer id, StorageType type, int blocks) {
			this.id = Optional.of(id);
			this.type = type;
			this.blocks = blocks;
		}

		public StorageObject(StorageType type, int blocks) {
			this.id = Optional.empty();
			this.type = type;
			this.blocks = blocks;
		}

		@Override
		public String toString() {
			return String.format("ID: %s, TYPE: %s, BLOCKS: %s", id.isPresent() ? id.get() : "N/A", type,
					blocks);
		}
	}

	public static List<List<Integer>> createIntegerListFromFilePath(String filePath) throws Exception {
		List<List<Integer>> lines = new ArrayList<>();
		try (FileReader fr = new FileReader(filePath);
				BufferedReader br = new BufferedReader(fr)) {
			lines = br.lines()
					.map(line -> line.chars().mapToObj(
							charInt -> Integer.valueOf(Character.toString((char) charInt)))
							.toList())
					.toList();
		}
		return lines;

	}

	public static List<StorageObject> createStorageObjectsFromIntegers(List<Integer> mainIntegerList) {
		List<StorageObject> objectList = new ArrayList<>();
		boolean isFile = true;
		int idIncrement = 0;
		for (Integer storageItem : mainIntegerList) {
			if (isFile) {
				objectList.add(new StorageObject(idIncrement, StorageType.FILE, storageItem));
				idIncrement++;
			} else {
				objectList.add(new StorageObject(StorageType.FREE_SPACE, storageItem));
			}
			isFile = !isFile;
		}

		return objectList;
	}

	public static List<Integer> createUncompactedListFromStorageObjects(List<StorageObject> storageObjects) {
		List<Integer> uncompactedList = new ArrayList<>();

		for (StorageObject object : storageObjects) {
			if (StorageType.FILE.equals(object.type) && object.id.isPresent()) {
				Integer id = object.id.get();
				for (int i = 0; i < object.blocks; i++) {
					uncompactedList.add(id);
				}
			} else {
				for (int i = 0; i < object.blocks; i++) {
					uncompactedList.add(null);
				}
			}
		}

		return uncompactedList;
	}

	public static <T extends Comparable<Integer>> List<T> deepcopy(List<T> l) {
		List<T> lc = new ArrayList<>();
		for (T i : l) {
			lc.add(i);
		}
		return lc;
	}

	public static List<Integer> findIndicesOfFileById(Integer latestFileId, List<Integer> compactedList) {
		// System.out.println(latestFileId);
		List<Integer> indices = new ArrayList<>();

		Integer currentFileId = null;
		for (int i = compactedList.size() - 1; i >= 0; i--) {

			// Get the index of the first occurrence of the file were looking for
			if (currentFileId == null && compactedList.get(i) != null && (latestFileId == null || compactedList.get(i) == latestFileId - 1)) {
				currentFileId = compactedList.get(i);
				indices.add(i);
				continue;
			}

			// Get the index of the last occurrence of the file were looking for
			// If the current file id doesn't equal the current value, then we've passed it and the previous index is the last index of the current file id
			if (currentFileId != null && currentFileId != compactedList.get(i)) {
				if (i + 1 != indices.get(0))  {
					indices.add(i + 1); // NOTE: adding one since we're going in reverse here
				}
				break;
			}
		}

		return indices;
	}

	public static List<Integer> findIndicesOfAvailableFreeSpace(int freeSpaceRequired, List<Integer> compactedList) {
		List<Integer> indicesOfAvailableFreeSpace = new ArrayList<>();

		Integer beginningIndex = null;
		int freeSpaceCounter = 0;
		for (int i = 0; i < compactedList.size(); i++) {

			if (compactedList.get(i) == null) {
				if (beginningIndex == null) {
					beginningIndex = i;
				}
				freeSpaceCounter++;
			} else if (beginningIndex != null) {
				// means we've found free space but it's not enough, so resetting to find next free space area
				beginningIndex = null;
				freeSpaceCounter = 0;
				continue;
			}

			if (freeSpaceCounter == freeSpaceRequired) {
				indicesOfAvailableFreeSpace.add(beginningIndex);
				if (beginningIndex != i) {
					indicesOfAvailableFreeSpace.add(i);
				}
				return indicesOfAvailableFreeSpace;
			}


		}

		return indicesOfAvailableFreeSpace;
	}

	public static <T> void swap(List<T> l, int i, int j) {
		T t = l.get(i);
		l.set(i, l.get(j));
		l.set(j, t);
	}

	public static <T> void swapAll(List<T> l, List<Integer> il, List<Integer> jl) {
		if (il.size() != jl.size()) {
			throw new IllegalArgumentException();
		}

		List<Integer> ilIndices = new ArrayList<>();

		for (int i = il.get(il.size() - 1); i < il.get(0) + 1; i++) {
			ilIndices.add(i);
		}

		List<Integer> jlIndices = new ArrayList<>();

		for (int j = jl.get(0); j < jl.get(jl.size() - 1) + 1; j++) {
			jlIndices.add(j);
		}

		// System.out.println(ilIndices);
		// System.out.println(jlIndices);
		// System.out.println(il);
		// System.out.println(jl);
		for (int i = 0; i < ilIndices.size(); i++) {
			// System.out.println(i);
			swap(l, ilIndices.get(i), jlIndices.get(i));
		}
		// for (int i = 0; i < il.size(); i++) {
		// 	swap(l, il.get(i), jl.get(i));
		// }
	}

	public static List<Integer> createCompactedListFromUncompactedList(List<Integer> uncompactedList) {
		List<Integer> compactedList = deepcopy(uncompactedList);

		Integer latestFileId = null;
		for (int i = compactedList.size() - 1; i >= 0; i--) {

			if (latestFileId != null && latestFileId == 0) {
				break;
			}

			List<Integer> indicesOfLastFile = findIndicesOfFileById(latestFileId, compactedList);
			// System.out.println("Indices of last file: " + indicesOfLastFile.toString());

			latestFileId = compactedList.get(indicesOfLastFile.get(0));
			// System.out.println("ID: " + latestFileId);
			int spaceRequired =  indicesOfLastFile.get(0) - indicesOfLastFile.get(indicesOfLastFile.size() - 1) + 1;
			// System.out.println("Free space required: " + spaceRequired);

			List<Integer> indicesOfAvailabeFreeSpace = findIndicesOfAvailableFreeSpace(spaceRequired, compactedList);
			// System.out.println("Indices of available free space: " + indicesOfAvailabeFreeSpace.toString());

			if (!indicesOfAvailabeFreeSpace.isEmpty() && indicesOfLastFile.get(0) > indicesOfAvailabeFreeSpace.get(0)) {
				// System.out.println("SIZE 1: " + indicesOfLastFile.size());
				// System.out.println("SIZE 2: " + indicesOfAvailabeFreeSpace.size());
				swapAll(compactedList, indicesOfLastFile, indicesOfAvailabeFreeSpace);
			}
			// System.out.println(compactedList);

			
			// if (indexOfLastFileId < indexOfFirstFreeSpace) {
			// 	break;
			// }
			//
			// // Can swap because we know there is more files on the end to place towards the
			// // beginning
			// swap(compactedList, indexOfLastFileId, indexOfFirstFreeSpace);
		}

		return compactedList;
	}

	public static long calculateChecksumFromCompactedList(List<Integer> compactedList) {
		long sum = 0;

		for (int i = 0; i < compactedList.size(); i++) {
			Integer object = compactedList.get(i);

			if (object == null) {
				continue;
			}

			sum += i * object;
		}

		return sum;
	}

	public static void main(String[] args) throws Exception {
		List<List<Integer>> lines = createIntegerListFromFilePath("./input");
		List<Integer> mainIntegerList = lines.get(0);
		List<StorageObject> storageObjects = createStorageObjectsFromIntegers(mainIntegerList);
		List<Integer> uncompactedList = createUncompactedListFromStorageObjects(storageObjects);
		// System.out.println(uncompactedList);
		List<Integer> compactedList = createCompactedListFromUncompactedList(uncompactedList);
		long checksum = calculateChecksumFromCompactedList(compactedList);
		System.out.println(checksum);
	}
}
