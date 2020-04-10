package com.hedgecourt.swarm.gui;

import javax.swing.SwingUtilities;

import com.hedgecourt.swarm.SwarmConfig;

public class ControlThread extends Thread {

	private boolean running = false;
	private boolean shutdown = false;

	private SwarmPanel swarmPane = null;

	public ControlThread() {
		super();
	}

	public ControlThread(Runnable target) {
		super(target);
	}

	public ControlThread(String name) {
		super(name);
	}

	public ControlThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	public ControlThread(ThreadGroup group, String name) {
		super(group, name);
	}

	public ControlThread(Runnable target, String name) {
		super(target, name);
	}

	public ControlThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	public ControlThread(ThreadGroup group, Runnable target, String name, long stackSize) {
		super(group, target, name, stackSize);
	}

	@Override
	public void run() {

		// System.out.println("begin run()");

		while (!this.isShutdown()) {

			if (this.isRunning()) {
				// System.out.println("running, advance the swarm and sleep for INTERVAL");
				this.swarmPane.getSwarm().advance();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						swarmPane.repaint();
					}
				});

				try {
					synchronized (this) {
						long waitMillis = SwarmConfig.CONTROL_THREAD_SLEEP_MILLIS;
						if (SwarmConfig.CONTROL_THREAD_ADJUST_TIME) {
							waitMillis -= this.getSwarmPane().getSwarm().getSwarmTimer().getIterationMillis();
						}
						if (waitMillis > 0) {
							wait(waitMillis);
						}

					}
				} catch (InterruptedException E) {
				}

			} else {
				// if we're not running, I'll just wait for a nudge
				try {
					// System.out.println("not running, waiting for a nudge");
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException E) {
					// System.out.println("they nudged me");
				}
			}
		}

		// System.out.println("exit run()");
	}

	public boolean isRunning() {
		return running;
	}

	public synchronized void setRunning(boolean running) {
		this.running = running;
		this.notifyAll();
	}

	public boolean isShutdown() {
		return shutdown;
	}

	public synchronized void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
		this.notifyAll();
	}

	public SwarmPanel getSwarmPane() {
		return swarmPane;
	}

	public void setSwarmPane(SwarmPanel swarmPane) {
		this.swarmPane = swarmPane;
	}

}
