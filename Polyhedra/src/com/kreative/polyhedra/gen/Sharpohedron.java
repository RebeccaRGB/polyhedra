package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.op.ConvexHull;
import com.kreative.polyhedra.op.Dual;
import com.kreative.polyhedra.op.Dual.RescaleMode;
import com.kreative.polyhedra.op.FaceVertexGen;

public class Sharpohedron extends PolyhedronGen {
	public static enum FormSpecifier {
		S1 (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_TETRAHEDRON,
			CatalanSolid.FormSpecifier.TRIAKIS_TETRAHEDRON,
			"udu2", "s1", "1"
		),
		S2 (
			ArchimedeanSolid.FormSpecifier.CUBOCTAHEDRON,
			CatalanSolid.FormSpecifier.RHOMBIC_DODECAHEDRON,
			"udu7", "s2", "2"
		),
		S3 (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_OCTAHEDRON,
			CatalanSolid.FormSpecifier.TETRAKIS_HEXAHEDRON,
			"udu8", "s3", "3"
		),
		S4 (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_CUBE,
			CatalanSolid.FormSpecifier.TRIAKIS_OCTAHEDRON,
			"udu9", "s4", "4"
		),
		S5 (
			ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICUBOCTAHEDRON,
			CatalanSolid.FormSpecifier.DELTOIDAL_ICOSITETRAHEDRON,
			"udu10", "s5", "5"
		),
		S6 (
			ArchimedeanSolid.FormSpecifier.GREAT_RHOMBICUBOCTAHEDRON,
			CatalanSolid.FormSpecifier.DISDYAKIS_DODECAHEDRON,
			"udu11", "s6", "6"
		),
		S7 (
			ArchimedeanSolid.FormSpecifier.SNUB_CUBE_LAEVO,
			CatalanSolid.FormSpecifier.PENTAGONAL_ICOSITETRAHEDRON_DEXTRO,
			"s7", "7"
		),
		S8 (
			ArchimedeanSolid.FormSpecifier.SNUB_CUBE_DEXTRO,
			CatalanSolid.FormSpecifier.PENTAGONAL_ICOSITETRAHEDRON_LAEVO,
			"s8", "8",
			"udu12"
		),
		S9 (
			ArchimedeanSolid.FormSpecifier.ICOSIDODECAHEDRON,
			CatalanSolid.FormSpecifier.RHOMBIC_TRIACONTAHEDRON,
			"udu24", "s9", "9"
		),
		S10 (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_ICOSAHEDRON,
			CatalanSolid.FormSpecifier.PENTAKIS_DODECAHEDRON,
			"udu25", "s10", "10"
		),
		S11 (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_DODECAHEDRON,
			CatalanSolid.FormSpecifier.TRIAKIS_ICOSAHEDRON,
			"udu26", "s11", "11"
		),
		S12 (
			ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
			CatalanSolid.FormSpecifier.DELTOIDAL_HEXECONTAHEDRON,
			"udu27", "s12", "12"
		),
		S13 (
			ArchimedeanSolid.FormSpecifier.GREAT_RHOMBICOSIDODECAHEDRON,
			CatalanSolid.FormSpecifier.DISDYAKIS_TRIACONTAHEDRON,
			"udu28", "s13", "13"
		),
		S14 (
			ArchimedeanSolid.FormSpecifier.SNUB_DODECAHEDRON_LAEVO,
			CatalanSolid.FormSpecifier.PENTAGONAL_HEXECONTAHEDRON_DEXTRO,
			"s14", "14"
		),
		S15 (
			ArchimedeanSolid.FormSpecifier.SNUB_DODECAHEDRON_DEXTRO,
			CatalanSolid.FormSpecifier.PENTAGONAL_HEXECONTAHEDRON_LAEVO,
			"s15", "15",
			"udu29"
		);
		public final ArchimedeanSolid.FormSpecifier archimedeanComponent;
		public final CatalanSolid.FormSpecifier catalanComponent;
		private final List<String> names;
		private FormSpecifier(ArchimedeanSolid.FormSpecifier a, CatalanSolid.FormSpecifier c, String... names) {
			this.archimedeanComponent = a;
			this.catalanComponent = c;
			this.names = Arrays.asList(names);
		}
		public static FormSpecifier forIndex(int index) {
			switch (index) {
				case  1: return S1;
				case  2: return S2;
				case  3: return S3;
				case  4: return S4;
				case  5: return S5;
				case  6: return S6;
				case  7: return S7;
				case  8: return S8;
				case  9: return S9;
				case 10: return S10;
				case 11: return S11;
				case 12: return S12;
				case 13: return S13;
				case 14: return S14;
				case 15: return S15;
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
	
	private final FormSpecifier form;
	private final double midradius;
	private final Color color;
	
	public Sharpohedron(FormSpecifier form, double midradius, Color color) {
		this.form = form;
		this.midradius = midradius;
		this.color = color;
	}
	
	public Polyhedron gen() {
		PolyhedronGen a = new ArchimedeanSolid(form.archimedeanComponent, ArchimedeanSolid.SizeSpecifier.MIDRADIUS, midradius, color);
		Dual d = new Dual(new FaceVertexGen.PolarReciprocal(midradius), RescaleMode.NONE, color);
		return new Construct(new ConvexHull(color), new Compose(a, new Construct(d, a))).gen();
	}
	
	public static class Factory extends PolyhedronGen.Factory<Sharpohedron> {
		public String name() { return "Sharpohedron"; }
		
		public Sharpohedron parse(String[] args) {
			FormSpecifier form = FormSpecifier.S1;
			double midradius = 1;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					String name = args[argi++];
					FormSpecifier f = FormSpecifier.forName(name);
					if (f == null) f = FormSpecifier.forIndex(parseInt(name, 0));
					if (f != null) form = f;
				} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
					midradius = parseDouble(args[argi++], midradius);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					FormSpecifier f = FormSpecifier.forName(arg);
					if (f == null) f = FormSpecifier.forIndex(parseInt(arg, 0));
					if (f == null) return null;
					form = f;
				}
			}
			return new Sharpohedron(form, midradius, color);
		}
		
		public Option[] options() {
			final String toc = (
				"which Archimedean and Catalan solid (by index or name)"
				+ "\n\t\t 1  UdU2   truncated tetrahedron + triakis tetrahedron"
				+ "\n\t\t 2  UdU7   cuboctahedron + rhombic dodecahedron"
				+ "\n\t\t 3  UdU8   truncated octahedron + tetrakis hexahedron"
				+ "\n\t\t 4  UdU9   truncated cube + triakis octahedron"
				+ "\n\t\t 5  UdU10  small rhombicuboctahedron (rhombicuboctahedron) + deltoidal icositetrahedron"
				+ "\n\t\t 6  UdU11  great rhombicuboctahedron (truncated cuboctahedron) + disdyakis dodecahedron"
				+ "\n\t\t 7  UdU12  snub cube (laevo) + pentagonal icositetrahedron (dextro)"
				+ "\n\t\t 8  UdU12  snub cube (dextro) + pentagonal icositetrahedron (laevo)"
				+ "\n\t\t 9  UdU24  icosidodecahedron + rhombic triacontahedron"
				+ "\n\t\t10  UdU25  truncated icosahedron + pentakis dodecahedron"
				+ "\n\t\t11  UdU26  truncated dodecahedron + triakis icosahedron"
				+ "\n\t\t12  UdU27  small rhombicosidodecahedron (rhombicosidodecahedron) + deltoidal hexecontahedron"
				+ "\n\t\t13  UdU28  great rhombicosidodecahedron (truncated icosidodecahedron) + disdyakis triacontahedron"
				+ "\n\t\t14  UdU29  snub dodecahedron (laevo) + pentagonal hexecontahedron (dextro)"
				+ "\n\t\t15  UdU29  snub dodecahedron (dextro) + pentagonal hexecontahedron (laevo)"
			);
			return new Option[] {
				new Option("n", Type.INT, toc),
				new Option("m", Type.REAL, "radius of sphere tangent to edges"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}