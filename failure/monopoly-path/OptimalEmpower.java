package system;

import battlecode.common.*;
import system.behaviour.*;

import java.util.*;

public class Politician implements Controller {
	RobotController self;
	Broadcast broadcast;
	Director director;
	Diffuse diffuse;
	Path path;

	private static final int THRESHOLD = 2;

	public Politician(RobotController self,
	                  Director director,
	                  Broadcast broadcast) {
		this.self = self;
		this.director = director;
		this.broadcast = broadcast;
		diffuse = new Diffuse(self);
		path = new Path(self);
	}

	public Politician(RobotController self) {
		this.self = self;
		director = new Director(self);
		broadcast = new Broadcast(self, director);
		diffuse = new Diffuse(self);
		path = new Path(self);
	}

	@Override
	public void run() throws GameActionException {
		director.tryRegister();
		broadcast.broadcast();

		Flag flag = director.readFlag();
		if (flag == null) {
			// Spread apart from allies.
			diffuse.fromAllies();
			return;
		}

		// Strike within threshold distance.
		MapLocation origin = self.getLocation();
		MapLocation target = flag.delta.apply(director.hub.location);
		if (origin.distanceSquaredTo(target) <= THRESHOLD) {
			if (self.canEmpower(THRESHOLD)) {
				self.empower(THRESHOLD);
				return;
			}
		}

		// Move towards target.
		Direction direction = path.find(target);
		if (direction != null && self.canMove(direction)) {
			self.move(direction);
			return;
		}

		// Rank reachable targets by distance.
		int actionRadius = RobotType.POLITICIAN.actionRadiusSquared;
		RobotInfo[] robots = self.senseNearbyRobots(actionRadius);
		Arrays.sort(robots, (a, b) -> {
			int aDistance = origin.distanceSquaredTo(a.location);
			int bDistance = origin.distanceSquaredTo(b.location);
			return aDistance - bDistance;
		});

		// Rank enemies by largest conviction.
		Comparator<Integer> order = Collections.reverseOrder();
		PriorityQueue<Integer> enemies = new PriorityQueue<>(order);
		int effective = self.getInfluence() - GameConstants.EMPOWER_TAX;

		int totalUnits = 0;
		int targetCount = 0;
		Integer radius = null;
		for (RobotInfo robot : robots) {
			++totalUnits;
			if (robot.team != self.getTeam())
				continue;

			// Prune persistent enemies.
			enemies.offer(robot.conviction);
			int effect = effective / totalUnits;
			while (!enemies.isEmpty() && enemies.peek() > effect)
				enemies.remove();

			// Optimise for largest kill count.
			if (enemies.size() <= targetCount) continue;
			radius = origin.distanceSquaredTo(robot.location);
		}

		// Empower if enemies exist.
		if (radius != null && self.canEmpower(radius))
			self.empower(radius);
	}
}
