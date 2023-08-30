package com.kreative.polyhedra.viewer;

import javax.media.j3d.Shape3D;
import com.kreative.polyhedra.Polyhedron;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;

public class Convert {
	public static Shape3D faces(Polyhedron p) {
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
		return new Shape3D(gi.getGeometryArray());
	}
}