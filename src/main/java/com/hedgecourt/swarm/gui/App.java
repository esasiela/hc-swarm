package com.hedgecourt.swarm.gui;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import com.hedgecourt.swarm.Speck;
import com.hedgecourt.swarm.SwarmConfig;

/**
 * Hello world!
 *
 */
public class App implements ActionListener, ListSelectionListener {

	private JButton advanceButton = null;
	private JButton runButton = null;
	private SwarmPanel swarmPane = null;
	private ControlThread controlThread = null;
	private JTable speckTable = null;

	private void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = null;
		GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		if (screenDevices.length > 1) {
			// we have a second monitor, send our window there
			frame = new JFrame(SwarmConfig.GUI_WINDOW_TITLE, screenDevices[1].getDefaultConfiguration());
		} else {
			// single monitor, just use default graphicsConfig
			frame = new JFrame(SwarmConfig.GUI_WINDOW_TITLE);
		}

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (controlThread != null) {

					controlThread.setShutdown(true);
					try {
						controlThread.join(2000);
					} catch (InterruptedException E) {

					}

				}

				e.getWindow().dispose();
			}
		});

		Border blackLine = BorderFactory.createLineBorder(Color.black);

		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.LINE_AXIS));

		/*
		 * CONTROL PANE
		 */
		JPanel controlPane = new JPanel();
		controlPane.setBorder(blackLine);
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.PAGE_AXIS));

		this.advanceButton = new JButton("Advance");
		this.advanceButton.addActionListener(this);
		controlPane.add(this.advanceButton);

		this.runButton = new JButton("Run");
		this.runButton.setActionCommand("run");
		this.runButton.addActionListener(this);
		controlPane.add(this.runButton);

		mainPane.add(controlPane);

		/*
		 * SWARM PANE
		 */
		this.swarmPane = new SwarmPanel();
		this.swarmPane.setBorder(blackLine);

		mainPane.add(this.swarmPane);

		/*
		 * TABBED PANE
		 */
		JTabbedPane tabPane = new JTabbedPane();

		/*
		 * DATA GRID PANE
		 */
		TableModel tableModel = new SwarmTableModel(this.swarmPane.getSwarm());
		speckTable = new JTable(tableModel);
		speckTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		speckTable.getSelectionModel().addListSelectionListener(this);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		for (int x = 1; x <= 6; x++) {
			speckTable.getColumnModel().getColumn(x).setCellRenderer(rightRenderer);
		}

		JScrollPane scrollPane = new JScrollPane(speckTable);
		scrollPane.setBorder(blackLine);
		speckTable.setFillsViewportHeight(true);

		tabPane.addTab("Speck Data", scrollPane);

		/*
		 * CONFIG CONSTANTS PANE
		 */

		tabPane.addTab("Config", new ConfigConstPanel());

		/*
		 * TIMER LOG PANE
		 */
		TextConsolePanel timerLogPane = new TextConsolePanel();
		this.swarmPane.getSwarm().getSwarmTimer().setPrintStream(timerLogPane.getPrintStream());
		tabPane.addTab("Performance", timerLogPane);
		tabPane.setSelectedIndex(2);

		mainPane.add(tabPane);

		/*
		 * CONTROL THREAD
		 */
		this.controlThread = new ControlThread();
		this.controlThread.setSwarmPane(this.swarmPane);
		this.controlThread.start();

		/*
		 * PACK AND SHOW
		 */
		frame.getContentPane().add(mainPane);
		frame.pack();

		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new App().createAndShowGUI();
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareToIgnoreCase("advance") == 0) {
			this.swarmPane.getSwarm().advance();
			this.swarmPane.repaint();
		} else if (e.getActionCommand().compareToIgnoreCase("run") == 0) {
			if (this.controlThread.isRunning()) {
				this.runButton.setText("Run");
				this.advanceButton.setEnabled(true);
				this.controlThread.setRunning(false);
			} else {
				this.runButton.setText("Stop");
				this.advanceButton.setEnabled(false);
				this.controlThread.setRunning(true);
			}
		}

	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() || speckTable.getSelectedRow() < 0) {
			return;
		}

		System.out.println("Row Selection Event - " + speckTable.getSelectedRow());
		Speck s = this.swarmPane.getSwarm().getSpecks().get(speckTable.getSelectedRow());

		System.out.println("\tSpeck ID " + s.id());
		if (s.position() == null) {
			System.out.println("\tPosition: null");
		} else {
			System.out.println("\tPosition: " + s.position().toString());
		}
		if (s.velocity() == null) {
			System.out.println("\tVelocity: null");
		} else {
			System.out.println("\tVelocity: " + s.velocity().toString());
		}
		if (s.queuedVelocity() == null) {
			System.out.println("\tqVel    : null");
		} else {
			System.out.println("\tqVel    : " + s.queuedVelocity().toString());
		}

	}
}
