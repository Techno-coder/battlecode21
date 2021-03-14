package system;

import battlecode.common.RobotType;

public class Flag {
	boolean owned;
	RobotType type;
	Delta delta;

	public static final int ORPHAN = new Flag(false).encode();
	public static final int EMPTY = new Flag(true).encode();

	private static final RobotType[] TYPE_VALUES = RobotType.values();

	public Flag(boolean owned, RobotType type, Delta delta) {
		this.owned = owned;
		this.type = type;
		this.delta = delta;
	}

	private Flag(boolean owned) {
		this(owned, RobotType.values()[0], new Delta(0, 0));
	}

	public static Flag read(int data) {
		if (data == ORPHAN) return null;
		int deltaX = data & 0xff;
		int deltaY = (data >> 8) & 0xff;
		Delta delta = new Delta(deltaX, deltaY);

		int rawType = (data >> 16) & 0x3;
		RobotType type = TYPE_VALUES[rawType];
		boolean owned = ((data >> 18) & 0x1) == 1;
		return new Flag(owned, type, delta);
	}

	public int encode() {
		int data = 0;
		data |= delta.x & 0xff;
		data |= (delta.y & 0xff) << 8;
		data |= (type.ordinal() & 0xf) << 16;
		data |= (owned ? 1 : 0) << 18;
		return data;
	}
}
