package com.hedgecourt.swarm.impl2;

import java.util.Random;

import com.hedgecourt.swarm.Position;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.Velocity;

public class BoidsAlignmentSwarm extends AbstractSwarm implements Swarm {

	public static boolean DEBUG_ALIGNMENT = false;
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
			/*
			 * Step 1: process velocity changes as a result of wall collisions
			 */
			boolean isBonk = false;
			if (s.position().y() <= 0 || s.position().y() >= this.getFieldDimensionY()) {
				s.queuedVelocity().setY(-1 * s.velocity().y());
				isBonk = true;
			}

			if (s.position().x() <= 0 || s.position().x() >= this.getFieldDimensionX()) {
				s.queuedVelocity().setX(-1 * s.velocity().x());
				isBonk = true;
			}

			/*
			 * Step 2: steer to align velocity with neighbors
			 */
			Velocity hoodAcc = new Velocity();
			int neighborCount = 0;

			for (Speck n : this.specks) {
				if (n.id() == s.id()) {
					continue;
				}

				double dist = s.position().distanceFrom(n.position());

				if (dist < s.neighborhoodRadius()) {
					neighborCount++;

					hoodAcc.setX(hoodAcc.x() + n.velocity().x());
					hoodAcc.setY(hoodAcc.y() + n.velocity().y());
				}
			}

			if (neighborCount > 0) {
				Velocity desiredVel = new Velocity();

				desiredVel.setX(hoodAcc.x() / neighborCount);
				desiredVel.setY(hoodAcc.y() / neighborCount);

				/*
				 * WALLFLOWERS
				 */
				if (s.position().x() == 0 && desiredVel.x() <= 0) {
					// quite possible we have a neighborhood of wallflowers
					desiredVel.setX(1);
				} else if (s.position().x() == this.getFieldDimensionX() && desiredVel.x() >= 0) {
					desiredVel.setX(-1);
				}

				if (s.position().y() == 0 && desiredVel.y() <= 0) {
					desiredVel.setY(1);
				} else if (s.position().y() == this.getFieldDimensionY() && desiredVel.y() >= 0) {
					desiredVel.setY(-1);
				}

				if (desiredVel.x() == 0 && desiredVel.y() == 0) {
					// The avg velocity of all my neighbors cancels out to exactly where I am
					desiredVel = new Velocity(s.velocity());
				}

				desiredVel.scaleTo(s.velocity().getSpeed());

				/*
				 * CONSIDER MAX TURN RATE
				 */

				double currentAngle = Math.atan2(s.velocity().y(), s.velocity().x());
				double desiredAngle = Math.atan2(desiredVel.y(), desiredVel.x());

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

					debug("\tTURNING TOO SHARP, new vel " + desiredVel.toString());
				}

				if (!isBonk) {
					s.setQueuedVelocity(desiredVel);
				}
			}
		}
	}

	public static void debug(String s) {
		if (DEBUG_ALIGNMENT) {
			System.out.println(s);
		}
	}

}
