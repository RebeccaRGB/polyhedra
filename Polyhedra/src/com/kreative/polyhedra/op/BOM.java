package com.kreative.polyhedra.op;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.PolyhedronOp;

public class BOM {
	public static final List<Class<? extends PolyhedronOp>> BOM = Collections.unmodifiableList(
		Arrays.asList(
			Chain.class,
			ConvexHull.class,
			Identity.class,
			Reflect.class
		)
	);
	
	public static final Map<String,Class<? extends PolyhedronOp>> MAP;
	static {
		Map<String,Class<? extends PolyhedronOp>> map =
			new HashMap<String,Class<? extends PolyhedronOp>>();
		for (Class<? extends PolyhedronOp> cls : BOM)
			map.put(cls.getSimpleName().toLowerCase(), cls);
		MAP = Collections.unmodifiableMap(map);
	}
}