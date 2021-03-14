package system.centre;

import battlecode.common.*;
import system.Controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static battlecode.common.GameConstants.MAP_MAX_HEIGHT;
import static battlecode.common.GameConstants.MAP_MAX_WIDTH;

public class Spawner {
	private final RobotController self;
	private final Analytics analytics;

	private double rateSlanderer = 0.0;
	private double ratePolitician = 0.5;
	private double rateMuckraker = 1.0;

	private static final double RATE = 0.005;
	private static final double BIAS_SLANDERER = -0.25;
	private static final double BIAS_POLITICIAN = 0.5;
	private static final double BIAS_MUCKRAKER = 0.1;
	private static final double INFLUENTIAL = 0.5;
	private static final int BASE_INFLUENCE = 25;
	private static final int BASE_EXPONENT = 3;

	public Spawner(RobotController self, Analytics analytics) {
		this.self = self;
		this.analytics = analytics;
	}

	public void spawn() throws GameActionException {
		adaptAnalytics();
		RobotType type = spawnType();
		Integer influence = influence(type);
		if (influence == null) return;

		// Randomise candidate directions.
		List<Direction> directions = Arrays.asList(Controller.DIRECTIONS);
		Collections.shuffle(directions);

		for (Direction direction : directions) {
			if (self.canBuildRobot(type, direction, influence)) {
				self.buildRobot(type, direction, influence);
				return;
			}
		}
	}

	private void adaptAnalytics() {
		int total = Math.max(analytics.total(), 1);
		double muckrakers = (double) analytics.muckrakers / total;
		double slanderers = (double) analytics.slanderers / total;

		// Reduce slanderers in presence of muckrakers.
		rateSlanderer = adjust(rateSlanderer, 1.0 - muckrakers, BIAS_SLANDERER);

		// Increase politicians in presence of muckrakers.
		ratePolitician = adjust(ratePolitician, muckrakers, BIAS_POLITICIAN);

		// Increase muckrakers in presence of slanderers.
		rateMuckraker = adjust(rateMuckraker, slanderers, BIAS_MUCKRAKER);

		// Constrain rates.
		rateSlanderer = constrain(rateSlanderer, 0.0, 1.0);
		ratePolitician = constrain(ratePolitician, 0.0, 1.0);
		rateMuckraker = constrain(rateMuckraker, 0.0, 1.0);
	}

	private static double adjust(double base, double target, double bias) {
		return base + constrain(target - base + bias, -RATE, RATE);
	}

	private static double constrain(double value, double minimum, double maximum) {
		return Math.min(Math.max(value, minimum), maximum);
	}

	private RobotType spawnType() {
		double totalRate = 0.0;
		totalRate += rateSlanderer;
		totalRate += ratePolitician;
		totalRate += rateMuckraker;

		double value = Math.random() * totalRate;
		if (value <= rateSlanderer)
			return RobotType.SLANDERER;
		if (value <= rateSlanderer + ratePolitician)
			return RobotType.POLITICIAN;
		return RobotType.MUCKRAKER;
	}

	private Integer influence(RobotType type) {
		// Minimise muckraker cost.
		if (type == RobotType.MUCKRAKER) return 1;

		// Increase minimum in proportion to fill.
		int mapSize = MAP_MAX_HEIGHT * MAP_MAX_WIDTH;
		double empty = 1.0 - (double) self.getRobotCount() / mapSize;
		double base = BASE_INFLUENCE / Math.pow(empty, BASE_EXPONENT);

		// Take proportion of existing influence.
		double target = self.getInfluence() * INFLUENTIAL;
		int influence = (int) Math.max(target, base);

		// Accept if remaining influence is safe.
		if (self.getInfluence() - influence >= base)
			return influence;
		return null;
	}
}
