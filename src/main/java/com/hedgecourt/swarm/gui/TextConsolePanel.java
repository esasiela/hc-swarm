package com.hedgecourt.swarm.gui;

import java.awt.LayoutManager;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextConsolePanel extends JPanel {

	private PrintStream printStream = null;
	private JTextArea textPane = null;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2421150658561303478L;

	public TextConsolePanel() {
		super();
		this.setup();
	}

	public TextConsolePanel(LayoutManager layout) {
		super(layout);
		this.setup();

	}

	public TextConsolePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		this.setup();
	}

	public TextConsolePanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		this.setup();
	}

	protected void setup() {

		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// text pane
		this.textPane = new JTextArea();

		// scroll pane
		JScrollPane scrollPane = new JScrollPane(this.textPane);

		this.setPrintStream(new PrintStream(new ByteArrayOutputStream(1024)));

		this.add(scrollPane);

	}

	public PrintStream getPrintStream() {
		return printStream;
	}

	public void setPrintStream(PrintStream printStream) {
		this.printStream = new TextAreaPrintStream(this.textPane, printStream);
	}

}
