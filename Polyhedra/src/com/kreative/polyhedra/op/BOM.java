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
			AffineTransform.class,
			Ambo.class,
			Center.class,
			Chain.class,
			ConvexHull.class,
			Dual.class,
			Identity.class,
			InsideOut.class,
			Join.class,
			Kis.class,
			Needle.class,
			NormalizeVertices.class,
			Recolor.class,
			Reflect.class,
			Resize.class,
			Rotate.class,
			Scale.class,
			Translate.class,
			Truncate.class,
			Zip.class
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