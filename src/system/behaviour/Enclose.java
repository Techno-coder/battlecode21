package system.behaviour;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import system.Controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Enclose {
	private final RobotController self;
	private final MapLocation origin;
	private final int radiusSquared;

	public Enclose(RobotController self, int radius) {
		this.self = self;
		radiusSquared = radius * radius;
		origin = self.getLocation();
	}

	public void move() throws GameActionException {
		// Randomise candidate directions.
		List<Direction> directions = Arrays.asList(Controller.DIRECTIONS);
		Collections.shuffle(directions);

		// Find directional location within bounds.
		MapLocation position = self.getLocation();
		for (Direction direction : directions) {
			if (!self.canMove(direction)) continue;
			MapLocation location = position.add(direction);
			int distance = origin.distanceSquaredTo(location);
			if (distance > radiusSquared) continue;
			self.move(direction);
			break;
		}

		// Out of bounds or waiting so
		// attempt to move towards origin.
		Direction direction = position.directionTo(origin);
		if (self.canMove(direction)) self.move(direction);
	}
}
