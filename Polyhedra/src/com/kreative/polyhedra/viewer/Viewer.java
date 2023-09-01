package com.kreative.polyhedra.viewer;

import java.io.FileInputStream;
import java.io.IOException;
import com.kreative.polyhedra.OFFReader;
import com.kreative.polyhedra.Polyhedron;

public class Viewer {
	public static void main(String[] args) {
		if (args.length == 0) {
			new ViewerFrame(new ColorCube()).setVisible(true);
		} else {
			for (String arg : args) {
				if (arg.equals("-")) {
					OFFReader reader = new OFFReader(System.in);
					Polyhedron p = reader.readPolyhedron();
					if (p == null) System.err.println("Error: No polyhedron found in standard input");
					else new ViewerFrame(p).setVisible(true);
				} else try {
					FileInputStream in = new FileInputStream(arg);
					OFFReader reader = new OFFReader(in);
					Polyhedron p = reader.readPolyhedron();
					in.close();
					if (p == null) System.err.println("Error: No polyhedron found in " + arg);
					else new ViewerFrame(p).setVisible(true);
				} catch (IOException e) {
					System.err.println("Error: No polyhedron found in " + arg + ": " + e);
				}
			}
		}
	}
}