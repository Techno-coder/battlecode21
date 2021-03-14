package system.behaviour;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import system.Candidate;
import system.Delta;
import system.Flag;

import java.util.ArrayList;
import java.util.List;

public class Broadcast {
	private final RobotController self;
	private final Director director;

	public Broadcast(RobotController self, Director director) {
		this.self = self;
		this.director = director;
	}

	public void broadcast() throws GameActionException {
		// Search surrounding candidate targets.
		List<Candidate> candidates = new ArrayList<>();
		for (RobotInfo robot : self.senseNearbyRobots()) {
			if (robot.team != self.getTeam()) {
				// Enemy or neutral enlightenment centre.
				int distance = self.getLocation().distanceSquaredTo(robot.location);
				candidates.add(new Candidate(distance, robot.location, robot.type));
			}
		}

		// Rank candidates.
		candidates.sort(null);
		RobotInfo hub = director.hub;
		if (hub == null) return;

		// Mark flag with target.
		if (!candidates.isEmpty()) {
			Candidate candidate = candidates.get(0);
			Delta delta = new Delta(hub.location, candidate.target);
			Flag flag = new Flag(true, candidate.type, delta);
			self.setIndicatorDot(candidate.target, 0, 0, 0);
			self.setFlag(flag.encode());
		} else self.setFlag(Flag.EMPTY);
	}
}
