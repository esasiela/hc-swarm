package com.hedgecourt.swarm.impl;

import java.util.List;

import com.hedgecourt.swarm.DesiredVelocity;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmRule;

public class AlignAwayFromPredatorRule extends AbstractSwarmRule implements SwarmRule {

	public AlignAwayFromPredatorRule() {
		super();
	}

	public DesiredVelocity getDesiredVelocity(Speck s, List<Speck> neighbors) {

		DesiredVelocity dVel = new DesiredVelocity();

		if (s.type() != Speck.TYPE_PREY || neighbors.size() == 1) {
			// only me, so lonely out here
			dVel.setWeight(SwarmConfig.RULE_WEIGHT_MOOT);

		} else {

			for (Speck n : neighbors) {
				if (s.id() != n.id() && n.type() == Speck.TYPE_PREDATOR) {
					dVel.incX(n.velocity().x());
					dVel.incY(n.velocity().y());
				}
			}
			dVel.setX(dVel.x() / neighbors.size());
			dVel.setY(dVel.y() / neighbors.size());

			// always scale to 1 to level the playing field among rules
			dVel.scaleTo(-1);

			dVel.setWeight(SwarmConfig.RULE_WEIGHT_DISALIGN_PREDATOR);
		}

		return dVel;
	}

}
