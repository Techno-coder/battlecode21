package system;

import battlecode.common.*;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class Node implements Comparable<Node> {
	Double distance;
	MapLocation location;

	public Node(Double distance, MapLocation location) {
		this.distance = distance;
		this.location = location;
	}

	@Override
	public int compareTo(Node o) {
		return distance.compareTo(o.distance);
	}
}

class Traversal {
	Map<MapLocation, Double> distances;
	Map<MapLocation, MapLocation> parents;

	public Traversal(Map<MapLocation, Double> distances,
	                 Map<MapLocation, MapLocation> parents) {
		this.distances = distances;
		this.parents = parents;
	}
}

public strictfp class Path {
	public static Direction find
			(RobotController self, MapLocation target)
			throws GameActionException {
		MapLocation nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		MapLocation origin = self.getLocation();
		Traversal traversal = traverse(self);
		traversal.distances.remove(origin);

		// Heuristically find closest tile to target within range.
		for (Map.Entry<MapLocation, Double> entry : traversal.distances.entrySet()) {
			double stretch = entry.getKey().distanceSquaredTo(target);
			double weight = Math.sqrt(stretch) * (1.0 / 0.1);
			if (entry.getValue() + weight < nearestDistance) {
				nearestDistance = entry.getValue() + weight;
				nearest = entry.getKey();
			}
		}

		// Find next direction on path.
		if (nearest == null) return null;
		self.setIndicatorDot(nearest, 0, 0, 0);
		while (traversal.parents.get(nearest) != origin)
			nearest = traversal.parents.get(nearest);
		return origin.directionTo(nearest);
	}

	private static Traversal traverse
			(RobotController self) throws GameActionException {
		Map<MapLocation, MapLocation> parents = new HashMap<>();
		Map<MapLocation, Double> distances = new HashMap<>();
		PriorityQueue<Node> queue = new PriorityQueue<>();
		queue.add(new Node(0.0, self.getLocation()));
		distances.put(self.getLocation(), 0.0);

		// Calculate distances to all tiles
		// within controller range.
		while (!queue.isEmpty()) {
			Node node = queue.poll();
			if (node.distance > distances.get(node.location))
				continue;

			// Expand into every direction. Occupied tiles are ignored.
			for (Direction direction : Controller.DIRECTIONS) {
				MapLocation next = node.location.add(direction);
				if (!self.canSenseLocation(next)) continue;
				if (self.isLocationOccupied(next)) continue;

				boolean absent = !distances.containsKey(next);
				double cost = 1.0 / self.sensePassability(next);
				double distance = node.distance + cost;

				if (absent || distance < distances.get(next)) {
					queue.add(new Node(distance, next));
					parents.put(next, node.location);
					distances.put(next, distance);
				}
			}
		}

		return new Traversal(distances, parents);
	}
}
