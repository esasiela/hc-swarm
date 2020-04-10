package com.hedgecourt.swarm.impl2;

import java.util.Random;

import com.hedgecourt.swarm.Position;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.Velocity;

public class WallBounceCollisionSwarm extends AbstractSwarm implements Swarm {

	public void initializeSwarm() {

		Random r = new Random(System.currentTimeMillis());

		for (int x = 0; x < 200; x++) {
			this.specks.add(new Speck(new Position(r.nextInt(this.getFieldDimensionX()), r.nextInt(this.getFieldDimensionY())), new Velocity(r.nextInt(14) - 5, r.nextInt(14) - 5)));
		}

	}

	public void queueVelocities() {

		for (Speck s : this.specks) {
			// System.out.println("speck: pos=" + s.position.toString() + " vel=" + s.velocity.toString());

			if (s.position().y() <= 0) {
				// too high, always move down
				s.queuedVelocity().setY(-1 * s.velocity().y());

			} else if (s.position().y() >= this.getFieldDimensionY()) {
				// to low, always move up
				s.queuedVelocity().setY(-1 * s.velocity().y());

			}

			if (s.position().x() <= 0 || s.position().x() >= this.getFieldDimensionX()) {
				s.queuedVelocity().setX(-1 * s.velocity().x());
			}

		}
	}

}
