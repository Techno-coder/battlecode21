package system.centre;

import system.Candidate;

import java.util.List;

public class Analytics {
	public int politicians;
	public int slanderers;
	public int muckrakers;

	public Analytics() {
		resetAnalytics();
	}

	public void recollect(List<Candidate> candidates) {
		resetAnalytics();
		collect(candidates);
	}

	public int total() {
		// Total number of movable units detected.
		return politicians + slanderers + muckrakers;
	}

	private void collect(List<Candidate> candidates) {
		for (Candidate candidate : candidates) {
			switch (candidate.type) {
				case POLITICIAN:
					++politicians;
					break;
				case SLANDERER:
					++slanderers;
					break;
				case MUCKRAKER:
					++muckrakers;
					break;
				default:
					break;
			}
		}
	}

	private void resetAnalytics() {
		politicians = 0;
		slanderers = 0;
		muckrakers = 0;
	}
}
