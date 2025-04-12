package com.kreative.polyhedra;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Path3D {
	private Cursor3D currentPoint = new Cursor3D();
	private Integer currentVertex = null;
	private List<Cursor3D> vertices = new ArrayList<Cursor3D>();
	private List<List<Integer>> faces = new ArrayList<List<Integer>>();
	private List<Color> faceColors = new ArrayList<Color>();
	private List<Integer> currentFace = null;
	private Color currentFaceColor = Color.gray;
	
	
	/** Moves the cursor to an existing vertex without affecting any existing vertices or faces. */
	public void moveVertex(int index) {
		if (index < 0) index += vertices.size();
		if (index < 0 || index >= vertices.size()) throw new ArrayIndexOutOfBoundsException(index);
		currentPoint.restore(vertices.get(index));
		currentVertex = index;
	}
	
	/** Moves the cursor to Cartesian coordinate x, y, z without affecting any existing vertices or faces. */
	public void moveXYZ(double x, double y, double z) {
		currentPoint.moveXYZ(x, y, z);
		currentVertex = null;
	}
	
	/** Moves the cursor to cylindrical coordinate r, y, θ without affecting any existing vertices or faces. */
	public void moveRYA(double cr, double y, double azimuth) {
		currentPoint.moveRYA(cr, y, azimuth);
		currentVertex = null;
	}
	
	/** Moves the cursor to spherical coordinate ρ, φ, θ without affecting any existing vertices or faces. */
	public void moveREA(double sr, double elevation, double azimuth) {
		currentPoint.moveREA(sr, elevation, azimuth);
		currentVertex = null;
	}
	
	/** Moves the cursor relative to the current Cartesian coordinate without affecting any existing vertices or faces. */
	public void moveDeltaXYZ(double dx, double dy, double dz) {
		currentPoint.deltaXYZ(dx, dy, dz);
		currentVertex = null;
	}
	
	/** Moves the cursor relative to the current cylindrical coordinate without affecting any existing vertices or faces. */
	public void moveDeltaRYA(double dcr, double dy, double dazimuth) {
		currentPoint.deltaRYA(dcr, dy, dazimuth);
		currentVertex = null;
	}
	
	/** Moves the cursor relative to the current spherical coordinate without affecting any existing vertices or faces. */
	public void moveDeltaREA(double dsr, double delevation, double dazimuth) {
		currentPoint.deltaREA(dsr, delevation, dazimuth);
		currentVertex = null;
	}
	
	
	/** Closes the current face if there is one, then moves the cursor to an existing vertex. */
	public void beginVertex(int index) {
		if (index < 0) index += vertices.size();
		if (index < 0 || index >= vertices.size()) throw new ArrayIndexOutOfBoundsException(index);
		currentPoint.restore(vertices.get(index));
		currentVertex = index;
		currentFace = null;
	}
	
	/** Closes the current face if there is one, moves the cursor to Cartesian coordinate x, y, z, then creates a new vertex. */
	public void beginXYZ(double x, double y, double z) {
		currentPoint.moveXYZ(x, y, z);
		beginNewVertex();
	}
	
	/** Closes the current face if there is one, moves the cursor to cylindrical coordinate r, y, θ, then creates a new vertex. */
	public void beginRYA(double cr, double y, double azimuth) {
		currentPoint.moveRYA(cr, y, azimuth);
		beginNewVertex();
	}
	
	/** Closes the current face if there is one, moves the cursor to spherical coordinate ρ, φ, θ, then creates a new vertex. */
	public void beginREA(double sr, double elevation, double azimuth) {
		currentPoint.moveREA(sr, elevation, azimuth);
		beginNewVertex();
	}
	
	/** Closes the current face if there is one, moves the cursor relative to the current Cartesian coordinate, then creates a new vertex. */
	public void beginDeltaXYZ(double dx, double dy, double dz) {
		currentPoint.deltaXYZ(dx, dy, dz);
		beginNewVertex();
	}
	
	/** Closes the current face if there is one, moves the cursor relative to the current cylindrical coordinate, then creates a new vertex. */
	public void beginDeltaRYA(double dcr, double dy, double dazimuth) {
		currentPoint.deltaRYA(dcr, dy, dazimuth);
		beginNewVertex();
	}
	
	/** Closes the current face if there is one, moves the cursor relative to the current spherical coordinate, then creates a new vertex. */
	public void beginDeltaREA(double dsr, double delevation, double dazimuth) {
		currentPoint.deltaREA(dsr, delevation, dazimuth);
		beginNewVertex();
	}
	
	private void beginNewVertex() {
		currentVertex = vertices.size();
		vertices.add(currentPoint.clone());
		currentFace = null;
	}
	
	
	/**
	 * Moves the cursor to an existing vertex then adds that vertex to the current face.
	 * If there is no current face, a new face is created with an edge connecting the old cursor position to the new cursor position.
	 */
	public void continueVertex(int index) {
		if (index < 0) index += vertices.size();
		if (index < 0 || index >= vertices.size()) throw new ArrayIndexOutOfBoundsException(index);
		continueOrCreateFace();
		currentPoint.restore(vertices.get(index));
		currentVertex = index;
		currentFace.add(index);
	}
	
	/**
	 * Moves the cursor to Cartesian coordinate x, y, z, creates a new vertex, then adds that vertex to the current face.
	 * If there is no current face, a new face is created with an edge connecting the old cursor position to the new cursor position.
	 */
	public void continueXYZ(double x, double y, double z) {
		continueOrCreateFace();
		currentPoint.moveXYZ(x, y, z);
		continueNewVertex();
	}
	
	/**
	 * Moves the cursor to cylindrical coordinate r, y, θ, creates a new vertex, then adds that vertex to the current face.
	 * If there is no current face, a new face is created with an edge connecting the old cursor position to the new cursor position.
	 */
	public void continueRYA(double cr, double y, double azimuth) {
		continueOrCreateFace();
		currentPoint.moveRYA(cr, y, azimuth);
		continueNewVertex();
	}
	
	/**
	 * Moves the cursor to spherical coordinate ρ, φ, θ, creates a new vertex, then adds that vertex to the current face.
	 * If there is no current face, a new face is created with an edge connecting the old cursor position to the new cursor position.
	 */
	public void continueREA(double sr, double elevation, double azimuth) {
		continueOrCreateFace();
		currentPoint.moveREA(sr, elevation, azimuth);
		continueNewVertex();
	}
	
	/**
	 * Moves the cursor relative to the current Cartesian coordinate, creates a new vertex, then adds that vertex to the current face.
	 * If there is no current face, a new face is created with an edge connecting the old cursor position to the new cursor position.
	 */
	public void continueDeltaXYZ(double dx, double dy, double dz) {
		continueOrCreateFace();
		currentPoint.deltaXYZ(dx, dy, dz);
		continueNewVertex();
	}
	
	/**
	 * Moves the cursor relative to the current cylindrical coordinate, creates a new vertex, then adds that vertex to the current face.
	 * If there is no current face, a new face is created with an edge connecting the old cursor position to the new cursor position.
	 */
	public void continueDeltaRYA(double dcr, double dy, double dazimuth) {
		continueOrCreateFace();
		currentPoint.deltaRYA(dcr, dy, dazimuth);
		continueNewVertex();
	}
	
	/**
	 * Moves the cursor relative to the current spherical coordinate, creates a new vertex, then adds that vertex to the current face.
	 * If there is no current face, a new face is created with an edge connecting the old cursor position to the new cursor position.
	 */
	public void continueDeltaREA(double dsr, double delevation, double dazimuth) {
		continueOrCreateFace();
		currentPoint.deltaREA(dsr, delevation, dazimuth);
		continueNewVertex();
	}
	
	private void continueOrCreateFace() {
		if (currentFace == null) {
			if (currentVertex == null) {
				currentVertex = vertices.size();
				vertices.add(currentPoint.clone());
			}
			currentFace = new ArrayList<Integer>();
			currentFace.add(currentVertex);
			faces.add(currentFace);
			faceColors.add(currentFaceColor);
		}
	}
	
	private void continueNewVertex() {
		currentVertex = vertices.size();
		vertices.add(currentPoint.clone());
		currentFace.add(currentVertex);
	}
	
	
	/** Moves the cursor to the start of the current face and closes the current face. If there is no current face, has no effect. */
	public void closeFace() {
		if (currentFace != null) {
			currentVertex = currentFace.get(0);
			currentPoint.restore(vertices.get(currentVertex));
			currentFace = null;
		}
	}
	
	
	/** Removes all vertices and faces. Does not move the cursor or change the color used when creating new faces. */
	public void clear() {
		currentVertex = null;
		vertices.clear();
		faces.clear();
		faceColors.clear();
		currentFace = null;
	}
	
	/** Converts this Path3D object to a Polyhedron object. */
	public Polyhedron createPolyhedron() {
		List<Point3D> points = new ArrayList<Point3D>(vertices.size());
		for (Cursor3D cursor : vertices) points.add(cursor.position());
		return new Polyhedron(points, faces, faceColors);
	}
	
	/** Returns the color used when creating new faces. */
	public Color getNewFaceColor() {
		return currentFaceColor;
	}
	
	/** Returns true if this object contains no vertices or faces. */
	public boolean isEmpty() {
		return vertices.isEmpty() && faces.isEmpty() && faceColors.isEmpty();
	}
	
	/** Sets the color used when creating new faces. */
	public void setNewFaceColor(Color color) {
		this.currentFaceColor = color;
	}
	
	
	// Group 1: Instruction ([A-Za-z])
	// Group 2: Vertex Reference String (#([+-]?[0-9]+))
	// Group 3: Vertex Reference Integer ([+-]?[0-9]+)
	// Group 4: Value String (([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?)([*°]?))
	// Group 5: Value Double ([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?)
	// Group 6: Value Mantissa ([0-9]+([.][0-9]*)?|[.][0-9]+)
	// Group 7: Value Fractional Part After Integer ([.][0-9]*)
	// Group 8: Value Exponent ([Ee][+-]?[0-9]+)
	// Group 9: Value Unit ([*°]?)
	// Group 10: Color Modifier String (\\[([^\\[\\]]+)\\])
	// Group 11: Color Modifier Name ([^\\[\\]]+)
	// We are interested in groups 1, 3, 5, 9, and 11.
	private static final Pattern PATH_COMPONENT = Pattern.compile("([A-Za-z])|(#([+-]?[0-9]+))|(([+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)([Ee][+-]?[0-9]+)?)([*°]?))|(\\[([^\\[\\]]+)\\])");
	
	/**
	 * Interprets a series of SVG-path-like operations. Each operation takes one of the following forms:
	 * <ul>
	 * <li><i>opcode</i> <b>#</b><i>index</i>
	 * <li><i>opcode</i> <i>x</i> <i>y</i> <i>z</i>
	 * <li><i>opcode</i> <i>cr</i> <i>y</i> <i>azimuth</i><b>*</b>
	 * <li><i>opcode</i> <i>sr</i> <i>elevation</i><b>*</b> <i>azimuth</i><b>*</b>
	 * <li><b>Z</b> | <b>z</b>
	 * <li><b>[</b><i>color</i><b>]</b>
	 * </ul>
	 * Where <i>opcode</i> is one of the following:
	 * <ul>
	 * <li><b>M</b> moves the cursor to an absolute position without affecting any existing vertices or faces
	 * <li><b>m</b> moves the cursor to a relative position without affecting any existing vertices or faces
	 * <li><b>V</b> moves the cursor to an absolute position and creates a vertex; if there is a current face, it is closed before the cursor moves
	 * <li><b>v</b> moves the cursor to a relative position and creates a vertex; if there is a current face, it is closed before the cursor moves
	 * <li><b>L</b> moves the cursor to an absolute position and adds a vertex to the current face; if there is no current face, a new one is created before the cursor moves
	 * <li><b>l</b> moves the cursor to a relative position and adds a vertex to the current face; if there is no current face, a new one is created before the cursor moves
	 * <li><b>Z</b> moves the cursor to the start of the current face and closes it; if there is no current face, there is no effect
	 * <li><b>z</b> moves the cursor to the start of the current face and closes it; if there is no current face, there is no effect
	 * </ul>
	 * With the following parameters:
	 * <ul>
	 * <li><i>index</i> is the index number of a vertex, starting at zero; negative indices can be used to count backwards from the last vertex created
	 * <li><i>x</i>, <i>y</i>, and <i>z</i> are coordinates along the X, Y, and Z axes, respectively
	 * <li><i>cr</i> is a distance from the Y axis
	 * <li><i>sr</i> is a distance from the origin
	 * <li><i>elevation</i> is an angle in degrees above the X-Z plane (or below if negative)
	 * <li><i>azimuth</i> is an angle in degrees from the X axis around the X-Z plane
	 * <li><i>color</i> is a CSS color name or an RGB color in the format #FFFFFF or 255,255,255
	 * </ul>
	 */
	public void parse(String s) {
		char instruction = 'M';
		List<Double> angles = new ArrayList<Double>();
		List<Double> lengths = new ArrayList<Double>();
		Matcher m = PATH_COMPONENT.matcher(s);
		while (m.find()) {
			if (m.group(1) != null && m.group(1).length() > 0) {
				instruction = m.group(1).charAt(0);
				switch (instruction) {
					case 'Z': closeFace(); instruction = 'M'; break;
					case 'z': closeFace(); instruction = 'm'; break;
				}
				angles.clear();
				lengths.clear();
			}
			if (m.group(3) != null && m.group(3).length() > 0) {
				int index = PolyhedronUtils.parseInt(m.group(3), 0);
				switch (instruction) {
					case 'M': case 'm': moveVertex(index); break;
					case 'V': case 'v': beginVertex(index); break;
					case 'L': case 'l': continueVertex(index); break;
				}
				angles.clear();
				lengths.clear();
			}
			if (m.group(5) != null && m.group(5).length() > 0) {
				double value = PolyhedronUtils.parseDouble(m.group(5), 0);
				if (m.group(9) != null && m.group(9).length() > 0) {
					angles.add(value);
				} else {
					lengths.add(value);
				}
				if (lengths.size() + angles.size() == 3) {
					if (lengths.size() == 3 && angles.size() == 0) {
						switch (instruction) {
							case 'M': moveXYZ(lengths.get(0), lengths.get(1), lengths.get(2)); break;
							case 'm': moveDeltaXYZ(lengths.get(0), lengths.get(1), lengths.get(2)); break;
							case 'V': beginXYZ(lengths.get(0), lengths.get(1), lengths.get(2)); break;
							case 'v': beginDeltaXYZ(lengths.get(0), lengths.get(1), lengths.get(2)); break;
							case 'L': continueXYZ(lengths.get(0), lengths.get(1), lengths.get(2)); break;
							case 'l': continueDeltaXYZ(lengths.get(0), lengths.get(1), lengths.get(2)); break;
						}
					}
					if (lengths.size() == 2 && angles.size() == 1) {
						switch (instruction) {
							case 'M': moveRYA(lengths.get(0), lengths.get(1), angles.get(0)); break;
							case 'm': moveDeltaRYA(lengths.get(0), lengths.get(1), angles.get(0)); break;
							case 'V': beginRYA(lengths.get(0), lengths.get(1), angles.get(0)); break;
							case 'v': beginDeltaRYA(lengths.get(0), lengths.get(1), angles.get(0)); break;
							case 'L': continueRYA(lengths.get(0), lengths.get(1), angles.get(0)); break;
							case 'l': continueDeltaRYA(lengths.get(0), lengths.get(1), angles.get(0)); break;
						}
					}
					if (lengths.size() == 1 && angles.size() == 2) {
						switch (instruction) {
							case 'M': moveREA(lengths.get(0), angles.get(0), angles.get(1)); break;
							case 'm': moveDeltaREA(lengths.get(0), angles.get(0), angles.get(1)); break;
							case 'V': beginREA(lengths.get(0), angles.get(0), angles.get(1)); break;
							case 'v': beginDeltaREA(lengths.get(0), angles.get(0), angles.get(1)); break;
							case 'L': continueREA(lengths.get(0), angles.get(0), angles.get(1)); break;
							case 'l': continueDeltaREA(lengths.get(0), angles.get(0), angles.get(1)); break;
						}
					}
					angles.clear();
					lengths.clear();
				}
			}
			if (m.group(11) != null && m.group(11).length() > 0) {
				currentFaceColor = PolyhedronUtils.parseColor(m.group(11), currentFaceColor);
			}
		}
	}
}