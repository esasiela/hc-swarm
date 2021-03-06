package com.hedgecourt.swarm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import com.hedgecourt.swarm.DesiredVelocity;
import com.hedgecourt.swarm.Position;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmRule;
import com.hedgecourt.swarm.SwarmRuleManager;
import com.hedgecourt.swarm.SwarmTimer;
import com.hedgecourt.swarm.Velocity;

public class RulesBasedSwarm2 extends Observable implements Swarm {

	protected List<Speck> initSpecks = null;
	protected List<Speck> specks = null;
	protected int fieldDimensionX = 1;
	protected int fieldDimensionY = 1;

	protected SwarmTimer swarmTimer = new SwarmTimer();

	public RulesBasedSwarm2() {
		super();
	}

	public void initializeSwarm() {
		if (this.specks == null) {
			this.specks = new ArrayList<Speck>();
		}

		if (SwarmConfig.SWARM_HARDCODE_INIT) {
			this.specks.add(new Speck(new Position(180, 200), new Velocity(5, 0)));
			this.specks.add(new Speck(new Position(200, 200), new Velocity(-5, 0)));
			// this.specks.add(new Speck(new Position(150, 150), new Velocity(0, 5)));
		} else {
			Random r = new Random(System.currentTimeMillis());

			for (int x = 0; x < SwarmConfig.SPECK_DEFAULT_COUNT; x++) {
				Speck s = new Speck();

				// Position: randomly distributed around the whole field
				s.setPosition(new Position(r.nextInt(this.getFieldDimensionX()), r.nextInt(this.getFieldDimensionY())));

				// Velocity:
				// 1) get a random speed that is less than maxSpeed
				// double speed = r.nextDouble() * s.maxSpeed;
				double speed = s.maxSpeed() - 1;
				// 2) get a random x component that is less than selected speed
				double xVel = r.nextDouble() * speed * (r.nextBoolean() ? 1 : -1);
				// 3) get the matching y component that combines with the selected x to result in the selected speed
				double yVel = Math.sqrt(speed * speed - xVel * xVel) * (r.nextBoolean() ? 1 : -1);

				s.setVelocity(new Velocity(xVel, yVel));
				this.specks.add(s);
			}
		}

	}

	public void resetSwarm() {
		// TODO Auto-generated method stub

	}

	public void advance() {
		/*
		 * 1) FIRST LOOP: Move by velocity
		 * 1.a) Check for out-of-bounds
		 * 
		 * 2) SECOND LOOP: queue new velocity
		 * 2.a) Find neighbors
		 * 2.b) average the desired velocity from each rule
		 * 2.c) punt wallflowers off the wall
		 * 2.d) scale the average to constant speed
		 * 2.e) apply max turn rate rules to the average
		 * 2.f) queue the modified desired velocity
		 * 
		 * 3) THIRD LOOP: apply queued velocities
		 * 
		 */

		/*
		 * 1) FIRST LOOP: Move by velocity
		 */
		for (Speck s : this.specks) {
			s.movePositionByVelocity();

			/*
			 * 1.a) Check for out-of-bounds
			 */
			if (s.position().x() > fieldDimensionX) {
				s.position().setX(fieldDimensionX - SwarmConfig.FIELD_OUT_OF_BOUNDS_OFFSET);
			} else if (s.position().x() < 0) {
				s.position().setX(SwarmConfig.FIELD_OUT_OF_BOUNDS_OFFSET);
			}

			if (s.position().y() > fieldDimensionY) {
				s.position().setY(fieldDimensionY - SwarmConfig.FIELD_OUT_OF_BOUNDS_OFFSET);
			} else if (s.position().y() < 0) {
				s.position().setY(SwarmConfig.FIELD_OUT_OF_BOUNDS_OFFSET);
			}
		}

		/*
		 * 2) SECOND LOOP: Queue new velocity
		 */
		this.swarmTimer.beginIteration();

		for (int threadIdx = 0; threadIdx < SwarmConfig.EXECUTION_THREAD_COUNT; threadIdx++) {

		}

		for (Speck s : this.specks) {
			/*
			 * 2.a) Find neighbors
			 */
			double currentHeading = Math.atan2(s.velocity().y(), s.velocity().x());

			List<Speck> neighbors = new ArrayList<Speck>();
			for (Speck n : this.specks) {
				// include myself in my neighbors list, its up to impl's to use or not use

				// double desiredHeading = Math.atan2(dVel.y(), dVel.x());
				// double turnRate = desiredHeading - currentHeading;
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
				} else if (dVel.x() == 0 && s.position().x() == this.getFieldDimensionX()) {
					dVel.setX(-1 * SwarmConfig.WALLFLOWER_BOOT_VELOCITY);
				}

				if (dVel.y() == 0 && s.position().y() == 0) {
					dVel.setY(SwarmConfig.WALLFLOWER_BOOT_VELOCITY);
				} else if (dVel.y() == 0 && s.position().y() == this.getFieldDimensionY()) {
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
		this.swarmTimer.endIteration();

		/*
		 * THIRD LOOP: apply queued velocities
		 */
		for (Speck s : this.specks) {
			s.applyQueuedVelocity();
		}

		/*
		 * Notify the data model watchers
		 */
		this.setChanged();
		this.notifyObservers();
	}

	public List<Speck> getSpecks() {
		return this.specks;
	}

	public void setFieldDimensions(int x, int y) {
		this.fieldDimensionX = x;
		this.fieldDimensionY = y;
	}

	public int getFieldDimensionX() {
		return fieldDimensionX;
	}

	public int getFieldDimensionY() {
		return fieldDimensionY;
	}

	public SwarmTimer getSwarmTimer() {
		return swarmTimer;
	}

	public void setSwarmTimer(SwarmTimer swarmTimer) {
		this.swarmTimer = swarmTimer;
	}

}
