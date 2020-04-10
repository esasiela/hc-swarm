package com.hedgecourt.swarm;

import java.util.List;

public interface Swarm {

	public void initializeSwarm();

	public void resetSwarm();

	public void advance();

	public List<Speck> getSpecks();

	public SwarmTimer getSwarmTimer();

	public void setFieldDimensions(int x, int y);

	public int getFieldDimensionX();

	public int getFieldDimensionY();

}
