package system;

import battlecode.common.*;

public strictfp class RobotPlayer {
	public static void run(RobotController self) throws GameActionException {
		Controller controller;
		switch (self.getType()) {
			case ENLIGHTENMENT_CENTER:
				controller = new Centre(self);
				break;
			case POLITICIAN:
				controller = new Politician(self);
				break;
			case SLANDERER:
				controller = new Slanderer(self);
				break;
			case MUCKRAKER:
				controller = new Muckraker(self);
				break;
			default:
				return;
		}

		while (true) {
			try {
				controller.run();
				Clock.yield();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
