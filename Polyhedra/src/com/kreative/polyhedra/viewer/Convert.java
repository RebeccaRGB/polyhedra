package com.kreative.polyhedra.viewer;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;
import com.kreative.polyhedra.AffineTransform3D;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Stripifier;

public class Convert {
	public static BranchGroup vertices(Polyhedron p, float r, Appearance a) {
		if (p == null) return null;
		BranchGroup g = new BranchGroup();
		g.setCapability(BranchGroup.ALLOW_DETACH);
		for (Polyhedron.Vertex v : p.vertices) {
			Point3D pt = v.point;
			Vector3d vec = new Vector3d(pt.getX(), pt.getY(), pt.getZ());
			Transform3D tx = new Transform3D(); tx.setTranslation(vec);
			TransformGroup tg = new TransformGroup(tx);
			tg.addChild(new Sphere(r, a));
			g.addChild(tg);
		}
		return g;
	}
	
	public static BranchGroup edges(Polyhedron p, float r, Appearance a) {
		if (p == null) return null;
		BranchGroup g = new BranchGroup();
		g.setCapability(BranchGroup.ALLOW_DETACH);
		Vector3d y = new Vector3d(0, 1, 0);
		for (Polyhedron.Edge e : p.edges) {
			float h = (float)e.length();
			// Java3D blows up at zero-length edges,
			// so don't even bother to generate them.
			if (h == 0) continue;
			Point3D m = e.midpoint();
			Vector3d t = new Vector3d(m.getX(), m.getY(), m.getZ());
			Point3D d = e.vertex2.point.subtract(e.vertex1.point);
			Vector3d v = new Vector3d(d.getX(), d.getY(), d.getZ()); v.normalize();
			Vector3d x = new Vector3d(); x.cross(y, v);
			AxisAngle4d aa = new AxisAngle4d(); aa.set(x, Math.acos(y.dot(v)));
			Transform3D ta = new Transform3D(); ta.set(aa);
			Transform3D tx = new Transform3D(); tx.setTranslation(t); tx.mul(ta);
			TransformGroup tg = new TransformGroup(tx);
			tg.addChild(new Cylinder(r, h, a));
			g.addChild(tg);
		}
		return g;
	}
	
	public static BranchGroup faces(Polyhedron p, Appearance a) {
		if (p == null) return null;
		BranchGroup g = new BranchGroup();
		g.setCapability(BranchGroup.ALLOW_DETACH);
		g.addChild(createShape3D(p, a));
		return g;
	}
	
	private static Shape3D createShape3D(Polyhedron p, Appearance a) {
		if (p == null) return null;
		
		List<Point3D> vertices = p.points();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		for (Polyhedron.Face f : p.faces) triangulate(f, vertices, faces, faceColors);
		if (vertices.isEmpty()) return null;
		if (faces.isEmpty()) return null;
		if (faceColors.isEmpty()) return null;
		
		int vertexCount = vertices.size();
		double[] coords = new double[vertexCount * 3];
		{
			int i = 0;
			for (Point3D v : vertices) {
				coords[i++] = v.getX();
				coords[i++] = v.getY();
				coords[i++] = v.getZ();
			}
		}
		
		int indexCount = 0;
		for (List<Integer> face : faces) indexCount += face.size();
		int faceCount = faces.size();
		int colorCount = faceColors.size();
		int[] coordIndices = new int[indexCount];
		int[] colorIndices = new int[indexCount];
		int[] stripCounts = new int[faceCount];
		int[] contourCounts = new int[faceCount];
		float[] colors = new float[colorCount * 3];
		{
			int vi = 0, fi = 0, ci = 0;
			for (List<Integer> face : faces) {
				for (int index : face) {
					coordIndices[vi] = index;
					colorIndices[vi] = fi;
					vi++;
				}
				stripCounts[fi] = face.size();
				contourCounts[fi] = 1;
				fi++;
			}
			for (Color color : faceColors) {
				colors[ci++] = color.getRed() / 255f;
				colors[ci++] = color.getGreen() / 255f;
				colors[ci++] = color.getBlue() / 255f;
			}
		}
		
		GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
		gi.setCoordinates(coords);
		gi.setColors3(colors);
		gi.setCoordinateIndices(coordIndices);
		gi.setColorIndices(colorIndices);
		gi.setStripCounts(stripCounts);
		gi.setContourCounts(contourCounts);
		new NormalGenerator().generateNormals(gi);
		new Stripifier().stripify(gi);
		return new Shape3D(gi.getGeometryArray(), a);
	}
	
	private static void triangulate(
		Polyhedron.Face face,
		List<Point3D> vertices,
		List<List<Integer>> faces,
		List<Color> faceColors
	) {
		// Java3D blows up at polygons of fewer than 3 vertices,
		// so don't even bother to generate them.
		if (face.vertices.size() < 3) return;
		
		List<Point3D> facePoints = face.points();
		Point3D center = Point3D.average(facePoints);
		Point3D normal = center.normal(facePoints);
		double azimuth = Math.atan2(normal.getX(), normal.getZ());
		double radius = Math.hypot(normal.getX(), normal.getZ());
		double elevation = Math.atan2(normal.getY(), radius);
		AffineTransform3D azimuthPos = AffineTransform3D.getRotateYInstance(azimuth);
		AffineTransform3D azimuthNeg = AffineTransform3D.getRotateYInstance(-azimuth);
		AffineTransform3D elevationPos = AffineTransform3D.getRotateXInstance(elevation);
		AffineTransform3D elevationNeg = AffineTransform3D.getRotateXInstance(-elevation);
		
		// Transform the face to lie on the XY axis and turn it into a 2D path!
		HashMap<Integer,Point2D.Double> transformedVertices = new HashMap<Integer,Point2D.Double>();
		HashMap<Point2D.Float,Integer> vertexIndices = new HashMap<Point2D.Float,Integer>();
		GeneralPath path = null;
		for (Polyhedron.Vertex v : face.vertices) {
			Point3D p = v.point.subtract(center);
			p = azimuthNeg.transform(p);
			p = elevationPos.transform(p);
			transformedVertices.put(v.index, new Point2D.Double(p.getX(), p.getY()));
			vertexIndices.put(new Point2D.Float((float)p.getX(), (float)p.getY()), v.index);
			if (path == null) {
				path = new GeneralPath(GeneralPath.WIND_NON_ZERO);
				path.moveTo(p.getX(), p.getY());
			} else {
				path.lineTo(p.getX(), p.getY());
			}
		}
		path.closePath();
		
		// Convert the GeneralPath to an Area to break it into non-intersecting polygons!
		Area area = new Area(path);
		
		// Transform the polygons from the Area back into 3D space!
		List<Integer> currentFace = null;
		PathIterator pi = area.getPathIterator(null);
		double[] coords = new double[6];
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
				case PathIterator.SEG_MOVETO:
					currentFace = new ArrayList<Integer>();
					// fallthrough;
				case PathIterator.SEG_LINETO:
					Point2D.Float p = new Point2D.Float((float)coords[0], (float)coords[1]);
					Integer i = vertexIndices.get(p);
					if (i == null) {
						i = vertices.size();
						vertexIndices.put(p, i);
						transformedVertices.put(i, new Point2D.Double(coords[0], coords[1]));
						Point3D q = new Point3D(coords[0], coords[1], 0);
						q = elevationNeg.transform(q);
						q = azimuthPos.transform(q);
						vertices.add(q.add(center));
					}
					currentFace.add(i);
					break;
				case PathIterator.SEG_CLOSE:
					Collections.reverse(currentFace);
					removeConsecutiveDuplicates(currentFace);
					// while there are more than three vertices:
					// find a triangle that lies completely inside the polygon
					// add the triangle to the list of faces
					// remove the triangle from the polygon
					// if no triangle is found, don't look again
					boolean modified = true;
					while (modified && currentFace.size() > 3) {
						modified = false;
						int j = 0, n = currentFace.size();
						while (j < n) {
							int cidx = currentFace.get(j);
							int nidx = currentFace.get((j + 1) % n);
							int pidx = currentFace.get((j + n - 1) % n);
							if (containsLine(area, transformedVertices, currentFace, nidx, pidx)) {
								faces.add(Arrays.asList(cidx, nidx, pidx));
								faceColors.add(face.color);
								currentFace.remove(j);
								modified = true;
								n--;
							} else {
								j++;
							}
						}
					}
					// add leftover triangle
					if (currentFace.size() >= 3) {
						faces.add(currentFace);
						faceColors.add(face.color);
					}
					currentFace = null;
					break;
				default:
					throw new IllegalStateException();
			}
			pi.next();
		}
	}
	
	private static void removeConsecutiveDuplicates(List<?> list) {
		int i = 0, n = list.size();
		while (i < n) {
			if (list.get(i).equals(list.get((i + 1) % n))) {
				list.remove(i);
				n--;
			} else {
				i++;
			}
		}
	}
	
	private static boolean containsLine(Area a, HashMap<Integer,Point2D.Double> v, List<Integer> f, int i0, int i1) {
		// return true if polygon completely contains a line segment
		Point2D p0 = v.get(i0);
		Point2D p1 = v.get(i1);
		// area must contain a point on the line segment (here the midpoint)
		if (!a.contains((p0.getX() + p1.getX()) / 2, (p0.getY() + p1.getY()) / 2)) return false;
		// line must not intersect any edge of the polygon
		for (int i = 0, n = f.size(); i < n; i++) {
			Point2D p2 = v.get(f.get(i));
			Point2D p3 = v.get(f.get((i + 1) % n));
			if (linesIntersect(p0, p1, p2, p3)) return false;
		}
		return true;
	}
	
	private static boolean linesIntersect(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
		// return true if two line segments intersect
		double s1x = p1.getX() - p0.getX();
		double s1y = p1.getY() - p0.getY();
		double s2x = p3.getX() - p2.getX();
		double s2y = p3.getY() - p2.getY();
		double det = s1x * s2y - s2x * s1y;
		if (det == 0) return false; // lines are parallel or colinear
		double t1 = ((p0.getY() - p2.getY()) * s1x - (p0.getX() - p2.getX()) * s1y) / det;
		double t2 = ((p0.getY() - p2.getY()) * s2x - (p0.getX() - p2.getX()) * s2y) / det;
		return (t1 > 0 && t1 < 1 && t2 > 0 && t2 < 1);
	}
}