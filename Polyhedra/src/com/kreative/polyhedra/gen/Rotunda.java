package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.gen.Polygon.Axis;
import com.kreative.polyhedra.gen.Polygon.SizeSpecifier;

public class Rotunda extends PolyhedronGen {
	private static final double PHI0 = 0.38196601125010515180; // (3-sqrt(5))/2, 1/(1+phi), 1-1/phi
	
	public static enum Extension {
		ORTHOCUPOLA {
			protected final void createVertices(
				List<Point3D> vertices, int n, double m, double r, double ph, Axis axis, double mz, double z
			) {
				Polygon.createVertices(vertices, n, r, ph-0.5, axis, z);
			}
			protected final void createFaces(
				List<List<Integer>> faces, List<Color> faceColors, int n, int firstIndex, Color baseColor, Color joinColor
			) {
				Polygon.createFaces(faces, faceColors, n, 1, firstIndex+n*2, true, baseColor);
				for (int i = 0; i < n; i++) {
					int j = (i + 1) % n;
					int k = (firstIndex + i + i);
					int l = (firstIndex + i + i + 1);
					int m = (firstIndex + j + j);
					int p = (firstIndex + n + n + i);
					int q = (firstIndex + n + n + j);
					faces.add(Arrays.asList(p, q, l, k));
					faces.add(Arrays.asList(q, m, l));
					faceColors.add(joinColor);
					faceColors.add(joinColor);
				}
			}
		},
		GYROCUPOLA {
			protected final void createVertices(
				List<Point3D> vertices, int n, double m, double r, double ph, Axis axis, double mz, double z
			) {
				Polygon.createVertices(vertices, n, r, ph, axis, z);
			}
			protected final void createFaces(
				List<List<Integer>> faces, List<Color> faceColors, int n, int firstIndex, Color baseColor, Color joinColor
			) {
				Polygon.createFaces(faces, faceColors, n, 1, firstIndex+n*2, true, baseColor);
				for (int i = 0; i < n; i++) {
					int j = (i + 1) % n;
					int k = (firstIndex + i + i);
					int l = (firstIndex + i + i + 1);
					int m = (firstIndex + j + j);
					int p = (firstIndex + n + n + i);
					int q = (firstIndex + n + n + j);
					faces.add(Arrays.asList(p, q, m, l));
					faces.add(Arrays.asList(p, l, k));
					faceColors.add(joinColor);
					faceColors.add(joinColor);
				}
			}
		},
		ORTHOROTUNDA {
			protected final void createVertices(
				List<Point3D> vertices, int n, double m, double r, double ph, Axis axis, double mz, double z
			) {
				Polygon.createVertices(vertices, n, m, ph, axis, mz);
				Polygon.createVertices(vertices, n, r, ph-0.5, axis, z);
			}
			protected final void createFaces(
				List<List<Integer>> faces, List<Color> faceColors, int n, int firstIndex, Color baseColor, Color joinColor
			) {
				Polygon.createFaces(faces, faceColors, n, 1, firstIndex+n*3, true, baseColor);
				for (int i = 0; i < n; i++) {
					int j = (i + 1) % n;
					faces.add(Arrays.asList(firstIndex+i+i+1, firstIndex+i+i, firstIndex+n+n+i));
					faces.add(Arrays.asList(firstIndex+n+n+j, firstIndex+j+j, firstIndex+i+i+1, firstIndex+n+n+i, firstIndex+n+n+n+j));
					faces.add(Arrays.asList(firstIndex+n+n+i, firstIndex+n+n+n+i, firstIndex+n+n+n+j));
					faceColors.add(joinColor);
					faceColors.add(joinColor);
					faceColors.add(joinColor);
				}
			}
		},
		GYROROTUNDA {
			protected final void createVertices(
				List<Point3D> vertices, int n, double m, double r, double ph, Axis axis, double mz, double z
			) {
				Polygon.createVertices(vertices, n, m, ph-0.5, axis, mz);
				Polygon.createVertices(vertices, n, r, ph-1, axis, z);
			}
			protected final void createFaces(
				List<List<Integer>> faces, List<Color> faceColors, int n, int firstIndex, Color baseColor, Color joinColor
			) {
				Polygon.createFaces(faces, faceColors, n, 1, firstIndex+n*3, true, baseColor);
				for (int i = 0; i < n; i++) {
					int j = (i + 1) % n;
					faces.add(Arrays.asList(firstIndex+j+j, firstIndex+i+i+1, firstIndex+n+n+j));
					faces.add(Arrays.asList(firstIndex+n+n+j, firstIndex+i+i+1, firstIndex+i+i, firstIndex+n+n+i, firstIndex+n+n+n+j));
					faces.add(Arrays.asList(firstIndex+n+n+i, firstIndex+n+n+n+i, firstIndex+n+n+n+j));
					faceColors.add(joinColor);
					faceColors.add(joinColor);
					faceColors.add(joinColor);
				}
			}
		};
		protected abstract void createVertices(
			List<Point3D> vertices, int n, double m, double r, double ph, Axis axis, double mz, double z
		);
		protected abstract void createFaces(
			List<List<Integer>> faces, List<Color> faceColors, int n, int firstIndex, Color baseColor, Color joinColor
		);
	}
	
	private final int n;
	private final double R;
	private final double r;
	private final Axis axis;
	private final double h;
	private final boolean gyro;
	private final double e;
	private final Extension ext;
	private final double eh;
	private final Color baseColor;
	private final Color prismColor;
	private final Color rotundaColor;
	
	public Rotunda(
		int n, double R, double r, Axis axis, double h,
		boolean gyro, double e, Extension ext, double eh,
		Color base, Color prism, Color rotunda
	) {
		this.n = n;
		this.R = R;
		this.r = r;
		this.axis = axis;
		this.h = h;
		this.gyro = gyro;
		this.e = e;
		this.ext = ext;
		this.eh = eh;
		this.baseColor = base;
		this.prismColor = prism;
		this.rotundaColor = rotunda;
	}
	
	public Polyhedron gen() {
		List<Point3D> vertices = new ArrayList<Point3D>();
		List<List<Integer>> faces = new ArrayList<List<Integer>>();
		List<Color> faceColors = new ArrayList<Color>();
		double A = SizeSpecifier.APOTHEM.fromRadius(R, n*2);
		double m = SizeSpecifier.APOTHEM.toRadius((A-r)*PHI0+r, n);
		Polygon.createVertices(vertices, n, r, 0, axis, (eh+e+h)/2);
		Polygon.createVertices(vertices, n, m, 0.5, axis, (eh+e+h)/2-h*PHI0);
		Polygon.createVertices(vertices, n*2, R, 0.5, axis, (eh+e-h)/2);
		if (e != 0) Polygon.createVertices(vertices, n*2, R, (gyro ? 1 : 0.5), axis, (eh-e-h)/2);
		Polygon.createFaces(faces, faceColors, n, 1, 0, false, baseColor);
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			faces.add(Arrays.asList(j, i, i+n));
			faces.add(Arrays.asList(j, i+n, i+i+1+n+n, j+j+n+n, j+n));
			faces.add(Arrays.asList(i+n, i+i+n+n, i+i+1+n+n));
			faceColors.add(rotundaColor);
			faceColors.add(rotundaColor);
			faceColors.add(rotundaColor);
			if (e != 0) {
				if (gyro) {
					faces.add(Arrays.asList(i+i+1+n*2, i+i+n*2, i+i+n*4));
					faces.add(Arrays.asList(i+i+1+n*2, i+i+n*4, i+i+1+n*4));
					faces.add(Arrays.asList(j+j+n*2, i+i+1+n*2, i+i+1+n*4));
					faces.add(Arrays.asList(j+j+n*2, i+i+1+n*4, j+j+n*4));
					faceColors.add(prismColor);
					faceColors.add(prismColor);
					faceColors.add(prismColor);
					faceColors.add(prismColor);
				} else {
					faces.add(Arrays.asList(i+i+1+n*2, i+i+n*2, i+i+n*4, i+i+1+n*4));
					faces.add(Arrays.asList(j+j+n*2, i+i+1+n*2, i+i+1+n*4, j+j+n*4));
					faceColors.add(prismColor);
					faceColors.add(prismColor);
				}
			}
		}
		if (ext != null && eh != 0) {
			ext.createVertices(vertices, n, m, r, ((e != 0 && gyro) ? 0.75 : 0.5), axis, h*PHI0-(eh+e+h)/2, -(eh+e+h)/2);
			ext.createFaces(faces, faceColors, n, ((e != 0) ? (n*4) : (n*2)), baseColor, rotundaColor);
		} else {
			Polygon.createFaces(faces, faceColors, n*2, 1, ((e != 0) ? (n*4) : (n*2)), true, baseColor);
		}
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronGen.Factory<Rotunda> {
		public String name() { return "Rotunda"; }
		
		public Rotunda parse(String[] args) {
			int n = 5;
			SizeSpecifier Spec = SizeSpecifier.RADIUS;
			SizeSpecifier spec = SizeSpecifier.RADIUS;
			double Size = 1;
			double size = 0.5;
			Axis axis = Axis.Y;
			double h = 1;
			boolean gyro = false;
			double e = 0;
			Extension ext = null;
			double eh = 0;
			Color c = Color.GRAY;
			Color baseColor = null;
			Color prismColor = null;
			Color rotundaColor = null;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					if ((n = Math.abs(parseInt(args[argi++], n))) < 3) n = 3;
				} else if (arg.equals("-R") && argi < args.length) {
					Spec = SizeSpecifier.RADIUS;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-D") && argi < args.length) {
					Spec = SizeSpecifier.DIAMETER;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-S") && argi < args.length) {
					Spec = SizeSpecifier.SIDE_LENGTH;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-A") && argi < args.length) {
					Spec = SizeSpecifier.APOTHEM;
					Size = parseDouble(args[argi++], Size);
				} else if (arg.equals("-r") && argi < args.length) {
					spec = SizeSpecifier.RADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-d") && argi < args.length) {
					spec = SizeSpecifier.DIAMETER;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-s") && argi < args.length) {
					spec = SizeSpecifier.SIDE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equals("-a") && argi < args.length) {
					spec = SizeSpecifier.APOTHEM;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-x")) {
					axis = Axis.X;
				} else if (arg.equalsIgnoreCase("-y")) {
					axis = Axis.Y;
				} else if (arg.equalsIgnoreCase("-z")) {
					axis = Axis.Z;
				} else if (arg.equalsIgnoreCase("-h") && argi < args.length) {
					h = parseDouble(args[argi++], h);
				} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
					gyro = false; e = parseDouble(args[argi++], e);
				} else if (arg.equalsIgnoreCase("-g") && argi < args.length) {
					gyro = true; e = parseDouble(args[argi++], e);
				} else if (arg.equalsIgnoreCase("-o") && argi < args.length) {
					ext = Extension.ORTHOCUPOLA; eh = parseDouble(args[argi++], eh);
				} else if (arg.equalsIgnoreCase("-q") && argi < args.length) {
					ext = Extension.GYROCUPOLA; eh = parseDouble(args[argi++], eh);
				} else if (arg.equalsIgnoreCase("-u") && argi < args.length) {
					ext = Extension.ORTHOROTUNDA; eh = parseDouble(args[argi++], eh);
				} else if (arg.equalsIgnoreCase("-w") && argi < args.length) {
					ext = Extension.GYROROTUNDA; eh = parseDouble(args[argi++], eh);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					c = parseColor(args[argi++], c);
				} else if (arg.equalsIgnoreCase("-b") && argi < args.length) {
					baseColor = parseColor(args[argi++], baseColor);
				} else if (arg.equalsIgnoreCase("-p") && argi < args.length) {
					prismColor = parseColor(args[argi++], prismColor);
				} else if (arg.equalsIgnoreCase("-j") && argi < args.length) {
					rotundaColor = parseColor(args[argi++], rotundaColor);
				} else {
					return null;
				}
			}
			double R = Spec.toRadius(Size, n*2);
			double r = spec.toRadius(size, n);
			return new Rotunda(
				n, R, r, axis, h, gyro, e, ext, eh,
				((baseColor != null) ? baseColor : c),
				((prismColor != null) ? prismColor : c),
				((rotundaColor != null) ? rotundaColor : c)
			);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("n", Type.INT, "number of sides on topmost face"),
				new Option("R", Type.REAL, "radius of bottom of top rotunda", "D","S","A"),
				new Option("D", Type.REAL, "diameter of bottom of top rotunda", "R","S","A"),
				new Option("S", Type.REAL, "side length of bottom of top rotunda", "R","D","A"),
				new Option("A", Type.REAL, "apothem of bottom of top rotunda", "R","D","S"),
				new Option("r", Type.REAL, "radius of topmost face", "d","s","a"),
				new Option("d", Type.REAL, "diameter of topmost face", "r","s","a"),
				new Option("s", Type.REAL, "side length of topmost face", "r","d","a"),
				new Option("a", Type.REAL, "apothem of topmost face", "r","d","s"),
				new Option("x", Type.VOID, "align central axis to X axis", "y","z"),
				new Option("y", Type.VOID, "align central axis to Y axis", "x","z"),
				new Option("z", Type.VOID, "align central axis to Z axis", "x","y"),
				new Option("h", Type.REAL, "height of top rotunda"),
				new Option("e", Type.REAL, "height of prism (elongate)", "g"),
				new Option("g", Type.REAL, "height of antiprism (gyroelongate)", "e"),
				new Option("o", Type.REAL, "height of bottom orthocupola", "q","u","w"),
				new Option("q", Type.REAL, "height of bottom gyrocupola", "o","u","w"),
				new Option("u", Type.REAL, "height of bottom orthorotunda", "o","q","w"),
				new Option("w", Type.REAL, "height of bottom gyrorotunda", "o","q","u"),
				new Option("c", Type.COLOR, "color", "b","p","j"),
				new Option("b", Type.COLOR, "base color", "c"),
				new Option("p", Type.COLOR, "prism color", "c"),
				new Option("j", Type.COLOR, "rotunda color", "c"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}