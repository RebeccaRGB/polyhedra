package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.op.Augment;
import com.kreative.polyhedra.op.Chain;
import com.kreative.polyhedra.op.ConvexHull;
import com.kreative.polyhedra.op.FacePredicate;
import com.kreative.polyhedra.op.FaceVertexGen;
import com.kreative.polyhedra.op.Kis;
import com.kreative.polyhedra.op.RemoveFaces;
import com.kreative.polyhedra.op.RemoveVertices;
import com.kreative.polyhedra.op.VertexPredicate;

public class JohnsonSolid extends PolyhedronGen {
	public static enum FormSpecifier {
		SQUARE_PYRAMID ("squarepyramid", "equilateralsquarepyramid", "squippy", "spy", "y4", "j1", "1") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_PYRAMID ("pentagonalpyramid", "equilateralpentagonalpyramid", "peppy", "ppy", "y5", "j2", "2") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, 0, c, c, c
				);
			}
		},
		TRIANGULAR_CUPOLA ("triangularcupola", "equilateraltriangularcupola", "tricu", "tcu", "u3", "j3", "3") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, 0, c, c, c
				);
			}
		},
		SQUARE_CUPOLA ("squarecupola", "equilateralsquarecupola", "squacu", "scu", "u4", "j4", "4") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_CUPOLA ("pentagonalcupola", "equilateralpentagonalcupola", "pecu", "pcu", "u5", "j5", "5") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_ROTUNDA ("pentagonalrotunda", "pero", "pro", "r5", "j6", "6") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, null, 0, c, c, c
				);
			}
		},
		ELONGATED_TRIANGULAR_PYRAMID ("elongatedtriangularpyramid", "etripy", "etpy", "py3", "j7", "7") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					3, 1, a * 0.57735026918962576451, // sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, a, c, c, c
				);
			}
		},
		ELONGATED_SQUARE_PYRAMID ("elongatedsquarepyramid", "esquipy", "espy", "py4", "j8", "8") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_PYRAMID ("elongatedpentagonalpyramid", "epeppy", "eppy", "py5", "j9", "9") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, a, c, c, c
				);
			}
		},
		GYROELONGATED_SQUARE_PYRAMID ("gyroelongatedsquarepyramid", "gyesp", "gyespy", "gesp", "gespy", "ay4", "j10", "10") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, a * 0.84089641525371454303, c, c, c // 1/sqrt(sqrt(2))
				);
			}
		},
		GYROELONGATED_PENTAGONAL_PYRAMID ("gyroelongatedpentagonalpyramid", "gyepip", "gyepipy", "gepp", "geppy", "ay5", "j11", "11") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, a * 0.85065080835203993218, c, c, c // sqrt((sqrt(5)+5)/10)
				);
			}
		},
		TRIANGULAR_BIPYRAMID ("triangularbipyramid", "tridpy", "tbpy", "yy3", "j12", "12") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					3, 1, a * 0.57735026918962576451, // sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, 0, c, c
				);
			}
		},
		PENTAGONAL_BIPYRAMID ("pentagonalbipyramid", "pedpy", "pbpy", "yy5", "j13", "13") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, 0, c, c
				);
			}
		},
		ELONGATED_TRIANGULAR_BIPYRAMID ("elongatedtriangularbipyramid", "etidpy", "etbpy", "ypy3", "j14", "14") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					3, 1, a * 0.57735026918962576451, // sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, a, c, c
				);
			}
		},
		ELONGATED_SQUARE_BIPYRAMID ("elongatedsquarebipyramid", "esquidpy", "esbpy", "ypy4", "j15", "15") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, a, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_BIPYRAMID ("elongatedpentagonalbipyramid", "epedpy", "epbpy", "ypy5", "j16", "16") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, a, c, c
				);
			}
		},
		GYROELONGATED_SQUARE_BIPYRAMID ("gyroelongatedsquarebipyramid", "gyesqidpy", "gesbpy", "yay4", "j17", "17") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, a * 0.84089641525371454303, c, c // 1/sqrt(sqrt(2))
				);
			}
		},
		ELONGATED_TRIANGULAR_CUPOLA ("elongatedtriangularcupola", "etcu", "pu3", "j18", "18") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, a, c, c, c
				);
			}
		},
		ELONGATED_SQUARE_CUPOLA ("elongatedsquarecupola", "escu", "pu4", "j19", "19") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_CUPOLA ("elongatedpentagonalcupola", "epcu", "pu5", "j20", "20") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_ROTUNDA ("elongatedpentagonalrotunda", "epro", "pr5", "j21", "21") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, null, 0, c, c, c
				);
			}
		},
		GYROELONGATED_TRIANGULAR_CUPOLA ("gyroelongatedtriangularcupola", "gyetcu", "getcu", "au3", "j22", "22") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					true, a * 0.85559967716735219297, c, c, c // sqrt(sqrt(3)-1)
				);
			}
		},
		GYROELONGATED_SQUARE_CUPOLA ("gyroelongatedsquarecupola", "gyescu", "gescu", "au4", "j23", "23") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, a * 0.86029556986297156644, c, c, c // sqrt(sqrt((sqrt(2)*7+10)/2)-sqrt(2)-1)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_CUPOLA ("gyroelongatedpentagonalcupola", "gyepcu", "gepcu", "au5", "j24", "24") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, a * 0.86239700385945848345, c, c, c // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_ROTUNDA ("gyroelongatedpentagonalrotunda", "gyepro", "gepro", "ar5", "j25", "25") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					true, a * 0.86239700385945848345, null, 0, c, c, c // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
				);
			}
		},
		GYROBIFASTIGIUM ("gyrobifastigium", "gybef", "gbf", "pgp3", "j26", "26") {
			public PolyhedronGen gen(double a, Color c) {
				return new Constant(new Polyhedron(
					Arrays.asList(
						new Point3D(0, a*0.86602540378443864676, +a/2), // sqrt(3)/2
						new Point3D(0, a*0.86602540378443864676, -a/2), // sqrt(3)/2
						new Point3D(+a/2, -a*0.86602540378443864676, 0), // sqrt(3)/2
						new Point3D(-a/2, -a*0.86602540378443864676, 0), // sqrt(3)/2
						new Point3D(+a/2, 0, +a/2),
						new Point3D(+a/2, 0, -a/2),
						new Point3D(-a/2, 0, +a/2),
						new Point3D(-a/2, 0, -a/2)
					),
					Arrays.asList(
						Arrays.asList(0, 1, 7, 6),
						Arrays.asList(1, 0, 4, 5),
						Arrays.asList(4, 6, 3, 2),
						Arrays.asList(7, 5, 2, 3),
						Arrays.asList(0, 6, 4),
						Arrays.asList(1, 5, 7),
						Arrays.asList(2, 5, 4),
						Arrays.asList(3, 6, 7)
					),
					Arrays.asList(c, c, c, c, c, c, c, c)
				));
			}
		},
		TRIANGULAR_ORTHOBICUPOLA ("triangularorthobicupola", "tobcu", "uu3", "j27", "27") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, false, 0, c, c, c
				);
			}
		},
		SQUARE_ORTHOBICUPOLA ("squareorthobicupola", "squobcu", "sobcu", "uu4", "j28", "28") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, false, 0, c, c, c
				);
			}
		},
		SQUARE_GYROBICUPOLA ("squaregyrobicupola", "squigybcu", "sgbcu", "ugu4", "j29", "29") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_ORTHOBICUPOLA ("pentagonalorthobicupola", "pobcu", "uu5", "j30", "30") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_GYROBICUPOLA ("pentagonalgyrobicupola", "pegybcu", "pgbcu", "ugu5", "j31", "31") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_ORTHOCUPOLAROTUNDA ("pentagonalorthocupolarotunda", "pocuro", "ur5", "j32", "32") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, Rotunda.Extension.ORTHOCUPOLA, a * 0.52573111211913360603, c, c, c // sqrt((5-sqrt(5))/10)
				);
			}
		},
		PENTAGONAL_GYROCUPOLAROTUNDA ("pentagonalgyrocupolarotunda", "pegycuro", "pgcuro", "ugr5", "j33", "33") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, Rotunda.Extension.GYROCUPOLA, a * 0.52573111211913360603, c, c, c // sqrt((5-sqrt(5))/10)
				);
			}
		},
		PENTAGONAL_ORTHOBIROTUNDA ("pentagonalorthobirotunda", "pobro", "rr5", "j34", "34") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, Rotunda.Extension.ORTHOROTUNDA, a * 1.3763819204711735382, c, c, c // sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		ELONGATED_TRIANGULAR_ORTHOBICUPOLA ("elongatedtriangularorthobicupola", "etobcu", "upu3", "j35", "35") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, false, a, c, c, c
				);
			}
		},
		ELONGATED_TRIANGULAR_GYROBICUPOLA ("elongatedtriangulargyrobicupola", "etigybcu", "etgbcu", "upgu3", "j36", "36") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					true, false, a, c, c, c
				);
			}
		},
		ELONGATED_SQUARE_GYROBICUPOLA ("elongatedsquaregyrobicupola", "esquigybcu", "esgbcu", "upgu4", "j37", "37") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_ORTHOBICUPOLA ("elongatedpentagonalorthobicupola", "epobcu", "upu5", "j38", "38") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_GYROBICUPOLA ("elongatedpentagonalgyrobicupola", "epigybcu", "epgbcu", "upgu5", "j39", "39") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_ORTHOCUPOLAROTUNDA ("elongatedpentagonalorthocupolarotunda", "epocuro", "upr5", "j40", "40") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.ORTHOCUPOLA, a * 0.52573111211913360603, c, c, c // 1; sqrt((5-sqrt(5))/10)
				);
			}
		},
		ELONGATED_PENTAGONAL_GYROCUPOLAROTUNDA ("elongatedpentagonalgyrocupolarotunda", "epgycuro", "epgcuro", "upgr5", "j41", "41") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.GYROCUPOLA, a * 0.52573111211913360603, c, c, c // 1; sqrt((5-sqrt(5))/10)
				);
			}
		},
		ELONGATED_PENTAGONAL_ORTHOBIROTUNDA ("elongatedpentagonalorthobirotunda", "epobro", "rpr5", "j42", "42") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.ORTHOROTUNDA, a * 1.3763819204711735382, c, c, c // 1; sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		ELONGATED_PENTAGONAL_GYROBIROTUNDA ("elongatedpentagonalgyrobirotunda", "epgybro", "epgbro", "rpgr5", "j43", "43") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.GYROROTUNDA, a * 1.3763819204711735382, c, c, c // 1; sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		GYROELONGATED_TRIANGULAR_BICUPOLA ("gyroelongatedtriangularbicupola", "gyetibcu", "getbcu", "uau3", "j44", "44") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, true, a * 0.85559967716735219297, c, c, c // sqrt(sqrt(3)-1)
				);
			}
		},
		GYROELONGATED_SQUARE_BICUPOLA ("gyroelongatedsquarebicupola", "gyesquibcu", "gesbcu", "uau4", "j45", "45") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, true, a * 0.86029556986297156644, c, c, c // sqrt(sqrt((sqrt(2)*7+10)/2)-sqrt(2)-1)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_BICUPOLA ("gyroelongatedpentagonalbicupola", "gyepibcu", "gepbcu", "uau5", "j46", "46") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, true, a * 0.86239700385945848345, c, c, c // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_CUPOLAROTUNDA ("gyroelongatedpentagonalcupolarotunda", "gyepcuro", "gepcuro", "uar5", "j47", "47") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					true, a * 0.86239700385945848345, // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
					Rotunda.Extension.GYROCUPOLA, a * 0.52573111211913360603, c, c, c // sqrt((5-sqrt(5))/10)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_BIROTUNDA ("gyroelongatedpentagonalbirotunda", "gyepabro", "gepbro", "rar5", "j48", "48") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					true, a * 0.86239700385945848345, // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
					Rotunda.Extension.ORTHOROTUNDA, a * 1.3763819204711735382, c, c, c // sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		AUGMENTED_TRIANGULAR_PRISM ("augmentedtriangularprism", "autip", "atp", "ap3", "j49", "49") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0)), new FaceVertexGen.Equilateral()),
					new Prism(3, 1, a*0.57735026918962576451, Polygon.Axis.Y, a, c, c) // sqrt(3)/3
				);
			}
		},
		BIAUGMENTED_TRIANGULAR_PRISM ("biaugmentedtriangularprism", "bautip", "batp", "bap3", "j50", "50") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0,1)), new FaceVertexGen.Equilateral()),
					new Prism(3, 1, a*0.57735026918962576451, Polygon.Axis.Y, a, c, c) // sqrt(3)/3
				);
			}
		},
		TRIAUGMENTED_TRIANGULAR_PRISM ("triaugmentedtriangularprism", "tautip", "tatp", "tap3", "j51", "51") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4)), new FaceVertexGen.Equilateral()),
					new Prism(3, 1, a*0.57735026918962576451, Polygon.Axis.Y, a, c, c) // sqrt(3)/3
				);
			}
		},
		AUGMENTED_PENTAGONAL_PRISM ("augmentedpentagonalprism", "aupip", "app", "ap5", "j52", "52") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0)), new FaceVertexGen.Equilateral()),
					new Prism(5, 1, a*0.85065080835203993218, Polygon.Axis.Y, a, c, c) // sqrt((sqrt(5)+5)/10)
				);
			}
		},
		BIAUGMENTED_PENTAGONAL_PRISM ("biaugmentedpentagonalprism", "baupip", "bapp", "bap5", "j53", "53") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.AtAngle(144)), new FaceVertexGen.Equilateral()),
					new Prism(5, 1, a*0.85065080835203993218, Polygon.Axis.Y, a, c, c) // sqrt((sqrt(5)+5)/10)
				);
			}
		},
		AUGMENTED_HEXAGONAL_PRISM ("augmentedhexagonalprism", "auhip", "ahp", "ap6", "j54", "54") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0)), new FaceVertexGen.Equilateral()),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		PARABIAUGMENTED_HEXAGONAL_PRISM ("parabiaugmentedhexagonalprism", "pabauhip", "pahp", "pap6", "j55", "55") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.AtAngle(180)), new FaceVertexGen.Equilateral()),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		METABIAUGMENTED_HEXAGONAL_PRISM ("metabiaugmentedhexagonalprism", "mabauhip", "mahp", "map6", "j56", "56") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.AtAngle(120), new FacePredicate.Index(0,1)), new FaceVertexGen.Equilateral()),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		TRIAUGMENTED_HEXAGONAL_PRISM ("triaugmentedhexagonalprism", "tauhip", "tahp", "tap6", "j57", "57") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.AtAngle(120)), new FaceVertexGen.Equilateral()),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		AUGMENTED_DODECAHEDRON ("augmenteddodecahedron", "aud", "ad", "j58", "58") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Index(0)), new FaceVertexGen.Equilateral()),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		PARABIAUGMENTED_DODECAHEDRON ("parabiaugmenteddodecahedron", "pabaud", "pad", "j59", "59") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.AtAngle(180)), new FaceVertexGen.Equilateral()),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		METABIAUGMENTED_DODECAHEDRON ("metabiaugmenteddodecahedron", "mabaud", "mad", "j60", "60") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.AtAngle(116.56505117707799), new FacePredicate.Index(0,1)), new FaceVertexGen.Equilateral()),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		TRIAUGMENTED_DODECAHEDRON ("triaugmenteddodecahedron", "taud", "tad", "j61", "61") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.AtAngle(116.56505117707799)), new FaceVertexGen.Equilateral()),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		METABIDIMINISHED_ICOSAHEDRON ("metabidiminishedicosahedron", "mibdi", "mdi", "j62", "62") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new RemoveVertices(Arrays.asList(new VertexPredicate.AtAngle(116.56505117707799), new VertexPredicate.Index(0,1)), c),
					new Icosahedron(Icosahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		TRIDIMINISHED_ICOSAHEDRON ("tridiminishedicosahedron", "teddi", "tdi", "j63", "63") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new RemoveVertices(Arrays.asList(new VertexPredicate.AtAngle(116.56505117707799)), c),
					new Icosahedron(Icosahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		AUGMENTED_TRIDIMINISHED_ICOSAHEDRON ("augmentedtridiminishedicosahedron", "auteddi", "atdi", "j64", "64") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Kis(Arrays.asList(new FacePredicate.AdjacentVertexDegree(3)), new FaceVertexGen.Equilateral()),
						new RemoveVertices(Arrays.asList(new VertexPredicate.AtAngle(116.56505117707799)), c)
					),
					new Icosahedron(Icosahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		AUGMENTED_TRUNCATED_TETRAHEDRON ("augmentedtruncatedtetrahedron", "autut", "att", "j65", "65") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Augment(
						Arrays.asList(new FacePredicate.Degree(6), new FacePredicate.Index(0)),
						Augment.AugmentationSpec.ORTHO, a * 0.57735026918962576451, a * 0.81649658092772603273
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.TRUNCATED_TETRAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		AUGMENTED_TRUNCATED_CUBE ("augmentedtruncatedcube", "autic", "atc", "j66", "66") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Augment(
						Arrays.asList(new FacePredicate.Degree(8), new FacePredicate.Index(0)),
						Augment.AugmentationSpec.ORTHO, a * 0.70710678118654752440, a * 0.70710678118654752440
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.TRUNCATED_CUBE,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		BIAUGMENTED_TRUNCATED_CUBE ("biaugmentedtruncatedcube", "bautic", "batc", "j67", "67") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Augment(
						Arrays.asList(new FacePredicate.Degree(8), new FacePredicate.AtAngle(180)),
						Augment.AugmentationSpec.ORTHO, a * 0.70710678118654752440, a * 0.70710678118654752440
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.TRUNCATED_CUBE,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		AUGMENTED_TRUNCATED_DODECAHEDRON ("augmentedtruncateddodecahedron", "autid", "atd", "j68", "68") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Augment(
						Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.Index(0)),
						Augment.AugmentationSpec.ORTHO, a * 0.85065080835203993218, a * 0.52573111211913360603
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.TRUNCATED_DODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		PARABIAUGMENTED_TRUNCATED_DODECAHEDRON ("parabiaugmentedtruncateddodecahedron", "pabautid", "patd", "j69", "69") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Augment(
						Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.AtAngle(180)),
						Augment.AugmentationSpec.ORTHO, a * 0.85065080835203993218, a * 0.52573111211913360603
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.TRUNCATED_DODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		METABIAUGMENTED_TRUNCATED_DODECAHEDRON ("metabiaugmentedtruncateddodecahedron", "mabautid", "matd", "j70", "70") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Augment(
						Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.AtAngle(116.56505117707799), new FacePredicate.Index(0,1)),
						Augment.AugmentationSpec.ORTHO, a * 0.85065080835203993218, a * 0.52573111211913360603
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.TRUNCATED_DODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		TRIAUGMENTED_TRUNCATED_DODECAHEDRON ("triaugmentedtruncateddodecahedron", "tautid", "tatd", "j71", "71") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Augment(
						Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.AtAngle(116.56505117707799)),
						Augment.AugmentationSpec.ORTHO, a * 0.85065080835203993218, a * 0.52573111211913360603
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.TRUNCATED_DODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		GYRATE_RHOMBICOSIDODECAHEDRON ("gyraterhombicosidodecahedron", "gyrid", "j72", "72") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.Index(0)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		PARABIGYRATE_RHOMBICOSIDODECAHEDRON ("parabigyraterhombicosidodecahedron", "pabgyrid", "pgyrid", "j73", "73") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(180)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		METABIGYRATE_RHOMBICOSIDODECAHEDRON ("metabigyraterhombicosidodecahedron", "mabgyrid", "mgyrid", "j74", "74") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(116.56505117707799), new FacePredicate.Index(0,1)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		TRIGYRATE_RHOMBICOSIDODECAHEDRON ("trigyraterhombicosidodecahedron", "tagyrid", "tgyrid", "j75", "75") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(116.56505117707799)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		DIMINISHED_RHOMBICOSIDODECAHEDRON ("diminishedrhombicosidodecahedron", "dirid", "drid", "j76", "76") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.Index(0)), c),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		PARAGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON ("paragyratediminishedrhombicosidodecahedron", "pagydrid", "pgydrid", "j77", "77") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.Index(1)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(180)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		METAGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON ("metagyratediminishedrhombicosidodecahedron", "magydrid", "mgydrid", "j78", "78") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.Index(1)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(116.56505117707799), new FacePredicate.Index(0,1)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		BIGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON ("bigyratediminishedrhombicosidodecahedron", "bagydrid", "bgydrid", "j79", "79") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.Index(0,1)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(116.56505117707799)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		PARABIDIMINISHED_RHOMBICOSIDODECAHEDRON ("parabidiminishedrhombicosidodecahedron", "pabidrid", "pdrid", "j80", "80") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(180)), c),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		METABIDIMINISHED_RHOMBICOSIDODECAHEDRON ("metabidiminishedrhombicosidodecahedron", "mabidrid", "mdrid", "j81", "81") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(116.56505117707799), new FacePredicate.Index(0,1)), c),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		GYRATE_BIDIMINISHED_RHOMBICOSIDODECAHEDRON ("gyratebidiminishedrhombicosidodecahedron", "gybadrid", "gybdrid", "j82", "82") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						new Augment(Arrays.asList(new FacePredicate.Degree(10), new FacePredicate.Index(1)), Augment.AugmentationSpec.GYRO, a * 0.85065080835203993218, a * 0.52573111211913360603),
						new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(116.56505117707799)), c)
					),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		TRIDIMINISHED_RHOMBICOSIDODECAHEDRON ("tridiminishedrhombicosidodecahedron", "tedrid", "tdrid", "j83", "83") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new RemoveFaces(Arrays.asList(new FacePredicate.Degree(5), new FacePredicate.AtAngle(116.56505117707799)), c),
					new ArchimedeanSolid(
						ArchimedeanSolid.FormSpecifier.SMALL_RHOMBICOSIDODECAHEDRON,
						ArchimedeanSolid.SizeSpecifier.EDGE_LENGTH, a, c
					)
				);
			}
		},
		SNUB_DISPHENOID ("snubdisphenoid", "snadow", "snds", "j84", "84") {
			public PolyhedronGen gen(double a, Color c) {
				double q = 0.16902222942417583090; // 2*x^3 + 11*x^2 + 4*x - 1 = 0
				double r = a/2 * Math.sqrt(q);
				double s = a/2 * Math.sqrt((1-q)/(2*q));
				double t = a/2 * Math.sqrt(2-2*q);
				double u = a/2;
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D(+t, +r,  0),
						new Point3D(-t, +r,  0),
						new Point3D( 0, -r, +t),
						new Point3D( 0, -r, -t),
						new Point3D(+u, -s,  0),
						new Point3D(-u, -s,  0),
						new Point3D( 0, +s, +u),
						new Point3D( 0, +s, -u)
					))
				);
			}
		},
		SNUB_SQUARE_ANTIPRISM ("snubsquareantiprism", "snisquap", "snsa", "sna4", "j85", "85") {
			public PolyhedronGen gen(double a, Color c) {
				double p = a * 0.18560702128217984757; // sqrt(8192*x^6 + 73728*x^5 + 90880*x^4 - 47872*x^3 + 5632*x^2 - 112*x - 1 = 0)
				double q = a * 0.67686850903135608229; // sqrt(8192*x^6 + 24576*x^5 + 768*x^4 - 8448*x^3 - 512*x^2 + 688*x - 1 = 0)
				double r = a * 0.70710678118654752440; // sqrt(2)/2
				double s = a * 0.85786586845519716687; // 16*x^6 - 16*x^5 - 52*x^4 + 16*x^3 + 32*x^2 - 4*x - 1 = 0
				double t = a * 1.21320554586631333083; // 2*x^6 + 4*x^5 - 11*x^4 - 12*x^3 + 12*x^2 + 8*x + 1 = 0
				double u = a * 0.5;
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D( 0, +p, +t),
						new Point3D( 0, +p, -t),
						new Point3D(+t, +p,  0),
						new Point3D(-t, +p,  0),
						new Point3D(+s, -p, +s),
						new Point3D(-s, -p, +s),
						new Point3D(+s, -p, -s),
						new Point3D(-s, -p, -s),
						new Point3D( 0, -q, +r),
						new Point3D( 0, -q, -r),
						new Point3D(+r, -q,  0),
						new Point3D(-r, -q,  0),
						new Point3D(+u, +q, +u),
						new Point3D(-u, +q, +u),
						new Point3D(+u, +q, -u),
						new Point3D(-u, +q, -u)
					))
				);
			}
		},
		SPHENOCORONA ("sphenocorona", "waco", "sc", "j86", "86") {
			public PolyhedronGen gen(double a, Color c) {
				double k = 0.85272694284641685541; // (6+sqrt(6)+2*sqrt(213-57*sqrt(6)))/30
				double u = a/2;
				double v = a*Math.sqrt(1-k*k);
				double w = a*k;
				double x = a*(Math.sqrt(3-4*k*k)/(Math.sqrt(1-k*k)*2)+0.5);
				double y = a*(1-2*k*k)/(Math.sqrt(1-k*k)*2);
				double z = a*Math.sqrt(k-k*k+0.5);
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D(+u, +v,  0),
						new Point3D(-u, +v,  0),
						new Point3D(+u,  0, +w),
						new Point3D(-u,  0, +w),
						new Point3D(+u,  0, -w),
						new Point3D(-u,  0, -w),
						new Point3D(+x, +y,  0),
						new Point3D(-x, +y,  0),
						new Point3D( 0, -z, +u),
						new Point3D( 0, -z, -u)
					))
				);
			}
		},
		AUGMENTED_SPHENOCORONA ("augmentedsphenocorona", "auwaco", "asc", "j87", "87") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0)), new FaceVertexGen.Equilateral()),
					new JohnsonSolid(SPHENOCORONA, a, c)
				);
			}
		},
		SPHENOMEGACORONA ("sphenomegacorona", "wamco", "smc", "j88", "88") {
			public PolyhedronGen gen(double a, Color c) {
				double k = 0.59463333563263853005; // 1680*x^16-4800*x^15-3712*x^14+17216*x^13+1568*x^12-24576*x^11+2464*x^10+17248*x^9-3384*x^8-5584*x^7+2000*x^6+240*x^5-776*x^4+304*x^3+200*x^2-56*x-23=0
				double u = a/2;
				double v = a*Math.sqrt(1-k*k);
				double w = a*k;
				double x = a*(Math.sqrt(3-4*k*k)/(Math.sqrt(1-k*k)*2)+0.5);
				double y = a*(1-2*k*k)/(Math.sqrt(1-k*k)*2);
				double z = a*Math.sqrt(4*k-4*k*k+2)/2;
				double p = a*(Math.sqrt(3-4*k*k)*(2*k*k-1)/(Math.sqrt(1-k*k)*(k*k-1)*2)+0.5);
				double q = a*(k*k*k*k*2-1)/(Math.sqrt((1-k*k)*(1-k*k)*(1-k*k))*2);
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D(+u, +v,  0),
						new Point3D(-u, +v,  0),
						new Point3D(+u,  0, +w),
						new Point3D(-u,  0, +w),
						new Point3D(+u,  0, -w),
						new Point3D(-u,  0, -w),
						new Point3D(+x, +y,  0),
						new Point3D(-x, +y,  0),
						new Point3D( 0, -z, +u),
						new Point3D( 0, -z, -u),
						new Point3D(+p, +q,  0),
						new Point3D(-p, +q,  0)
					))
				);
			}
		},
		HEBESPHENOMEGACORONA ("hebesphenomegacorona", "hawmco", "hsmc", "j89", "89") {
			public PolyhedronGen gen(double a, Color c) {
				double k = 0.21684481571345683717; // 26880*x^10+35328*x^9-25600*x^8-39680*x^7+6112*x^6+13696*x^5+2128*x^4-1808*x^3-1119*x^2+494*x-47=0
				double u = Math.sqrt(1-k*k);
				double v = Math.sqrt(2-2*k-4*k*k);
				double w = Math.sqrt(3-4*k*k);
				double x = k+0.5;
				double y = (v/(2*u))+0.5;
				double z = (v*v)/(4*u);
				double p = (w*v+k+1)/(4*u*u);
				double q = (w*(2*k-1)/(4-4*k))-(v/(4*u*u));
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D(+a/2, -a*u, +a/2),
						new Point3D(-a/2, -a*u, +a/2),
						new Point3D(+a/2, -a*u, -a/2),
						new Point3D(-a/2, -a*u, -a/2),
						new Point3D(0, a*w/2, +a/2),
						new Point3D(0, a*w/2, -a/2),
						new Point3D(+a/2, 0, +a*x),
						new Point3D(-a/2, 0, +a*x),
						new Point3D(+a/2, 0, -a*x),
						new Point3D(-a/2, 0, -a*x),
						new Point3D(+a*y, -a*z, 0),
						new Point3D(-a*y, -a*z, 0),
						new Point3D(+a*p, -a*q, 0),
						new Point3D(-a*p, -a*q, 0)
					))
				);
			}
		},
		DISPHENOCINGULUM ("disphenocingulum", "dawci", "dsc", "j90", "90") {
			public PolyhedronGen gen(double a, Color c) {
				double k = 0.76713111398346150192; // 256*x^12-512*x^11-1664*x^10+3712*x^9+1552*x^8-6592*x^7+1248*x^6+4352*x^5-2024*x^4-944*x^3+672*x^2-24*x-23=0
				double u = Math.sqrt(1-k*k);
				double v = Math.sqrt(2+8*k-8*k*k);
				double w = Math.sqrt(3-4*k*k);
				double p = a/2;
				double q = a*(u+v/4);
				double r = a*(w/(2*u)+0.5);
				double s = a*(u-1/(2*u)+v/4);
				double t = a*v/4;
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D(0, +q, +p),
						new Point3D(0, +q, -p),
						new Point3D(0, +s, +r),
						new Point3D(0, +s, -r),
						new Point3D(+p, -q, 0),
						new Point3D(-p, -q, 0),
						new Point3D(+r, -s, 0),
						new Point3D(-r, -s, 0),
						new Point3D(+a*k, +t, +p),
						new Point3D(-a*k, +t, +p),
						new Point3D(+a*k, +t, -p),
						new Point3D(-a*k, +t, -p),
						new Point3D(+p, -t, +a*k),
						new Point3D(-p, -t, +a*k),
						new Point3D(+p, -t, -a*k),
						new Point3D(-p, -t, -a*k)
					))
				);
			}
		},
		BILUNABIROTUNDA ("bilunabirotunda", "bilbiro", "blbr", "j91", "91") {
			public PolyhedronGen gen(double a, Color c) {
				double u = a/2;
				double v = a*(Math.sqrt(5)+3)/4;
				double w = a*(Math.sqrt(5)+1)/4;
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D(+u,  0, +v),
						new Point3D(+u,  0, -v),
						new Point3D(-u,  0, +v),
						new Point3D(-u,  0, -v),
						new Point3D(+w, +u, +u),
						new Point3D(+w, +u, -u),
						new Point3D(+w, -u, +u),
						new Point3D(+w, -u, -u),
						new Point3D(-w, +u, +u),
						new Point3D(-w, +u, -u),
						new Point3D(-w, -u, +u),
						new Point3D(-w, -u, -u),
						new Point3D( 0, +w,  0),
						new Point3D( 0, -w,  0)
					))
				);
			}
		},
		TRIANGULAR_HEBESPHENOROTUNDA ("triangularhebesphenorotunda", "thawro", "thsr", "j92", "92") {
			public PolyhedronGen gen(double a, Color c) {
				double o = a/2;
				double p = a*Math.sqrt(3)/6;
				double q = a*(Math.sqrt(3)*3+Math.sqrt(15))/6;
				double r = a*Math.sqrt(3)/3;
				double s = a*(Math.sqrt(3)*2+Math.sqrt(15))/6;
				double t = a*(Math.sqrt(3)+Math.sqrt(15))/6;
				double u = a*(Math.sqrt(5)+3)/4;
				double v = a*(Math.sqrt(15)-Math.sqrt(3))/12;
				double w = a*(Math.sqrt(5)+1)/4;
				double x = a*(Math.sqrt(3)*5+Math.sqrt(15))/12;
				double y = a*(Math.sqrt(3)*3+Math.sqrt(15))/12;
				double z = a*Math.sqrt(3)/2;
				return new Construct(
					new ConvexHull(c),
					new PointCloud(Arrays.asList(
						new Point3D(-p,  +q/2, +o),
						new Point3D(-p,  +q/2, -o),
						new Point3D(+r,  +q/2,  0),
						new Point3D(+s, t-q/2, +o),
						new Point3D(+s, t-q/2, -o),
						new Point3D(-v, t-q/2, +u),
						new Point3D(-v, t-q/2, -u),
						new Point3D(-x, t-q/2, +w),
						new Point3D(-x, t-q/2, -w),
						new Point3D(+y, r-q/2, +u),
						new Point3D(+y, r-q/2, -u),
						new Point3D(-q, r-q/2,  0),
						new Point3D(+z,  -q/2, +o),
						new Point3D(-z,  -q/2, +o),
						new Point3D(+z,  -q/2, -o),
						new Point3D(-z,  -q/2, -o),
						new Point3D( 0,  -q/2, +a),
						new Point3D( 0,  -q/2, -a)
					))
				);
			}
		};
		private final List<String> names;
		private FormSpecifier(String... names) { this.names = Arrays.asList(names); }
		public abstract PolyhedronGen gen(double edgeLength, Color color);
		public static FormSpecifier forIndex(int index) {
			switch (index) {
				case  1: return SQUARE_PYRAMID;
				case  2: return PENTAGONAL_PYRAMID;
				case  3: return TRIANGULAR_CUPOLA;
				case  4: return SQUARE_CUPOLA;
				case  5: return PENTAGONAL_CUPOLA;
				case  6: return PENTAGONAL_ROTUNDA;
				case  7: return ELONGATED_TRIANGULAR_PYRAMID;
				case  8: return ELONGATED_SQUARE_PYRAMID;
				case  9: return ELONGATED_PENTAGONAL_PYRAMID;
				case 10: return GYROELONGATED_SQUARE_PYRAMID;
				case 11: return GYROELONGATED_PENTAGONAL_PYRAMID;
				case 12: return TRIANGULAR_BIPYRAMID;
				case 13: return PENTAGONAL_BIPYRAMID;
				case 14: return ELONGATED_TRIANGULAR_BIPYRAMID;
				case 15: return ELONGATED_SQUARE_BIPYRAMID;
				case 16: return ELONGATED_PENTAGONAL_BIPYRAMID;
				case 17: return GYROELONGATED_SQUARE_BIPYRAMID;
				case 18: return ELONGATED_TRIANGULAR_CUPOLA;
				case 19: return ELONGATED_SQUARE_CUPOLA;
				case 20: return ELONGATED_PENTAGONAL_CUPOLA;
				case 21: return ELONGATED_PENTAGONAL_ROTUNDA;
				case 22: return GYROELONGATED_TRIANGULAR_CUPOLA;
				case 23: return GYROELONGATED_SQUARE_CUPOLA;
				case 24: return GYROELONGATED_PENTAGONAL_CUPOLA;
				case 25: return GYROELONGATED_PENTAGONAL_ROTUNDA;
				case 26: return GYROBIFASTIGIUM;
				case 27: return TRIANGULAR_ORTHOBICUPOLA;
				case 28: return SQUARE_ORTHOBICUPOLA;
				case 29: return SQUARE_GYROBICUPOLA;
				case 30: return PENTAGONAL_ORTHOBICUPOLA;
				case 31: return PENTAGONAL_GYROBICUPOLA;
				case 32: return PENTAGONAL_ORTHOCUPOLAROTUNDA;
				case 33: return PENTAGONAL_GYROCUPOLAROTUNDA;
				case 34: return PENTAGONAL_ORTHOBIROTUNDA;
				case 35: return ELONGATED_TRIANGULAR_ORTHOBICUPOLA;
				case 36: return ELONGATED_TRIANGULAR_GYROBICUPOLA;
				case 37: return ELONGATED_SQUARE_GYROBICUPOLA;
				case 38: return ELONGATED_PENTAGONAL_ORTHOBICUPOLA;
				case 39: return ELONGATED_PENTAGONAL_GYROBICUPOLA;
				case 40: return ELONGATED_PENTAGONAL_ORTHOCUPOLAROTUNDA;
				case 41: return ELONGATED_PENTAGONAL_GYROCUPOLAROTUNDA;
				case 42: return ELONGATED_PENTAGONAL_ORTHOBIROTUNDA;
				case 43: return ELONGATED_PENTAGONAL_GYROBIROTUNDA;
				case 44: return GYROELONGATED_TRIANGULAR_BICUPOLA;
				case 45: return GYROELONGATED_SQUARE_BICUPOLA;
				case 46: return GYROELONGATED_PENTAGONAL_BICUPOLA;
				case 47: return GYROELONGATED_PENTAGONAL_CUPOLAROTUNDA;
				case 48: return GYROELONGATED_PENTAGONAL_BIROTUNDA;
				case 49: return AUGMENTED_TRIANGULAR_PRISM;
				case 50: return BIAUGMENTED_TRIANGULAR_PRISM;
				case 51: return TRIAUGMENTED_TRIANGULAR_PRISM;
				case 52: return AUGMENTED_PENTAGONAL_PRISM;
				case 53: return BIAUGMENTED_PENTAGONAL_PRISM;
				case 54: return AUGMENTED_HEXAGONAL_PRISM;
				case 55: return PARABIAUGMENTED_HEXAGONAL_PRISM;
				case 56: return METABIAUGMENTED_HEXAGONAL_PRISM;
				case 57: return TRIAUGMENTED_HEXAGONAL_PRISM;
				case 58: return AUGMENTED_DODECAHEDRON;
				case 59: return PARABIAUGMENTED_DODECAHEDRON;
				case 60: return METABIAUGMENTED_DODECAHEDRON;
				case 61: return TRIAUGMENTED_DODECAHEDRON;
				case 62: return METABIDIMINISHED_ICOSAHEDRON;
				case 63: return TRIDIMINISHED_ICOSAHEDRON;
				case 64: return AUGMENTED_TRIDIMINISHED_ICOSAHEDRON;
				case 65: return AUGMENTED_TRUNCATED_TETRAHEDRON;
				case 66: return AUGMENTED_TRUNCATED_CUBE;
				case 67: return BIAUGMENTED_TRUNCATED_CUBE;
				case 68: return AUGMENTED_TRUNCATED_DODECAHEDRON;
				case 69: return PARABIAUGMENTED_TRUNCATED_DODECAHEDRON;
				case 70: return METABIAUGMENTED_TRUNCATED_DODECAHEDRON;
				case 71: return TRIAUGMENTED_TRUNCATED_DODECAHEDRON;
				case 72: return GYRATE_RHOMBICOSIDODECAHEDRON;
				case 73: return PARABIGYRATE_RHOMBICOSIDODECAHEDRON;
				case 74: return METABIGYRATE_RHOMBICOSIDODECAHEDRON;
				case 75: return TRIGYRATE_RHOMBICOSIDODECAHEDRON;
				case 76: return DIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 77: return PARAGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 78: return METAGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 79: return BIGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 80: return PARABIDIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 81: return METABIDIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 82: return GYRATE_BIDIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 83: return TRIDIMINISHED_RHOMBICOSIDODECAHEDRON;
				case 84: return SNUB_DISPHENOID;
				case 85: return SNUB_SQUARE_ANTIPRISM;
				case 86: return SPHENOCORONA;
				case 87: return AUGMENTED_SPHENOCORONA;
				case 88: return SPHENOMEGACORONA;
				case 89: return HEBESPHENOMEGACORONA;
				case 90: return DISPHENOCINGULUM;
				case 91: return BILUNABIROTUNDA;
				case 92: return TRIANGULAR_HEBESPHENOROTUNDA;
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
	private final double edgeLength;
	private final Color color;
	
	public JohnsonSolid(FormSpecifier form, double edgeLength, Color color) {
		this.form = form;
		this.edgeLength = edgeLength;
		this.color = color;
	}
	
	public Polyhedron gen() {
		PolyhedronGen gen = form.gen(edgeLength, color);
		return (gen != null) ? gen.gen() : null;
	}
	
	public static class Factory extends PolyhedronGen.Factory<JohnsonSolid> {
		public String name() { return "JohnsonSolid"; }
		
		public JohnsonSolid parse(String[] args) {
			FormSpecifier form = FormSpecifier.SQUARE_PYRAMID;
			double edgeLength = 1;
			Color color = Color.GRAY;
			int argi = 0;
			while (argi < args.length) {
				String arg = args[argi++];
				if (arg.equalsIgnoreCase("-n") && argi < args.length) {
					String name = args[argi++];
					FormSpecifier f = FormSpecifier.forName(name);
					if (f == null) f = FormSpecifier.forIndex(parseInt(name, 0));
					if (f != null) form = f;
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					edgeLength = parseDouble(args[argi++], edgeLength);
				} else if (arg.equalsIgnoreCase("-c") && argi < args.length) {
					color = parseColor(args[argi++], color);
				} else {
					FormSpecifier f = FormSpecifier.forName(arg);
					if (f == null) f = FormSpecifier.forIndex(parseInt(arg, 0));
					if (f == null) return null;
					form = f;
				}
			}
			return new JohnsonSolid(form, edgeLength, color);
		}
		
		public Option[] options() {
			final String toc = (
				"which Johnson solid (by index or name)"
				+ "\n\t\t 1  J1   y4       square pyramid"
				+ "\n\t\t 2  J2   y5       pentagonal pyramid"
				+ "\n\t\t 3  J3   u3       triangular cupola"
				+ "\n\t\t 4  J4   u4       square cupola"
				+ "\n\t\t 5  J5   u5       pentagonal cupola"
				+ "\n\t\t 6  J6   r5       pentagonal rotunda"
				+ "\n\t\t 7  J7   py3      elongated triangular pyramid"
				+ "\n\t\t 8  J8   py4      elongated square pyramid"
				+ "\n\t\t 9  J9   py5      elongated pentagonal pyramid"
				+ "\n\t\t10  J10  ay4      gyroelongated square pyramid"
				+ "\n\t\t11  J11  ay5      gyroelongated pentagonal pyramid"
				+ "\n\t\t12  J12  yy3      triangular bipyramid"
				+ "\n\t\t13  J13  yy5      pentagonal bipyramid"
				+ "\n\t\t14  J14  ypy3     elongated triangular bipyramid"
				+ "\n\t\t15  J15  ypy4     elongated square bipyramid"
				+ "\n\t\t16  J16  ypy5     elongated pentagonal bipyramid"
				+ "\n\t\t17  J17  yay4     gyroelongated square bipyramid"
				+ "\n\t\t18  J18  pu3      elongated triangular cupola"
				+ "\n\t\t19  J19  pu4      elongated square cupola"
				+ "\n\t\t20  J20  pu5      elongated pentagonal cupola"
				+ "\n\t\t21  J21  pr5      elongated pentagonal rotunda"
				+ "\n\t\t22  J22  au3      gyroelongated triangular cupola"
				+ "\n\t\t23  J23  au4      gyroelongated square cupola"
				+ "\n\t\t24  J24  au5      gyroelongated pentagonal cupola"
				+ "\n\t\t25  J25  ar5      gyroelongated pentagonal rotunda"
				+ "\n\t\t26  J26  pgp3     gyrobifastigium"
				+ "\n\t\t27  J27  uu3      triangular orthobicupola"
				+ "\n\t\t28  J28  uu4      square orthobicupola"
				+ "\n\t\t29  J29  ugu4     square gyrobicupola"
				+ "\n\t\t30  J30  uu5      pentagonal orthobicupola"
				+ "\n\t\t31  J31  ugu5     pentagonal gyrobicupola"
				+ "\n\t\t32  J32  ur5      pentagonal orthocupolarotunda"
				+ "\n\t\t33  J33  ugr5     pentagonal gyrocupolarotunda"
				+ "\n\t\t34  J34  rr5      pentagonal orthobirotunda"
				+ "\n\t\t35  J35  upu3     elongated triangular orthobicupola"
				+ "\n\t\t36  J36  upgu3    elongated triangular gyrobicupola"
				+ "\n\t\t37  J37  upgu4    elongated square gyrobicupola"
				+ "\n\t\t38  J38  upu5     elongated pentagonal orthobicupola"
				+ "\n\t\t39  J39  upgu5    elongated pentagonal gyrobicupola"
				+ "\n\t\t40  J40  upr5     elongated pentagonal orthocupolarotunda"
				+ "\n\t\t41  J41  upgr5    elongated pentagonal gyrocupolarotunda"
				+ "\n\t\t42  J42  rpr5     elongated pentagonal orthobirotunda"
				+ "\n\t\t43  J43  rpgr5    elongated pentagonal gyrobirotunda"
				+ "\n\t\t44  J44  uau3     gyroelongated triangular bicupola"
				+ "\n\t\t45  J45  uau4     gyroelongated square bicupola"
				+ "\n\t\t46  J46  uau5     gyroelongated pentagonal bicupola"
				+ "\n\t\t47  J47  uar5     gyroelongated pentagonal cupolarotunda"
				+ "\n\t\t48  J48  rar5     gyroelongated pentagonal birotunda"
				+ "\n\t\t49  J49  ap3      augmented triangular prism"
				+ "\n\t\t50  J50  bap3     biaugmented triangular prism"
				+ "\n\t\t51  J51  tap3     triaugmented triangular prism"
				+ "\n\t\t52  J52  ap5      augmented pentagonal prism"
				+ "\n\t\t53  J53  bap5     biaugmented pentagonal prism"
				+ "\n\t\t54  J54  ap6      augmented hexagonal prism"
				+ "\n\t\t55  J55  pap6     parabiaugmented hexagonal prism"
				+ "\n\t\t56  J56  map6     metabiaugmented hexagonal prism"
				+ "\n\t\t57  J57  tap6     triaugmented hexagonal prism"
				+ "\n\t\t58  J58  ad       augmented dodecahedron"
				+ "\n\t\t59  J59  pad      parabiaugmented dodecahedron"
				+ "\n\t\t60  J60  mad      metabiaugmented dodecahedron"
				+ "\n\t\t61  J61  tad      triaugmented dodecahedron"
				+ "\n\t\t62  J62  mdi      metabidiminished icosahedron"
				+ "\n\t\t63  J63  tdi      tridiminished icosahedron"
				+ "\n\t\t64  J64  atdi     augmented tridiminished icosahedron"
				+ "\n\t\t65  J65  att      augmented truncated tetrahedron"
				+ "\n\t\t66  J66  atc      augmented truncated cube"
				+ "\n\t\t67  J67  batc     biaugmented truncated cube"
				+ "\n\t\t68  J68  atd      augmented truncated dodecahedron"
				+ "\n\t\t69  J69  patd     parabiaugmented truncated dodecahedron"
				+ "\n\t\t70  J70  matd     metabiaugmented truncated dodecahedron"
				+ "\n\t\t71  J71  tatd     triaugmented truncated dodecahedron"
				+ "\n\t\t72  J72  gyrid    gyrate rhombicosidodecahedron"
				+ "\n\t\t73  J73  pgyrid   parabigyrate rhombicosidodecahedron"
				+ "\n\t\t74  J74  mgyrid   metabigyrate rhombicosidodecahedron"
				+ "\n\t\t75  J75  tgyrid   trigyrate rhombicosidodecahedron"
				+ "\n\t\t76  J76  drid     diminished rhombicosidodecahedron"
				+ "\n\t\t77  J77  pgydrid  paragyrate diminished rhombicosidodecahedron"
				+ "\n\t\t78  J78  mgydrid  metagyrate diminished rhombicosidodecahedron"
				+ "\n\t\t79  J79  bgydrid  bigyrate diminished rhombicosidodecahedron"
				+ "\n\t\t80  J80  pdrid    parabidiminished rhombicosidodecahedron"
				+ "\n\t\t81  J81  mdrid    metabidiminished rhombicosidodecahedron"
				+ "\n\t\t82  J82  gybdrid  gyrate bidiminished rhombicosidodecahedron"
				+ "\n\t\t83  J83  tdrid    tridiminished rhombicosidodecahedron"
				+ "\n\t\t84  J84  snds     snub disphenoid"
				+ "\n\t\t85  J85  sna4     snub square antiprism"
				+ "\n\t\t86  J86  sc       sphenocorona"
				+ "\n\t\t87  J87  asc      augmented sphenocorona"
				+ "\n\t\t88  J88  smc      sphenomegacorona"
				+ "\n\t\t89  J89  hsmc     hebesphenomegacorona"
				+ "\n\t\t90  J90  dsc      disphenocingulum"
				+ "\n\t\t91  J91  blbr     bilunabirotunda"
				+ "\n\t\t92  J92  thsr     triangular hebesphenorotunda"
			);
			return new Option[] {
				new Option("n", Type.INT, toc),
				new Option("a", Type.REAL, "edge length"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}