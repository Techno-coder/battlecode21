package system;

import battlecode.common.*;
import system.centre.*;

import java.util.*;

public class Centre implements Controller {
	private final RobotController self;
	private final Analytics analytics;
	private final CacheCentres centres;
	private final Registry registry;
	private final Spawner spawner;
	private final Auction auction;

	public Centre(RobotController self) {
		this.self = self;
		analytics = new Analytics();
		spawner = new Spawner(self, analytics);
		centres = new CacheCentres(self);
		registry = new Registry(self);
		auction = new Auction(self);
	}

	@Override
	public void run() throws GameActionException {
		spawner.spawn();
		auction.bid();
		target();
		registry.registerNew();
	}

	private void target() throws GameActionException {
		MapLocation origin = self.getLocation();
		List<Candidate> candidates = new ArrayList<>();
		Set<MapLocation> encountered = new HashSet<>();

		// Add immediate enemies.
		Team enemy = self.getTeam().opponent();
		RobotInfo[] enemies = self.senseNearbyRobots(-1, enemy);
		for (RobotInfo robot : enemies) {
			int distance = origin.distanceSquaredTo(robot.location);
			candidates.add(new Candidate(distance, Integer.MAX_VALUE,
					robot.location, robot.type));
		}

		// Filter broadcasting candidates.
		Iterator<Integer> iterator = registry.children.iterator();
		while (iterator.hasNext()) {
			Flag flag = null;
			Integer name = iterator.next();
			if (self.canGetFlag(name))
				flag = Flag.read(self.getFlag(name));

			if (flag == null) {
				// Child has died or
				// has been orphaned.
				iterator.remove();
				continue;
			}

			if (flag.encode() == Flag.EMPTY) continue;
			MapLocation target = flag.delta.apply(origin);

			// Add unique candidate target.
			if (encountered.contains(target)) continue;
			int distance = origin.distanceSquaredTo(target);
			candidates.add(new Candidate(distance, target, flag.type));
			encountered.add(target);
		}

		// Cache centres and rank candidates.
		analytics.recollect(candidates);
		centres.amend(candidates);
		candidates.sort(null);

		// Set flag target.
		if (!candidates.isEmpty()) {
			Candidate candidate = candidates.get(0);
			self.setIndicatorDot(candidate.target, 0, 0, 0);
			Delta delta = new Delta(origin, candidate.target);
			Flag flag = new Flag(true, candidate.type, delta);
			self.setFlag(flag.encode());
		} else {
			// Mark flag with no target.
			self.setFlag(Flag.ORPHAN);
		}
	}
}
