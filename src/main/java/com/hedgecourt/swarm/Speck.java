package com.hedgecourt.swarm;

import java.text.DecimalFormat;
import java.text.Format;

public class Speck {

	public static int TYPE_PREY = 1;
	public static int TYPE_PREDATOR = 2;

	private int type = TYPE_PREY;

	private static int NEXT_ID = 0;

	public static final Format FMT = new DecimalFormat("0.000");

	private Position position = new Position();
	private Velocity velocity = new Velocity();
	private Velocity queuedVelocity = new Velocity();

	private double mass = SwarmConfig.SPECK_DEFAULT_MASS;
	private double maxSpeed = SwarmConfig.SPECK_DEFAULT_MAX_SPEED;
	private double separationDistanceThreshold = SwarmConfig.SPECK_DEFAULT_SEPARATION_DISTANCE;
	private double neighborhoodRadius = SwarmConfig.SPECK_DEFAULT_NEIGHBORHOOD_RADIUS;
	private double maxTurnRate = SwarmConfig.SPECK_DEFAULT_MAX_TURN_RATE;

	private int id;

	public Speck() {
		super();
		this.id = getNextId();
		// System.out.println("NEW SPECK");
	}

	public Speck(Position position) {
		this();
		this.position = position;
	}

	public Speck(Position position, Velocity velocity) {
		this(position);
		this.setVelocity(velocity);
	}

	public void movePositionByVelocity() {
		position.setX(position.x() + velocity.x());
		position.setY(position.y() + velocity.y());
	}

	public void applyQueuedVelocity() {
		if (this.velocity == null) {
			this.velocity = this.queuedVelocity;
		} else {
			this.velocity.setX(this.queuedVelocity.x());
			this.velocity.setY(this.queuedVelocity.y());
		}
	}

	protected static synchronized int getNextId() {
		return NEXT_ID++;
	}

	public Position position() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Velocity velocity() {
		return velocity;
	}

	public void setVelocity(Velocity velocity) {
		this.queuedVelocity = velocity;
		this.applyQueuedVelocity();
	}

	public Velocity queuedVelocity() {
		return queuedVelocity;
	}

	public void setQueuedVelocity(Velocity queuedVelocity) {
		this.queuedVelocity = queuedVelocity;
	}

	public double mass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double maxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double separationDistanceThreshold() {
		return separationDistanceThreshold;
	}

	public void setSeparationDistanceThreshold(double separationDistanceThreshold) {
		this.separationDistanceThreshold = separationDistanceThreshold;
	}

	public double maxTurnRate() {
		return maxTurnRate;
	}

	public void setMaxTurnRate(double maxTurnRate) {
		this.maxTurnRate = maxTurnRate;
	}

	public int id() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double neighborhoodRadius() {
		return neighborhoodRadius;
	}

	public void setNeighborhoodRadius(double neighborhoodRadius) {
		this.neighborhoodRadius = neighborhoodRadius;
	}

	public int type() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
