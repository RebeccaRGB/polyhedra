package com.kreative.polyhedra.viewer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.swing.SwingUtilities;
import com.kreative.polyhedra.OFFReader;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.PolyhedronUtils;
import com.kreative.polyhedra.gen.Construct;

public class Viewer {
	public static void main(String[] args) {
		if (args.length == 0) {
			open(null, new ColorCube());
		} else {
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-")) {
					open("standard input", System.in);
				} else if (arg.equals("--") && argi < args.length) {
					String className = args[argi++];
					open(className, args, argi, args.length);
					break;
				} else if (arg.equals("-c") && argi < args.length) {
					String s = args[argi++];
					PolyhedronGen gen = Construct.parse(new String[]{s});
					if (gen != null) open(s, gen.gen());
				} else if (arg.equals("-f") && argi < args.length) {
					open(args[argi++]);
				} else if (arg.equals("-g") && argi < args.length) {
					String s = args[argi++];
					PolyhedronGen gen = PolyhedronUtils.parseGen(s);
					if (gen != null) open(s, gen.gen());
				} else if (arg.equals("[") && argi < args.length) {
					String className = args[argi++];
					int startIndex = argi, endIndex = argi, level = 0;
					while (argi < args.length) {
						arg = args[argi++];
						if (arg.equals("[")) {
							level++;
						} else if (arg.equals("]")) {
							if (level == 0) break;
							level--;
						}
						endIndex++;
					}
					open(className, args, startIndex, endIndex);
				} else {
					open(arg);
				}
			}
		}
	}
	
	public static void open(String className, String[] args, int argi, int argn) {
		Class<? extends PolyhedronGen> genClass;
		genClass = com.kreative.polyhedra.gen.BOM.MAP.get(className);
		if (genClass == null) {
			genClass = com.kreative.polyhedra.gen.BOM.MAP.get(className.toLowerCase());
			if (genClass == null) {
				System.err.println("Unknown generator " + className);
				return;
			}
		}
		try {
			Method parse = genClass.getMethod("parse", String[].class);
			List<String> cargl = Arrays.asList(args).subList(argi, argn);
			String[] cargs = cargl.toArray(new String[argn - argi]);
			Object gen = parse.invoke(null, (Object)cargs);
			if (gen == null) return;
			open(className, ((PolyhedronGen)gen).gen());
		} catch (Exception e) {
			System.err.println("Error invoking generator " + className + ": " + e);
		}
	}
	
	public static void open(String src, InputStream in) {
		OFFReader reader = new OFFReader(in);
		Polyhedron p = reader.readPolyhedron();
		open(src, p);
	}
	
	public static void open(String path) {
		try {
			FileInputStream in = new FileInputStream(path);
			OFFReader reader = new OFFReader(in);
			Polyhedron p = reader.readPolyhedron();
			in.close();
			open(path, p);
		} catch (IOException e) {
			System.err.println("Error: No polyhedron found in " + path + ": " + e);
		}
	}
	
	public static void open(String src, final Polyhedron p) {
		if (p != null) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new ViewerFrame(p).setVisible(true);
				}
			});
		} else {
			System.err.println("Error: No polyhedron found in " + src);
		}
	}
}