package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Dual extends PolyhedronOp {
	public static enum RescaleMode {
		NONE {
			public void rescale(List<Point3D> seedVertices, List<Point3D> dualVertices) {
				return; // Do nothing.
			}
		},
		MAX_MAGNITUDE {
			public void rescale(List<Point3D> seedVertices, List<Point3D> dualVertices) {
				double seedScale = Point3D.maxMagnitude(seedVertices);
				double dualScale = Point3D.maxMagnitude(dualVertices);
				if (seedScale == 0 || dualScale == 0 || seedScale == dualScale) return;
				for (int i = 0, n = dualVertices.size(); i < n; i++) {
					dualVertices.set(i, dualVertices.get(i).multiply(seedScale / dualScale));
				}
			}
		},
		AVERAGE_MAGNITUDE {
			public void rescale(List<Point3D> seedVertices, List<Point3D> dualVertices) {
				double seedScale = Point3D.averageMagnitude(seedVertices);
				double dualScale = Point3D.averageMagnitude(dualVertices);
				if (seedScale == 0 || dualScale == 0 || seedScale == dualScale) return;
				for (int i = 0, n = dualVertices.size(); i < n; i++) {
					dualVertices.set(i, dualVertices.get(i).multiply(seedScale / dualScale));
				}
			}
		};
		public abstract void rescale(List<Point3D> seedVertices, List<Point3D> dualVertices);
	}
	
	private final RescaleMode rescale;
	private final Color color;
	
	public Dual(RescaleMode rescale, Color color) {
		this.rescale = rescale;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		List<Point3D> vertices = new ArrayList<Point3D>(seed.faces.size());
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.vertices.size());
		List<Color> faceColors = new ArrayList<Color>(seed.vertices.size());
		for (Polyhedron.Face f : seed.faces) vertices.add(f.center());
		for (Polyhedron.Vertex v : seed.vertices) {
			List<Polyhedron.Face> seedFaces = seed.getFaces(v);
			while (!seedFaces.isEmpty()) {
				List<Integer> dualFace = new ArrayList<Integer>();
				for (Polyhedron.Face seedFace : seed.getOrderedFaces(v, seedFaces)) {
					dualFace.add(seedFace.index);
					seedFaces.remove(seedFace);
				}
				faces.add(dualFace);
				faceColors.add(color);
			}
		}
		rescale.rescale(seed.points(), vertices);
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static class Factory extends PolyhedronOp.Factory<Dual> {
		public String name() { return "Dual"; }
		
		public Dual parse(String[] args) {
			RescaleMode rescale = RescaleMode.MAX_MAGNITUDE;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-s")) {
					rescale = RescaleMode.NONE;
				} else if (arg.equalsIgnoreCase("-m")) {
					rescale = RescaleMode.MAX_MAGNITUDE;
				} else if (arg.equalsIgnoreCase("-a")) {
					rescale = RescaleMode.AVERAGE_MAGNITUDE;
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					return null;
				}
			}
			return new Dual(rescale, color);
		}
		
		public Option[] options() {
			return new Option[] {
				new Option("m", Type.VOID, "rescale dual polyhedron to match original maximum magnitude", "a","s"),
				new Option("a", Type.VOID, "rescale dual polyhedron to match original average magnitude", "m","s"),
				new Option("s", Type.VOID, "do not rescale dual polyhedron (strict mode)", "m","a"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}