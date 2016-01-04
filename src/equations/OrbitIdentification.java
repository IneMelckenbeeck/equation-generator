package equations;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class OrbitIdentification {

	private static List<OrbitRepresentative> orbits;
	private static List<Integer> graphletsPerSize;
//	private static boolean ready = false;
	private static Map<OrbitRepresentative, Integer> orbitNumbers;
	private static List<List<OrbitRepresentative>> orbitsPerSize;

	/**
	 * Reads all orbits in from file and stores them by number for
	 * quick reference.
	 */
	public static void readGraphlets(String filename) {
		orbits = new ArrayList<OrbitRepresentative>();
		graphletsPerSize = new ArrayList<Integer>();
		orbitsPerSize = new ArrayList<List<OrbitRepresentative>> ();
		orbitNumbers = new HashMap<OrbitRepresentative, Integer>();
		File file = new File(filename);// this file contains the graphlets
											// in order
		try {
			Scanner scanner = new Scanner(file);
			int counter = 0;
			int size = 0;
			int orbitNumber = 0;
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				Set<Edge> set = new HashSet<Edge>();
				int max = 0;
				for (int i = 0; i < s.length() - 2; i += 4) {
					Edge e = new Edge(s.charAt(i) - '0', s.charAt(i + 2) - '0');
					set.add(e);
					if (s.charAt(i) - '0' > max) {
						max = s.charAt(i) - '0';
					}
					if (s.charAt(i + 2) - '0' > max) {
						max = s.charAt(i + 2) - '0';
					}
				}
				if (max + 1 > size) {
					size = max + 1;
					graphletsPerSize.add(counter);
					orbitsPerSize.add(new ArrayList<OrbitRepresentative>());
					counter = 0;
				}
				counter++;
				OrbitRepresentative og = new OrbitRepresentative(set, max + 1);
				orbits.add(og);
				orbitNumbers.put(og, orbitNumber);
				orbitsPerSize.get(size-2).add(og);
				orbitNumber++;
			}
			graphletsPerSize.add(counter);
//			ready = true;
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Ongeldige bestandsnaam");
		}
		System.out.println(graphletsPerSize);
	}

	public static void readGraphlets(String filename, int maxorder) {
		orbits = new ArrayList<OrbitRepresentative>();
		graphletsPerSize = new ArrayList<Integer>();
		orbitsPerSize = new ArrayList<List<OrbitRepresentative>> ();
		orbitNumbers = new HashMap<OrbitRepresentative, Integer>();
		File file = new File(filename);// this file contains the graphlets
											// in order
		try {
			Scanner scanner = new Scanner(file);
			int counter = 0;
			int size = 0;
			int orbitNumber = 0;
			while (scanner.hasNextLine()) {
				String s = scanner.nextLine();
				Set<Edge> set = new HashSet<Edge>();
				int max = 0;
				for (int i = 0; i < s.length() - 2; i += 4) {
					Edge e = new Edge(s.charAt(i) - '0', s.charAt(i + 2) - '0');
					set.add(e);
					if (s.charAt(i) - '0' > max) {
						max = s.charAt(i) - '0';
					}
					if (s.charAt(i + 2) - '0' > max) {
						max = s.charAt(i + 2) - '0';
					}
				}
				if (max + 1 > size) {
					size = max + 1;
					if(size>maxorder)break;
					graphletsPerSize.add(counter);
					orbitsPerSize.add(new ArrayList<OrbitRepresentative>());
					counter = 0;
				}
				counter++;
				OrbitRepresentative og = new OrbitRepresentative(set, max + 1);
				og.calculateSymmetry();
				orbits.add(og);
				orbitNumbers.put(og, orbitNumber);
				orbitsPerSize.get(size-2).add(og);
				orbitNumber++;
			}
			graphletsPerSize.add(counter);
//			ready = true;
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Ongeldige bestandsnaam");
		}
	}
	/**
	 * Returns the number of a given orbit.
	 * 
	 * @param g
	 *            OrbitGraphlet representing the orbit.
	 * @return The number of the orbit.
	 */
	public static int identifyOrbit(OrbitRepresentative g) {
//		if (!ready)
//			readGraphlets();
//		if (orbits == null) {
//			readGraphlets();
//		}
		Integer i = orbitNumbers.get(g);
		if (i == null)
			return -1;
		return i;
	}

	/**
	 * Returns the number of orbits of the given order
	 * 
	 * @param order
	 *            The order of which the number of graphlets is asked
	 * @return The number of orbits of the given order
	 */
	public static int getNOrbitsForOrder(int order) {
//		if (!ready)
//			readGraphlets();
		return graphletsPerSize.get(order - 1);
	}

	/**
	 * Returns the total number of orbits of the given order or lower
	 * 
	 * @param order
	 *            The maximal order of the counted orbits
	 * @return The number of orbits in graphlets of no higher than the given
	 *         order
	 */
	public static int getNOrbitsTotal(int order) {
//		if (!ready)
//			readGraphlets();
		int result = 0;
		for (int i = 0; i < order; i++) {
			result += graphletsPerSize.get(i);
		}
		return result;
	}
	
	public static void main(String[] args){
		readGraphlets("Orbits.txt");
		System.out.println(graphletsPerSize);
		System.out.println(getNOrbitsForOrder(2));
		System.out.println(getOrbitsOfOrder(4));
	}
	
	public static List<OrbitRepresentative> getOrbitsOfOrder(int order){
		return orbitsPerSize.get(order-2);
	}

}
