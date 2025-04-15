package com.kreative.polyhedra;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.gen.Construct;
import com.kreative.polyhedra.gen.Path;

public abstract class PolyhedronCon {
	public abstract void defaultAction();
	public abstract void consume(String source, Polyhedron p);
	public abstract void reportError(String message, Exception e);
	
	public final void consumeOrReportError(String source, Polyhedron p) {
		if (p != null) {
			consume(source, p);
		} else {
			reportError("Error: No polyhedron found in " + source, null);
		}
	}
	
	public final void consumeOFFOrReportError(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			OFFReader reader = new OFFReader(in);
			Polyhedron p = reader.readPolyhedron();
			in.close();
			consumeOrReportError(file.toString(), p);
		} catch (IOException e) {
			reportError("Error: Cannot read " + file + ": " + e, e);
		}
	}
	
	public final void consumeOFFOrReportError(String source, InputStream in) {
		OFFReader reader = new OFFReader(in);
		Polyhedron p = reader.readPolyhedron();
		consumeOrReportError(source, p);
	}
	
	public final void consumeMcCooeyOrReportError(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			McCooeyReader reader = new McCooeyReader(in, Color.gray);
			Polyhedron p = reader.readPolyhedron();
			in.close();
			consumeOrReportError(file.toString(), p);
		} catch (IOException e) {
			reportError("Error: Cannot read " + file + ": " + e, e);
		}
	}
	
	public final void consumeMcCooeyOrReportError(String source, InputStream in) {
		McCooeyReader reader = new McCooeyReader(in, Color.gray);
		Polyhedron p = reader.readPolyhedron();
		consumeOrReportError(source, p);
	}
	
	public final void consumeOrReportError(String factoryName, String[] args, int argi, int argn) {
		PolyhedronGen.Factory<? extends PolyhedronGen> genFactory;
		genFactory = com.kreative.polyhedra.gen.BOM.MAP.get(factoryName);
		if (genFactory != null) {
			List<String> cargl = Arrays.asList(args).subList(argi, argn);
			String[] cargs = cargl.toArray(new String[argn - argi]);
			PolyhedronGen gen = genFactory.parse(cargs);
			if (gen != null) {
				Polyhedron p = gen.gen();
				if (p != null) {
					consume(factoryName, p);
				} else {
					reportError("Invalid parameters for generator " + factoryName, null);
				}
			} else {
				reportError("Invalid options for generator " + factoryName, null);
			}
		} else {
			reportError("Unknown generator " + factoryName, null);
		}
	}
	
	public final void printOptions() {
		System.err.println("Options:");
		System.err.println("  -                     consume polyhedron in OFF format from standard input");
		System.err.println("  -- <gen> <arg> ...    consume polyhedron generated from remaining arguments");
		System.err.println("  -c <text>             consume polyhedron generated from Conway notation");
		System.err.println("  -f <path>             consume polyhedron in OFF format from file");
		System.err.println("  -g <text>             consume polyhedron generated from arguments string");
		System.err.println("  -m <path>             consume polyhedron in McCooey format from file");
		System.err.println("  -p <text>             consume polyhedron generated from SVG-path-like string");
		System.err.println("  [ <gen> <arg> ... ]   consume polyhedron generated from arguments between [ ]");
	}
	
	public final void processArgs(String[] args) {
		if (args.length == 0) {
			defaultAction();
		} else {
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-h") || arg.equals("-help") || arg.equals("--help")) {
					printOptions();
				} else if (arg.equals("-")) {
					consumeOFFOrReportError("standard input", System.in);
				} else if (arg.equals("--") && argi < args.length) {
					String factoryName = args[argi++];
					consumeOrReportError(factoryName, args, argi, args.length);
					break;
				} else if (arg.equals("-c") && argi < args.length) {
					String[] cargs = new String[]{ args[argi++] };
					PolyhedronGen gen = new Construct.Factory().parse(cargs);
					Polyhedron p = (gen != null) ? gen.gen() : null;
					if (p != null) consume(cargs[0], p);
					else reportError("Invalid parameter for -c: " + cargs[0], null);
				} else if (arg.equals("-f") && argi < args.length) {
					consumeOFFOrReportError(new File(args[argi++]));
				} else if (arg.equals("-g") && argi < args.length) {
					String s = args[argi++];
					PolyhedronGen gen = PolyhedronUtils.parseGen(s);
					Polyhedron p = (gen != null) ? gen.gen() : null;
					if (p != null) consume(s, p);
					else reportError("Invalid parameter for -g: " + s, null);
				} else if (arg.equals("-m") && argi < args.length) {
					consumeMcCooeyOrReportError(new File(args[argi++]));
				} else if (arg.equals("-p") && argi < args.length) {
					String[] cargs = new String[]{ args[argi++] };
					PolyhedronGen gen = new Path.Factory().parse(cargs);
					Polyhedron p = (gen != null) ? gen.gen() : null;
					if (p != null) consume(cargs[0], p);
					else reportError("Invalid parameter for -p: " + cargs[0], null);
				} else if (arg.equals("[") && argi < args.length) {
					String factoryName = args[argi++];
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
					consumeOrReportError(factoryName, args, startIndex, endIndex);
				} else {
					consumeOFFOrReportError(new File(arg));
				}
			}
		}
	}
}