package com.kreative.polyhedra;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class PolyhedronUtils {
	public static int parseInt(String s, int def) {
		if (s == null) return def;
		try { return Integer.parseInt(s.trim()); }
		catch (NumberFormatException e) { return def; }
	}
	
	public static double parseDouble(String s, double def) {
		if (s == null) return def;
		try { return Double.parseDouble(s.trim()); }
		catch (NumberFormatException e) { return def; }
	}
	
	private static final Map<String,Color> namedColors = new NamedColors();
	private static final Pattern colorHexPattern = Pattern.compile("^(#|0[Xx])([0-9A-Fa-f]+)$");
	private static final Pattern colorDecPattern = Pattern.compile("^([0-9]+)(\\s|[.,;:])+([0-9]+)(\\s|[.,;:])+([0-9]+)$");
	
	public static Color parseColor(String s, Color def) {
		if (s == null || (s = s.trim()).length() == 0) return def;
		Color namedColor = namedColors.get(s.toLowerCase());
		if (namedColor != null) return namedColor;
		
		Matcher m = colorHexPattern.matcher(s);
		if (m.matches()) {
			String h = m.group(2);
			switch (h.length()) {
				case 1: return new Color(Integer.parseInt(h, 16) * 0x111111);
				case 2: return new Color(Integer.parseInt(h, 16) * 0x010101);
				case 3: return new Color(
					Integer.parseInt(h.substring(0, 1), 16) * 0x110000 +
					Integer.parseInt(h.substring(1, 2), 16) * 0x001100 +
					Integer.parseInt(h.substring(2, 3), 16) * 0x000011
				);
				case 6: return new Color(
					Integer.parseInt(h.substring(0, 2), 16) * 0x010000 +
					Integer.parseInt(h.substring(2, 4), 16) * 0x000100 +
					Integer.parseInt(h.substring(4, 6), 16) * 0x000001
				);
			}
		}
		
		m = colorDecPattern.matcher(s);
		if (m.matches()) {
			return new Color(
				Integer.parseInt(m.group(1)),
				Integer.parseInt(m.group(3)),
				Integer.parseInt(m.group(5))
			);
		}
		
		return def;
	}
	
	public static String[] parseArgs(String s) {
		List<String> parsedArgs = new ArrayList<String>();
		char[] chars = s.toCharArray(); int ci = 0, cn = chars.length;
		while (ci < cn && Character.isWhitespace(chars[ci])) ci++;
		while (ci < cn) {
			StringBuffer arg = new StringBuffer();
			while (ci < cn && !Character.isWhitespace(chars[ci])) {
				char ch = chars[ci++];
				if (ci >= cn) {
					arg.append(ch);
				} else if (ch == '\"') {
					while (ci < cn) {
						ch = chars[ci++];
						if (ch == '\"') {
							break;
						} else if (ch == '\\' && ci < cn) {
							ci = parseEscapeSequence(chars, ci, cn, arg);
						} else {
							arg.append(ch);
						}
					}
				} else if (ch == '\'') {
					while (ci < cn) {
						ch = chars[ci++];
						if (ch == '\'') {
							break;
						} else if (ch == '\\' && ci < cn) {
							ci = parseEscapeSequence(chars, ci, cn, arg);
						} else {
							arg.append(ch);
						}
					}
				} else if (ch == '\\') {
					ci = parseEscapeSequence(chars, ci, cn, arg);
				} else {
					arg.append(ch);
				}
			}
			parsedArgs.add(arg.toString());
			while (ci < cn && Character.isWhitespace(chars[ci])) ci++;
		}
		return parsedArgs.toArray(new String[parsedArgs.size()]);
	}
	
	private static int parseEscapeSequence(char[] chars, int ci, int cn, StringBuffer arg) {
		char ch = chars[ci++];
		switch (ch) {
			case '@': arg.append((char)0x00); return ci;
			case 'a': arg.append((char)0x07); return ci;
			case 'b': arg.append((char)0x08); return ci;
			case 't': arg.append((char)0x09); return ci;
			case 'n': arg.append((char)0x0A); return ci;
			case 'v': arg.append((char)0x0B); return ci;
			case 'f': arg.append((char)0x0C); return ci;
			case 'r': arg.append((char)0x0D); return ci;
			case 'o': arg.append((char)0x0E); return ci;
			case 'i': arg.append((char)0x0F); return ci;
			case 'z': arg.append((char)0x1A); return ci;
			case 'e': arg.append((char)0x1B); return ci;
			case ' ': arg.append((char)0x20); return ci;
			case '\"': arg.append((char)0x22); return ci;
			case '\'': arg.append((char)0x27); return ci;
			case '\\': arg.append((char)0x5C); return ci;
			case 'd': arg.append((char)0x7F); return ci;
			case 'c':
				if (ci < cn && chars[ci] >= 0x40 && chars[ci] <= 0x5F) {
					arg.append((char)(chars[ci] & 0x1F));
					return ci + 1;
				}
				arg.append("\\c");
				return ci;
			case 'x':
				if (ci + 2 <= cn) {
					int v = (
						(Character.digit(chars[ci+0], 16) << 4) |
						(Character.digit(chars[ci+1], 16) << 0)
					);
					if (Character.isValidCodePoint(v)) {
						arg.append(Character.toChars(v));
						return ci + 2;
					}
				}
				arg.append("\\x");
				return ci;
			case 'u':
				if (ci + 4 <= cn) {
					int v = (
						(Character.digit(chars[ci+0], 16) << 12) |
						(Character.digit(chars[ci+1], 16) <<  8) |
						(Character.digit(chars[ci+2], 16) <<  4) |
						(Character.digit(chars[ci+3], 16) <<  0)
					);
					if (Character.isValidCodePoint(v)) {
						arg.append(Character.toChars(v));
						return ci + 4;
					}
				}
				arg.append("\\u");
				return ci;
			case 'w':
				if (ci + 6 <= cn) {
					int v = (
						(Character.digit(chars[ci+0], 16) << 20) |
						(Character.digit(chars[ci+1], 16) << 16) |
						(Character.digit(chars[ci+2], 16) << 12) |
						(Character.digit(chars[ci+3], 16) <<  8) |
						(Character.digit(chars[ci+4], 16) <<  4) |
						(Character.digit(chars[ci+5], 16) <<  0)
					);
					if (Character.isValidCodePoint(v)) {
						arg.append(Character.toChars(v));
						return ci + 6;
					}
				}
				arg.append("\\w");
				return ci;
			case 'U':
				if (ci + 8 <= cn) {
					int v = (
						(Character.digit(chars[ci+0], 16) << 28) |
						(Character.digit(chars[ci+1], 16) << 24) |
						(Character.digit(chars[ci+2], 16) << 20) |
						(Character.digit(chars[ci+3], 16) << 16) |
						(Character.digit(chars[ci+4], 16) << 12) |
						(Character.digit(chars[ci+5], 16) <<  8) |
						(Character.digit(chars[ci+6], 16) <<  4) |
						(Character.digit(chars[ci+7], 16) <<  0)
					);
					if (Character.isValidCodePoint(v)) {
						arg.append(Character.toChars(v));
						return ci + 8;
					}
				}
				arg.append("\\U");
				return ci;
			default:
				arg.append('\\');
				arg.append(ch);
				return ci;
		}
	}
	
	public static PolyhedronOp parseOp(String s) {
		String[] args = parseArgs(s);
		if (args.length == 0) return null;
		String opName = args[0].toLowerCase();
		Class<? extends PolyhedronOp> opClass = com.kreative.polyhedra.op.BOM.MAP.get(opName);
		if (opClass == null) {
			System.err.println("Unknown operation " + args[0]);
			return null;
		}
		try {
			Method parse = opClass.getMethod("parse", String[].class);
			List<String> cargl = Arrays.asList(args).subList(1, args.length);
			String[] cargs = cargl.toArray(new String[args.length - 1]);
			Object op = parse.invoke(null, (Object)cargs);
			return (PolyhedronOp)op;
		} catch (Exception e) {
			System.err.println("Error invoking operation " + args[0] + ": " + e);
			return null;
		}
	}
	
	public static PolyhedronGen parseGen(String s) {
		String[] args = parseArgs(s);
		if (args.length == 0) return null;
		String genName = args[0].toLowerCase();
		Class<? extends PolyhedronGen> genClass = com.kreative.polyhedra.gen.BOM.MAP.get(genName);
		if (genClass == null) {
			System.err.println("Unknown generator " + args[0]);
			return null;
		}
		try {
			Method parse = genClass.getMethod("parse", String[].class);
			List<String> cargl = Arrays.asList(args).subList(1, args.length);
			String[] cargs = cargl.toArray(new String[args.length - 1]);
			Object gen = parse.invoke(null, (Object)cargs);
			return (PolyhedronGen)gen;
		} catch (Exception e) {
			System.err.println("Error invoking generator " + args[0] + ": " + e);
			return null;
		}
	}
}