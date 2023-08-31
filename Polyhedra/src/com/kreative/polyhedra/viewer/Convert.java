package com.kreative.polyhedra.viewer;

import javax.media.j3d.Appearance;
import javax.media.j3d.Group;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Vector3d;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.geometry.Stripifier;

public class Convert {
	public static Group vertices(Polyhedron p, float r, Appearance a) {
		Group g = new Group();
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
	
	public static Group edges(Polyhedron p, float r, Appearance a) {
		Group g = new Group();
		Vector3d y = new Vector3d(0, 1, 0);
		for (Polyhedron.Edge e : p.edges) {
			float h = (float)e.vertex1.point.distance(e.vertex2.point);
			Point3D m = e.vertex1.point.midpoint(e.vertex2.point);
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
	
	public static Shape3D faces(Polyhedron p, Appearance a) {
		int vertexCount = p.vertices.size();
		double[] coords = new double[vertexCount * 3];
		{
			int i = 0;
			for (Polyhedron.Vertex v : p.vertices) {
				coords[i++] = v.point.getX();
				coords[i++] = v.point.getY();
				coords[i++] = v.point.getZ();
			}
		}
		
		int indexCount = 0;
		int faceCount = p.faces.size();
		int[] stripCounts = new int[faceCount];
		int[] contourCounts = new int[faceCount];
		float[] colors = new float[faceCount * 3];
		{
			int fi = 0;
			int ci = 0;
			for (Polyhedron.Face f : p.faces) {
				indexCount += f.vertices.size();
				stripCounts[fi] = f.vertices.size();
				contourCounts[fi] = 1;
				colors[ci++] = f.color.getRed() / 255f;
				colors[ci++] = f.color.getGreen() / 255f;
				colors[ci++] = f.color.getBlue() / 255f;
				fi++;
			}
		}
		
		int[] coordIndices = new int[indexCount];
		int[] colorIndices = new int[indexCount];
		{
			int i = 0;
			for (Polyhedron.Face f : p.faces) {
				for (Polyhedron.Vertex v : f.vertices) {
					coordIndices[i] = v.index;
					colorIndices[i] = f.index;
					i++;
				}
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
}