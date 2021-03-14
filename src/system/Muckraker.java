package system;

import battlecode.common.*;
import system.behaviour.Broadcast;
import system.behaviour.Director;
import system.behaviour.Diffuse;

public class Muckraker implements Controller {
	RobotController self;
	Broadcast broadcast;
	Director director;
	Diffuse diffuse;

	public Muckraker(RobotController self) {
		this.self = self;
		director = new Director(self);
		broadcast = new Broadcast(self, director);
		diffuse = new Diffuse(self);
	}

	@Override
	public void run() throws GameActionException {
		director.tryRegister();
		broadcast.broadcast();

		// Expose slanderers in vicinity.
		for (MapLocation location : self.detectNearbyRobots()) {
			if (!self.canExpose(location)) continue;
			self.expose(location);
			return;
		}

		// Otherwise, spread apart from allies.
		diffuse.fromAllies();
	}
}
