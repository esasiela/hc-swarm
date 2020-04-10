package com.hedgecourt.swarm.impl2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.hedgecourt.swarm.Position;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.Velocity;

public class BoidsSeparationSwarm extends AbstractSwarm implements Swarm {

	public static boolean DEBUG_SEPARATION = false;
	public static boolean HARDCODED_INIT = false;

	public void initializeSwarm() {

		if (HARDCODED_INIT) {

			// this.specks.add(new Speck(new Position(200, 75), new Velocity(0, 5)));
			// this.specks.add(new Speck(new Position(200, 125), new Velocity(0, -5)));

			// this.specks.add(new Speck(new Position(100, 200), new Velocity(5, 0)));
			// this.specks.add(new Speck(new Position(200, 205), new Velocity(-5, 0)));

			this.specks.add(new Speck(new Position(100, 200), new Velocity(5, 0)));
			this.specks.add(new Speck(new Position(200, 205), new Velocity(-5, 0)));
			this.specks.add(new Speck(new Position(150, 150), new Velocity(0, 5)));

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

				// System.out.println("adding speck: pos=" + s.position.toString() + " vel=" + s.velocity.toString());

				this.specks.add(s);
			}
		}
	}

	public void queueVelocities() {

		for (Speck s : this.specks) {
			// System.out.println("speck: pos=" + s.position.toString() + " vel=" + s.velocity.toString());

			if (s.velocity().getSpeed() < 5.99999 || s.velocity().getSpeed() > 6.00001) {
				System.out.println("queueVelocities(1), I have a non-6 speed, speck " + s.id());
			}

			/*
			 * Step 1: process velocity changes as a result of wall collisions
			 */
			if (s.position().y() <= 0 || s.position().y() >= this.getFieldDimensionY()) {
				s.queuedVelocity().setY(-1 * s.velocity().y());
			}

			if (s.position().x() <= 0 || s.position().x() >= this.getFieldDimensionX()) {
				s.queuedVelocity().setX(-1 * s.velocity().x());
			}

			if (s.velocity().getSpeed() < 5.99999 || s.velocity().getSpeed() > 6.00001) {
				System.out.println("queueVelocities(2), I have a non-6 speed, speck " + s.id());
			}

			/*
			 * Step 2: steer to separate yourself from neighbors
			 */
			List<Speck> tooCloseList = new ArrayList<Speck>();
			List<Velocity> awayUnitVelocities = new ArrayList<Velocity>();
			for (Speck n : this.specks) {
				if (n.id() == s.id()) {
					continue;
				}
				// if i'm too close to you, then your position goes on the list
				double dist = s.position().distanceFrom(n.position());

				/*
				 * if (Double.isNaN(dist)) { System.out.println("distance is NaN"); } else if (dist == 0) { System.out.println("distance is 0"); }
				 */

				if (dist < s.separationDistanceThreshold()) {
					debug("speck " + s.id() + " thinks " + n.id() + " is too close");
					tooCloseList.add(n);

					double awayX = -1 * (n.position().x() - s.position().x()) / dist;
					double awayY = -1 * (n.position().y() - s.position().y()) / dist;

					Velocity awayV = new Velocity(awayX, awayY);
					awayUnitVelocities.add(awayV);
					debug("\tunit velocity to steer away is " + awayV.toString());
				}
			}
			// the resultant velocity direction i want is the sum of moving directly away from each tooClose neighbor.
			if (awayUnitVelocities.size() > 0) {
				double tmpX = 0, tmpY = 0;
				for (Velocity a : awayUnitVelocities) {
					tmpX += a.x();
					tmpY += a.y();
				}

				Velocity desiredVel = new Velocity(tmpX / awayUnitVelocities.size(), tmpY / awayUnitVelocities.size());
				if (desiredVel.x() == 0 && desiredVel.y() == 0) {
					/*
					 * most frequent case, I am directly in between two neighbors, their escape vectors cancel out. what to do? choose a neighbor and take a right angle away from his escape vector?
					 * choose a direction and just go max turn rate? (which will actually be the same as above unless your max turn rate is > 90 deg do nothing and maintain current heading, hoping the
					 * situation resolves until the next iteration?
					 */

					// easiest thing is just maintain heading
					// TODO figure out a better way to escape from the middle of evenly spaced neighbors
					desiredVel = new Velocity(s.velocity());

					if (awayUnitVelocities.size() > 2) {
						// wow, 3 or more vectors that perfectly cancel out? i gotta see this one
						System.out.println("Trying to separate from my neighbors, and I am directly in the middle of 3 or more (" + awayUnitVelocities.size()
								+ ") neighbors. Seems unlikely, so I'm showing you.");
						for (Velocity a : awayUnitVelocities) {
							System.out.println("\tvel " + a.toString());
						}

					}
				}

				if (Double.isNaN(desiredVel.x()) || Double.isNaN(desiredVel.y())) {
					System.out.println("desiredVel is NAN - a");
				}

				// the average is not typically a unit vector, so scale it to 1
				desiredVel.scaleTo(1);

				if (Double.isNaN(desiredVel.x()) || Double.isNaN(desiredVel.y())) {
					System.out.println("desiredVel is NAN - b");

					System.out.println("showing my speck " + s.id());
					System.out.println("\tPosition: " + s.position().toString());
					System.out.println("\tVelocity: " + s.velocity().toString());
				}

				debug("\taverage unit awayVel is " + desiredVel.toString() + " and should be unit velocity " + Speck.FMT.format(desiredVel.getSpeed()));

				if (s.velocity().getSpeed() < 5.99999 || s.velocity().getSpeed() > 6.00001) {
					System.out.println("scaling velocity to non-6 speed for speck " + s.id() + " vel " + s.velocity().toString());
				}

				desiredVel.scaleTo(s.velocity().getSpeed());

				if (Double.isNaN(desiredVel.x()) || Double.isNaN(desiredVel.y())) {
					System.out.println("desiredVel is NAN - c");
				}

				debug("\t\tmy current speed is " + Speck.FMT.format(s.velocity().getSpeed()) + " and away speed is " + Speck.FMT.format(desiredVel.getSpeed()));

				/*
				 * CONSIDER MAX TURN RATE
				 */
				// show me what the desiredVel means as a turn-rate
				// if (s.velocity.getSpeed() != 0) {

				double currentAngle = Math.atan2(s.velocity().y(), s.velocity().x());
				double desiredAngle = Math.atan2(desiredVel.y(), desiredVel.x());

				if (Double.isNaN(currentAngle)) {
					System.out.println("currentAngle is NAN - x");
				}

				if (Double.isNaN(desiredAngle)) {
					System.out.println("desiredAngle is NAN - x");
				}

				// double turnRate = Math.acos((s.velocity.x * desiredVel.x + s.velocity.y * desiredVel.y) / (s.velocity.getSpeed() * desiredVel.getSpeed()));
				debug("\tangle of desiredVel " + Speck.FMT.format(desiredAngle) + " angle of currentVel " + Speck.FMT.format(currentAngle));
				double turnRate = desiredAngle - currentAngle;

				debug("\tdesired turn rate " + Speck.FMT.format(turnRate));

				if (Math.abs(turnRate) == Math.PI) {
					debug("\tHEAD ON COLLISION, NICE!");
				} else if (Math.abs(turnRate) > s.maxTurnRate()) {

					int sign = 1;
					if (currentAngle < desiredAngle) {
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

					Velocity adjustedVel = new Velocity();
					adjustedVel.setX(Math.cos(sign * s.maxTurnRate()) * s.velocity().x() - Math.sin(sign * s.maxTurnRate()) * s.velocity().y());
					adjustedVel.setY(Math.sin(sign * s.maxTurnRate()) * s.velocity().x() + Math.cos(sign * s.maxTurnRate()) * s.velocity().y());

					desiredVel = adjustedVel;

					if (Double.isNaN(desiredVel.x()) || Double.isNaN(desiredVel.y())) {
						System.out.println("desiredVel is NAN - d");
					}

					debug("\tTURNING TOO SHARP, new vel " + desiredVel.toString());
				}

				// }

				if (desiredVel.getSpeed() < 5.99999 || desiredVel.getSpeed() > 6.00001) {
					System.out.println("queuing velocity to non-6 speed for speck " + s.id() + ", vel " + desiredVel.toString());
				}

				s.setQueuedVelocity(desiredVel);
			}
		}
	}

	public static void debug(String s) {
		if (DEBUG_SEPARATION) {
			System.out.println(s);
		}
	}

}
