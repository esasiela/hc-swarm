package com.hedgecourt.swarm.gui;

import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;

public class SwarmTableModel extends AbstractTableModel implements Observer {

	private Swarm swarm = null;

	private String[] columnNames = { "ID", "px", "py", "vx", "vy", "qx", "qy" };

	/**
	 * 
	 */
	private static final long serialVersionUID = -2419730451210827894L;

	public SwarmTableModel(Swarm swarm) {
		super();
		this.setSwarm(swarm);
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getRowCount() {
		return swarm.getSpecks().size();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		Speck s = swarm.getSpecks().get(rowIndex);

		StringBuilder ret = new StringBuilder();

		if (columnIndex == 0) {
			ret.append(s.id());
		} else if (columnIndex == 1) {
			ret.append(Speck.FMT.format(s.position().x()));
		} else if (columnIndex == 2) {
			ret.append(Speck.FMT.format(s.position().y()));
		} else if (columnIndex == 3) {
			ret.append(Speck.FMT.format(s.velocity().x()));
		} else if (columnIndex == 4) {
			ret.append(Speck.FMT.format(s.velocity().y()));
		} else if (columnIndex == 5) {
			ret.append(Speck.FMT.format(s.queuedVelocity().x()));
		} else if (columnIndex == 6) {
			ret.append(Speck.FMT.format(s.queuedVelocity().y()));
		}

		return ret.toString();
	}

	public Swarm getSwarm() {
		return swarm;
	}

	public void setSwarm(Swarm swarm) {
		this.swarm = swarm;
		if (this.swarm instanceof Observable) {
			((Observable) swarm).addObserver(this);
			// System.out.println("SwarmTableModel registering as an observer of the swarm");
		}
	}

	public void update(Observable o, Object arg) {
		// this means the swarm just finished an advance() iteration
		// System.out.println("Update fired on SwarmTableModel, passing on to table");
		this.fireTableDataChanged();
	}

}
