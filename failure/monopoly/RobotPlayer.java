package failure.monopoly;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.List;

public strictfp class RobotPlayer {
	static final Direction[] DIRECTIONS = {
			Direction.NORTH,
			Direction.NORTHEAST,
			Direction.EAST,
			Direction.SOUTHEAST,
			Direction.SOUTH,
			Direction.SOUTHWEST,
			Direction.WEST,
			Direction.NORTHWEST,
	};

	public static void run(RobotController self) throws GameActionException {
		while (true) {
			try {
				switch (self.getType()) {
					case ENLIGHTENMENT_CENTER:
						runCentre(self);
						break;
					case POLITICIAN:
						runPolitician(self);
						break;
					case SLANDERER:
					case MUCKRAKER:
						break;
				}

				Clock.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static void runCentre(RobotController self) throws GameActionException {
		for (Direction direction : DIRECTIONS) {
			if (self.canBuildRobot(RobotType.POLITICIAN, direction, self.getInfluence())) {
				self.buildRobot(RobotType.POLITICIAN, direction, self.getInfluence());
				break;
			}
		}
	}

	static void runPolitician(RobotController self) throws GameActionException {
		Flag flag = Flag.read(self.getFlag(self.getID()));
		RobotInfo[] robots = self.senseNearbyRobots();
		MapLocation origin = self.getLocation();

		List<Candidate> candidates = new ArrayList<>();
		for (RobotInfo robot : robots) {
			if (robot.team == self.getTeam()) {
				Flag other = Flag.read(self.getFlag(robot.ID));
				if (other == null) continue;

				// Always move towards smaller distance candidates.
				int distance = origin.distanceSquaredTo(robot.location) + other.getDistance();
				if (flag != null && other.getDistance() >= flag.getDistance()) continue;
				candidates.add(new Candidate(distance, other.getKind(), robot.location));
			} else {
				// Enemy or neutral enlightenment centre.
				int distance = origin.distanceSquaredTo(robot.location);
				candidates.add(new Candidate(distance, robot.type, robot.location));
			}
		}

		candidates.sort(null);
		if (!candidates.isEmpty()) {
			Candidate candidate = candidates.get(0);

			// Strike within threshold distance.
			if (candidate.targetDistance <= 2)
				if (self.canEmpower(2))
					self.empower(2);

			// Set flag to target.
			flag = new Flag();
			flag.setDistance(candidate.targetDistance);
			flag.setKind(candidate.targetKind);
			self.setFlag(flag.data);

			// TODO: optimize path finding
			// Move towards candidate.
			Direction target = origin.directionTo(candidate.goal);
			if (self.canMove(target)) self.move(target);
			else {
				// TODO: use path finding mechanism
				// Move in any direction.
				for (Direction direction : DIRECTIONS) {
					if (self.canMove(direction)) {
						self.move(direction);
					}
				}
			}
		} else {
			if (flag != null) {
				// Reset flag and set flag delay. Flag delay
				// ensures no cyclic resetting will occur.
				self.setFlag(new Flag().data);
			}

			// Spread apart from allies.
			RobotInfo[] allies = self.senseNearbyRobots(-1, self.getTeam());
			Direction target = spreadDirection(self, allies);
			if (target != null) self.move(target);
		}
	}

	static Direction spreadDirection(RobotController self, RobotInfo[] avoid) {
		MapLocation origin = self.getLocation();
		RobotInfo nearestAvoid = nearestRobot(origin, avoid);
		if (nearestAvoid == null) return null;

		Direction target = null;
		int nearest = origin.distanceSquaredTo(nearestAvoid.location);
		for (Direction direction : DIRECTIONS) {
			MapLocation nextOrigin = origin.add(direction);
			RobotInfo nextNearest = nearestRobot(nextOrigin, avoid);
			int next = nextOrigin.distanceSquaredTo(nextNearest.location);

			// Maximise distance from all avoidant robots.
			if (next > nearest && self.canMove(direction)) {
				target = direction;
				nearest = next;
			}
		}

		return target;
	}

	static RobotInfo nearestRobot(MapLocation origin, RobotInfo[] robots) {
		int minimumDistance = Integer.MAX_VALUE;
		RobotInfo nearest = null;

		for (RobotInfo robot : robots) {
			int distance = origin.distanceSquaredTo(robot.location);
			if (distance < minimumDistance) {
				minimumDistance = distance;
				nearest = robot;
			}
		}

		return nearest;
	}
}
