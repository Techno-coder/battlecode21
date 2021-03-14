package system;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class Candidate implements Comparable<Candidate> {
	public Integer distance;
	public Integer priority;
	public MapLocation target;
	public RobotType type;

	public Candidate(Integer distance, Integer priority,
	                 MapLocation target, RobotType type) {
		this.distance = distance;
		this.priority = priority;
		this.target = target;
		this.type = type;
	}

	public Candidate(int distance, MapLocation target, RobotType type) {
		this(distance, priority(type), target, type);
	}

	private static Integer priority(RobotType type) {
		if (type == null) return -1;
		switch (type) {
			case ENLIGHTENMENT_CENTER:
				return 2;
			case MUCKRAKER:
				return 1;
			case POLITICIAN:
			case SLANDERER:
			default:
				return 0;
		}
	}

	@Override
	public int compareTo(Candidate o) {
		// Highest kind priority and lowest distance.
		int kind = this.priority.compareTo(o.priority);
		int target = this.distance.compareTo(o.distance);
		return kind == 0 ? target : -kind;
	}
}
