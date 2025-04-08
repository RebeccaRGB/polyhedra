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
			Bevel.class,
			// Bowtie.class,
			Center.class,
			Chain.class,
			// Chamfer.class,
			ConvexHull.class,
			// Cross.class,
			Dual.class,
			// Ethyl.class,
			Expand.class,
			Gyro.class,
			Identity.class,
			InsideOut.class,
			Join.class,
			// JoinKisKis.class,
			// JoinLace.class,
			// JoinMedial.class,
			Kis.class,
			// Lace.class,
			// Loft.class,
			// Medial.class,
			Meta.class,
			Needle.class,
			NormalizeVertices.class,
			// OppositeLace.class,
			Ortho.class,
			// Propeller.class,
			// Quinto.class,
			Recolor.class,
			Reflect.class,
			Resize.class,
			Rotate.class,
			Scale.class,
			Snub.class,
			// Stake.class,
			// Subdivide.class,
			Translate.class,
			Truncate.class,
			// Volute.class,
			// Waffle.class,
			// Whirl.class,
			Zip.class
		)
	);
	
	public static final Map<String,Class<? extends PolyhedronOp>> CON = Collections.unmodifiableMap(
		Arrayz.asMap(
			// Arrayz.mapEntry("B", Bowtie.class),
			// Arrayz.mapEntry("E", Ethyl.class),
			// Arrayz.mapEntry("G", OppositeLace.class),
			Arrayz.mapEntry("I", Identity.class),
			// Arrayz.mapEntry("J", JoinMedial.class),
			// Arrayz.mapEntry("K", Stake.class),
			// Arrayz.mapEntry("L", Lace.class),
			// Arrayz.mapEntry("L0", JoinLace.class),
			// Arrayz.mapEntry("M", Medial.class),
			// Arrayz.mapEntry("Q0", JoinKisKis.class),
			// Arrayz.mapEntry("W", Waffle.class),
			// Arrayz.mapEntry("X", Cross.class),
			Arrayz.mapEntry("a", Ambo.class),
			Arrayz.mapEntry("b", Bevel.class),
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