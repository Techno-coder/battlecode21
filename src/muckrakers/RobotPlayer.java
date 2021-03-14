package muckrakers;

import battlecode.common.*;

public strictfp class RobotPlayer {
	static RobotController rc;

	static final Direction[] directions = {
			Direction.NORTH,
			Direction.NORTHEAST,
			Direction.EAST,
			Direction.SOUTHEAST,
			Direction.SOUTH,
			Direction.SOUTHWEST,
			Direction.WEST,
			Direction.NORTHWEST,
	};

	public static void run(RobotController rc) throws GameActionException {
		RobotPlayer.rc = rc;
		while (true) {
			try {
				switch (rc.getType()) {
					case ENLIGHTENMENT_CENTER:
						runEnlightenmentCenter();
						break;
					case POLITICIAN:
					case SLANDERER:
					case MUCKRAKER:
						for (Direction dir : directions) {
							if (rc.canMove(dir)) {
								rc.move(dir);
								break;
							}
						}
						break;
				}

				Clock.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static void runEnlightenmentCenter() throws GameActionException {
		RobotType toBuild = RobotType.MUCKRAKER;
		int influence = 1;
		for (Direction dir : directions) {
			if (rc.canBuildRobot(toBuild, dir, influence)) {
				rc.buildRobot(toBuild, dir, influence);
				break;
			}
		}
	}
}
