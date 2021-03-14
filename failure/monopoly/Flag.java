package failure.monopoly;

import battlecode.common.RobotType;

public class Flag {
	private final int DISTANCE_MASK = 0xffff;
	private final int KIND_SHIFT = 2 * 8;
	private final int KIND_MASK = 0xf;

	public int data;

	public Flag() {
		this(0);
	}

	private Flag(int data) {
		this.data = data;
	}

	public static Flag read(int flag) {
		if (flag == 0) return null;
		return new Flag(flag);
	}

	public int getDistance() {
		// Maximum distance is 2 * 64^2 (2 bytes).
		return data & DISTANCE_MASK;
	}

	public void setDistance(int distance) {
		data &= ~DISTANCE_MASK;
		data |= distance;
	}

	public RobotType getKind() {
		int kind = (data >> KIND_SHIFT) & KIND_MASK;
		switch (kind) {
			case 0:
				return RobotType.ENLIGHTENMENT_CENTER;
			case 1:
				return RobotType.POLITICIAN;
			case 2:
				return RobotType.SLANDERER;
			case 3:
				return RobotType.MUCKRAKER;
			default:
				assert false;
				return null;
		}
	}

	public void setKind(RobotType kind) {
		int value = 0;
		switch (kind) {
			case ENLIGHTENMENT_CENTER:
				value = 0;
				break;
			case POLITICIAN:
				value = 1;
				break;
			case SLANDERER:
				value = 2;
				break;
			case MUCKRAKER:
				value = 3;
				break;
		}

		data &= ~(KIND_MASK << KIND_SHIFT);
		data |= value << KIND_SHIFT;
	}
}
