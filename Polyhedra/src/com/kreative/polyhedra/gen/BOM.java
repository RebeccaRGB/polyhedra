package com.kreative.polyhedra.gen;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.PolyhedronGen;

public class BOM {
	public static final List<Class<? extends PolyhedronGen>> BOM = Collections.unmodifiableList(
		Arrays.asList(
			Antifrustum.class,
			Antiprism.class,
			Bipyramid.class,
			Box.class,
			Compose.class,
			Constant.class,
			Construct.class,
			Cube.class,
			Dodecahedron.class,
			Frustum.class,
			Octahedron.class,
			Polygon.class,
			Prism.class,
			Pyramid.class,
			Tetrahedron.class
		)
	);
	
	public static final Map<String,Class<? extends PolyhedronGen>> MAP;
	static {
		Map<String,Class<? extends PolyhedronGen>> map =
			new HashMap<String,Class<? extends PolyhedronGen>>();
		for (Class<? extends PolyhedronGen> cls : BOM)
			map.put(cls.getSimpleName().toLowerCase(), cls);
		MAP = Collections.unmodifiableMap(map);
	}
}