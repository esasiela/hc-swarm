package com.hedgecourt.swarm;

import java.util.ArrayList;
import java.util.List;

import com.hedgecourt.swarm.impl.AlignAwayFromPredatorRule;
import com.hedgecourt.swarm.impl.BoidsAlignmentSwarmRule;
import com.hedgecourt.swarm.impl.BoidsCohesionSwarmRule;
import com.hedgecourt.swarm.impl.BoidsSeparationSwarmRule;
import com.hedgecourt.swarm.impl.SeparateFromPredatorRule;
import com.hedgecourt.swarm.impl.WallBounceSwarmRule;

public class SwarmRuleManager {

	private static List<SwarmRule> allRules = new ArrayList<SwarmRule>();

	private static List<SwarmRule> predatorRules = new ArrayList<SwarmRule>();

	public static void initialize(Swarm sw) {
		allRules.add(new WallBounceSwarmRule());

		allRules.add(new BoidsCohesionSwarmRule());
		allRules.add(new BoidsSeparationSwarmRule());
		allRules.add(new BoidsAlignmentSwarmRule());
		allRules.add(new SeparateFromPredatorRule());
		allRules.add(new AlignAwayFromPredatorRule());

		predatorRules.add(new WallBounceSwarmRule());

		for (SwarmRule rule : allRules) {
			rule.setSwarm(sw);
		}

		for (SwarmRule rule : predatorRules) {
			rule.setSwarm(sw);
		}
	}

	public static List<SwarmRule> getEnabledRules(int speckType) {
		if (speckType == Speck.TYPE_PREY) {
			return allRules;
		} else if (speckType == Speck.TYPE_PREDATOR) {
			return predatorRules;
		} else {
			// never gets here
			return allRules;
		}
	}
}
