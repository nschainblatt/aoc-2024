
import java.util.*;
import java.io.*;

public class DayNinePartOne {

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
		try (FileReader fr = new FileReader("./input");
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

	public static int findIndexOfLastFildId(List<Integer> compactedList) {

		for (int i = compactedList.size() - 1; i >= 0; i--) {

			if (compactedList.get(i) != null) {
				return i;
			}
		}

		throw new NoSuchElementException();
	}

	public static int findIndexOfFirstFreeSpace(List<Integer> compactedList) {

		for (int i = 0; i < compactedList.size(); i++) {

			if (compactedList.get(i) == null) {
				return i;
			}
		}

		throw new NoSuchElementException();
	}

	public static <T> void swap(List<T> l, int i, int j) {
		T t = l.get(i);
		l.set(i, l.get(j));
		l.set(j, t);
	}

	public static List<Integer> createCompactedListFromUncompactedList(List<Integer> uncompactedList) {
		List<Integer> compactedList = deepcopy(uncompactedList);

		for (int i = compactedList.size() - 1; i >= 0; i--) {

			int indexOfLastFileId = findIndexOfLastFildId(compactedList);
			int indexOfFirstFreeSpace = findIndexOfFirstFreeSpace(compactedList);

			if (indexOfLastFileId < indexOfFirstFreeSpace) {
				break;
			}

			// Can swap because we know there is more files on the end to place towards the
			// beginning
			swap(compactedList, indexOfLastFileId, indexOfFirstFreeSpace);
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
		List<List<Integer>> lines = createIntegerListFromFilePath("./sampleInput");
		List<Integer> mainIntegerList = lines.get(0);
		List<StorageObject> storageObjects = createStorageObjectsFromIntegers(mainIntegerList);
		List<Integer> uncompactedList = createUncompactedListFromStorageObjects(storageObjects);
		List<Integer> compactedList = createCompactedListFromUncompactedList(uncompactedList);
		long checksum = calculateChecksumFromCompactedList(compactedList);
		System.out.println(checksum);
	}
}
