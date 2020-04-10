package com.hedgecourt.swarm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.hedgecourt.swarm.DesiredVelocity;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmRule;
import com.hedgecourt.swarm.SwarmRuleManager;
import com.hedgecourt.swarm.Velocity;

public class RulesBasedWorker implements Runnable {

	protected CountDownLatch latch = null;
	protected int threadId = 0;

	protected Swarm swarm = null;

	protected int speckIndexMin = 0;
	protected int speckIndexMax = 0;

	public RulesBasedWorker(CountDownLatch latch, int threadId, Swarm swarm, int speckIndexMin, int speckIndexMax) {
		super();
		this.setLatch(latch);
		this.setThreadId(threadId);
		this.setSwarm(swarm);
		this.setSpeckIndexMin(speckIndexMin);
		this.setSpeckIndexMax(speckIndexMax);

	}

	public void run() {
		/*
		 * 2) SECOND LOOP: queue new velocity
		 * 2.a) Find neighbors
		 * 2.b) average the desired velocity from each rule
		 * 2.c) punt wallflowers off the wall
		 * 2.d) scale the average to constant speed
		 * 2.e) apply max turn rate rules to the average
		 * 2.f) queue the modified desired velocity
		 * 
		 */

		/*
		 * 2) SECOND LOOP: Queue new velocity
		 */
		this.getSwarm().getSwarmTimer().beginThread(this.getThreadId());

		for (int speckIdx = this.getSpeckIndexMin(); speckIdx < this.getSpeckIndexMax(); speckIdx++) {
			Speck s = this.getSwarm().getSpecks().get(speckIdx);

			/*
			 * 2.a) Find neighbors
			 */
			double currentHeading = Math.atan2(s.velocity().y(), s.velocity().x());

			List<Speck> neighbors = new ArrayList<Speck>();
			for (Speck n : this.getSwarm().getSpecks()) {
				// include myself in my neighbors list, its up to impl's to use or not use

				if (s.position().distanceFrom(n.position()) < s.neighborhoodRadius()) {
					if (SwarmConfig.SPECK_DEFAULT_NEIGHBORHOOD_ANGLE > 0) {
						double neighborHeading = Math.atan2(n.position().y() - s.position().y(), n.position().x() - s.position().x());

						if (s.id() == n.id() || Math.abs(neighborHeading - currentHeading) < SwarmConfig.SPECK_DEFAULT_NEIGHBORHOOD_ANGLE) {
							// System.out.println("speck " + s.id() + " nearby speck " + n.id() + " is in my angle");
							neighbors.add(n);
						} else {
							// System.out.println("speck " + s.id() + " nearby speck " + n.id() + " is OUTSIDE my angle");
						}
					} else {
						neighbors.add(n);
					}
				}
			}

			/*
			 * 2.b.1) process each rule and get a weighted desired velocity
			 */
			List<DesiredVelocity> componentVels = new ArrayList<DesiredVelocity>();
			for (SwarmRule rule : SwarmRuleManager.getEnabledRules(s.type())) {
				componentVels.add(rule.getDesiredVelocity(s, neighbors));
			}

			/*
			 * 2.b.2) average the desired velocities
			 */
			Velocity dVel = new Velocity();
			int weightCounter = 0;
			for (DesiredVelocity componentVel : componentVels) {
				if (componentVel.weight() == SwarmConfig.RULE_WEIGHT_WALL_COLLISION) {
					// special rule that vetos them all

					// setting weightCounter to -1 will keep the dVel we set here and bypass the logic later (which is what we want)
					weightCounter = -1;
					dVel = new Velocity(componentVel);
					break;
				} else {
					for (int i = 0; i < componentVel.weight(); i++) {
						/*
						 * if (s.id() == 0) {
						 * System.out.println("Speck " + s.id() + " adding component " + componentVel.toString());
						 * }
						 */
						dVel.add(componentVel);
						weightCounter++;
					}
				}
			}

			if (weightCounter == 0) {
				// means none of the rules provided a non-MOOT dVel, so maintain current heading
				dVel = new Velocity(s.velocity());

			} else if (weightCounter > 0) {
				// if there is actually a new velocity (otherwise doing nothing maintains course)
				dVel.setX(dVel.x() / weightCounter);
				dVel.setY(dVel.y() / weightCounter);

				/*
				 * 2.c) punt wallflowers
				 */
				if (dVel.x() == 0 && s.position().x() == 0) {
					dVel.setX(SwarmConfig.WALLFLOWER_BOOT_VELOCITY);
				} else if (dVel.x() == 0 && s.position().x() == this.getSwarm().getFieldDimensionX()) {
					dVel.setX(-1 * SwarmConfig.WALLFLOWER_BOOT_VELOCITY);
				}

				if (dVel.y() == 0 && s.position().y() == 0) {
					dVel.setY(SwarmConfig.WALLFLOWER_BOOT_VELOCITY);
				} else if (dVel.y() == 0 && s.position().y() == this.getSwarm().getFieldDimensionY()) {
					dVel.setY(-1 * SwarmConfig.WALLFLOWER_BOOT_VELOCITY);
				}

				/*
				 * 2.x) whatever you do , dont stop moving
				 */
				if (dVel.x() == 0 && dVel.y() == 0) {
					// although, as long as the rest of the neighborhood isnt also stopped, this
					// might represent a turn-around or something legit where next iteration
					// im moving again.
					dVel = new Velocity(s.velocity());
				}

				/*
				 * 2.d) scale the average to constant speed
				 */
				if (SwarmConfig.FORCE_CONSTANT_SPEED) {
					// kinda hacky, this isnt technically constant but i'm not changing it so it behaves constant
					dVel.scaleTo(s.velocity().getSpeed());
				}

				/*
				 * 2.e) apply max turn rate rules to the average
				 */
				// double currentHeading = Math.atan2(s.velocity().y(), s.velocity().x());
				// currentHeading gets calculated up above
				double desiredHeading = Math.atan2(dVel.y(), dVel.x());
				double turnRate = desiredHeading - currentHeading;

				if (Math.abs(turnRate) == Math.PI) {
					// want to turn around 180 degrees, wow!
					// the challenge is picking a direction to move MAX_TURN
				} else if (Math.abs(turnRate) > s.maxTurnRate()) {
					int sign = 1;
					if (currentHeading < desiredHeading) {
						if (Math.abs(turnRate) < Math.PI) {
							sign = 1;
						} else {
							sign = -1;
						}
					} else {
						if (Math.abs(turnRate) < Math.PI) {
							sign = -1;
						} else {
							sign = 1;
						}
					}

					dVel.setX(Math.cos(sign * s.maxTurnRate()) * s.velocity().x() - Math.sin(sign * s.maxTurnRate()) * s.velocity().y());
					dVel.setY(Math.sin(sign * s.maxTurnRate()) * s.velocity().x() + Math.cos(sign * s.maxTurnRate()) * s.velocity().y());
				}
			}
			/*
			 * 2.f) queue the modified desired velocity
			 */
			s.setQueuedVelocity(dVel);
		}

		this.getSwarm().getSwarmTimer().endThread(this.getThreadId());

		this.getLatch().countDown();
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public Swarm getSwarm() {
		return swarm;
	}

	public void setSwarm(Swarm swarm) {
		this.swarm = swarm;
	}

	public int getSpeckIndexMin() {
		return speckIndexMin;
	}

	public void setSpeckIndexMin(int speckIndexMin) {
		this.speckIndexMin = speckIndexMin;
	}

	public int getSpeckIndexMax() {
		return speckIndexMax;
	}

	public void setSpeckIndexMax(int speckIndexMax) {
		this.speckIndexMax = speckIndexMax;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

}
