package com.kreative.polyhedra.gen;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import com.kreative.polyhedra.Path3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;

public class Path extends PolyhedronGen {
	private final Path3D path;
	
	public Path(Path3D path) {
		this.path = path;
	}
	
	public Polyhedron gen() {
		return path.createPolyhedron();
	}
	
	public static class Factory extends PolyhedronGen.Factory<Path> {
		public String name() { return "Path"; }
		
		public Path parse(String[] args) {
			Path3D path = new Path3D();
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					path.setNewFaceColor(parseColor(args[argi++], path.getNewFaceColor()));
				} else if (arg.equalsIgnoreCase("-f") && argi < args.length) {
					StringBuffer sb = new StringBuffer();
					File file = new File(args[argi++]);
					try {
						Scanner scan = new Scanner(file);
						while (scan.hasNextLine()) {
							sb.append(scan.nextLine());
							sb.append("\n");
						}
						scan.close();
					} catch (IOException e) {
						System.err.println(e);
					}
					path.parse(sb.toString());
				} else {
					path.parse(arg);
				}
			}
			if (path.isEmpty()) {
				System.err.println("Error: No polyhedron found in arguments.");
				return null;
			}
			return new Path(path);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("c", Type.COLOR, "color of new faces"),
				new Option("f", Type.PATH, "read path from text file"),
				new Option(Mult.REPEATED, Type.TEXT, "path given as argument")
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}