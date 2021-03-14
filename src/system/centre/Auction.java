package system.centre;

import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;

public class Auction {
	private final RobotController self;

	private int lastVotes = 0;
	private boolean lastStatus = false;
	private boolean applied = false;
	private double change;
	private double target;

	private static final double RATE = 2.0;
	private static final double DELAY = 0.2;
	private static final double LIMITER = 0.1;

	public Auction(RobotController self) {
		this.self = self;
		reset();
	}

	public void bid() throws GameActionException {
		// Delay bidding until established.
		int rounds = GameConstants.GAME_MAX_NUMBER_OF_ROUNDS;
		if (self.getRoundNum() <= rounds * DELAY) return;

		// Reduce target if successful or too high.
		int votes = self.getTeamVotes();
		boolean lower = votes > lastVotes || !applied;
		lastVotes = votes;

		if (applied) {
			// Match delta with consecutive choices.
			// Apply changes only after bid.
			if (lower == lastStatus) {
				change *= RATE;
			} else change /= RATE;
			lastStatus = lower;
		}

		// Apply and constrain change.
		target += lower ? -change : change;
		if (target < 1.0) reset();

		// Apply target bid if under limit.
		double limit = self.getInfluence() * LIMITER;
		boolean safe = (int) target <= (int) limit;
		applied = safe && self.canBid((int) target);
		if (applied) self.bid((int) target);
	}

	private void reset() {
		change = 1.0;
		target = 1.0;
	}
}
