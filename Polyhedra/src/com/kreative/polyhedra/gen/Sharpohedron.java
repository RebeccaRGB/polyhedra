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
		TRIAETOS_TETRAHEDRON (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_TETRAHEDRON,
			CatalanSolid.FormSpecifier.TRIAKIS_TETRAHEDRON,
			"triaetostetrahedron", "tat", "udu2", "s1", "1"
		),
		DELTOIDAL_CUBOCTAHEDRON (
			ArchimedeanSolid.FormSpecifier.CUBOCTAHEDRON,
			CatalanSolid.FormSpecifier.RHOMBIC_DODECAHEDRON,
			"deltoidalcuboctahedron", "dco", "udu7", "s2", "2"
		),
		TETRAETOS_HEXAHEDRON (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_OCTAHEDRON,
			CatalanSolid.FormSpecifier.TETRAKIS_HEXAHEDRON,
			"tetraetoshexahedron", "tah", "udu8", "s3", "3"
		),
		TRIAETOS_OCTAHEDRON (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_CUBE,
			CatalanSolid.FormSpecifier.TRIAKIS_OCTAHEDRON,
			"triaetosoctahedron", "tao", "udu9", "s4", "4"
		),
		RHOMBIDELTOIDAL_CUBOCTAHEDRON (
			ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICUBOCTAHEDRON,
			CatalanSolid.FormSpecifier.DELTOIDAL_ICOSITETRAHEDRON,
			"rhombideltoidalcuboctahedron", "rdco", "udu10", "s5", "5"
		),
		DELTOIDAL_RHOMBICUBOCTAHEDRON (
			ArchimedeanSolid.FormSpecifier.GREAT_RHOMBICUBOCTAHEDRON,
			CatalanSolid.FormSpecifier.DISDYAKIS_DODECAHEDRON,
			"deltoidalrhombicuboctahedron", "drco", "udu11", "s6", "6"
		),
		RHOMBISNUB_TETRADELTOIDAL_HEXAHEDRON_LAEVO (
			ArchimedeanSolid.FormSpecifier.SNUB_CUBE_LAEVO,
			CatalanSolid.FormSpecifier.PENTAGONAL_ICOSITETRAHEDRON_DEXTRO,
			"rhombisnubtetradeltoidalhexahedronlaevo", "lrsntdh", "s7", "7"
		),
		RHOMBISNUB_TETRADELTOIDAL_HEXAHEDRON_DEXTRO (
			ArchimedeanSolid.FormSpecifier.SNUB_CUBE_DEXTRO,
			CatalanSolid.FormSpecifier.PENTAGONAL_ICOSITETRAHEDRON_LAEVO,
			"rhombisnubtetradeltoidalhexahedrondextro", "rrsntdh", "s8", "8",
			"rhombisnubtetradeltoidalhexahedron", "rsntdh", "udu12"
		),
		DELTOIDAL_ICOSIDODECAHEDRON (
			ArchimedeanSolid.FormSpecifier.ICOSIDODECAHEDRON,
			CatalanSolid.FormSpecifier.RHOMBIC_TRIACONTAHEDRON,
			"deltoidalicosidodecahedron", "did", "udu24", "s9", "9"
		),
		PENTAETOS_DODECAHEDRON (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_ICOSAHEDRON,
			CatalanSolid.FormSpecifier.PENTAKIS_DODECAHEDRON,
			"pentaetosdodecahedron", "pad", "udu25", "s10", "10"
		),
		TRIAETOS_ICOSAHEDRON (
			ArchimedeanSolid.FormSpecifier.TRUNCATED_DODECAHEDRON,
			CatalanSolid.FormSpecifier.TRIAKIS_ICOSAHEDRON,
			"triaetosicosahedron", "tai", "udu26", "s11", "11"
		),
		SMALL_DELTOIDAL_RHOMBICOSIDODECAHEDRON (
			ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
			CatalanSolid.FormSpecifier.DELTOIDAL_HEXECONTAHEDRON,
			"smalldeltoidalrhombicosidodecahedron", "sdrid", "udu27", "s12", "12"
		),
		GREAT_DELTOIDAL_RHOMBICOSIDODECAHEDRON (
			ArchimedeanSolid.FormSpecifier.GREAT_RHOMBICOSIDODECAHEDRON,
			CatalanSolid.FormSpecifier.DISDYAKIS_TRIACONTAHEDRON,
			"greatdeltoidalrhombicosidodecahedron", "gdrid", "udu28", "s13", "13"
		),
		RHOMBISNUB_PENTADELTOIDAL_DODECAHEDRON_LAEVO (
			ArchimedeanSolid.FormSpecifier.SNUB_DODECAHEDRON_LAEVO,
			CatalanSolid.FormSpecifier.PENTAGONAL_HEXECONTAHEDRON_DEXTRO,
			"rhombisnubpentadeltoidaldodecahedronlaevo", "lrsnpdd", "s14", "14"
		),
		RHOMBISNUB_PENTADELTOIDAL_DODECAHEDRON_DEXTRO (
			ArchimedeanSolid.FormSpecifier.SNUB_DODECAHEDRON_DEXTRO,
			CatalanSolid.FormSpecifier.PENTAGONAL_HEXECONTAHEDRON_LAEVO,
			"rhombisnubpentadeltoidaldodecahedrondextro", "rrsnpdd", "s15", "15",
			"rhombisnubpentadeltoidaldodecahedron", "rsnpdd", "udu29"
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
				case  1: return TRIAETOS_TETRAHEDRON;
				case  2: return DELTOIDAL_CUBOCTAHEDRON;
				case  3: return TETRAETOS_HEXAHEDRON;
				case  4: return TRIAETOS_OCTAHEDRON;
				case  5: return RHOMBIDELTOIDAL_CUBOCTAHEDRON;
				case  6: return DELTOIDAL_RHOMBICUBOCTAHEDRON;
				case  7: return RHOMBISNUB_TETRADELTOIDAL_HEXAHEDRON_LAEVO;
				case  8: return RHOMBISNUB_TETRADELTOIDAL_HEXAHEDRON_DEXTRO;
				case  9: return DELTOIDAL_ICOSIDODECAHEDRON;
				case 10: return PENTAETOS_DODECAHEDRON;
				case 11: return TRIAETOS_ICOSAHEDRON;
				case 12: return SMALL_DELTOIDAL_RHOMBICOSIDODECAHEDRON;
				case 13: return GREAT_DELTOIDAL_RHOMBICOSIDODECAHEDRON;
				case 14: return RHOMBISNUB_PENTADELTOIDAL_DODECAHEDRON_LAEVO;
				case 15: return RHOMBISNUB_PENTADELTOIDAL_DODECAHEDRON_DEXTRO;
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
			FormSpecifier form = FormSpecifier.TRIAETOS_TETRAHEDRON;
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
				"which Sharpohedron (by index or name)"
				+ "\n\t\t 1  UdU2   tat      triaetos tetrahedron"
				+ "\n\t\t 2  UdU7   dco      deltoidal cuboctahedron"
				+ "\n\t\t 3  UdU8   tah      tetraetos hexahedron"
				+ "\n\t\t 4  UdU9   tao      triaetos octahedron"
				+ "\n\t\t 5  UdU10  rdco     rhombideltoidal cuboctahedron"
				+ "\n\t\t 6  UdU11  drco     deltoidal rhombicuboctahedron"
				+ "\n\t\t 7  UdU12  lrsntdh  rhombisnub tetradeltoidal hexahedron (laevo)"
				+ "\n\t\t 8  UdU12  rrsntdh  rhombisnub tetradeltoidal hexahedron (dextro)"
				+ "\n\t\t 9  UdU24  did      deltoidal icosidodecahedron"
				+ "\n\t\t10  UdU25  pad      pentaetos dodecahedron"
				+ "\n\t\t11  UdU26  tai      triaetos icosahedron"
				+ "\n\t\t12  UdU27  sdrid    small deltoidal rhombicosidodecahedron"
				+ "\n\t\t13  UdU28  gdrid    great deltoidal rhombicosidodecahedron"
				+ "\n\t\t14  UdU29  lrsnpdd  rhombisnub pentadeltoidal dodecahedron (laevo)"
				+ "\n\t\t15  UdU29  rrsnpdd  rhombisnub pentadeltoidal dodecahedron (dextro)"
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