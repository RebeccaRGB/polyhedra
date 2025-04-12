package com.kreative.polyhedra;

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
	
	private final ArrayList<String> factoryNames = new ArrayList<String>();
	private final ArrayList<String> numericArguments = new ArrayList<String>();
	private final ArrayList<String[]> stringArguments = new ArrayList<String[]>();
	private int count = 0;
	
	public void parse(String s) {
		int si = 0, sn = s.length(), cp;
		while (si < sn && Character.isWhitespace((cp = s.codePointAt(si)))) si += Character.charCount(cp);
		while (si < sn) {
			
			// Get factory name.
			StringBuffer factoryName = new StringBuffer();
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
					factoryName.append(ch);
				}
			} else if (Character.isLetter((cp = s.codePointAt(si)))) {
				si += Character.charCount(cp);
				factoryName.append(Character.toChars(cp));
				if (si < sn && s.charAt(si) == '*') {
					factoryName.append(s.charAt(si++));
				}
			}
			if (factoryName.length() == 0) {
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
			this.factoryNames.add(factoryName.toString());
			this.numericArguments.add(numericArgument.toString());
			this.stringArguments.add(PolyhedronUtils.parseArgs(stringArguments.toString()));
			this.count++;
		}
	}
	
	private static boolean hasNFlag(PolyhedronOp.Factory<? extends PolyhedronOp> opFactory) {
		PolyhedronUtils.Option[] options = opFactory.options();
		if (options != null) {
			for (PolyhedronUtils.Option option : options) {
				if ("-n".equals(option.flag)) return true;
			}
		}
		return false;
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
		String factoryName = factoryNames.get(index);
		String numericArg = numericArguments.get(index);
		String[] stringArgs = stringArguments.get(index);
		PolyhedronGen.Factory<? extends PolyhedronGen> genFactory;
		
		if (numericArg.length() > 0) {
			genFactory = com.kreative.polyhedra.gen.BOM.MAP.get(factoryName + numericArg);
			if (genFactory != null) return genFactory.parse(stringArgs);
			
			stringArgs = mergeArgs(numericArg, stringArgs);
			
			genFactory = com.kreative.polyhedra.gen.BOM.MAP.get(factoryName + "#");
			if (genFactory != null) return genFactory.parse(stringArgs);
		}
		
		genFactory = com.kreative.polyhedra.gen.BOM.MAP.get(factoryName);
		if (genFactory != null) return genFactory.parse(stringArgs);
		
		throw new IllegalArgumentException("Unknown generator " + factoryName);
	}
	
	public PolyhedronOp getOperation(int index) {
		String factoryName = factoryNames.get(index);
		String numericArg = numericArguments.get(index);
		String[] stringArgs = stringArguments.get(index);
		PolyhedronOp.Factory<? extends PolyhedronOp> opFactory;
		
		if (numericArg.length() > 0) {
			opFactory = com.kreative.polyhedra.op.BOM.MAP.get(factoryName + numericArg);
			if (opFactory != null) return opFactory.parse(stringArgs);
			
			opFactory = com.kreative.polyhedra.op.BOM.MAP.get(factoryName + "#");
			if (opFactory != null) return opFactory.parse(mergeArgs(numericArg, stringArgs));
		}
		
		opFactory = com.kreative.polyhedra.op.BOM.MAP.get(factoryName);
		if (opFactory != null) {
			if (numericArg.length() > 0) {
				int repeat = PolyhedronUtils.parseInt(numericArg, 0);
				if (repeat > 0 && !hasNFlag(opFactory)) {
					return new Repeater(opFactory.parse(stringArgs), repeat);
				}
				stringArgs = mergeArgs(numericArg, stringArgs);
			}
			return opFactory.parse(stringArgs);
		}
		
		throw new IllegalArgumentException("Unknown operation " + factoryName);
	}
	
	public boolean isEmpty() {
		return count == 0;
	}
	
	public int size() {
		return count;
	}
}