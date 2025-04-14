package com.kreative.polyhedra.viewer;

import javax.swing.SwingUtilities;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronCon;

public class Viewer extends PolyhedronCon {
	public static void main(String[] args) {
		new Viewer().processArgs(args);
	}
	
	public void defaultAction() {
		consume(null, new ColorCube());
	}
	
	public void consume(final String source, final Polyhedron p) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ViewerFrame(p).setVisible(true);
			}
		});
	}
	
	public void reportError(String message, Exception e) {
		System.err.println(message);
	}
}