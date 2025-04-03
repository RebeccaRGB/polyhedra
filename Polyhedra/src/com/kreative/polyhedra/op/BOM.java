package com.kreative.polyhedra.op;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.Arrayz;
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
			Expand.class,
			Gyro.class,
			Identity.class,
			InsideOut.class,
			Join.class,
			Kis.class,
			Meta.class,
			Needle.class,
			NormalizeVertices.class,
			Ortho.class,
			Recolor.class,
			Reflect.class,
			Resize.class,
			Rotate.class,
			Scale.class,
			Snub.class,
			Translate.class,
			Truncate.class,
			Zip.class
		)
	);
	
	public static final Map<String,Class<? extends PolyhedronOp>> CON = Collections.unmodifiableMap(
		Arrayz.asMap(
			Arrayz.mapEntry("I", Identity.class),
			// Arrayz.mapEntry("K", Stake.class),
			// Arrayz.mapEntry("L", Lace.class),
			// Arrayz.mapEntry("L0", JoinLace.class),
			// Arrayz.mapEntry("X", Cross.class),
			Arrayz.mapEntry("a", Ambo.class),
			// Arrayz.mapEntry("b", Bevel.class),
			// Arrayz.mapEntry("c", Chamfer.class),
			Arrayz.mapEntry("d", Dual.class),
			Arrayz.mapEntry("e", Expand.class),
			Arrayz.mapEntry("g", Gyro.class),
			Arrayz.mapEntry("j", Join.class),
			Arrayz.mapEntry("k", Kis.class),
			// Arrayz.mapEntry("kk0", JoinKisKis.class),
			// Arrayz.mapEntry("l", Loft.class),
			Arrayz.mapEntry("m", Meta.class),
			Arrayz.mapEntry("n", Needle.class),
			Arrayz.mapEntry("o", Ortho.class),
			// Arrayz.mapEntry("p", Propeller.class),
			// Arrayz.mapEntry("q", Quinto.class),
			Arrayz.mapEntry("r", Reflect.class),
			Arrayz.mapEntry("s", Snub.class),
			Arrayz.mapEntry("t", Truncate.class),
			// Arrayz.mapEntry("u", Subdivide.class),
			// Arrayz.mapEntry("v", Volute.class),
			// Arrayz.mapEntry("w", Whirl.class),
			Arrayz.mapEntry("z", Zip.class)
		)
	);
	
	public static final Map<String,Class<? extends PolyhedronOp>> MAP;
	static {
		Map<String,Class<? extends PolyhedronOp>> map =
			new HashMap<String,Class<? extends PolyhedronOp>>(CON);
		for (Class<? extends PolyhedronOp> cls : BOM)
			map.put(cls.getSimpleName().toLowerCase(), cls);
		MAP = Collections.unmodifiableMap(map);
	}
}