package com.kreative.polyhedra;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class McCooeyReader {
	private static final String NUMBER = "([+-]?(?:[0-9]+(?:[.][0-9]*)?|[.][0-9]+)(?:[Ee][+-]?[0-9]+)?)";
	private static final String COORDINATE = "(" + NUMBER + "|([+-]?\\w+))";
	private static final Pattern COORDINATE_LINE = Pattern.compile("(\\w+)\\s*=\\s*" + NUMBER);
	private static final Pattern VERTEX_LINE = Pattern.compile("(\\w+)\\s*=\\s*\\(\\s*" + COORDINATE + "\\s*,\\s*" + COORDINATE + "\\s*,\\s*" + COORDINATE + "\\s*\\)");
	private static final Pattern FACE_LINE = Pattern.compile("\\{\\s*(\\d+(?:\\s*,\\s*\\d+)*)\\s*\\}");
	
	private final Map<String,Double> coordinates;
	private final List<String> vertexNames;
	private final List<Point3D> vertexPoints;
	private final List<List<Integer>> faces;
	private final List<Color> faceColors;
	private final Scanner scanner;
	
	public McCooeyReader(Scanner scanner) {
		this.coordinates = new HashMap<String,Double>();
		this.vertexNames = new ArrayList<String>();
		this.vertexPoints = new ArrayList<Point3D>();
		this.faces = new ArrayList<List<Integer>>();
		this.faceColors = new ArrayList<Color>();
		this.scanner = scanner;
	}
	
	public McCooeyReader(InputStream in) {
		this(new Scanner(in, "UTF-8"));
	}
	
	public Polyhedron readPolyhedron(Color color) {
		nextLine: while (scanner.hasNextLine()) {
			String line = scanner.nextLine().trim();
			Matcher m = COORDINATE_LINE.matcher(line);
			if (m.matches()) {
				Double v = PolyhedronUtils.parseDouble(m.group(2));
				if (v != null) coordinates.put(m.group(1), v);
				continue nextLine;
			}
			m = VERTEX_LINE.matcher(line);
			if (m.matches()) {
				Double x = null, y = null, z = null;
				if (m.group(3) != null && m.group(3).length() > 0) {
					x = PolyhedronUtils.parseDouble(m.group(3));
				} else if (m.group(4) != null && m.group(4).length() > 0) {
					String xk = m.group(4);
					boolean xn = xk.startsWith("-");
					if (xn || xk.startsWith("+")) xk = xk.substring(1);
					x = coordinates.get(xk);
					if (xn && x != null) x = -x;
				}
				if (m.group(6) != null && m.group(6).length() > 0) {
					y = PolyhedronUtils.parseDouble(m.group(6));
				} else if (m.group(7) != null && m.group(7).length() > 0) {
					String yk = m.group(7);
					boolean yn = yk.startsWith("-");
					if (yn || yk.startsWith("+")) yk = yk.substring(1);
					y = coordinates.get(yk);
					if (yn && y != null) y = -y;
				}
				if (m.group(9) != null && m.group(9).length() > 0) {
					z = PolyhedronUtils.parseDouble(m.group(9));
				} else if (m.group(10) != null && m.group(10).length() > 0) {
					String zk = m.group(10);
					boolean zn = zk.startsWith("-");
					if (zn || zk.startsWith("+")) zk = zk.substring(1);
					z = coordinates.get(zk);
					if (zn && z != null) z = -z;
				}
				if (x != null && y != null && z != null) {
					Point3D p = new Point3D(x, y, z);
					String k = m.group(1);
					int i = vertexNames.indexOf(k);
					if (i < 0) {
						vertexNames.add(k);
						vertexPoints.add(p);
					} else {
						vertexPoints.set(i, p);
					}
				}
				continue nextLine;
			}
			m = FACE_LINE.matcher(line);
			if (m.matches()) {
				List<Integer> vl = new ArrayList<Integer>();
				for (String vs : m.group(1).split(",")) {
					int vi = vertexNames.indexOf("V" + vs.trim());
					if (vi < 0) continue nextLine;
					vl.add(vi);
				}
				faces.add(vl);
				faceColors.add(color);
				continue nextLine;
			}
		}
		return new Polyhedron(vertexPoints, faces, faceColors);
	}
}