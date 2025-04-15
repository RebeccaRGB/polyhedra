package com.kreative.polyhedra;

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
	
	public final void consumeOrReportError(Format format, File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			Polyhedron p = format.read(in);
			in.close();
			consumeOrReportError(file.getName(), p);
		} catch (IOException e) {
			reportError("Error: Cannot read from " + file.getName() + ": " + e, e);
		}
	}
	
	public final void consumeOrReportError(String source, Format format, InputStream in) {
		Polyhedron p = format.read(in);
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
		System.err.println("  -f <path>             consume polyhedron from file (format from extension)");
		System.err.println("  -g <text>             consume polyhedron generated from arguments string");
		System.err.println("  -m <path>             consume polyhedron in McCooey format from file");
		System.err.println("  -o <path>             consume polyhedron in OFF format from file");
		System.err.println("  -p <text>             consume polyhedron generated from Path3D string");
		System.err.println("  -q <path>             consume polyhedron in Path3D format from file");
		System.err.println("  [ <gen> <arg> ... ]   consume polyhedron generated from arguments between [ ]");
	}
	
	public final void processArgs(String[] args) {
		if (args.length == 0) {
			defaultAction();
		} else {
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equals("-")) {
					consumeOrReportError(STDIN, Format.OFF, System.in);
				} else if (arg.equals("--") && argi < args.length) {
					String factoryName = args[argi++];
					consumeOrReportError(factoryName, args, argi, args.length);
					return;
				} else if (arg.equals("-c") && argi < args.length) {
					String[] cargs = new String[]{ args[argi++] };
					PolyhedronGen gen = new Construct.Factory().parse(cargs);
					Polyhedron p = (gen != null) ? gen.gen() : null;
					if (p != null) consume(cargs[0], p);
					else reportError("Invalid parameter for -c: " + cargs[0], null);
				} else if (arg.equals("-f") && argi < args.length) {
					File file = new File(args[argi++]);
					Format format = Format.forFile(file);
					if (format == null) format = Format.OFF;
					consumeOrReportError(format, file);
				} else if (arg.equals("-g") && argi < args.length) {
					String s = args[argi++];
					PolyhedronGen gen = PolyhedronUtils.parseGen(s);
					Polyhedron p = (gen != null) ? gen.gen() : null;
					if (p != null) consume(s, p);
					else reportError("Invalid parameter for -g: " + s, null);
				} else if (arg.equals("-h") || arg.equals("-help") || arg.equals("--help")) {
					printOptions();
					return;
				} else if (arg.equals("-m") && argi < args.length) {
					String file = args[argi++];
					if (file.equals("-")) consumeOrReportError(STDIN, Format.MCCOOEY, System.in);
					else consumeOrReportError(Format.MCCOOEY, new File(file));
				} else if (arg.equals("-o") && argi < args.length) {
					String file = args[argi++];
					if (file.equals("-")) consumeOrReportError(STDIN, Format.OFF, System.in);
					else consumeOrReportError(Format.OFF, new File(file));
				} else if (arg.equals("-p") && argi < args.length) {
					String[] cargs = new String[]{ args[argi++] };
					PolyhedronGen gen = new Path.Factory().parse(cargs);
					Polyhedron p = (gen != null) ? gen.gen() : null;
					if (p != null) consume(cargs[0], p);
					else reportError("Invalid parameter for -p: " + cargs[0], null);
				} else if (arg.equals("-q") && argi < args.length) {
					String file = args[argi++];
					if (file.equals("-")) consumeOrReportError(STDIN, Format.PATH3D, System.in);
					else consumeOrReportError(Format.PATH3D, new File(file));
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
				} else if (arg.startsWith("-")) {
					String fs = arg.replaceFirst("^-+", "");
					Format format = Format.forName(fs);
					if (format != null && argi < args.length) {
						String file = args[argi++];
						if (file.equals("-")) consumeOrReportError(STDIN, format, System.in);
						else consumeOrReportError(format, new File(file));
					} else {
						printOptions();
						return;
					}
				} else {
					File file = new File(arg);
					Format format = Format.forFile(file);
					if (format == null) format = Format.OFF;
					consumeOrReportError(format, file);
				}
			}
		}
	}
	
	private static final String STDIN = "standard input";
}