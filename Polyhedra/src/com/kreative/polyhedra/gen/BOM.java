package com.kreative.polyhedra.gen;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Arrayz;
import com.kreative.polyhedra.PolyhedronGen;

public class BOM {
	public static final List<Class<? extends PolyhedronGen>> BOM = Collections.unmodifiableList(
		Arrays.asList(
			Antibifrustum.class,
			Antifrustum.class,
			Antiprism.class,
			Bifrustum.class,
			Bipyramid.class,
			Box.class,
			Compose.class,
			Constant.class,
			Construct.class,
			Cube.class,
			Dodecahedron.class,
			Frustum.class,
			Icosahedron.class,
			Octahedron.class,
			Polygon.class,
			Prism.class,
			Pyramid.class,
			Tetrahedron.class,
			Trapezohedron.class
		)
	);
	
	public static final Map<String,Class<? extends PolyhedronGen>> CON = Collections.unmodifiableMap(
		Arrayz.asMap(
			Arrayz.mapEntry("A", Antiprism.class),
			Arrayz.mapEntry("C", Cube.class),
			Arrayz.mapEntry("D", Dodecahedron.class),
			// Arrayz.mapEntry("H", Hextille.class),
			Arrayz.mapEntry("I", Icosahedron.class),
			// Arrayz.mapEntry("J", JohnsonSolid.class),
			Arrayz.mapEntry("O", Octahedron.class),
			Arrayz.mapEntry("P", Prism.class),
			// Arrayz.mapEntry("Q", Quadrille.class),
			Arrayz.mapEntry("T", Tetrahedron.class),
			// Arrayz.mapEntry("U", Cupola.class),
			// Arrayz.mapEntry("V", Anticupola.class),
			Arrayz.mapEntry("Y", Pyramid.class)
			// Arrayz.mapEntry("Δ", Deltille.class)
		)
	);
	
	public static final Map<String,Class<? extends PolyhedronGen>> MAP;
	static {
		Map<String,Class<? extends PolyhedronGen>> map =
			new HashMap<String,Class<? extends PolyhedronGen>>(CON);
		for (Class<? extends PolyhedronGen> cls : BOM)
			map.put(cls.getSimpleName().toLowerCase(), cls);
		MAP = Collections.unmodifiableMap(map);
	}
}