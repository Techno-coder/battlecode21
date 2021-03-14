package system;

import battlecode.common.*;
import system.behaviour.Broadcast;
import system.behaviour.Diffuse;
import system.behaviour.Director;
import system.behaviour.Enclose;

import java.util.ArrayList;
import java.util.List;

public class Slanderer implements Controller {
	Politician promotion = null;

	RobotController self;
	Broadcast broadcast;
	Director director;
	Enclose enclose;
	Diffuse diffuse;

	private static final int ENCLOSE_RADIUS = 5;

	public Slanderer(RobotController self) {
		this.self = self;
		director = new Director(self);
		broadcast = new Broadcast(self, director);
		enclose = new Enclose(self, ENCLOSE_RADIUS);
		diffuse = new Diffuse(self);
	}

	@Override
	public void run() throws GameActionException {
		// Delegate to politician if promoted.
		if (self.getType() == RobotType.POLITICIAN) {
			if (promotion == null) promotion = promote();
			promotion.run();
		}

		// Broadcast detected entities.
		director.tryRegister();
		broadcast.broadcast();

		// Detect muckrakers.
		Team enemy = self.getTeam().opponent();
		RobotInfo[] enemies = self.senseNearbyRobots(-1, enemy);
		List<RobotInfo> avoid = new ArrayList<>();
		for (RobotInfo robot : enemies)
			if (robot.type == RobotType.MUCKRAKER)
				avoid.add(robot);

		if (!avoid.isEmpty()) {
			// Flee from muckrakers.
			Direction direction = diffuse.direction(avoid);
			if (direction != null) self.move(direction);
		} else enclose.move();
	}

	private Politician promote() {
		return new Politician(self, director, broadcast);
	}
}
