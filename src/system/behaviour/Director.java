package system.behaviour;

import battlecode.common.*;
import system.Controller;
import system.Flag;

public class Director {
	private final RobotController self;
	public RobotInfo hub = null;

	public Director(RobotController self) {
		this.self = self;
	}

	public Flag readFlag() throws GameActionException {
		if (hub != null && self.canGetFlag(hub.ID))
			return Flag.read(self.getFlag(hub.ID));
		return null;
	}

	public void tryRegister() throws GameActionException {
		if (hub != null) return;
		RobotInfo centre = null;

		// Search adjacent tiles for centres.
		for (Direction direction : Controller.DIRECTIONS) {
			MapLocation location = self.getLocation().add(direction);
			if (!self.canSenseLocation(location)) continue;
			RobotInfo robot = self.senseRobotAtLocation(location);
			if (robot == null) continue;

			// Prioritise centres with a lower identifier.
			if (robot.type != RobotType.ENLIGHTENMENT_CENTER) continue;
			if (centre != null && robot.ID > centre.ID) continue;
			if (robot.team != self.getTeam()) continue;
			centre = robot;
		}

		hub = centre;
	}
}
