package com.kreative.polyhedra;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ConwayNotationParser {
	public static final class Repeater extends PolyhedronOp {
		private final PolyhedronOp op;
		private final int repeat;
		private Repeater(PolyhedronOp op, int repeat) {
			this.op = op;
			this.repeat = repeat;
		}
		public Polyhedron op(Polyhedron seed) {
			for (int i = 0; i < repeat; i++) seed = op.op(seed);
			return seed;
		}
	}
	
	private final ArrayList<String> classNames = new ArrayList<String>();
	private final ArrayList<String> numericArguments = new ArrayList<String>();
	private final ArrayList<String[]> stringArguments = new ArrayList<String[]>();
	private int count = 0;
	
	public void parse(String s) {
		int si = 0, sn = s.length(), cp;
		while (si < sn && Character.isWhitespace((cp = s.codePointAt(si)))) si += Character.charCount(cp);
		while (si < sn) {
			
			// Get class name.
			StringBuffer className = new StringBuffer();
			if (s.charAt(si) == '(') {
				si++;
				int level = 0;
				while (si < sn) {
					char ch = s.charAt(si++);
					if (ch == '(') {
						level++;
					} else if (ch == ')') {
						if (level == 0) break;
						level--;
					}
					className.append(ch);
				}
			} else if (Character.isLetter((cp = s.codePointAt(si)))) {
				si += Character.charCount(cp);
				className.append(Character.toChars(cp));
			}
			if (className.length() == 0) {
				throw new IllegalArgumentException(s);
			}
			
			// Skip whitespace.
			while (si < sn && Character.isWhitespace((cp = s.codePointAt(si)))) si += Character.charCount(cp);
			
			// Get numeric argument.
			StringBuffer numericArgument = new StringBuffer();
			while (si < sn && (Character.isDigit((cp = s.codePointAt(si))) || cp == '.' || cp == ',')) {
				si += Character.charCount(cp);
				numericArgument.append(Character.toChars(cp));
			}
			
			// Skip whitespace.
			while (si < sn && Character.isWhitespace((cp = s.codePointAt(si)))) si += Character.charCount(cp);
			
			// Get string arguments.
			StringBuffer stringArguments = new StringBuffer();
			if (si < sn && s.charAt(si) == '{') {
				si++;
				int level = 0;
				while (si < sn) {
					char ch = s.charAt(si++);
					if (ch == '{') {
						level++;
					} else if (ch == '}') {
						if (level == 0) break;
						level--;
					}
					stringArguments.append(ch);
				}
			}
			
			// Skip whitespace.
			while (si < sn && Character.isWhitespace((cp = s.codePointAt(si)))) si += Character.charCount(cp);
			
			// Add to lists.
			this.classNames.add(className.toString());
			this.numericArguments.add(numericArgument.toString());
			this.stringArguments.add(PolyhedronUtils.parseArgs(stringArguments.toString()));
			this.count++;
		}
	}
	
	private static PolyhedronGen instantiateGen(Class<? extends PolyhedronGen> genClass, String... args) {
		try {
			Method parse = genClass.getMethod("parse", String[].class);
			Object gen = parse.invoke(null, (Object)args);
			return (PolyhedronGen)gen;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error invoking generator " + genClass.getSimpleName(), e);
		}
	}
	
	private static PolyhedronOp instantiateOp(Class<? extends PolyhedronOp> opClass, String... args) {
		try {
			Method parse = opClass.getMethod("parse", String[].class);
			Object op = parse.invoke(null, (Object)args);
			return (PolyhedronOp)op;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error invoking operation " + opClass.getSimpleName(), e);
		}
	}
	
	private static boolean hasNFlag(Class<? extends PolyhedronOp> opClass) {
		try {
			Method optionsMethod = opClass.getMethod("options");
			if (optionsMethod == null) return false;
			Object options = optionsMethod.invoke(null);
			if (options == null) return false;
			for (PolyhedronUtils.Option option : (PolyhedronUtils.Option[])options) {
				if ("-n".equals(option.flag)) return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private static String[] mergeArgs(String numericArg, String[] stringArgs) {
		String[] newStringArgs = new String[stringArgs.length + 2];
		int index = 0;
		newStringArgs[index++] = "-n";
		newStringArgs[index++] = numericArg;
		for (String arg : stringArgs) newStringArgs[index++] = arg;
		return newStringArgs;
	}
	
	public PolyhedronGen getGenerator(int index) {
		String className = classNames.get(index);
		String numericArg = numericArguments.get(index);
		String[] stringArgs = stringArguments.get(index);
		Class<? extends PolyhedronGen> genClass;
		
		genClass = com.kreative.polyhedra.gen.BOM.MAP.get(className + numericArg);
		if (genClass != null) return instantiateGen(genClass, stringArgs);
		
		if (numericArg.length() > 0) stringArgs = mergeArgs(numericArg, stringArgs);
		
		genClass = com.kreative.polyhedra.gen.BOM.MAP.get(className);
		if (genClass != null) return instantiateGen(genClass, stringArgs);
		
		genClass = com.kreative.polyhedra.gen.BOM.MAP.get(className.toLowerCase());
		if (genClass != null) return instantiateGen(genClass, stringArgs);
		
		throw new IllegalArgumentException("Unknown generator " + className);
	}
	
	public PolyhedronOp getOperation(int index) {
		String className = classNames.get(index);
		String numericArg = numericArguments.get(index);
		String[] stringArgs = stringArguments.get(index);
		Class<? extends PolyhedronOp> opClass;
		
		opClass = com.kreative.polyhedra.op.BOM.MAP.get(className + numericArg);
		if (opClass != null) return instantiateOp(opClass, stringArgs);
		
		opClass = com.kreative.polyhedra.op.BOM.MAP.get(className);
		if (opClass != null) {
			if (numericArg.length() > 0) {
				int repeat = PolyhedronUtils.parseInt(numericArg, 0);
				if (repeat > 0 && !hasNFlag(opClass)) return new Repeater(instantiateOp(opClass, stringArgs), repeat);
				stringArgs = mergeArgs(numericArg, stringArgs);
			}
			return instantiateOp(opClass, stringArgs);
		}
		
		opClass = com.kreative.polyhedra.op.BOM.MAP.get(className.toLowerCase());
		if (opClass != null) {
			if (numericArg.length() > 0) {
				int repeat = PolyhedronUtils.parseInt(numericArg, 0);
				if (repeat > 0 && !hasNFlag(opClass)) return new Repeater(instantiateOp(opClass, stringArgs), repeat);
				stringArgs = mergeArgs(numericArg, stringArgs);
			}
			return instantiateOp(opClass, stringArgs);
		}
		
		throw new IllegalArgumentException("Unknown operation " + className);
	}
	
	public boolean isEmpty() {
		return count == 0;
	}
	
	public int size() {
		return count;
	}
}