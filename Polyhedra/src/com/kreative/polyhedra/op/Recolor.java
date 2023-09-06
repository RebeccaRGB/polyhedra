package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.Polyhedron.Face;
import com.kreative.polyhedra.Polyhedron.Vertex;
import com.kreative.polyhedra.PolyhedronOp;

public class Recolor extends PolyhedronOp {
	public static enum Classifier {
		ALL {
			public boolean matches(Face f, double e) {
				return true;
			}
		},
		EQUIANGULAR {
			public boolean matches(Face f, double e) {
				int n = f.vertices.size();
				if (n < 3) return true;
				double angle = f.vertices.get(0).point.angle(
					f.vertices.get(n - 1).point,
					f.vertices.get(1).point
				);
				for (int i = 1; i < n; i++) {
					double a = f.vertices.get(i).point.angle(
						f.vertices.get(i - 1).point,
						f.vertices.get((i + 1) % n).point
					);
					if (Math.abs(angle - a) > e) return false;
				}
				return true;
			}
		},
		EQUILATERAL {
			public boolean matches(Face f, double e) {
				int n = f.vertices.size();
				if (n < 3) return true;
				double length = f.vertices.get(0).point.distance(f.vertices.get(n - 1).point);
				for (int i = 1; i < n; i++) {
					double l = f.vertices.get(i).point.distance(f.vertices.get(i - 1).point);
					if (Math.abs(length - l) > e) return false;
				}
				return true;
			}
		},
		REGULAR {
			public boolean matches(Face f, double e) {
				return EQUIANGULAR.matches(f, e) && EQUILATERAL.matches(f, e);
			}
		},
		MONOGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 1;
			}
		},
		DIGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 2;
			}
		},
		TRIANGLE {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 3;
			}
		},
		ACUTE_TRIANGLE {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 3) return false;
				Point3D A = f.vertices.get(0).point;
				Point3D B = f.vertices.get(1).point;
				Point3D C = f.vertices.get(2).point;
				double a = A.angle(C, B);
				double b = B.angle(A, C);
				double c = C.angle(B, A);
				return (a < 90 && b < 90 && c < 90);
			}
		},
		OBTUSE_TRIANGLE {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 3) return false;
				Point3D A = f.vertices.get(0).point;
				Point3D B = f.vertices.get(1).point;
				Point3D C = f.vertices.get(2).point;
				double a = A.angle(C, B);
				double b = B.angle(A, C);
				double c = C.angle(B, A);
				return (a > 90 || b > 90 || c > 90);
			}
		},
		RIGHT_TRIANGLE {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 3) return false;
				Point3D A = f.vertices.get(0).point;
				Point3D B = f.vertices.get(1).point;
				Point3D C = f.vertices.get(2).point;
				double a = A.angle(C, B);
				double b = B.angle(A, C);
				double c = C.angle(B, A);
				return (Math.abs(a - 90) < e || Math.abs(b - 90) < e || Math.abs(c - 90) < e);
			}
		},
		SCALENE_TRIANGLE {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 3) return false;
				double a = f.vertices.get(0).point.distance(f.vertices.get(1).point);
				double b = f.vertices.get(1).point.distance(f.vertices.get(2).point);
				double c = f.vertices.get(2).point.distance(f.vertices.get(0).point);
				return (Math.abs(a-b) > e && Math.abs(b-c) > e && Math.abs(c-a) > e);
			}
		},
		ISOSCELES_TRIANGLE {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 3) return false;
				double a = f.vertices.get(0).point.distance(f.vertices.get(1).point);
				double b = f.vertices.get(1).point.distance(f.vertices.get(2).point);
				double c = f.vertices.get(2).point.distance(f.vertices.get(0).point);
				return (Math.abs(a-b) < e || Math.abs(b-c) < e || Math.abs(c-a) < e);
			}
		},
		EQUILATERAL_TRIANGLE {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 3) return false;
				double a = f.vertices.get(0).point.distance(f.vertices.get(1).point);
				double b = f.vertices.get(1).point.distance(f.vertices.get(2).point);
				double c = f.vertices.get(2).point.distance(f.vertices.get(0).point);
				return (Math.abs(a-b) < e && Math.abs(b-c) < e && Math.abs(c-a) < e);
			}
		},
		QUADRILATERAL {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 4;
			}
		},
		IRREGULAR_QUADRILATERAL {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 4) return false;
				Point3D av = f.vertices.get(0).point.subtract(f.vertices.get(1).point).normalize();
				Point3D bv = f.vertices.get(1).point.subtract(f.vertices.get(2).point).normalize();
				Point3D cv = f.vertices.get(2).point.subtract(f.vertices.get(3).point).normalize();
				Point3D dv = f.vertices.get(3).point.subtract(f.vertices.get(0).point).normalize();
				return !(av.negate().equals(cv, e) || bv.negate().equals(dv, e));
			}
		},
		TRAPEZOID {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 4) return false;
				Point3D av = f.vertices.get(0).point.subtract(f.vertices.get(1).point).normalize();
				Point3D bv = f.vertices.get(1).point.subtract(f.vertices.get(2).point).normalize();
				Point3D cv = f.vertices.get(2).point.subtract(f.vertices.get(3).point).normalize();
				Point3D dv = f.vertices.get(3).point.subtract(f.vertices.get(0).point).normalize();
				return (av.negate().equals(cv, e) || bv.negate().equals(dv, e));
			}
		},
		ISOSCELES_TRAPEZOID {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 4) return false;
				Point3D av = f.vertices.get(0).point.subtract(f.vertices.get(1).point).normalize();
				Point3D bv = f.vertices.get(1).point.subtract(f.vertices.get(2).point).normalize();
				Point3D cv = f.vertices.get(2).point.subtract(f.vertices.get(3).point).normalize();
				Point3D dv = f.vertices.get(3).point.subtract(f.vertices.get(0).point).normalize();
				double a = f.vertices.get(0).point.distance(f.vertices.get(1).point);
				double b = f.vertices.get(1).point.distance(f.vertices.get(2).point);
				double c = f.vertices.get(2).point.distance(f.vertices.get(3).point);
				double d = f.vertices.get(3).point.distance(f.vertices.get(0).point);
				return (
					(av.negate().equals(cv, e) && Math.abs(b-d) < e) ||
					(bv.negate().equals(dv, e) && Math.abs(a-c) < e)
				);
			}
		},
		PARALLELOGRAM {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 4) return false;
				double a = f.vertices.get(0).point.distance(f.vertices.get(1).point);
				double b = f.vertices.get(1).point.distance(f.vertices.get(2).point);
				double c = f.vertices.get(2).point.distance(f.vertices.get(3).point);
				double d = f.vertices.get(3).point.distance(f.vertices.get(0).point);
				return (Math.abs(a-c) < e && Math.abs(b-d) < e);
			}
		},
		KITE {
			public boolean matches(Face f, double e) {
				if (f.vertices.size() != 4) return false;
				double a = f.vertices.get(0).point.distance(f.vertices.get(1).point);
				double b = f.vertices.get(1).point.distance(f.vertices.get(2).point);
				double c = f.vertices.get(2).point.distance(f.vertices.get(3).point);
				double d = f.vertices.get(3).point.distance(f.vertices.get(0).point);
				return (
					(Math.abs(a-b) < e && Math.abs(c-d) < e) ||
					(Math.abs(b-c) < e && Math.abs(d-a) < e)
				);
			}
		},
		RHOMBUS {
			public boolean matches(Face f, double e) {
				return QUADRILATERAL.matches(f, e) && EQUILATERAL.matches(f, e);
			}
		},
		RECTANGLE {
			public boolean matches(Face f, double e) {
				return QUADRILATERAL.matches(f, e) && EQUIANGULAR.matches(f, e);
			}
		},
		SQUARE {
			public boolean matches(Face f, double e) {
				return QUADRILATERAL.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		PENTAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 5;
			}
		},
		IRREGULAR_PENTAGON {
			public boolean matches(Face f, double e) {
				return PENTAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_PENTAGON {
			public boolean matches(Face f, double e) {
				return PENTAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		HEXAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 6;
			}
		},
		IRREGULAR_HEXAGON {
			public boolean matches(Face f, double e) {
				return HEXAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_HEXAGON {
			public boolean matches(Face f, double e) {
				return HEXAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		HEPTAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 7;
			}
		},
		IRREGULAR_HEPTAGON {
			public boolean matches(Face f, double e) {
				return HEPTAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_HEPTAGON {
			public boolean matches(Face f, double e) {
				return HEPTAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		OCTAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 8;
			}
		},
		IRREGULAR_OCTAGON {
			public boolean matches(Face f, double e) {
				return OCTAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_OCTAGON {
			public boolean matches(Face f, double e) {
				return OCTAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		NONAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 9;
			}
		},
		IRREGULAR_NONAGON {
			public boolean matches(Face f, double e) {
				return NONAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_NONAGON {
			public boolean matches(Face f, double e) {
				return NONAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		DECAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 10;
			}
		},
		IRREGULAR_DECAGON {
			public boolean matches(Face f, double e) {
				return DECAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_DECAGON {
			public boolean matches(Face f, double e) {
				return DECAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		HENDECAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 11;
			}
		},
		IRREGULAR_HENDECAGON {
			public boolean matches(Face f, double e) {
				return HENDECAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_HENDECAGON {
			public boolean matches(Face f, double e) {
				return HENDECAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		},
		DODECAGON {
			public boolean matches(Face f, double e) {
				return f.vertices.size() == 12;
			}
		},
		IRREGULAR_DODECAGON {
			public boolean matches(Face f, double e) {
				return DODECAGON.matches(f, e) && !REGULAR.matches(f, e);
			}
		},
		REGULAR_DODECAGON {
			public boolean matches(Face f, double e) {
				return DODECAGON.matches(f, e) && REGULAR.matches(f, e);
			}
		};
		public abstract boolean matches(Face face, double epsilon);
		private final String nname = name().toLowerCase().replaceAll("[^A-Za-z0-9]", "");
		public static Classifier forString(String name) {
			String nname = name.toLowerCase().replaceAll("[^A-Za-z0-9]", "");
			for (Classifier cl : values()) if (cl.nname.equals(nname)) return cl;
			return null;
		}
	}
	
	private final SortedMap<Classifier,Color> colorMap;
	
	public Recolor(Map<? extends Classifier,? extends Color> map) {
		this.colorMap = new TreeMap<Classifier,Color>();
		this.colorMap.putAll(map);
	}
	
	public Color getFaceColor(Face face, double epsilon) {
		Color color = face.color;
		for (Map.Entry<Classifier,Color> e : colorMap.entrySet()) {
			if (e.getKey().matches(face, epsilon)) {
				color = e.getValue();
			}
		}
		return color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.vertices.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.faces.size());
		List<Color> faceColors = new ArrayList<Color>(seed.faces.size());
		for (Vertex vertex : seed.vertices) vertices.add(vertex.point);
		for (Face face : seed.faces) {
			List<Integer> indices = new ArrayList<Integer>(face.vertices.size());
			for (Vertex v : face.vertices) indices.add(v.index);
			faces.add(indices);
			faceColors.add(getFaceColor(face, 1e-10));
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Recolor parse(String[] args) {
		Map<Classifier,Color> colorMap = new HashMap<Classifier,Color>();
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			Classifier cl = Classifier.forString(arg);
			if (cl != null && argi < args.length) {
				Color c = parseColor(args[argi++], Color.GRAY);
				colorMap.put(cl, c);
			} else {
				System.err.println("Options:");
				for (Classifier c1 : Classifier.values()) {
					System.err.println("  -" + c1.nname + " <color>");
				}
				return null;
			}
		}
		return new Recolor(colorMap);
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}