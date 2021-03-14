package system.behaviour;

import battlecode.common.*;
import system.Controller;

import java.util.Arrays;
import java.util.List;

public class Diffuse {
	RobotController self;

	public Diffuse(RobotController self) {
		this.self = self;
	}

	public void fromAllies() throws GameActionException {
		RobotInfo[] allies = self.senseNearbyRobots(-1, self.getTeam());
		Direction direction = direction(Arrays.asList(allies));
		if (direction != null) self.move(direction);
	}

	public Direction direction(List<RobotInfo> avoid) {
		MapLocation origin = self.getLocation();
		double total = distanceTotal(origin, avoid);

		Direction target = null;
		for (Direction direction : Controller.DIRECTIONS) {
			if (!self.canMove(direction)) continue;
			MapLocation location = origin.add(direction);

			// Maximise distance from all avoidant robots.
			double next = distanceTotal(location, avoid);
			if (next > total) {
				target = direction;
				total = next;
			}
		}

		return target;
	}

	private static int distanceTotal
			(MapLocation origin, List<RobotInfo> robots) {
		int distance = 0;
		for (RobotInfo robot : robots)
			distance += origin.distanceSquaredTo(robot.location);
		return distance;
	}
}
