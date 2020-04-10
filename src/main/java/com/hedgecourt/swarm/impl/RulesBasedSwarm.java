package com.hedgecourt.swarm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.hedgecourt.swarm.Position;
import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmTimer;
import com.hedgecourt.swarm.Velocity;

public class RulesBasedSwarm extends Observable implements Swarm {

	protected List<Speck> initSpecks = null;
	protected List<Speck> specks = null;
	protected int fieldDimensionX = 1;
	protected int fieldDimensionY = 1;

	protected SwarmTimer swarmTimer = new SwarmTimer();

	public RulesBasedSwarm() {
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

				if (x == 0) {
					s.setType(Speck.TYPE_PREDATOR);
				}

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

		int threadCount = SwarmConfig.EXECUTION_THREAD_COUNT;
		if (threadCount > this.specks.size()) {
			// revert to single thread, this'll only happen for very small speck sizes anyways so performance isnt an issue
			threadCount = 1;
		}

		CountDownLatch latch = new CountDownLatch(threadCount);

		int specksPerThread = this.specks.size() / threadCount;
		for (int threadIdx = 0; threadIdx < threadCount; threadIdx++) {
			int indexMin = specksPerThread * threadIdx;
			int indexMax = indexMin + specksPerThread;

			if (threadIdx == threadCount - 1) {
				// last thread just gets any remainder left over
				indexMax = this.specks.size();
			}
			// System.out.println("Starting thread " + threadIdx + " with " + (indexMax - indexMin + 1) + " specks.");
			RulesBasedWorker w = new RulesBasedWorker(latch, threadIdx, this, indexMin, indexMax);

			new Thread(w).start();
		}

		try {
			latch.await();
		} catch (InterruptedException E) {

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
