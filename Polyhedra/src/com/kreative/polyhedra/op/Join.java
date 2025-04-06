package com.kreative.polyhedra.op;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronOp;

public class Join extends PolyhedronOp {
	private final FaceVertexGen gen;
	private final double size;
	private final Color color;
	
	public Join(FaceVertexGen gen, double size, Color color) {
		this.gen = gen;
		this.size = size;
		this.color = color;
	}
	
	public Polyhedron op(Polyhedron seed) {
		int vfSize = seed.vertices.size() + seed.faces.size();
		List<Point3D> vertices = new ArrayList<Point3D>(vfSize);
		List<List<Integer>> faces = new ArrayList<List<Integer>>(seed.edges.size());
		List<Color> faceColors = new ArrayList<Color>(seed.edges.size());
		
		for (Polyhedron.Vertex v : seed.vertices) vertices.add(v.point);
		List<Point3D> seedVertices = new ArrayList<Point3D>(vertices);
		
		Map<Polyhedron.Edge,Integer> edgeVertexMap = new HashMap<Polyhedron.Edge,Integer>();
		for (Polyhedron.Face f : seed.faces) {
			List<Point3D> faceVertices = new ArrayList<Point3D>(f.vertices.size());
			for (Polyhedron.Vertex v : f.vertices) faceVertices.add(v.point);
			Point3D newVertex = gen.createVertex(seed, seedVertices, f, faceVertices, size);
			if (newVertex != null) {
				int i0 = vertices.size();
				vertices.add(newVertex);
				for (int i = 0, n = f.edges.size(); i < n; i++) {
					Integer i2 = edgeVertexMap.get(f.edges.get(i));
					if (i2 == null) {
						edgeVertexMap.put(f.edges.get(i), i0);
					} else {
						int i1 = f.vertices.get(i).index;
						int i3 = f.vertices.get((i + 1) % n).index;
						faces.add(Arrays.asList(i0, i1, i2, i3));
						faceColors.add(color);
					}
				}
			}
		}
		
		return new Polyhedron(vertices, faces, faceColors);
	}
	
	public static Join parse(String[] args) {
		FaceVertexGen gen = FaceVertexGen.PLANAR;
		double size = 0;
		Color color = Color.GRAY;
		FaceVertexGen tmp;
		int argi = 0;
		while (argi < args.length) {
			String arg = args[argi++];
			if (arg.equalsIgnoreCase("-s")) {
				gen = FaceVertexGen.FACE_OFFSET;
				size = 0;
			} else if ((tmp = FaceVertexGen.forFlagIgnoreCase(arg)) != null && (tmp.isVoidType() || argi < args.length)) {
				gen = tmp;
				size = tmp.isVoidType() ? 0 : parseDouble(args[argi++], size);
			} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
				color = parseColor(args[argi++], color);
			} else {
				printOptions(options());
				return null;
			}
		}
		return new Join(gen, size, color);
	}
	
	public static Option[] options() {
		return new Option[] {
			FaceVertexGen.FACE_OFFSET.option("s"),
			FaceVertexGen.MAX_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.AVERAGE_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.FACE_MAGNITUDE_OFFSET.option("s"),
			FaceVertexGen.PLANAR.option("s"),
			new Option("s", Type.VOID, "create new vertices at centers of original faces (strict mode)", FaceVertexGen.allOptionMutexes()),
			new Option("c", Type.COLOR, "color"),
		};
	}
	
	public static void main(String[] args) {
		main(parse(args));
	}
}