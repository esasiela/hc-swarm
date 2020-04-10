package com.hedgecourt.swarm.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.Swarm;
import com.hedgecourt.swarm.SwarmConfig;
import com.hedgecourt.swarm.SwarmRuleManager;
import com.hedgecourt.swarm.impl2.WallBounceSwarm;

public class SwarmPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7663161941979785587L;

	private Swarm swarm = null;

	public SwarmPanel() {
		super();
		this.setPreferredSize(new Dimension(SwarmConfig.SWARM_PANEL_WIDTH, SwarmConfig.SWARM_PANEL_HEIGHT));
		this.initSwarm();
	}

	public SwarmPanel(LayoutManager layout) {
		super(layout);
		this.setPreferredSize(new Dimension(SwarmConfig.SWARM_PANEL_WIDTH, SwarmConfig.SWARM_PANEL_HEIGHT));
		this.initSwarm();
	}

	public SwarmPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		this.setPreferredSize(new Dimension(SwarmConfig.SWARM_PANEL_WIDTH, SwarmConfig.SWARM_PANEL_HEIGHT));
		this.initSwarm();
	}

	public SwarmPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		this.setPreferredSize(new Dimension(SwarmConfig.SWARM_PANEL_WIDTH, SwarmConfig.SWARM_PANEL_HEIGHT));
		this.initSwarm();
	}

	/*
	 * @Override public Dimension getPreferredSize() { return new Dimension(401, 400); }
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int centerX = 0, centerY = 0;

		for (Speck s : this.swarm.getSpecks()) {
			centerX = (int) Math.round(s.position().x()) + SwarmConfig.SPECK_RADIUS_X + 1;
			centerY = (int) Math.round(s.position().y()) + SwarmConfig.SPECK_RADIUS_Y + 1;

			if (s.type() == Speck.TYPE_PREY) {

				if (SwarmConfig.SHOW_SPECK_HEAD) {
					g2.drawLine(centerX, centerY, centerX + 2 * (int) Math.round(s.velocity().x()), centerY + 2 * (int) Math.round(s.velocity().y()));
				}

				g2.setColor(Color.orange);
				g2.fillOval(centerX - SwarmConfig.SPECK_RADIUS_X, centerY - SwarmConfig.SPECK_RADIUS_Y, 2 * SwarmConfig.SPECK_RADIUS_X, 2 * SwarmConfig.SPECK_RADIUS_Y);
				g2.setColor(Color.BLACK);
				g2.drawOval(centerX - SwarmConfig.SPECK_RADIUS_X, centerY - SwarmConfig.SPECK_RADIUS_Y, 2 * SwarmConfig.SPECK_RADIUS_X, 2 * SwarmConfig.SPECK_RADIUS_Y);

				if (SwarmConfig.SHOW_SPECK_ID) {
					g2.drawString("" + s.id(), centerX + SwarmConfig.SPECK_RADIUS_X + 1, centerY + SwarmConfig.SPECK_RADIUS_Y + 2);
				}
			} else if (s.type() == Speck.TYPE_PREDATOR) {

				if (SwarmConfig.SHOW_SPECK_HEAD) {
					g2.drawLine(centerX, centerY, centerX + 2 * (int) Math.round(s.velocity().x()), centerY + 2 * (int) Math.round(s.velocity().y()));
				}

				g2.setColor(Color.blue);
				g2.fillOval(centerX - SwarmConfig.SPECK_RADIUS_X + 2, centerY - SwarmConfig.SPECK_RADIUS_Y + 2, 2 * SwarmConfig.SPECK_RADIUS_X + 2, 2 * SwarmConfig.SPECK_RADIUS_Y + 2);
				g2.setColor(Color.BLACK);
				g2.drawOval(centerX - SwarmConfig.SPECK_RADIUS_X + 2, centerY - SwarmConfig.SPECK_RADIUS_Y + 2, 2 * SwarmConfig.SPECK_RADIUS_X + 2, 2 * SwarmConfig.SPECK_RADIUS_Y + 2);

				if (SwarmConfig.SHOW_SPECK_ID) {
					g2.drawString("" + s.id(), centerX + SwarmConfig.SPECK_RADIUS_X + 1, centerY + SwarmConfig.SPECK_RADIUS_Y + 2);
				}

			}

		}
	}

	private void initSwarm() {

		Dimension d = this.getPreferredSize();

		try {
			this.setSwarm((Swarm) Class.forName(SwarmConfig.SWARM_IMPL_CLASSNAME).newInstance());

			SwarmRuleManager.initialize(this.getSwarm());

		} catch (Exception E) {
			System.err.println("Cannot load impl class [" + SwarmConfig.SWARM_IMPL_CLASSNAME + "] - " + E.toString());
			this.setSwarm(new WallBounceSwarm());
		}

		this.getSwarm().setFieldDimensions(d.width - (2 * SwarmConfig.SPECK_RADIUS_X + 3), d.height - (2 * SwarmConfig.SPECK_RADIUS_Y + 3));
		this.getSwarm().initializeSwarm();
	}

	public Swarm getSwarm() {
		return swarm;
	}

	public void setSwarm(Swarm swarm) {
		this.swarm = swarm;
	}

}
