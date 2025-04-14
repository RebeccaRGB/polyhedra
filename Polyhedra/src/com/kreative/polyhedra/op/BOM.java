package com.kreative.polyhedra.op;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kreative.polyhedra.PolyhedronOp;

public class BOM {
	public static final List<PolyhedronOp.Factory<? extends PolyhedronOp>> BOM = Collections.unmodifiableList(
		Arrays.asList(
			new AffineTransform.Factory(),
			new Ambo.Factory(),
			new Bevel.Factory(),
			// new Bowtie.Factory(),
			new Center.Factory(),
			new Chain.Factory(),
			// new Chamfer.Factory(),
			new ConvexHull.Factory(),
			// new Cross.Factory(),
			new Dual.Factory(),
			// new Ethyl.Factory(),
			new Expand.Factory(),
			new Gyro.Factory(),
			new Identity.Factory(),
			new InsideOut.Factory(),
			new Join.Factory(),
			// new JoinKisKis.Factory(),
			// new JoinLace.Factory(),
			// new JoinMedial.Factory(),
			new Kis.Factory(),
			// new Lace.Factory(),
			// new Loft.Factory(),
			// new Medial.Factory(),
			new Meta.Factory(),
			new Needle.Factory(),
			new NormalizeVertices.Factory(),
			// new OppositeLace.Factory(),
			new Ortho.Factory(),
			new PlanarizeFaces.Factory(),
			// new Propeller.Factory(),
			// new Quinto.Factory(),
			new Recolor.Factory(),
			new Reflect.Factory(),
			new Resize.Factory(),
			new Rotate.Factory(),
			new Scale.Factory(),
			new Snub.Factory(),
			// new Stake.Factory(),
			// new Subdivide.Factory(),
			new Translate.Factory(),
			new Truncate.Factory(),
			// new Volute.Factory(),
			// new Waffle.Factory(),
			// new Whirl.Factory(),
			new Zip.Factory()
		)
	);
	
	public static final Map<String,PolyhedronOp.Factory<? extends PolyhedronOp>> CON;
	public static final Map<String,PolyhedronOp.Factory<? extends PolyhedronOp>> MAP;
	
	static {
		Map<String,PolyhedronOp.Factory<? extends PolyhedronOp>> con =
			new HashMap<String,PolyhedronOp.Factory<? extends PolyhedronOp>>();
		
		// con.put("B", new Bowtie.Factory());
		// con.put("E", new Ethyl.Factory());
		// con.put("G", new OppositeLace.Factory());
		con.put("I", new Identity.Factory());
		// con.put("J", new JoinMedial.Factory());
		// con.put("K", new Stake.Factory());
		// con.put("L", new Lace.Factory());
		// con.put("L0", new JoinLace.Factory());
		// con.put("M", new Medial.Factory());
		// con.put("Q0", new JoinKisKis.Factory());
		// con.put("W", new Waffle.Factory());
		// con.put("X", new Cross.Factory());
		con.put("a", new Ambo.Factory());
		con.put("b", new Bevel.Factory());
		// con.put("c", new Chamfer.Factory());
		con.put("d", new Dual.Factory());
		con.put("e", new Expand.Factory());
		con.put("g", new Gyro.Factory());
		con.put("j", new Join.Factory());
		con.put("k", new Kis.Factory());
		// con.put("kk0", new JoinKisKis.Factory());
		// con.put("l", new Loft.Factory());
		con.put("m", new Meta.Factory());
		con.put("n", new Needle.Factory());
		con.put("o", new Ortho.Factory());
		// con.put("p", new Propeller.Factory());
		// con.put("q", new Quinto.Factory());
		con.put("r", new Reflect.Factory());
		con.put("s", new Snub.Factory());
		con.put("t", new Truncate.Factory());
		// con.put("u", new Subdivide.Factory());
		// con.put("v", new Volute.Factory());
		// con.put("w", new Whirl.Factory());
		con.put("z", new Zip.Factory());
		
		Map<String,PolyhedronOp.Factory<? extends PolyhedronOp>> map =
			new HashMap<String,PolyhedronOp.Factory<? extends PolyhedronOp>>(con);
		
		for (PolyhedronOp.Factory<? extends PolyhedronOp> factory : BOM) {
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