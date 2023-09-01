package com.kreative.polyhedra;

import java.awt.Color;
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
}