package com.hedgecourt.swarm.impl;

import java.util.List;

import com.hedgecourt.swarm.DesiredVelocity;
import com.hedgecourt.swarm.Position;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmRule;

public class BoidsCohesionSwarmRule extends AbstractSwarmRule implements SwarmRule {

	public BoidsCohesionSwarmRule() {
		super();
	}

	public DesiredVelocity getDesiredVelocity(Speck s, List<Speck> neighbors) {

		DesiredVelocity dVel = new DesiredVelocity();

		Position nCenter = new Position();

		if (s.type() != Speck.TYPE_PREY || neighbors.size() == 1) {
			// only me, so lonely out here
			dVel.setWeight(SwarmConfig.RULE_WEIGHT_MOOT);

		} else {
			// System.out.println("have some neighbors");
			for (Speck n : neighbors) {
				if (s.id() != n.id()) {
					nCenter.setX(nCenter.x() + n.position().x());
					nCenter.setY(nCenter.y() + n.position().y());
				}
			}
			nCenter.setX(nCenter.x() / (neighbors.size() - 1));
			nCenter.setY(nCenter.y() / (neighbors.size() - 1));

			dVel.setX(nCenter.x() - s.position().x());
			dVel.setY(nCenter.y() - s.position().y());

			// scaling to 1 because the big magnitudes in here tend to overwhelm other rule sets
			dVel.scaleTo(1);

			dVel.setWeight(SwarmConfig.RULE_WEIGHT_COHESION);
		}

		// System.out.println("ret dVel " + dVel.toString());
		return dVel;
	}

}
