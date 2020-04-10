package com.hedgecourt.swarm.gui;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class TextAreaPrintStream extends PrintStream {

	// The JTextArea to wich the output stream will be redirected.
	private JTextArea textArea;

	/**
	 * Method TextAreaPrintStream
	 * The constructor of the class.
	 * 
	 * @param the
	 *            JTextArea to wich the output stream will be redirected.
	 * @param a
	 *            standard output stream (needed by super method)
	 **/
	public TextAreaPrintStream(JTextArea area, OutputStream out) {
		super(out);
		textArea = area;
	}

	/**
	 * Method println
	 * 
	 * @param the
	 *            String to be output in the JTextArea textArea (private
	 *            attribute of the class).
	 *            After having printed such a String, prints a new line.
	 **/
	@Override
	public void println(String string) {
		this.print(string + "\n");

	}

	/**
	 * Method print
	 * 
	 * @param the
	 *            String to be output in the JTextArea textArea (private
	 *            attribute of the class).
	 **/
	@Override
	public void print(String string) {
		textArea.append(string);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}