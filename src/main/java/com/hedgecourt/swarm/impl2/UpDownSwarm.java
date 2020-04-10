package com.hedgecourt.swarm.impl2;

import com.hedgecourt.swarm.Position;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.Velocity;

public class UpDownSwarm extends AbstractSwarm implements Swarm {

	public void initializeSwarm() {

		int maxY = 150;
		int minY = 50;

		int y = minY;
		int vel = 5;

		for (int x = 5; x < this.getFieldDimensionX(); x += 5) {
			this.specks.add(new Speck(new Position(x, y), new Velocity(0, vel)));

			y += vel;
			if (y == maxY || y == minY) {
				vel = -1 * vel;
			}
		}

		/*
		 * this.specks.add(new Speck(new Position(5, 105), new Velocity(0, -5))); this.specks.add(new Speck(new Position(10, 110), new Velocity(0, -5))); this.specks.add(new Speck(new Position(15,
		 * 115), new Velocity(0, -5))); this.specks.add(new Speck(new Position(20, 120), new Velocity(0, -5))); this.specks.add(new Speck(new Position(25, 125), new Velocity(0, -5)));
		 * this.specks.add(new Speck(new Position(30, 130), new Velocity(0, -5))); this.specks.add(new Speck(new Position(35, 135), new Velocity(0, -5))); this.specks.add(new Speck(new Position(40,
		 * 140), new Velocity(0, -5))); this.specks.add(new Speck(new Position(45, 145), new Velocity(0, -5))); this.specks.add(new Speck(new Position(50, 150), new Velocity(0, -5)));
		 * 
		 * this.specks.add(new Speck(new Position(55, 145), new Velocity(0, 5))); this.specks.add(new Speck(new Position(60, 140), new Velocity(0, 5))); this.specks.add(new Speck(new Position(65,
		 * 135), new Velocity(0, 5))); this.specks.add(new Speck(new Position(70, 130), new Velocity(0, 5))); this.specks.add(new Speck(new Position(75, 125), new Velocity(0, 5))); this.specks.add(new
		 * Speck(new Position(80, 120), new Velocity(0, 5))); this.specks.add(new Speck(new Position(85, 115), new Velocity(0, 5))); this.specks.add(new Speck(new Position(90, 110), new Velocity(0,
		 * 5))); this.specks.add(new Speck(new Position(95, 105), new Velocity(0, 5))); this.specks.add(new Speck(new Position(100, 100), new Velocity(0, 5)));
		 * 
		 * this.specks.add(new Speck(new Position(105, 95), new Velocity(0, 5))); this.specks.add(new Speck(new Position(110, 90), new Velocity(0, 5))); this.specks.add(new Speck(new Position(115,
		 * 85), new Velocity(0, 5))); this.specks.add(new Speck(new Position(120, 80), new Velocity(0, 5))); this.specks.add(new Speck(new Position(125, 75), new Velocity(0, 5))); this.specks.add(new
		 * Speck(new Position(130, 70), new Velocity(0, 5))); this.specks.add(new Speck(new Position(135, 65), new Velocity(0, 5))); this.specks.add(new Speck(new Position(140, 60), new Velocity(0,
		 * 5))); this.specks.add(new Speck(new Position(145, 55), new Velocity(0, 5)));
		 * 
		 * this.specks.add(new Speck(new Position(150, 50), new Velocity(0, 5)));
		 */
	}

	public void queueVelocities() {
		double GO = 5;

		for (Speck s : this.specks) {
			// System.out.println("speck: pos=" + s.position.toString() + " vel=" + s.velocity.toString());

			if (s.position().y() <= 50) {
				// too high, always move down
				s.queuedVelocity().setY(GO);
				s.queuedVelocity().setX(0);
			} else if (s.position().y() >= 150) {
				// to low, always move up
				s.queuedVelocity().setY(-1 * GO);
				s.queuedVelocity().setX(0);
			} else {
				// just leave the velocity alone, continue on current heading
				// System.out.println("leave alone, queued velocity = " + s.queuedVelocity.toString());
			}
		}
	}

}
