package system;

import battlecode.common.Direction;
import battlecode.common.GameActionException;

public interface Controller {
	Direction[] DIRECTIONS = {
			Direction.NORTH,
			Direction.NORTHEAST,
			Direction.EAST,
			Direction.SOUTHEAST,
			Direction.SOUTH,
			Direction.SOUTHWEST,
			Direction.WEST,
			Direction.NORTHWEST,
	};

	void run() throws GameActionException;
}
