package com.hedgecourt.swarm;

public class Velocity extends Position {

	public Velocity() {
		super();
	}

	public Velocity(double x, double y) {
		super(x, y);
	}

	public Velocity(Velocity v) {
		this(v.x(), v.y());
	}

	public void add(Velocity v) {
		this.setX(this.x() + v.x());
		this.setY(this.y() + v.y());
	}

	public double getSpeed() {

		return Math.sqrt(this.x() * this.x() + this.y() * this.y());
	}

	public void scaleTo(double magnitude) {
		double oldMag = this.getSpeed();

		if (oldMag == 0) {
			// System.out.println("in scaleTo(" + Speck.FMT.format(magnitude) + "), oldMag is 0 for vel " + this.toString());
		} else {
			this.setX(magnitude * this.x() / oldMag);
			this.setY(magnitude * this.y() / oldMag);
		}

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + " mag " + Speck.FMT.format(this.getSpeed());
	}

}
