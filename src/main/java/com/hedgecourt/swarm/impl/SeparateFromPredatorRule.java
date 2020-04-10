package com.hedgecourt.swarm.impl;

import java.util.ArrayList;
import java.util.List;

import com.hedgecourt.swarm.DesiredVelocity;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmRule;
import com.hedgecourt.swarm.Velocity;

public class SeparateFromPredatorRule extends AbstractSwarmRule implements SwarmRule {

	public SeparateFromPredatorRule() {
		super();
	}

	public DesiredVelocity getDesiredVelocity(Speck s, List<Speck> neighbors) {

		DesiredVelocity dVel = new DesiredVelocity();

		if (s.type() != Speck.TYPE_PREY || neighbors.size() == 1) {
			// only me, so lonely out here
			dVel.setWeight(SwarmConfig.RULE_WEIGHT_MOOT);

		} else {

			List<Velocity> aVels = new ArrayList<Velocity>();

			for (Speck n : neighbors) {

				if (s.id() != n.id() && n.type() == Speck.TYPE_PREDATOR) {
					double dist = s.position().distanceFrom(n.position());

					// any predator in my neighborhood is too close
					aVels.add(new Velocity(-1 * (n.position().x() - s.position().x()) / dist, -1 * (n.position().y() - s.position().y()) / dist));
				}
			}
			if (aVels.size() > 0) {
				// there's ppl to close to me
				for (Velocity aVel : aVels) {
					dVel.add(aVel);
				}
				dVel.setX(dVel.x() / aVels.size());
				dVel.setY(dVel.y() / aVels.size());

				// always scale to 1 to level the playing field among rules
				dVel.scaleTo(1);

				dVel.setWeight(SwarmConfig.RULE_WEIGHT_SEPARATE_PREDATOR);
			} else {
				// none of my neighbors are too close
				dVel.setWeight(SwarmConfig.RULE_WEIGHT_MOOT);
			}

		}

		return dVel;
	}

}
