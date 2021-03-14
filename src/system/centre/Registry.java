package system.centre;

import battlecode.common.*;
import system.Controller;
import system.Flag;

import java.util.ArrayList;
import java.util.List;

public class Registry {
	private final RobotController self;
	public final List<Integer> children = new ArrayList<>();

	public Registry(RobotController self) {
		this.self = self;
	}

	public void registerNew() throws GameActionException {
		// Register new adjacent children. Children
		// must be registered after candidate search
		// to prevent immediate erasure.
		for (Direction direction : Controller.DIRECTIONS) {
			MapLocation location = self.getLocation().add(direction);
			if (!self.canSenseLocation(location)) continue;
			RobotInfo robot = self.senseRobotAtLocation(location);
			if (robot == null) continue;

			// Ensure robot is spawned and unregistered.
			if (robot.type == RobotType.ENLIGHTENMENT_CENTER) continue;
			if (Flag.read(self.getFlag(robot.ID)) != null) continue;
			if (robot.team != self.getTeam()) continue;

			// Search for conflicting centres. Conflicts are
			// resolved through the ranking of identifiers.
			boolean preceded = false;
			for (Direction other : Controller.DIRECTIONS) {
				MapLocation otherLocation = location.add(other);
				if (!self.canSenseLocation(otherLocation)) continue;
				RobotInfo target = self.senseRobotAtLocation(otherLocation);
				if (target == null) continue;

				// Identifier must be the lowest of conflicting centres.
				if (target.type != RobotType.ENLIGHTENMENT_CENTER) continue;
				if (target.team != self.getTeam()) continue;
				preceded |= target.ID < self.getID();
			}

			if (preceded) continue;
			children.add(robot.ID);
		}
	}
}
