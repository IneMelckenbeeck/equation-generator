package equations;

import java.util.*;

public class Program {

	public static boolean latex = false;

	/**
	 * The main method for equation generating.
	 * 
	 * @param args
	 *            0 to 3 arguments can be passed.
	 * 
	 *            If no arguments are passed, the program will ask for the
	 *            graphlets' order, use the standard file (Orbits.txt) for orbit
	 *            identification and print the equations in human-readable form.
	 * 
	 *            If any arguments are passed, the first one must be the
	 *            graphlets' order. Additionally, a filename may be passed,
	 *            which will be the file used for orbit identification. If LaTeX
	 *            output is wanted for the equations, the argument "latex" may
	 *            be passed as well.
	 */
	public static void main(String[] args) {
		int size = 0;
		String filename = "Orbits.txt";
		Scanner s = new Scanner(System.in);
		if (args.length != 0) {
			try {
				size = Integer.parseInt(args[0]);
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("latex")) {
						latex = true;
						if (args.length > 2)
							filename =(args[2]);

					} else
						filename=(args[1]);
					if (args.length > 2 && args[2].equalsIgnoreCase("latex"))
						latex = true;
				} 

			} catch (NumberFormatException e) {
				System.out.println("Invalid argument.");
			}
		}
		while (size == 0) {
			System.out
					.println("Please enter the order of graphlets for which you want the equations.");
			try {
				size = s.nextInt();
			} catch (InputMismatchException e) {
				System.out.println("Invalid entry.");
			}
		}
		OrbitIdentification.readGraphlets(filename,size);
		s.close();
		EquationManager em = generateEquations(size);
		System.out.println(em);
	}

	/**
	 * Generates all orbit representatives in graphlets of the given order.
	 * 
	 * @param order
	 *            The order of which the orbit representatives must be
	 *            calculated.
	 * @return All orbit representatives of the given order.
	 */
	public static Set<OrbitRepresentative> generateOrbits(int order) {
		if (order < 2)
			return null;
		Set<OrbitRepresentative> orbits = new HashSet<OrbitRepresentative>();
		orbits.add(new OrbitRepresentative());
		for (int i = 2; i < order; i++) {
			Set<OrbitRepresentative> newOrbits = new HashSet<OrbitRepresentative>();
			for (OrbitRepresentative or : orbits) {
				newOrbits.addAll(or.generateNext(new LinkedList<Integer>()));
			}
			orbits = newOrbits;
		}
		return orbits;
	}

	/**
	 * Generates all equations for counting graphlets of the given order.
	 * 
	 * @param order
	 *            The order of the graphlets that can be counted with the
	 *            resulting equations.
	 * @return An EquationManager containing all equations.
	 */
	public static EquationManager generateEquations(int order) {
		EquationManager result = new EquationManager(order);
		List<List<Integer>> commons = commons(order-1);
		for (OrbitRepresentative g : OrbitIdentification.getOrbitsOfOrder(order - 1)) {
//			g.calculateSymmetry();
			for (List<Integer> connections : commons) {
				Set<OrbitRepresentative> og = g.generateNext(connections);
				List<Integer> lhs = new ArrayList<Integer>();
				List<OrbitRepresentative> lhsGraphlets = new ArrayList<OrbitRepresentative>();
				for (OrbitRepresentative o : og) {
					o.calculateSymmetry();
					lhs.add(o.orbitSize(o.order() - 1));
					lhsGraphlets.add(o);
				}
				result.addEquation(new Equation(lhsGraphlets, lhs, g,
						connections));
			}
		}
		return result;
	}

	/**
	 * Generates all possible combinations from a collection of a certain size
	 * of any number 0<n<=size elements
	 * 
	 * @param size
	 *            The size of the collection of elements.
	 * @return All possible combinations.
	 */
	private static List<List<Integer>> commons(int size) {
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		for (int i = 1; i < Math.pow(2, size) - 1; i++) {
			List<Integer> common = new ArrayList<Integer>();
			int icopy = i;
			for (int j = 0; j < size; j++) {
				int a = icopy % 2;
				if (a == 1)
					common.add(j);
				icopy /= 2;
			}
			result.add(common);
		}
		return result;
	}
	
}
