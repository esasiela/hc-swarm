package com.hedgecourt.swarm;

public class Position {

	private double x = 0.0;
	private double y = 0.0;

	public Position() {
		super();
		// System.out.println("NEW POSITION");
	}

	public Position(double x, double y) {
		this();
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return ("(" + Speck.FMT.format(x) + "," + Speck.FMT.format(y) + ")");
	}

	public double distanceFrom(Position p) {
		return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
	}

	public double x() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void incX(double x) {
		this.x += x;
	}

	public double y() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void incY(double y) {
		this.y += y;
	}

}
