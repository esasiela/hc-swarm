package com.hedgecourt.swarm.impl;

import java.util.List;

import com.hedgecourt.swarm.DesiredVelocity;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmRule;

public class WallBounceSwarmRule extends AbstractSwarmRule implements SwarmRule {

	public WallBounceSwarmRule() {
		super();
	}

	public DesiredVelocity getDesiredVelocity(Speck s, List<Speck> neighbors) {

		DesiredVelocity dVel = new DesiredVelocity(s.velocity(), SwarmConfig.RULE_WEIGHT_MOOT);

		if (s.position().y() <= 0 || s.position().y() >= this.getSwarm().getFieldDimensionY()) {
			// hit a wall in the Y direction
			dVel.setWeight(SwarmConfig.RULE_WEIGHT_WALL_COLLISION);
			dVel.setY(-1 * s.velocity().y());
		}
		if (s.position().x() <= 0 || s.position().x() >= this.getSwarm().getFieldDimensionX()) {
			// hit a wall in the X direction
			dVel.setWeight(SwarmConfig.RULE_WEIGHT_WALL_COLLISION);
			dVel.setX(-1 * s.velocity().x());
		}

		return dVel;
	}

}
