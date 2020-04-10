package com.hedgecourt.swarm;

import java.util.List;

public interface SwarmRule {

	public DesiredVelocity getDesiredVelocity(Speck s, List<Speck> neighbors);

	public void setSwarm(Swarm sw);
}
