package equations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class OrbitRepresentative implements Comparable<OrbitRepresentative>{

	protected Set<Edge> edges;
	protected int order;
	protected Set<Set<Edge>> permutedEdges;
	protected List<Set<Integer>> orbits;

	/**
	 * Creates a new orbit-graphlet representing the orbit of the 2-graphlet.
	 */
	public OrbitRepresentative() {
		edges = new HashSet<Edge>();
		edges.add(new Edge(0, 1));
		order = 2;
		orbits = new ArrayList<Set<Integer>>();
	}

	/**
	 * Copies a given OrbitGraphlet.
	 * 
	 * @param g
	 *            The OrbitGraphlet to be copied.
	 */
	public OrbitRepresentative(OrbitRepresentative g) {
		edges = new HashSet<Edge>(g.edges);
		order = g.order;
		orbits = g.orbits;
	}

	/**
	 * Creates a new OrbitGraphlet from a set of edges and its order.
	 * 
	 * @param e
	 *            A Set containing the edges of the new graphlet.
	 * @param order
	 *            The order of the new OrbitGraphlet.
	 */
	public OrbitRepresentative(Set<Edge> e, int order) {
		edges = e;
		this.order = order;
	}

	/**
	 * Generates all possible permutations of the integers ranging from 1 to
	 * size-1. All permutations also contain the number 0 at the beginning of
	 * the permutation, 0 being the node that cannot be permuted.
	 * 
	 * @param size
	 *            The number of elements of the permutation.
	 * @return A list containing lists of integers representing the
	 *         permutations.
	 */
	public static List<List<Integer>> generatePermutations(int size) {
		List<List<Integer>> results = new ArrayList<List<Integer>>();
		results.add(new ArrayList<Integer>());
		results.get(0).add(0);
		for (int i = 1; i < size; i++) {
			results = generatePermutations(results);
		}
		return results;
	}

	
	private static List<List<Integer>> generatePermutations(
			List<List<Integer>> current) {
		List<List<Integer>> permutations = new ArrayList<List<Integer>>();
		int length = current.get(0).size();
		for (int i = 0; i < current.size(); i++) {
			for (int j = 0; j < length; j++) {
				List<Integer> permutation = new ArrayList<Integer>(
						current.get(i));
				permutation.add(j, length);
				permutations.add(permutation);
			}
			List<Integer> permutation = new ArrayList<Integer>(current.get(i));

			permutation.add(length);
			permutations.add(permutation);
		}
		return permutations;
	}

	/**
	 * Rewrites a set of edges so that each node number is changed to the value
	 * corresponding to it in the given list.
	 * 
	 * @param edges
	 *            The set of edges that must be rewritten.
	 * @param order
	 *            A list which defines how each number must be changed. Each
	 *            time the int 0 appears in the set of edges, it will be changed
	 *            into the int on position 0 in the list, and so on.
	 * @return The rewritten set of edges.
	 */
	public static Set<Edge> permute(Set<Edge> edges, List<Integer> order) {
		Set<Edge> result = new HashSet<Edge>();
		for (Edge e : edges) {
			result.add(new Edge(order.get(e.getNodes()[0]), order.get(e
					.getNodes()[1])));
		}
		return result;
	}

	/**
	 * Saves all orbit-graphlets that are isomorphic to this one. Calculating
	 * isomorphism is done by changing the node names according to all possible
	 * permutations - which means the '0' node is not changed. All possible
	 * permutations give rise to an isomorphic graph, but because edge sets are
	 * saved, only those that are not exactly identical will be saved.
	 * 
	 * Also calculates the sub-orbits of this orbit-graphlet. When a permutation
	 * results in the set of edges being unchanged, all changed nodes are in the
	 * same orbit. The maximal sets of such nodes are the orbit-graphlet's
	 * sub-orbits.
	 */
	public void calculateSymmetry() {
		permutedEdges = new HashSet<Set<Edge>>();
		
		List<List<Integer>> permutations = generatePermutations(order - 1);
		for (List<Integer> l : permutations) {
			for (int j = 0; j < l.size(); j++) {
				l.set(j, l.get(j) + 1);
			}
			l.add(0, 0);
		}
		List<Set<Integer>> orbitsTemporary = new ArrayList<Set<Integer>>();
		for (List<Integer> permutation : permutations) {
			Set<Edge> s = permute(edges, permutation);
			permutedEdges.add(s);
			if (edges.equals(s)) {
				for (int i = 0; i < permutation.size(); i++) {
					Set<Integer> permuted = new HashSet<Integer>();
					permuted.add(i);
					permuted.add(permutation.get(i));
					orbitsTemporary.add(permuted);
				}
			}
		}
		for (int i = 0; i < orbitsTemporary.size() - 1; i++) {
			for (int j = i + 1; j < orbitsTemporary.size(); j++) {
				if (orbitsTemporary.get(i).containsAll(orbitsTemporary.get(j))) {
					orbitsTemporary.remove(j);
					j--;
				} else if (orbitsTemporary.get(j).containsAll(
						orbitsTemporary.get(i))) {
					orbitsTemporary.remove(i);
					i--;
					j--;
					break;
				} else {
					List<Integer> undoubled = new ArrayList<Integer>();
					for (int k : orbitsTemporary.get(j)) {
						if (!orbitsTemporary.get(i).contains(k)) {
							undoubled.add(k);
						}
					}
					if (undoubled.size() != orbitsTemporary.get(j).size()) {
						orbitsTemporary.get(i).addAll(undoubled);
						orbitsTemporary.remove(j);
						i--;
						break;
					}
				}
			}
		}
		orbits = new ArrayList<Set<Integer>>();
		orbits.addAll(orbitsTemporary);
	}

	/**
	 * 
	 * @return A set containing the edges of this orbit-graphlet.
	 */
	public Set<Edge> getEdges() {
		return edges;
	}

	/**
	 * 
	 * @return The order of this orbit-graphlet.
	 */
	public int order() {
		return order;
	}

	/**
	 * Returns the size of the sub-orbit the given node is in.
	 * 
	 * @param node
	 *            The node of which the orbit size must be found.
	 * @return The size of the sub-orbit the given node is in.
	 */
	public int orbitSize(int node) {
		for (Set<Integer> n : orbits) {
			if (n.contains(node)) {
				return n.size();
			}
		}
		return -1;
	}

	/**
	 * Adds a new node to this orbit-graphlet, connected to the nodes given in
	 * the argument.
	 * 
	 * @param connected
	 *            An array containing the adjacency of the new node to the nodes
	 *            already present. Its length must be equal to the order of the
	 *            original graphlet. If a value is true, the new node is
	 *            connected to the corresponding node; if the value is false, it
	 *            is not.
	 */
	public void addNode(boolean[] connected) {
		assert (connected.length == order);
		for (int i = 0; i < connected.length; i++) {
			if (connected[i]) {
				edges.add(new Edge(i, order));
			}
		}
		order++;
	}

	/**
	 * Generates all orbit-graphlets that can be made from this orbit-graphlet
	 * by adding one node. This node must be connected to the nodes in the given
	 * list, but may or may not be connected to all other nodes. When the list
	 * is empty, all possible combinations of connections will be generated.
	 * 
	 * @param connected
	 *            A list of the numbers of nodes the new node must be connected
	 *            to.
	 * @return A set containing all orbit-graphlets that can be created by
	 *         adding one node with at least the specified connections.
	 */
	public SortedSet<OrbitRepresentative> generateNext(List<Integer> connected) {
		SortedSet<OrbitRepresentative> result = new TreeSet<OrbitRepresentative>();
		for (int i = connected.size() == 0 ? 1 : 0; i < Math.pow(2, order
				- connected.size()); i++) {
			boolean[] connections = new boolean[order];
			int icopy = i;
			int counter = 0;
			for (int j = 0; j < order; j++) {
				if (counter < connected.size() && connected.get(counter) <= j) {
					connections[j] = true;
					counter++;
				} else {
					connections[j] = icopy % 2 == 1;
					icopy /= 2;
				}
			}
			OrbitRepresentative h;
			h = new OrbitRepresentative(this);
			h.addNode(connections);
			h.calculateSymmetry();
			result.add(h);
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + order;
		result = prime * result + edges.size();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof OrbitRepresentative))
			return false;
		OrbitRepresentative other = (OrbitRepresentative) obj;
		if (order != other.order)
			return false;
		if (edges.size() != other.edges.size()) {
			return false;
		}
		if (permutedEdges == null || permutedEdges.size() == 0) {
			calculateSymmetry();
		}
		if (permutedEdges.contains(other.edges)) {
			return true;
		}
		return false;

	}
	
	
	@Override
	public String toString(){
		if(Program.latex){
		int name =OrbitIdentification.identifyOrbit(this);
		String result = ("\\[\\begin{split}P_{"+ name+"}(");
		String list = "x";
		for(char i=0;i<order-1;i++){
			list+=","+(char)('a'+i);
		}
		result+=list +") = ";
		result+="\\{ &\\{"+list+" \\} \\subset V(G) | \\\\";
		String edges="";
		String noEdges = "";
		for(int i=0;i<order-1;i++){
			for(int j=i+1;j<order;j++){
				Edge e = new Edge(i,j);
				if(this.edges.contains(e)){
					edges+= e.toString() + ", ";
				}else{
					noEdges+=e+", ";
				}
			}
		}
		if(edges.length()!=0){
			result+="&\\{"+edges.substring(0, edges.length()-2)+"\\} \\subset E(G) ";
			if(noEdges.length()!=0){
				result+="\\wedge \\\\";
			}
		}
		if(noEdges.length()!=0){
			result+="&\\{"+noEdges.substring(0, noEdges.length()-2)+" \\} \\cap E(G) = \\emptyset ";
		}
		result +="\\}\\end{split} \\]";
		
		return result;
		}else{
			int name =OrbitIdentification.identifyOrbit(this);
			String result = ("P_"+ name+" (");
			String list = "x";
			for(char i=0;i<order-1;i++){
				list+=","+(char)('a'+i);
			}
			result+=list +") = ";
			result+="{{"+list+"} in V(G) | ";
			String edges="";
			String noEdges = "";
			for(int i=0;i<order-1;i++){
				for(int j=i+1;j<order;j++){
					Edge e = new Edge(i,j);
					if(this.edges.contains(e)){
						edges+= e.toString() + ", ";
					}else{
						noEdges+=e+", ";
					}
				}
			}
			if(edges.length()!=0){
				result+="{"+edges.substring(0, edges.length()-2)+"} in E(G)";
				if(noEdges.length()!=0){
					result+=" & ";
				}
			}
			if(noEdges.length()!=0){
				result+="{"+noEdges.substring(0, noEdges.length()-2)+"} not in E(G)";
			}
			result +="}";
			
			return result;
		}
	}

	@Override
	public int compareTo(OrbitRepresentative o) {
		int a = OrbitIdentification.identifyOrbit(this);
		int b = OrbitIdentification.identifyOrbit(o);
		return (a==b?0:a>b?1:-1);
	}
}
