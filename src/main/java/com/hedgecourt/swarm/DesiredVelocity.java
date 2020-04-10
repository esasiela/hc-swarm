package com.hedgecourt.swarm;

public class DesiredVelocity extends Velocity {

	private int weight = 1;

	public DesiredVelocity() {
		super();
	}

	public DesiredVelocity(double x, double y) {
		super(x, y);
	}

	public DesiredVelocity(Velocity v) {
		super(v);
	}

	public DesiredVelocity(Velocity v, int weight) {
		this(v);
		this.setWeight(weight);
	}

	public int weight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String toString() {
		return super.toString() + " weight " + this.weight;
	}

}
