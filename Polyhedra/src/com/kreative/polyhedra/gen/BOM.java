package com.kreative.polyhedra.gen;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.PolyhedronGen;

public class BOM {
	public static final List<PolyhedronGen.Factory<? extends PolyhedronGen>> BOM = Collections.unmodifiableList(
		Arrays.asList(
			new Antibicupola.Factory(),
			new Antibifrustum.Factory(),
			new Anticupola.Factory(),
			new Antifrustum.Factory(),
			new Antiprism.Factory(),
			new ArchimedeanSolid.Factory(),
			new Bicupola.Factory(),
			new Bifrustum.Factory(),
			new Bipyramid.Factory(),
			new Box.Factory(),
			new CatalanSolid.Factory(),
			new Compose.Factory(),
			new Constant.Factory(),
			new Construct.Factory(),
			new Cube.Factory(),
			new Cupola.Factory(),
			new Deltille.Factory(),
			new Dodecahedron.Factory(),
			new Frustum.Factory(),
			new GreatDodecahedron.Factory(),
			new GreatIcosahedron.Factory(),
			new GreatStellatedDodecahedron.Factory(),
			new Hextille.Factory(),
			new Icosahedron.Factory(),
			new JohnsonSolid.Factory(),
			new Octahedron.Factory(),
			new Path.Factory(),
			new PointCloud.Factory(),
			new Polygon.Factory(),
			new Prism.Factory(),
			new Pyramid.Factory(),
			new Quadrille.Factory(),
			new Rotunda.Factory(),
			new Sharpohedron.Factory(),
			new SmallStellatedDodecahedron.Factory(),
			new Tetrahedron.Factory(),
			new Trapezohedron.Factory()
			// new UniformPolyhedron.Factory()
		)
	);
	
	public static final Map<String,PolyhedronGen.Factory<? extends PolyhedronGen>> CON;
	public static final Map<String,PolyhedronGen.Factory<? extends PolyhedronGen>> MAP;
	
	static {
		Map<String,PolyhedronGen.Factory<? extends PolyhedronGen>> con =
			new HashMap<String,PolyhedronGen.Factory<? extends PolyhedronGen>>();
		
		con.put("A", new Antiprism.Factory());
		con.put("C", new Cube.Factory());
		con.put("D", new Dodecahedron.Factory());
		con.put("D*", new SmallStellatedDodecahedron.Factory());
		con.put("E", new GreatDodecahedron.Factory());
		con.put("E*", new GreatStellatedDodecahedron.Factory());
		con.put("F", new Frustum.Factory()); // extension
		con.put("FF", new Bifrustum.Factory()); // extension
		con.put("G", new Antifrustum.Factory()); // extension
		con.put("GG", new Antibifrustum.Factory()); // extension
		con.put("H", new Hextille.Factory());
		con.put("I", new Icosahedron.Factory());
		con.put("J", new GreatIcosahedron.Factory());
		con.put("J#", new JohnsonSolid.Factory());
		con.put("K", new CatalanSolid.Factory()); // extension
		// con.put("N", new UniformPolyhedron.Factory()); // extension
		con.put("O", new Octahedron.Factory());
		con.put("P", new Prism.Factory());
		con.put("Q", new Quadrille.Factory());
		con.put("R", new Trapezohedron.Factory()); // extension
		con.put("S", new Sharpohedron.Factory()); // extension
		con.put("T", new Tetrahedron.Factory());
		con.put("U", new Cupola.Factory());
		con.put("UU", new Bicupola.Factory()); // extension
		con.put("V", new Anticupola.Factory());
		con.put("VV", new Antibicupola.Factory()); // extension
		con.put("X", new ArchimedeanSolid.Factory()); // extension
		con.put("Y", new Pyramid.Factory());
		con.put("YY", new Bipyramid.Factory()); // extension
		con.put("Z", new Polygon.Factory()); // extension
		con.put("Δ", new Deltille.Factory());
		
		Map<String,PolyhedronGen.Factory<? extends PolyhedronGen>> map =
			new HashMap<String,PolyhedronGen.Factory<? extends PolyhedronGen>>(con);
		
		for (PolyhedronGen.Factory<? extends PolyhedronGen> factory : BOM) {
			String name = factory.name();
			map.put(name, factory);
			map.put(name.toUpperCase(), factory);
			map.put(name.toLowerCase(), factory);
			name = name.replaceAll("\\s+", "");
			map.put(name, factory);
			map.put(name.toUpperCase(), factory);
			map.put(name.toLowerCase(), factory);
		}
		
		CON = Collections.unmodifiableMap(con);
		MAP = Collections.unmodifiableMap(map);
	}
}