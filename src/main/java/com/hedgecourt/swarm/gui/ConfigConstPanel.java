package com.hedgecourt.swarm.gui;

import java.awt.LayoutManager;
import java.lang.reflect.Field;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.hedgecourt.swarm.SwarmConfig;

public class ConfigConstPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2688898167301097300L;

	public ConfigConstPanel() {
		super();
		this.doIntrospection();
	}

	public ConfigConstPanel(LayoutManager layout) {
		super(layout);
		this.doIntrospection();
	}

	public ConfigConstPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		this.doIntrospection();
	}

	public ConfigConstPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		this.doIntrospection();
	}

	protected void doIntrospection() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		for (Field f : SwarmConfig.class.getDeclaredFields()) {
			try {
				this.add(new JLabel(f.getName() + " [" + f.get(null).toString() + "]"));
			} catch (Exception E) {
				System.err.println("failed introspection on field [" + f.getName() + "]");
			}
		}
	}

}
