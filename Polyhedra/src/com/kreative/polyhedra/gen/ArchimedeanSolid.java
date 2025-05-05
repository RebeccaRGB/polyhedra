package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.PolyhedronOp;
import com.kreative.polyhedra.op.Ambo;
import com.kreative.polyhedra.op.Bevel;
import com.kreative.polyhedra.op.EdgeVertexGen;
import com.kreative.polyhedra.op.Expand;
import com.kreative.polyhedra.op.GyroVertexGen;
import com.kreative.polyhedra.op.Reflect;
import com.kreative.polyhedra.op.Scale;
import com.kreative.polyhedra.op.Snub;
import com.kreative.polyhedra.op.Truncate;

public class ArchimedeanSolid extends PolyhedronGen {
	public static enum FormSpecifier {
		TRUNCATED_TETRAHEDRON (
			1.1726039399558573886, // sqrt(22)/4
			1.0606601717798212866, // sqrt(2)*3/4
			"truncatedtetrahedron", "tut", "tt", "u2", "1"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Truncate(null, Truncate.TruncatedVertexGen.FIXED_DISTANCE_ALONG_EDGE, 1, c),
					new Tetrahedron(Tetrahedron.SizeSpecifier.EDGE_LENGTH, 3, c)
				);
			}
		},
		CUBOCTAHEDRON (
			1, 0.86602540378443864676, // 1; sqrt(3)/2
			"cuboctahedron", "coe", "co", "u7", "2"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Ambo(c),
					new Cube(Cube.SizeSpecifier.EDGE_LENGTH, 1.4142135623730950488, c) // sqrt(2)
				);
			}
		},
		TRUNCATED_OCTAHEDRON (
			1.5811388300841896660, 1.5, // sqrt(10)/2; 3/2
			"truncatedoctahedron", "toe", "to", "u8", "3"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Truncate(null, Truncate.TruncatedVertexGen.FIXED_DISTANCE_ALONG_EDGE, 1, c),
					new Octahedron(Octahedron.SizeSpecifier.EDGE_LENGTH, 3, c)
				);
			}
		},
		TRUNCATED_CUBE (
			1.7788236456639244509, // sqrt(sqrt(2)*4+7)/2
			1.7071067811865475244, // sqrt(2)/2+1
			"truncatedcube", "truncatedhexahedron", "tic", "tc", "u9", "4"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Truncate(null, Truncate.TruncatedVertexGen.FIXED_DISTANCE_ALONG_EDGE, 0.70710678118654752440, c), // sqrt(2)/2
					new Cube(Cube.SizeSpecifier.EDGE_LENGTH, 2.4142135623730950488, c) // sqrt(2)+1
				);
			}
		},
		SMALL_RHOMBICUBOCTAHEDRON (
			1.3989663259659067020, // sqrt(sqrt(2)*2+5)/2
			1.3065629648763765279, // sqrt((sqrt(2)+2)*2)/2
			"smallrhombicuboctahedron", "rhombicuboctahedron", "sirco", "srco", "rco", "u10", "5"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Expand(Expand.ExpandedFaceGen.FIXED_DISTANCE_FROM_VERTEX, 1, c, c),
					new Cube(Cube.SizeSpecifier.EDGE_LENGTH, 2.4142135623730950488, c) // sqrt(2)+1
				);
			}
		},
		GREAT_RHOMBICUBOCTAHEDRON (
			2.3176109128927665138, // sqrt(sqrt(2)*6+13)/2
			2.2630334384537146236, // sqrt((sqrt(2)+2)*6)/2
			"greatrhombicuboctahedron", "truncatedcuboctahedron", "girco", "grco", "tco", "u11", "6"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Bevel(Bevel.VertexGen.FIXED_DISTANCE_FROM_EDGE_ALONG_APOTHEM, 0.70710678118654752440, c, c), // sqrt(2)/2
					new Cube(Cube.SizeSpecifier.EDGE_LENGTH, 3.8284271247461900976, c) // sqrt(2)*2+1
				);
			}
		},
		SNUB_CUBE_LAEVO (
			1.3437133737446017013, // sqrt(3*(10+cbrt(199+3*sqrt(33))+cbrt(199−3*sqrt(33))))/6
			1.2472231679936432518, // sqrt(3*(7+cbrt(199+3*sqrt(33))+cbrt(199−3*sqrt(33))))/6
			"snubcubelaevo", "snubcuboctahedronlaevo", "elsnic", "lsnc", "7"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(new Reflect(), SNUB_CUBE_DEXTRO.gen(c));
			}
		},
		SNUB_CUBE_DEXTRO (
			1.3437133737446017013, // sqrt(3*(10+cbrt(199+3*sqrt(33))+cbrt(199−3*sqrt(33))))/6
			1.2472231679936432518, // sqrt(3*(7+cbrt(199+3*sqrt(33))+cbrt(199−3*sqrt(33))))/6
			"snubcubedextro", "snubcuboctahedrondextro", "arsnic", "rsnc", "8",
			"snubcube", "snubcuboctahedron", "snic", "snc", "u12"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Snub(
						new GyroVertexGen.TwistAngle(16.4675604003863613990), // ArcCos[Root[8x^6-4x^4-2x^2-1,2]]*180/Pi
						new EdgeVertexGen.FaceOffset(0.64261350892596209348), c // Root[64x^6+192x^5+176x^4+32x^3-60x^2-44x-11,2]
					),
					new Cube(Cube.SizeSpecifier.EDGE_LENGTH, 1, c)
				);
			}
		},
		ICOSIDODECAHEDRON (
			1.6180339887498948482, // (sqrt(5)+1)/2
			1.5388417685876267013, // sqrt(sqrt(5)*2+5)/2
			"icosidodecahedron", "id", "u24", "9"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Ambo(c),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, 1.2360679774997896964, c) // sqrt(5)-1
				);
			}
		},
		TRUNCATED_ICOSAHEDRON (
			2.4780186590676155376, // sqrt((sqrt(5)*9+29)*2)/4
			2.4270509831248422723, // (sqrt(5)+1)*3/4
			"truncatedicosahedron", "ti", "u25", "10",
			"soccerball", "football", "futbol", "buckyball"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Truncate(null, Truncate.TruncatedVertexGen.FIXED_DISTANCE_ALONG_EDGE, 1, c),
					new Icosahedron(Icosahedron.SizeSpecifier.EDGE_LENGTH, 3, c)
				);
			}
		},
		TRUNCATED_DODECAHEDRON (
			2.9694490158633984670, // sqrt((sqrt(5)*15+37)*2)/4
			2.9270509831248422723, // (sqrt(5)*3+5)/4
			"truncateddodecahedron", "tid", "td", "u26", "11"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Truncate(null, Truncate.TruncatedVertexGen.FIXED_DISTANCE_ALONG_EDGE, 0.61803398874989484820, c), // (sqrt(5)-1)/2
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, 2.2360679774997896964, c) // sqrt(5)
				);
			}
		},
		SMALL_RHOMBICOSIDODECAHEDRON (
			2.2329505094156900495, // sqrt(sqrt(5)*4+11)/2
			2.1762508994828215111, // sqrt((sqrt(5)*2+5)*2)/2
			"smallrhombicosidodecahedron", "rhombicosidodecahedron", "srid", "rid", "u27", "12"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Expand(Expand.ExpandedFaceGen.FIXED_DISTANCE_FROM_VERTEX, 0.726542528005360885895, c, c), // sqrt(5-2*sqrt(5))
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, 1.8541019662496845446, c) // (sqrt(5)-1)*3/2
				);
			}
		},
		GREAT_RHOMBICOSIDODECAHEDRON (
			3.8023944998512935848, // sqrt(sqrt(5)*12+31)/2
			3.7693771279217166027, // sqrt((sqrt(5)*2+5)*6)/2
			"greatrhombicosidodecahedron", "truncatedicosidodecahedron", "grid", "tid", "u28", "13"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Bevel(Bevel.VertexGen.FIXED_DISTANCE_FROM_EDGE_ALONG_APOTHEM, 0.58778525229247312917, c, c), // sqrt((5-sqrt(5))/2)/2
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, 3.0901699437494742410, c) // (sqrt(5)-1)*5/2
				);
			}
		},
		SNUB_DODECAHEDRON_LAEVO (
			// phi = (sqrt(5)+1)/2
			// xi = cbrt((phi+sqrt(phi−5/27))/2)+cbrt((phi−sqrt(phi−5/27))/2)
			2.1558373751156397018, // phi*sqrt(xi*(xi+phi)+(3−phi))/2
			2.0970538352520879924, // phi*sqrt(xi*(xi+phi)+1)/2
			"snubdodecahedronlaevo", "snubicosidodecahedronlaevo", "elsnid", "lsnd", "14"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(new Reflect(), SNUB_DODECAHEDRON_DEXTRO.gen(c));
			}
		},
		SNUB_DODECAHEDRON_DEXTRO (
			// phi = (sqrt(5)+1)/2
			// xi = cbrt((phi+sqrt(phi−5/27))/2)+cbrt((phi−sqrt(phi−5/27))/2)
			2.1558373751156397018, // phi*sqrt(xi*(xi+phi)+(3−phi))/2
			2.0970538352520879924, // phi*sqrt(xi*(xi+phi)+1)/2
			"snubdodecahedrondextro", "snubicosidodecahedrondextro", "arsnid", "rsnd", "15",
			"snubdodecahedron", "snubicosidodecahedron", "snid", "snd", "u29"
		) {
			public PolyhedronGen gen(Color c) {
				return new Construct(
					new Snub(
						new GyroVertexGen.TwistAngle(13.106403376935798910), // 54-ArcCos[Root[64x^6+64x^5+800x^4+240x^3-800x^2-306x+59,3]]*90/Pi
						new EdgeVertexGen.FaceOffsetFromOrigin(1.980915947281840739), c // Root[512000x^12-1920000x^10-460800x^8+424000x^6+53040x^4-20600x^2+961,8]
					),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, 1, c)
				);
			}
		};
		public final double circumradiusFactor;
		public final double midradiusFactor;
		private final List<String> names;
		private FormSpecifier(double cf, double mf, String... names) {
			this.circumradiusFactor = cf;
			this.midradiusFactor = mf;
			this.names = Arrays.asList(names);
		}
		public abstract PolyhedronGen gen(Color color);
		public static FormSpecifier forIndex(int index) {
			switch (index) {
				case  1: return TRUNCATED_TETRAHEDRON;
				case  2: return CUBOCTAHEDRON;
				case  3: return TRUNCATED_OCTAHEDRON;
				case  4: return TRUNCATED_CUBE;
				case  5: return SMALL_RHOMBICUBOCTAHEDRON;
				case  6: return GREAT_RHOMBICUBOCTAHEDRON;
				case  7: return SNUB_CUBE_LAEVO;
				case  8: return SNUB_CUBE_DEXTRO;
				case  9: return ICOSIDODECAHEDRON;
				case 10: return TRUNCATED_ICOSAHEDRON;
				case 11: return TRUNCATED_DODECAHEDRON;
				case 12: return SMALL_RHOMBICOSIDODECAHEDRON;
				case 13: return GREAT_RHOMBICOSIDODECAHEDRON;
				case 14: return SNUB_DODECAHEDRON_LAEVO;
				case 15: return SNUB_DODECAHEDRON_DEXTRO;
				default: return null;
			}
		}
		public static FormSpecifier forName(String name) {
			if (name != null) {
				name = name.replaceAll("[^\\p{L}\\p{M}\\p{N}]+", "").toLowerCase();
				for (FormSpecifier form : values()) {
					if (form.names.contains(name)) {
						return form;
					}
				}
			}
			return null;
		}
	}
	
	public static enum SizeSpecifier {
		CIRCUMRADIUS {
			public double toScale(FormSpecifier form, double radius) { return radius / form.circumradiusFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.circumradiusFactor / scale; }
		},
		MIDRADIUS {
			public double toScale(FormSpecifier form, double radius) { return radius / form.midradiusFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.midradiusFactor / scale; }
		},
		EDGE_LENGTH {
			public double toScale(FormSpecifier form, double length) { return length; }
			public double fromScale(FormSpecifier form, double scale) { return scale; }
		};
		public abstract double toScale(FormSpecifier form, double size);
		public abstract double fromScale(FormSpecifier form, double scale);
	}
	
	private final PolyhedronGen gen;
	private final PolyhedronOp op;
	
	public ArchimedeanSolid(FormSpecifier form, SizeSpecifier spec, double size, Color color) {
		this.gen = form.gen(color);
		this.op = new Scale(spec.toScale(form, size));
	}
	
	public Polyhedron gen() {
		return op.op(gen.gen());
	}
	
	public static class Factory extends PolyhedronGen.Factory<ArchimedeanSolid> {
		public String name() { return "ArchimedeanSolid"; }
		
		public ArchimedeanSolid parse(String[] args) {
			FormSpecifier form = FormSpecifier.TRUNCATED_TETRAHEDRON;
			SizeSpecifier spec = SizeSpecifier.CIRCUMRADIUS;
			double size = 1;
			Color c = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					String name = args[argi++];
					FormSpecifier f = FormSpecifier.forName(name);
					if (f == null) f = FormSpecifier.forIndex(parseInt(name, 0));
					if (f != null) form = f;
				} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
					spec = SizeSpecifier.CIRCUMRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
					spec = SizeSpecifier.MIDRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					spec = SizeSpecifier.EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					c = parseColor(args[argi++], c);
				} else {
					FormSpecifier f = FormSpecifier.forName(arg);
					if (f == null) f = FormSpecifier.forIndex(parseInt(arg, 0));
					if (f == null) return null;
					form = f;
				}
			}
			return new ArchimedeanSolid(form, spec, size, c);
		}
		
		public Option[] options() {
			final String toc = (
				"which Archimedean solid (by index or name)"
				+ "\n\t\t 1  U2   tt    truncated tetrahedron"
				+ "\n\t\t 2  U7   co    cuboctahedron"
				+ "\n\t\t 3  U8   to    truncated octahedron"
				+ "\n\t\t 4  U9   tc    truncated cube"
				+ "\n\t\t 5  U10  srco  small rhombicuboctahedron (rhombicuboctahedron)"
				+ "\n\t\t 6  U11  grco  great rhombicuboctahedron (truncated cuboctahedron)"
				+ "\n\t\t 7  U12  lsnc  snub cube (laevo)"
				+ "\n\t\t 8  U12  rsnc  snub cube (dextro)"
				+ "\n\t\t 9  U24  id    icosidodecahedron"
				+ "\n\t\t10  U25  ti    truncated icosahedron"
				+ "\n\t\t11  U26  td    truncated dodecahedron"
				+ "\n\t\t12  U27  srid  small rhombicosidodecahedron (rhombicosidodecahedron)"
				+ "\n\t\t13  U28  grid  great rhombicosidodecahedron (truncated icosidodecahedron)"
				+ "\n\t\t14  U29  lsnd  snub dodecahedron (laevo)"
				+ "\n\t\t15  U29  rsnd  snub dodecahedron (dextro)"
			);
			return new Option[] {
				new Option("n", Type.INT, toc),
				new Option("r", Type.REAL, "radius of circumscribed sphere", "m","a"),
				new Option("m", Type.REAL, "radius of sphere tangent to edges", "r","a"),
				new Option("a", Type.REAL, "edge length", "r","m"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}