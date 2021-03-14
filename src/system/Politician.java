package system;

import battlecode.common.*;
import system.behaviour.*;

// TODO: attack even if hub is null
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

		// Calculate target location.
		MapLocation origin = self.getLocation();
		MapLocation target = flag.delta.apply(director.hub.location);
		int limit = RobotType.POLITICIAN.actionRadiusSquared;
		int distance = origin.distanceSquaredTo(target);

		// Find target at location.
		RobotInfo robot = null;
		if (self.canSenseLocation(target))
			robot = self.senseRobotAtLocation(target);

		if (robot != null) {
			// Strike largest area while destructing target.
			for (int r = limit; r >= distance; --r) {
				int count = self.senseNearbyRobots(r).length;
				int effect = self.getInfluence() / count;
				if (effect < robot.conviction) continue;
				if (!self.canEmpower(r)) continue;
				self.empower(r);
				return;
			}

			// Otherwise, strike within threshold.
			if (distance <= THRESHOLD)
				if (self.canEmpower(THRESHOLD))
					self.empower(THRESHOLD);
		}

		// Otherwise, move towards target.
		Direction direction = path.find(target);
		if (direction != null && self.canMove(direction)) {
			self.move(direction);
			return;
		}

		// Otherwise, strike if enemies exist.
		Team enemy = self.getTeam().opponent();
		RobotInfo[] enemies = self.senseNearbyRobots(limit, enemy);
		if (enemies.length == 0) return;

		// Strike full area.
		if (self.canEmpower(limit))
			self.empower(limit);
	}
}
