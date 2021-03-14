package system.behaviour;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.ArrayList;
import java.util.List;

public class Path {
	private final RobotController self;

	public Path(RobotController self) {
		this.self = self;
	}

	public Direction find(MapLocation target) throws GameActionException {
		MapLocation origin = self.getLocation();
		Direction direct = origin.directionTo(target);
		Direction right = direct.rotateRight();
		Direction left = direct.rotateLeft();

		// Find easiest progress developing direction.
		List<Direction> candidates = new ArrayList<>();
		if (self.canMove(direct)) candidates.add(direct);
		if (self.canMove(right)) candidates.add(right);
		if (self.canMove(left)) candidates.add(left);
		Direction easiest = easiest(candidates);
		if (easiest != null) return easiest;

		// Find surrounding direction.
		for (int i = 0; i < 2; ++i) {
			left = left.rotateLeft();
			right = right.rotateRight();
			if (self.canMove(right)) return right;
			if (self.canMove(left)) return left;
		}

		// Otherwise, try opposite direction.
		if (self.canMove(direct.opposite()))
			return direct.opposite();
		return null;
	}

	private Direction easiest(List<Direction> candidates) throws GameActionException {
		double ease = 0.0;
		Direction easiest = null;
		for (Direction direction : candidates) {
			MapLocation location = self.getLocation().add(direction);
			if (!self.canSenseLocation(location)) continue;
			double other = self.sensePassability(location);
			if (other <= ease) continue;
			easiest = direction;
			ease = other;
		}

		return easiest;
	}
}
