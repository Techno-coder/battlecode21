package system;

import battlecode.common.MapLocation;

public class Delta {
	byte x;
	byte y;

	public Delta(int x, int y) {
		this.x = (byte) x;
		this.y = (byte) y;
	}

	public Delta(MapLocation from, MapLocation to) {
		this(to.x - from.x, to.y - from.y);
	}

	public MapLocation apply(MapLocation from) {
		return from.translate(x, y);
	}
}
