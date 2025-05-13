package com.kreative.polyhedra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Report extends PolyhedronCon {
	public static void main(String[] args) {
		new Report().processArgs(args);
	}
	
	public void defaultAction() {
		printOptions();
	}
	
	public void consume(String source, Polyhedron p) {
		System.out.println(source);
		Point3D o = Point3D.ZERO;
		Point3D c = p.center();
		System.out.println("\tVertices: " + p.vertices.size());
		TreeMap<Float,Integer> circumradii = new TreeMap<Float,Integer>();
		for (Polyhedron.Vertex v : p.vertices) {
			double m = v.point.distance(o);
			double r = v.point.distance(c);
			System.out.println("\t\t#" + v.index + "\t" + v.point + "\tmag=" + m + "\trad=" + r);
			Float key = (float)r;
			Integer value = circumradii.get(key);
			circumradii.put(key, ((value != null) ? (value + 1) : 1));
		}
		System.out.println("\tEdges: " + p.edges.size());
		TreeMap<Float,Integer> midradii = new TreeMap<Float,Integer>();
		TreeMap<Float,Integer> lengths = new TreeMap<Float,Integer>();
		TreeMap<Float,Integer> angles = new TreeMap<Float,Integer>();
		for (Polyhedron.Edge e : p.edges) {
			Point3D p1 = e.vertex1.point;
			Point3D p2 = e.vertex2.point;
			Point3D m = e.midpoint();
			double l = e.length();
			double d = o.distanceToLine(e.vertex1.point, e.vertex2.point);
			double r = c.distanceToLine(e.vertex1.point, e.vertex2.point);
			System.out.println("\t\t#" + e.vertex1.index + "-#" + e.vertex2.index);
			System.out.println("\t\t\tV0: #" + e.vertex1.index + "\t" + p1 + "\tmag=" + p1.distance(o) + "\trad=" + p1.distance(c));
			System.out.println("\t\t\tV1: #" + e.vertex2.index + "\t" + p2 + "\tmag=" + p2.distance(o) + "\trad=" + p2.distance(c));
			System.out.println("\t\t\tmidpoint\t" + m + "\tmag=" + m.distance(o) + "\trad=" + m.distance(c));
			System.out.println("\t\t\tlength\t" + l);
			System.out.println("\t\t\tdistance to origin\t" + d);
			System.out.println("\t\t\tdistance to center\t" + r);
			List<Polyhedron.Face> faces = p.getFaces(e);
			List<Point3D> normals = new ArrayList<Point3D>();
			for (Polyhedron.Face face : faces) {
				List<Point3D> fv = face.points();
				normals.add(Point3D.average(fv).normal(fv));
			}
			for (int i = 0; i < faces.size(); i++) {
				Point3D ni = normals.get(i);
				for (int j = i + 1; j < faces.size(); j++) {
					Point3D nj = normals.get(j);
					double deg = 180 - ni.angle(nj);
					double rad = Math.PI - ni.angleRad(nj);
					System.out.println("\t\t\tdihedral angle\tdeg=" + deg + "\trad=" + rad);
					Float key = (float)deg;
					Integer value = angles.get(key);
					angles.put(key, ((value != null) ? (value + 1) : 1));
				}
			}
			Float key = (float)l;
			Integer value = lengths.get(key);
			lengths.put(key, ((value != null) ? (value + 1) : 1));
			key = (float)r;
			value = midradii.get(key);
			midradii.put(key, ((value != null) ? (value + 1) : 1));
		}
		System.out.println("\tFaces: " + p.faces.size());
		TreeMap<Float,Integer> inradii = new TreeMap<Float,Integer>();
		HashMap<FaceInfo,Integer> faces = new HashMap<FaceInfo,Integer>();
		for (Polyhedron.Face f: p.faces) {
			FaceInfo info = new FaceInfo(f);
			Integer value = faces.get(info);
			faces.put(info, ((value != null) ? (value + 1) : 1));
			List<Point3D> fv = f.points();
			Point3D center = Point3D.average(fv);
			Point3D normal = center.normal(fv);
			double d = o.distanceToPlane(center, normal);
			double r = c.distanceToPlane(center, normal);
			System.out.println("\t\t#" + f.index + "\t" + info.typeString(1));
			System.out.println("\t\t\tVertices: " + f.vertices.size());
			for (int i = 0; i < f.vertices.size(); i++) {
				Polyhedron.Vertex v = f.vertices.get(i);
				System.out.println(
					"\t\t\t\tV" + i + ": #" + v.index + "\t" + v.point +
					"\tmag=" + v.point.distance(o) + "\trad=" + v.point.distance(c)
				);
			}
			System.out.println("\t\t\tEdges: " + f.edges.size());
			for (int i = 0; i < f.edges.size(); i++) {
				Polyhedron.Edge e = f.edges.get(i);
				System.out.println(
					"\t\t\t\tE" + i + ": #" + e.vertex1.index + "-#" + e.vertex2.index +
					"\tlen=" + e.length() + "\trad=" + c.distanceToLine(e.vertex1.point, e.vertex2.point)
				);
			}
			System.out.println("\t\t\tAngles: " + f.vertices.size());
			for (int i = 0, n = f.vertices.size(); i < n; i++) {
				Polyhedron.Vertex v = f.vertices.get(i);
				Polyhedron.Vertex pv = f.vertices.get((i + n - 1) % n);
				Polyhedron.Vertex nv = f.vertices.get((i + 1) % n);
				System.out.println(
					"\t\t\t\tA" + i + ": #" + pv.index + "-#" + v.index + "-#" + nv.index +
					"\tdeg=" + v.point.angle(pv.point, nv.point) +
					"\trad=" + v.point.angleRad(pv.point, nv.point)
				);
			}
			System.out.println("\t\t\tcenter\t" + center + "\tmag=" + center.distance(o) + "\trad=" + center.distance(c));
			System.out.println("\t\t\tnormal\t" + normal);
			System.out.println("\t\t\tdistance to origin\t" + d);
			System.out.println("\t\t\tdistance to center\t" + r);
			System.out.println("\t\t\tcolor\t" + f.color.getRed() + "," + f.color.getGreen() + "," + f.color.getBlue());
			Float key = (float)r;
			value = inradii.get(key);
			inradii.put(key, ((value != null) ? (value + 1) : 1));
		}
		System.out.println("\tCircumradii: " + circumradii.size());
		for (Map.Entry<Float,Integer> e : circumradii.entrySet()) {
			System.out.println("\t\t" + e.getValue() + "×" + e.getKey());
		}
		System.out.println("\tMidradii: " + midradii.size());
		for (Map.Entry<Float,Integer> e : midradii.entrySet()) {
			System.out.println("\t\t" + e.getValue() + "×" + e.getKey());
		}
		System.out.println("\tInradii: " + inradii.size());
		for (Map.Entry<Float,Integer> e : inradii.entrySet()) {
			System.out.println("\t\t" + e.getValue() + "×" + e.getKey());
		}
		System.out.println("\tEdge Lengths: " + lengths.size());
		for (Map.Entry<Float,Integer> e : lengths.entrySet()) {
			System.out.println("\t\t" + e.getValue() + "×" + e.getKey());
		}
		System.out.println("\tDihedral Angles: " + angles.size());
		for (Map.Entry<Float,Integer> e : angles.entrySet()) {
			System.out.println("\t\t" + e.getValue() + "×" + e.getKey() + "°");
		}
		System.out.println("\tFace Types: " + faces.size());
		for (Map.Entry<FaceInfo,Integer> e : faces.entrySet()) {
			System.out.println("\t\t" + e.getValue() + " " + e.getKey().typeString(e.getValue()) + ":");
			e.getKey().print("\t\t\t");
		}
		System.out.println("\tMetrics About Origin:");
		for (Metric metric : Metric.values()) {
			System.out.print("\t\t" + metric);
			for (MetricAggregator agg : MetricAggregator.values()) {
				System.out.print("\t" + agg + "=" + agg.aggregate(metric.iterator(p, o)));
			}
			System.out.println();
		}
		System.out.println("\tMetrics About Center:");
		for (Metric metric : Metric.values()) {
			System.out.print("\t\t" + metric);
			for (MetricAggregator agg : MetricAggregator.values()) {
				System.out.print("\t" + agg + "=" + agg.aggregate(metric.iterator(p, c)));
			}
			System.out.println();
		}
	}
	
	public void reportError(String message, Exception e) {
		System.err.println(message);
	}
	
	private static class FaceInfo {
		private final ArrayList<Float> edgeOrder = new ArrayList<Float>();
		private final ArrayList<Float> angleOrder = new ArrayList<Float>();
		private final TreeMap<Float,Integer> edges = new TreeMap<Float,Integer>();
		private final TreeMap<Float,Integer> angles = new TreeMap<Float,Integer>();
		public FaceInfo(Polyhedron.Face face) {
			for (Polyhedron.Edge edge : face.edges) {
				Float key = (float)edge.length();
				edgeOrder.add(key);
				Integer value = edges.get(key);
				edges.put(key, ((value != null) ? (value + 1) : 1));
			}
			for (int i = 0, n = face.vertices.size(); i < n; i++) {
				Point3D vp = face.vertices.get(i).point;
				Point3D np = face.vertices.get((i + 1) % n).point;
				Point3D pp = face.vertices.get((i + n - 1) % n).point;
				Float key = (float)vp.angle(pp, np);
				angleOrder.add(key);
				Integer value = angles.get(key);
				angles.put(key, ((value != null) ? (value + 1) : 1));
			}
		}
		public final boolean equals(Object o) {
			return (
				(o instanceof FaceInfo)
				&& this.edges.equals(((FaceInfo)o).edges)
				&& this.angles.equals(((FaceInfo)o).angles)
				&& ringEquals(this.edgeOrder, ((FaceInfo)o).edgeOrder)
				&& ringEquals(this.angleOrder, ((FaceInfo)o).angleOrder)
			);
		}
		private static boolean ringEquals(List<?> a, List<?> b) {
			int n = a.size();
			if (b.size() == n) {
				for (int o = 0; o < n; o++) {
					boolean e1 = true;
					boolean e2 = true;
					for (int i = 0; i < n && (e1 || e2); i++) {
						Object c = a.get(i);
						Object d1 = b.get((o + i) % n);
						Object d2 = b.get((o + n - i) % n);
						if (!simpleEquals(c, d1)) e1 = false;
						if (!simpleEquals(c, d2)) e2 = false;
					}
					if (e1 || e2) return true;
				}
			}
			return false;
		}
		private static boolean simpleEquals(Object a, Object b) {
			return (a == null) ? (b == null) : (b == null) ? (a == null) : (a == b || a.equals(b));
		}
		public final int hashCode() {
			return edges.hashCode() + angles.hashCode();
		}
		public final void print(String prefix) {
			System.out.print(prefix + "Edges:");
			for (Map.Entry<Float,Integer> e : edges.entrySet()) {
				System.out.print(" " + e.getValue() + "×" + e.getKey());
			}
			System.out.println();
			System.out.print(prefix + "Angles:");
			for (Map.Entry<Float,Integer> e : angles.entrySet()) {
				System.out.print(" " + e.getValue() + "×" + e.getKey() + "°");
			}
			System.out.println();
		}
		private final String typeBasicAdjective() {
			if (angles.size() == 1 && edges.size() == 1) return "Regular";
			if (angles.size() == 1) return "Equiangular";
			if (edges.size() == 1) return "Equilateral";
			return "Irregular";
		}
		private final String typeAngleAdjective() {
			for (float a : angles.keySet()) if (a == 90) return "Right";
			for (float a : angles.keySet()) if (a > 90) return "Obtuse";
			return "Acute";
		}
		private final String typeBasicNoun(int count) {
			int n = edgeOrder.size();
			switch (n) {
				case 1: return (count == 1) ? "Monogon" : "Monogons";
				case 2: return (count == 1) ? "Digon" : "Digons";
				case 3: return (count == 1) ? "Triangle" : "Triangles";
				case 4: return (count == 1) ? "Quadrilateral" : "Quadrilaterals";
				case 5: return (count == 1) ? "Pentagon" : "Pentagons";
				case 6: return (count == 1) ? "Hexagon" : "Hexagons";
				case 7: return (count == 1) ? "Heptagon" : "Heptagons";
				case 8: return (count == 1) ? "Octagon" : "Octagons";
				case 9: return (count == 1) ? "Nonagon" : "Nonagons";
				case 10: return (count == 1) ? "Decagon" : "Decagons";
				case 11: return (count == 1) ? "Hendecagon" : "Hendecagons";
				case 12: return (count == 1) ? "Dodecagon" : "Dodecagons";
				default: return (count == 1) ? (n + "-gon") : (n + "-gons");
			}
		}
		public final String typeString(int count) {
			switch (edgeOrder.size()) {
				case 1: return typeBasicNoun(count);
				case 2: return typeBasicNoun(count);
				case 3:
					switch (edges.size()) {
						case 1: return "Equilateral " + typeBasicNoun(count);
						case 2: return "Isosceles " + typeAngleAdjective() + " " + typeBasicNoun(count);
						case 3: return "Scalene " + typeAngleAdjective() + " " + typeBasicNoun(count);
						default: throw new IllegalStateException();
					}
				case 4:
					if (angles.size() == 1 && edges.size() == 1) return (count == 1) ? "Square" : "Squares";
					if (angles.size() == 1) return (count == 1) ? "Rectangle" : "Rectangles";
					if (edges.size() == 1) return (count == 1) ? "Rhombus" : "Rhombi";
					float A = edgeOrder.get(0);
					float B = edgeOrder.get(1);
					float C = edgeOrder.get(2);
					float D = edgeOrder.get(3);
					if ((A == B && C == D) || (A == D && B == C)) return (count == 1) ? "Kite" : "Kites";
					if (A == C && B == D) return (count == 1) ? "Parallelogram" : "Parallelograms";
					float a = angleOrder.get(0);
					float b = angleOrder.get(1);
					float c = angleOrder.get(2);
					float d = angleOrder.get(3);
					if ((a+b == 180 && c+d == 180) || (a+d == 180 && b+c == 180)) {
						boolean i = ((a == b && c == d) || (a == d && b == c));
						String ad = (i ? "Isosceles" : typeAngleAdjective());
						return ad + " " + ((count == 1) ? "Trapezoid" : "Trapezoids");
					}
					return "Irregular " + typeBasicNoun(count);
				default:
					return typeBasicAdjective() + " " + typeBasicNoun(count);
			}
		}
	}
}