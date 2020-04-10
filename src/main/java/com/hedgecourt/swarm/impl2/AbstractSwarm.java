package com.hedgecourt.swarm.impl2;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmTimer;

public abstract class AbstractSwarm extends Observable implements Swarm {

	protected List<Speck> specks = new ArrayList<Speck>();
	protected int fieldDimensionX = 1;
	protected int fieldDimensionY = 1;

	protected SwarmTimer swarmTimer = new SwarmTimer();

	public abstract void initializeSwarm();

	public abstract void queueVelocities();

	public void resetSwarm() {
		// who the F cares, this class is on its way out
	}

	public void advance() {

		/*
		 * FIRST LOOP: move to new position based on current velocity
		 */
		for (Speck s : this.specks) {

			s.movePositionByVelocity();

			/*
			 * CHECK FOR OUT-OF-BOUNDS
			 */
			double flimFlam = 0.0;
			if (s.position().x() > fieldDimensionX) {
				s.position().setX(fieldDimensionX - flimFlam);
			} else if (s.position().x() < 0) {
				s.position().setX(flimFlam);
			}

			if (s.position().y() > fieldDimensionY) {
				s.position().setY(fieldDimensionY);
			} else if (s.position().y() < 0) {
				s.position().setY(0);
			}
		}

		/*
		 * SECOND LOOP: we are in new positions, calculate new velocity and queue it
		 */
		this.swarmTimer.beginIteration();
		this.queueVelocities();
		this.swarmTimer.endIteration();

		/*
		 * THIRD LOOP: apply velocity
		 */
		for (Speck s : this.specks) {
			s.applyQueuedVelocity();
		}

		/*
		 * NOTIFY THE DATA MODEL WATCHERS
		 */
		this.setChanged();
		this.notifyObservers();
	}

	public boolean haveNullPositions() {
		for (Speck s : specks) {
			if (s.position() == null) {
				return true;
			}
		}
		return false;
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
