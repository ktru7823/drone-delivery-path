import java.util.*;

class Vertex {
	int id;
	int topId;
	double trueDist;
	Drone drone;
	List<Edge> outgoingEdges;
	List<Edge> incomingEdges;

	public Vertex(int vertexId) {
		id = vertexId;
		incomingEdges = new ArrayList<>();
	}

	public Vertex(int vertexId, double droneRate, double droneCooldown) {
		id = vertexId;
		drone = new Drone(vertexId, droneRate, droneCooldown);
		outgoingEdges = new ArrayList<>();
		incomingEdges = new ArrayList<>();
	}

	public int getId() { return id; }
	public int getTopId() { return topId; }
	public double getTrueDist() { return trueDist; }
	public Drone getDrone() { return drone; }
	public List<Edge> getOutgoingEdges() { return outgoingEdges; }
	public List<Edge> getIncomingEdges() { return incomingEdges; }

	public void setTopId(int newRank) { topId = newRank; }
	public void setTrueDist(double dist) { trueDist = dist; }
	public void addOutgoingEdge(Edge newEdge) { outgoingEdges.add(newEdge); }
	public void addIncomingEdge(Edge newEdge) { incomingEdges.add(newEdge); }

}

class Edge {
	Vertex fromVertex;
	Vertex toVertex;
	double weight;

	public Edge(Vertex vertexA, Vertex vertexB, double weight) {
		fromVertex = vertexA;
		toVertex = vertexB;
		this.weight = weight;
	}

	public Vertex getOriginVertex() { return fromVertex; }
	public Vertex getDestVertex() { return toVertex; }
	public double getWeight() { return weight; }

}

class Drone {
	double rate;
	double cooldown;

	public Drone(int id, double droneRate, double droneCooldown) {
		rate = droneRate;
		cooldown = droneCooldown;
	}

	public double calculateWeight(Edge e) { return e.getWeight() * rate + cooldown; }
}

public class A1 {

	private static boolean readInput(Map<Integer, Vertex> vList) {

		boolean valid = true;
		Scanner keyboard = new Scanner(System.in);
		try {
			int vertexCount = Integer.parseInt(keyboard.nextLine());
			for (int i = 0; i < vertexCount; i++) {
				String[] input = keyboard.nextLine().split(" ");
				int id = Integer.parseInt(input[0]);
				if (i == vertexCount - 1 && input.length == 1) {
					vList.put(id, new Vertex(id));
					continue;
				}
				double rate = Double.parseDouble(input[1]);
				double cooldown = Double.parseDouble(input[2]);

				vList.put(id, new Vertex(id, rate, cooldown));
			}

			int edgeCount = Integer.parseInt(keyboard.nextLine());

			for (int i = 0; i < edgeCount; i++) {
				String[] edgeInput = keyboard.nextLine().split(" ");

				int a = Integer.parseInt(edgeInput[0]);
				int b = Integer.parseInt(edgeInput[1]);
				double weight = Double.parseDouble(edgeInput[2]);

				Vertex vertexA = vList.get(a);
				Vertex vertexB = vList.get(b);
				Edge e = new Edge(vertexA, vertexB, weight);

				vertexA.addOutgoingEdge(e);
				vertexB.addIncomingEdge(e);
			}

		} catch (NumberFormatException|IndexOutOfBoundsException|NullPointerException e) {
			System.err.println("\nerror: faulty input");
			valid = false;
		}

		keyboard.close();
		return valid;
	}

	private static Map<Integer, Vertex> topologicalSort(Map<Integer, Vertex> vertexMap) {
		Map<Integer, Vertex> topSorted = new TreeMap<>();
		Queue<Integer> queue = new LinkedList<>();
		int vSize = vertexMap.size();
		int[] inDegree = new int[vSize];
		int counter = 0;

		for (int i = 0; i < vSize; i++) {
			inDegree[i] = vertexMap.get(i).getIncomingEdges().size();
			if (inDegree[i] == 0) {
				queue.add(i);
			}
		}

		while (!queue.isEmpty()) {
			int index = queue.remove();
			Vertex v = vertexMap.get(index);
			topSorted.put(counter, v);
			counter++;

			List<Edge> outEdges = v.getOutgoingEdges();
			if (outEdges == null) {
				continue;
			}
			for (Edge e : outEdges) {
				int vIndex = e.getDestVertex().getId();
				inDegree[vIndex]--;
				if (inDegree[vIndex] == 0) {
					queue.add(vIndex);
				}
			}
		}
		
		for (int i = 0; i < vSize; i++) {
			topSorted.get(i).setTopId(i);
		}
		
		return topSorted;
	}
	
	public static double[][] getShortestPaths(Map<Integer, Vertex> topSorted, int vertexCount) {
		
		// tempDist[X][Y] = shortest distance from X to Y using drone X only
		double[][] tempDist = new double[vertexCount][];

		for (int i = 0; i < vertexCount; i++) {
			tempDist[i] = new double[vertexCount];
			for (int j = 0; j < vertexCount; j++) {
				if (i == j) {
					tempDist[i][j] = 0;
				} else {
					tempDist[i][j] = Double.MAX_VALUE;
				}
			}
		}

		// get shortest path from each vertex to every other vertex using one drone
		for (int i = 0; i < vertexCount; i++) {
			Vertex v = topSorted.get(i);
			Drone droneA = v.getDrone();

			for (int j = i; j < vertexCount; j++) {
				Vertex vertexA = topSorted.get(j);
				List<Edge> outEdges = vertexA.getOutgoingEdges();

				if (outEdges == null) {
					continue;
				}
				
				for (Edge e : outEdges) {
					int fromVertId = e.getOriginVertex().getTopId();
					Vertex toVert = e.getDestVertex();
					int toVertId = toVert.getTopId();
					double weight = droneA.calculateWeight(e);

					if (tempDist[i][toVertId] > tempDist[i][fromVertId] + weight) {
						tempDist[i][toVertId] = tempDist[i][fromVertId] + weight;
					}
				}
			}
		}
		
		return tempDist;
	}
	
	
	public static double[] getOptimalPaths(double[][] tempDist, int vertexCount) {
		
		// trueDist[A] = shortest distance from A to S using any combination of drones
		double[] trueDist = new double[vertexCount];
			
		for (int i = 0; i < vertexCount - 1; i++) {
			trueDist[i] = Double.MAX_VALUE;
		}
		trueDist[vertexCount - 1] = 0;

		for (int i = vertexCount - 2; i >= 0; i--) {
			for (int j = i + 1; j < vertexCount; j++) {
				if (trueDist[i] > tempDist[i][j] + trueDist[j]) {
					trueDist[i] = tempDist[i][j] + trueDist[j];
				}
			}
		}
		
		return trueDist;
	}

	public static void main(String[] args) {
		Map<Integer, Vertex> vertexList = new TreeMap<>();

		if (!readInput(vertexList)) {
			return;
		}

		int vertexCount = vertexList.size();
		Map<Integer, Vertex> topSorted = topologicalSort(vertexList);

		// tempShortestPaths[X][Y] = shortest distance from X to Y using drone X only
		double[][] tempShortestPaths = getShortestPaths(topSorted, vertexCount);

		// optimalPaths[A] = shortest distance from A to S using any combination of drones
		double[] optimalPaths = getOptimalPaths(tempShortestPaths, vertexCount);
		
		for (int i = 0; i < vertexCount; i++) {
			topSorted.get(i).setTrueDist(optimalPaths[i]);
		}

		for (int i = 1; i < vertexCount; i++) {
			System.out.printf("%.2f\n", vertexList.get(i).getTrueDist());
		}
	}
}
