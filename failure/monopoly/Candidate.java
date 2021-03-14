package failure.monopoly;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class Candidate implements Comparable<Candidate> {
	Integer targetDistance;
	RobotType targetKind;
	MapLocation goal;

	public Candidate(int targetDistance, RobotType targetKind, MapLocation goal) {
		this.targetDistance = targetDistance;
		this.targetKind = targetKind;
		this.goal = goal;
	}

	private Integer kindPriority() {
		if (targetKind == null) return -1;
		switch (targetKind) {
			case ENLIGHTENMENT_CENTER:
				return 1;
			case POLITICIAN:
			case SLANDERER:
			case MUCKRAKER:
			default:
				return 0;
		}
	}

	@Override
	public int compareTo(Candidate o) {
		// Highest kind priority and lowest distance.
		int kind = this.kindPriority().compareTo(o.kindPriority());
		int target = this.targetDistance.compareTo(o.targetDistance);
		return kind == 0 ? target : -kind;
	}
}
