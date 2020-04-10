package com.hedgecourt.swarm.impl;

import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmRule;

public abstract class AbstractSwarmRule implements SwarmRule {
	private Swarm swarm = null;

	public Swarm getSwarm() {
		return swarm;
	}

	public void setSwarm(Swarm swarm) {
		this.swarm = swarm;
	}

}
