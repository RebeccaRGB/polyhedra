package com.kreative.polyhedra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Convert {
	public static void printHelp() {
		System.err.println("Usage:");
		System.err.println("  convert [<input-format>] <input-file> [<output-format>] <output-file>");
		System.err.println("  convert [<input-format>] <input-file> <output-format> -");
		System.err.println("  convert <input-format> - [<output-format>] <output-file>");
		System.err.println("  convert <input-format> - <output-format> -");
		System.err.println();
		System.err.println("Formats:");
		for (Format format : Format.values()) {
			for (String name : format.getNames()) {
				if (!name.startsWith(".")) {
					System.err.print("  --");
					System.err.print(name);
				}
			}
			System.err.println();
		}
		System.err.println();
		System.err.println("Specify a file path of - for standard input or output.");
		System.err.println("Format is required for standard input or output, optional for files.");
	}
	
	public static void main(String[] args) {
		Format inputFormat;
		Format outputFormat;
		File inputFile;
		File outputFile;
		int argi = 0;
		
		// Input file
		if (argi < args.length) {
			String arg = args[argi++];
			if (arg.startsWith("-")) {
				arg = arg.replaceFirst("^-+", "");
				inputFormat = Format.forName(arg);
				if (inputFormat != null && argi < args.length) {
					arg = args[argi++];
					if (arg.equals("-")) inputFile = null;
					else inputFile = new File(arg);
				} else {
					printHelp();
					return;
				}
			} else {
				inputFile = new File(arg);
				inputFormat = Format.forFile(inputFile);
				if (inputFormat == null) {
					printHelp();
					return;
				}
			}
		} else {
			printHelp();
			return;
		}
		
		// Output file
		if (argi < args.length) {
			String arg = args[argi++];
			if (arg.startsWith("-")) {
				arg = arg.replaceFirst("^-+", "");
				outputFormat = Format.forName(arg);
				if (outputFormat != null && argi < args.length) {
					arg = args[argi++];
					if (arg.equals("-")) outputFile = null;
					else outputFile = new File(arg);
				} else {
					printHelp();
					return;
				}
			} else {
				outputFile = new File(arg);
				outputFormat = Format.forFile(outputFile);
				if (outputFormat == null) {
					printHelp();
					return;
				}
			}
		} else {
			printHelp();
			return;
		}
		
		// Check for no more arguments
		if (argi < args.length) {
			printHelp();
			return;
		}
		
		convert(inputFormat, inputFile, outputFormat, outputFile);
	}
	
	public static void convert(Format inputFormat, File inputFile, Format outputFormat, File outputFile) {
		if (inputFile == null) {
			Polyhedron p = inputFormat.read(System.in);
			writeOne("standard input", p, outputFormat, outputFile);
		} else if (inputFile.isDirectory()) {
			convertMany(inputFormat, inputFile, outputFormat, outputFile);
		} else {
			convertOne(inputFormat, inputFile, outputFormat, outputFile);
		}
	}
	
	private static void convertMany(Format inputFormat, File inputFile, Format outputFormat, File outputFile) {
		if (!outputFile.exists()) outputFile.mkdir();
		for (File child : inputFile.listFiles()) {
			String childName = child.getName();
			if (!childName.startsWith(".")) {
				if (child.isDirectory()) {
					convertMany(inputFormat, child, outputFormat, new File(outputFile, childName));
				} else {
					int o = childName.lastIndexOf('.');
					if (o > 0) childName = childName.substring(0, o);
					childName += outputFormat.getExtension();
					convertOne(inputFormat, child, outputFormat, new File(outputFile, childName));
				}
			}
		}
	}
	
	private static void convertOne(Format inputFormat, File inputFile, Format outputFormat, File outputFile) {
		try {
			FileInputStream in = new FileInputStream(inputFile);
			Polyhedron p = inputFormat.read(in);
			in.close();
			writeOne(inputFile.getName(), p, outputFormat, outputFile);
		} catch (IOException e) {
			System.err.println("Error: Cannot read from " + inputFile.getName() + ": " + e);
		}
	}
	
	private static void writeOne(String source, Polyhedron p, Format outputFormat, File outputFile) {
		if (p != null) {
			if (outputFile == null) {
				outputFormat.write(p, System.out);
			} else {
				try {
					FileOutputStream out = new FileOutputStream(outputFile);
					outputFormat.write(p, out);
					out.close();
				} catch (IOException e) {
					System.err.println("Error: Cannot write to " + outputFile.getName() + ": " + e);
				}
			}
		} else {
			System.err.println("Error: No polyhedron found in " + source);
		}
	}
}