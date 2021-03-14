package system.centre;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import system.Candidate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class Entry {
	Candidate centre;
	int expiry;

	public Entry(Candidate centre, int expiry) {
		this.centre = centre;
		this.expiry = expiry;
	}
}

public class CacheCentres {
	private final RobotController self;
	private final Map<MapLocation, Entry> cache = new HashMap<>();

	static final int CACHE_ROUNDS = 10;

	public CacheCentres(RobotController self) {
		this.self = self;
	}

	public void amend(List<Candidate> candidates) {
		// Temporarily cache the location of candidate
		// centres. This adds a grace period for spawned
		// units to reach and refresh the candidate.
		for (Candidate candidate : candidates) {
			if (candidate.type == RobotType.ENLIGHTENMENT_CENTER) {
				int expiry = self.getRoundNum() + CACHE_ROUNDS;
				Entry entry = new Entry(candidate, expiry);
				cache.put(candidate.target, entry);
			}
		}

		// Add cached centres to the
		// candidate pool and remove stale entries.
		Iterator<Entry> iterator = cache.values().iterator();
		while (iterator.hasNext()) {
			Entry entry = iterator.next();
			if (self.getRoundNum() < entry.expiry)
				candidates.add(entry.centre);
			else iterator.remove();
		}
	}
}
