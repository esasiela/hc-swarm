package com.hedgecourt.swarm;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SwarmTimer {

	private NumberFormat AVG_FMT = new DecimalFormat("###,##0.000");

	private PrintStream out = System.out;

	private long iterationCount = 0;

	private int threadCount = 1;

	private long iterationStartNanos = 0;
	private long iterationEndNanos = 0;
	private long iterationTotalNanos = 0;
	private boolean iterationIsRunning = false;

	private long[] threadStartNanos = null;
	private long[] threadEndNanos = null;
	private long[] threadTotalNanos = null;
	private boolean[] threadIsRunning = null;

	public SwarmTimer() {
		super();
	}

	public SwarmTimer(int threadCount) {
		this();
		this.setThreadCount(threadCount);
	}

	public synchronized void beginIteration() {
		this.incIterationCount();
		this.iterationIsRunning = true;
		this.iterationStartNanos = System.nanoTime();
	}

	public synchronized void endIteration() {
		this.iterationEndNanos = System.nanoTime();
		this.iterationIsRunning = false;
		this.iterationTotalNanos += this.iterationEndNanos - this.iterationStartNanos;

		if (this.iterationCount % SwarmConfig.PERFORMANCE_TIMER_MODULUS == 0) {
			this.printReport();
		}
	}

	public void printReport() {

		this.out.println("Swarm Timer Report:");
		this.out.println("\tIteration Count: " + this.iterationCount);
		this.out.println("\tAvg Exec Time  : " + this.AVG_FMT.format(((double) this.iterationTotalNanos) / (this.iterationCount * 1000000)));

	}

	public long getIterationMillis() {
		return (this.iterationEndNanos - this.iterationStartNanos) / 1000000;
	}

	public synchronized void beginThread(int threadId) {

	}

	public synchronized void endThread(int threadId) {

	}

	public long getIterationCount() {
		return iterationCount;
	}

	public synchronized void setIterationCount(long iterationCount) {
		this.iterationCount = iterationCount;
	}

	private synchronized void incIterationCount() {
		this.iterationCount++;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public void setPrintStream(PrintStream out) {
		this.out = out;
	}

}
