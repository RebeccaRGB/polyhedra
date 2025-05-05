package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.kreative.polyhedra.Metric;
import com.kreative.polyhedra.MetricAggregator;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.op.Chain;
import com.kreative.polyhedra.op.FacePredicate;
import com.kreative.polyhedra.op.FaceVertexGen;
import com.kreative.polyhedra.op.Kis;
import com.kreative.polyhedra.op.RemoveVertices;
import com.kreative.polyhedra.op.VertexPredicate;

public class JohnsonSolid extends PolyhedronGen {
	public static enum FormSpecifier {
		SQUARE_PYRAMID ("squarepyramid", "equilateralsquarepyramid", "y4", "j1", "1") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_PYRAMID ("pentagonalpyramid", "equilateralpentagonalpyramid", "y5", "j2", "2") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, 0, c, c, c
				);
			}
		},
		TRIANGULAR_CUPOLA ("triangularcupola", "equilateraltriangularcupola", "u3", "j3", "3") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, 0, c, c, c
				);
			}
		},
		SQUARE_CUPOLA ("squarecupola", "equilateralsquarecupola", "u4", "j4", "4") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_CUPOLA ("pentagonalcupola", "equilateralpentagonalcupola", "u5", "j5", "5") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_ROTUNDA ("pentagonalrotunda", "r5", "j6", "6") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, null, 0, c, c, c
				);
			}
		},
		ELONGATED_TRIANGULAR_PYRAMID ("elongatedtriangularpyramid", "py3", "j7", "7") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					3, 1, a * 0.57735026918962576451, // sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, a, c, c, c
				);
			}
		},
		ELONGATED_SQUARE_PYRAMID ("elongatedsquarepyramid", "py4", "j8", "8") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_PYRAMID ("elongatedpentagonalpyramid", "py5", "j9", "9") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, a, c, c, c
				);
			}
		},
		GYROELONGATED_SQUARE_PYRAMID ("gyroelongatedsquarepyramid", "ay4", "j10", "10") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, a * 0.84089641525371454303, c, c, c // 1/sqrt(sqrt(2))
				);
			}
		},
		GYROELONGATED_PENTAGONAL_PYRAMID ("gyroelongatedpentagonalpyramid", "ay5", "j11", "11") {
			public PolyhedronGen gen(double a, Color c) {
				return new Pyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, a * 0.85065080835203993218, c, c, c // sqrt((sqrt(5)+5)/10)
				);
			}
		},
		TRIANGULAR_BIPYRAMID ("triangularbipyramid", "yy3", "j12", "12") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					3, 1, a * 0.57735026918962576451, // sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, 0, c, c
				);
			}
		},
		PENTAGONAL_BIPYRAMID ("pentagonalbipyramid", "yy5", "j13", "13") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, 0, c, c
				);
			}
		},
		ELONGATED_TRIANGULAR_BIPYRAMID ("elongatedtriangularbipyramid", "ypy3", "j14", "14") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					3, 1, a * 0.57735026918962576451, // sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, a, c, c
				);
			}
		},
		ELONGATED_SQUARE_BIPYRAMID ("elongatedsquarebipyramid", "ypy4", "j15", "15") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, a, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_BIPYRAMID ("elongatedpentagonalbipyramid", "ypy5", "j16", "16") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					5, 1, a * 0.85065080835203993218, // sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, a, c, c
				);
			}
		},
		GYROELONGATED_SQUARE_BIPYRAMID ("gyroelongatedsquarebipyramid", "yay4", "j17", "17") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bipyramid(
					4, 1, a * 0.70710678118654752440, // sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, a * 0.84089641525371454303, c, c // 1/sqrt(sqrt(2))
				);
			}
		},
		ELONGATED_TRIANGULAR_CUPOLA ("elongatedtriangularcupola", "pu3", "j18", "18") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, a, c, c, c
				);
			}
		},
		ELONGATED_SQUARE_CUPOLA ("elongatedsquarecupola", "pu4", "j19", "19") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_CUPOLA ("elongatedpentagonalcupola", "pu5", "j20", "20") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_ROTUNDA ("elongatedpentagonalrotunda", "pr5", "j21", "21") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, null, 0, c, c, c
				);
			}
		},
		GYROELONGATED_TRIANGULAR_CUPOLA ("gyroelongatedtriangularcupola", "au3", "j22", "22") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					true, a * 0.85559967716735219297, c, c, c // sqrt(sqrt(3)-1)
				);
			}
		},
		GYROELONGATED_SQUARE_CUPOLA ("gyroelongatedsquarecupola", "au4", "j23", "23") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, a * 0.86029556986297156644, c, c, c // sqrt(sqrt((sqrt(2)*7+10)/2)-sqrt(2)-1)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_CUPOLA ("gyroelongatedpentagonalcupola", "au5", "j24", "24") {
			public PolyhedronGen gen(double a, Color c) {
				return new Cupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, a * 0.86239700385945848345, c, c, c // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_ROTUNDA ("gyroelongatedpentagonalrotunda", "ar5", "j25", "25") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					true, a * 0.86239700385945848345, null, 0, c, c, c // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
				);
			}
		},
		GYROBIFASTIGIUM ("gyrobifastigium", "pgp3", "j26", "26") {
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
		TRIANGULAR_ORTHOBICUPOLA ("triangularorthobicupola", "uu3", "j27", "27") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, false, 0, c, c, c
				);
			}
		},
		SQUARE_ORTHOBICUPOLA ("squareorthobicupola", "uu4", "j28", "28") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, false, 0, c, c, c
				);
			}
		},
		SQUARE_GYROBICUPOLA ("squaregyrobicupola", "ugu4", "j29", "29") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_ORTHOBICUPOLA ("pentagonalorthobicupola", "uu5", "j30", "30") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_GYROBICUPOLA ("pentagonalgyrobicupola", "ugu5", "j31", "31") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, false, 0, c, c, c
				);
			}
		},
		PENTAGONAL_ORTHOCUPOLAROTUNDA ("pentagonalorthocupolarotunda", "ur5", "j32", "32") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, Rotunda.Extension.ORTHOCUPOLA, a * 0.52573111211913360603, c, c, c // sqrt((5-sqrt(5))/10)
				);
			}
		},
		PENTAGONAL_GYROCUPOLAROTUNDA ("pentagonalgyrocupolarotunda", "ugr5", "j33", "33") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, Rotunda.Extension.GYROCUPOLA, a * 0.52573111211913360603, c, c, c // sqrt((5-sqrt(5))/10)
				);
			}
		},
		PENTAGONAL_ORTHOBIROTUNDA ("pentagonalorthobirotunda", "rr5", "j34", "34") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, 0, Rotunda.Extension.ORTHOROTUNDA, a * 1.3763819204711735382, c, c, c // sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		ELONGATED_TRIANGULAR_ORTHOBICUPOLA ("elongatedtriangularorthobicupola", "upu3", "j35", "35") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, false, a, c, c, c
				);
			}
		},
		ELONGATED_TRIANGULAR_GYROBICUPOLA ("elongatedtriangulargyrobicupola", "upgu3", "j36", "36") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					true, false, a, c, c, c
				);
			}
		},
		ELONGATED_SQUARE_GYROBICUPOLA ("elongatedsquaregyrobicupola", "upgu4", "j37", "37") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					true, false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_ORTHOBICUPOLA ("elongatedpentagonalorthobicupola", "upu5", "j38", "38") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_GYROBICUPOLA ("elongatedpentagonalgyrobicupola", "upgu5", "j39", "39") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					true, false, a, c, c, c
				);
			}
		},
		ELONGATED_PENTAGONAL_ORTHOCUPOLAROTUNDA ("elongatedpentagonalorthocupolarotunda", "upr5", "j40", "40") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.ORTHOCUPOLA, a * 0.52573111211913360603, c, c, c // 1; sqrt((5-sqrt(5))/10)
				);
			}
		},
		ELONGATED_PENTAGONAL_GYROCUPOLAROTUNDA ("elongatedpentagonalgyrocupolarotunda", "upgr5", "j41", "41") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.GYROCUPOLA, a * 0.52573111211913360603, c, c, c // 1; sqrt((5-sqrt(5))/10)
				);
			}
		},
		ELONGATED_PENTAGONAL_ORTHOBIROTUNDA ("elongatedpentagonalorthobirotunda", "rpr5", "j42", "42") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.ORTHOROTUNDA, a * 1.3763819204711735382, c, c, c // 1; sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		ELONGATED_PENTAGONAL_GYROBIROTUNDA ("elongatedpentagonalgyrobirotunda", "rpgr5", "j43", "43") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					false, a, Rotunda.Extension.GYROROTUNDA, a * 1.3763819204711735382, c, c, c // 1; sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		GYROELONGATED_TRIANGULAR_BICUPOLA ("gyroelongatedtriangularbicupola", "uau3", "j44", "44") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					3, a, a * 0.57735026918962576451, // 1; sqrt(3)/3
					Polygon.Axis.Y, a * 0.81649658092772603273, // sqrt(6)/3
					false, true, a * 0.85559967716735219297, c, c, c // sqrt(sqrt(3)-1)
				);
			}
		},
		GYROELONGATED_SQUARE_BICUPOLA ("gyroelongatedsquarebicupola", "uau4", "j45", "45") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					4, a * 1.3065629648763765279, a * 0.70710678118654752440, // sqrt(sqrt(2)/2+1); sqrt(2)/2
					Polygon.Axis.Y, a * 0.70710678118654752440, // sqrt(2)/2
					false, true, a * 0.86029556986297156644, c, c, c // sqrt(sqrt((sqrt(2)*7+10)/2)-sqrt(2)-1)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_BICUPOLA ("gyroelongatedpentagonalbicupola", "uau5", "j46", "46") {
			public PolyhedronGen gen(double a, Color c) {
				return new Bicupola(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 0.52573111211913360603, // sqrt((5-sqrt(5))/10)
					false, true, a * 0.86239700385945848345, c, c, c // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_CUPOLAROTUNDA ("gyroelongatedpentagonalcupolarotunda", "uar5", "j47", "47") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					true, a * 0.86239700385945848345, // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
					Rotunda.Extension.GYROCUPOLA, a * 0.52573111211913360603, c, c, c // sqrt((5-sqrt(5))/10)
				);
			}
		},
		GYROELONGATED_PENTAGONAL_BIROTUNDA ("gyroelongatedpentagonalbirotunda", "rar5", "j48", "48") {
			public PolyhedronGen gen(double a, Color c) {
				return new Rotunda(
					5, a * 1.6180339887498948482, a * 0.85065080835203993218, // (sqrt(5)+1)/2; sqrt((sqrt(5)+5)/10)
					Polygon.Axis.Y, a * 1.3763819204711735382, // sqrt((sqrt(5)*2+5)/5)
					true, a * 0.86239700385945848345, // sqrt((sqrt((sqrt(5)*11+25)*2)-sqrt(5)*2-4)/2)
					Rotunda.Extension.ORTHOROTUNDA, a * 1.3763819204711735382, c, c, c // sqrt((sqrt(5)*2+5)/5)
				);
			}
		},
		AUGMENTED_TRIANGULAR_PRISM ("augmentedtriangularprism", "ap3", "j49", "49") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0)), FaceVertexGen.EQUILATERAL, null),
					new Prism(3, 1, a*0.57735026918962576451, Polygon.Axis.Y, a, c, c) // sqrt(3)/3
				);
			}
		},
		BIAUGMENTED_TRIANGULAR_PRISM ("biaugmentedtriangularprism", "bap3", "j50", "50") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0,1)), FaceVertexGen.EQUILATERAL, null),
					new Prism(3, 1, a*0.57735026918962576451, Polygon.Axis.Y, a, c, c) // sqrt(3)/3
				);
			}
		},
		TRIAUGMENTED_TRIANGULAR_PRISM ("triaugmentedtriangularprism", "tap3", "j51", "51") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4)), FaceVertexGen.EQUILATERAL, null),
					new Prism(3, 1, a*0.57735026918962576451, Polygon.Axis.Y, a, c, c) // sqrt(3)/3
				);
			}
		},
		AUGMENTED_PENTAGONAL_PRISM ("augmentedpentagonalprism", "ap5", "j52", "52") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0)), FaceVertexGen.EQUILATERAL, null),
					new Prism(5, 1, a*0.85065080835203993218, Polygon.Axis.Y, a, c, c) // sqrt((sqrt(5)+5)/10)
				);
			}
		},
		BIAUGMENTED_PENTAGONAL_PRISM ("biaugmentedpentagonalprism", "bap5", "j53", "53") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0,2)), FaceVertexGen.EQUILATERAL, null),
					new Prism(5, 1, a*0.85065080835203993218, Polygon.Axis.Y, a, c, c) // sqrt((sqrt(5)+5)/10)
				);
			}
		},
		AUGMENTED_HEXAGONAL_PRISM ("augmentedhexagonalprism", "ap6", "j54", "54") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0)), FaceVertexGen.EQUILATERAL, null),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		PARABIAUGMENTED_HEXAGONAL_PRISM ("parabiaugmentedhexagonalprism", "pap6", "j55", "55") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0,3)), FaceVertexGen.EQUILATERAL, null),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		METABIAUGMENTED_HEXAGONAL_PRISM ("metabiaugmentedhexagonalprism", "map6", "j56", "56") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0,2)), FaceVertexGen.EQUILATERAL, null),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		TRIAUGMENTED_HEXAGONAL_PRISM ("triaugmentedhexagonalprism", "tap6", "j57", "57") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Degree(4), new FacePredicate.Index(0,2,4)), FaceVertexGen.EQUILATERAL, null),
					new Prism(6, 1, a, Polygon.Axis.Y, a, c, c)
				);
			}
		},
		AUGMENTED_DODECAHEDRON ("augmenteddodecahedron", "ad", "j58", "58") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Kis(Arrays.asList(new FacePredicate.Index(0)), FaceVertexGen.EQUILATERAL, null),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		PARABIAUGMENTED_DODECAHEDRON ("parabiaugmenteddodecahedron", "pad", "j59", "59") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					// IFTTT: If the order of faces in Dodecahedron.java changes, these indices will need to be updated.
					new Kis(Arrays.asList(new FacePredicate.Index(0,7)), FaceVertexGen.EQUILATERAL, null),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		METABIAUGMENTED_DODECAHEDRON ("metabiaugmenteddodecahedron", "mad", "j60", "60") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					// IFTTT: If the order of faces in Dodecahedron.java changes, these indices will need to be updated.
					new Kis(Arrays.asList(new FacePredicate.Index(0,3)), FaceVertexGen.EQUILATERAL, null),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		TRIAUGMENTED_DODECAHEDRON ("triaugmenteddodecahedron", "tad", "j61", "61") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					// IFTTT: If the order of faces in Dodecahedron.java changes, these indices will need to be updated.
					new Kis(Arrays.asList(new FacePredicate.Index(0,3,6)), FaceVertexGen.EQUILATERAL, null),
					new Dodecahedron(Dodecahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		METABIDIMINISHED_ICOSAHEDRON ("metabidiminishedicosahedron", "mdi", "j62", "62") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					// IFTTT: If the order of vertices in Icosahedron.java changes, these indices will need to be updated.
					new RemoveVertices(Arrays.asList(new VertexPredicate.Index(0, 1)), c),
					new Icosahedron(Icosahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		TRIDIMINISHED_ICOSAHEDRON ("tridiminishedicosahedron", "tdi", "j63", "63") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					// IFTTT: If the order of vertices in Icosahedron.java changes, these indices will need to be updated.
					new RemoveVertices(Arrays.asList(new VertexPredicate.Index(0, 1, 5)), c),
					new Icosahedron(Icosahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		AUGMENTED_TRIDIMINISHED_ICOSAHEDRON ("augmentedtridiminishedicosahedron", "atdi", "j64", "64") {
			public PolyhedronGen gen(double a, Color c) {
				return new Construct(
					new Chain(
						// IFTTT: If the order of vertices or faces in Icosahedron.java changes, these indices will need to be updated.
						new Kis(Arrays.asList(new FacePredicate.Degree(3), new FacePredicate.Index(3)), FaceVertexGen.EQUILATERAL, null),
						new RemoveVertices(Arrays.asList(new VertexPredicate.Index(0, 1, 5)), c)
					),
					new Icosahedron(Icosahedron.SizeSpecifier.EDGE_LENGTH, a, c)
				);
			}
		},
		AUGMENTED_TRUNCATED_TETRAHEDRON ("augmentedtruncatedtetrahedron", "att", "j65", "65") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		AUGMENTED_TRUNCATED_CUBE ("augmentedtruncatedcube", "atc", "j66", "66") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		BIAUGMENTED_TRUNCATED_CUBE ("biaugmentedtruncatedcube", "batc", "j67", "67") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		AUGMENTED_TRUNCATED_DODECAHEDRON ("augmentedtruncateddodecahedron", "atd", "j68", "68") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		PARABIAUGMENTED_TRUNCATED_DODECAHEDRON ("parabiaugmentedtruncateddodecahedron", "patd", "j69", "69") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		METABIAUGMENTED_TRUNCATED_DODECAHEDRON ("metabiaugmentedtruncateddodecahedron", "matd", "j70", "70") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		TRIAUGMENTED_TRUNCATED_DODECAHEDRON ("triaugmentedtruncateddodecahedron", "tatd", "j71", "71") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		GYRATE_RHOMBICOSIDODECAHEDRON ("gyraterhombicosidodecahedron", "gyrid", "j72", "72") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		PARABIGYRATE_RHOMBICOSIDODECAHEDRON ("parabigyraterhombicosidodecahedron", "pgyrid", "j73", "73") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		METABIGYRATE_RHOMBICOSIDODECAHEDRON ("metabigyraterhombicosidodecahedron", "mgyrid", "j74", "74") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		TRIGYRATE_RHOMBICOSIDODECAHEDRON ("trigyraterhombicosidodecahedron", "tgyrid", "j75", "75") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		DIMINISHED_RHOMBICOSIDODECAHEDRON ("diminishedrhombicosidodecahedron", "drid", "j76", "76") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		PARAGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON ("paragyratediminishedrhombicosidodecahedron", "pgydrid", "j77", "77") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		METAGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON ("metagyratediminishedrhombicosidodecahedron", "mgydrid", "j78", "78") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		BIGYRATE_DIMINISHED_RHOMBICOSIDODECAHEDRON ("bigyratediminishedrhombicosidodecahedron", "bgydrid", "j79", "79") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		PARABIDIMINISHED_RHOMBICOSIDODECAHEDRON ("parabidiminishedrhombicosidodecahedron", "pdrid", "j80", "80") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		METABIDIMINISHED_RHOMBICOSIDODECAHEDRON ("metabidiminishedrhombicosidodecahedron", "mdrid", "j81", "81") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		GYRATE_BIDIMINISHED_RHOMBICOSIDODECAHEDRON ("gyratebidiminishedrhombicosidodecahedron", "gybdrid", "j82", "82") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		TRIDIMINISHED_RHOMBICOSIDODECAHEDRON ("tridiminishedrhombicosidodecahedron", "tdrid", "j83", "83") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		SNUB_DISPHENOID ("snubdisphenoid", "snds", "j84", "84") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		SNUB_SQUARE_ANTIPRISM ("snubsquareantiprism", "sna4", "j85", "85") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		SPHENOCORONA ("sphenocorona", "sc", "j86", "86") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		AUGMENTED_SPHENOCORONA ("augmentedsphenocorona", "asc", "j87", "87") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		SPHENOMEGACORONA ("sphenomegacorona", "smc", "j88", "88") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		HEBESPHENOMEGACORONA ("hebesphenomegacorona", "hsmc", "j89", "89") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		DISPHENOCINGULUM ("disphenocingulum", "dsc", "j90", "90") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		BILUNABIROTUNDA ("bilunabirotunda", "blbr", "j91", "91") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
			}
		},
		TRIANGULAR_HEBESPHENOROTUNDA ("triangularhebesphenorotunda", "thsr", "j92", "92") {
			public PolyhedronGen gen(double a, Color c) {
				return null; // TODO stub
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
				} else if (arg.equalsIgnoreCase("--debug")) {
					List<Float> validAngles = Arrays.asList(60f, 90f, 108f, 120f, 135f, 144f);
					for (FormSpecifier f : FormSpecifier.values()) {
						System.out.print("\u001B[1;34m" + f + "\u001B[0m");
						Polyhedron p1 = new JohnsonSolid(f, 1, Color.GRAY).gen();
						Polyhedron p2 = new JohnsonSolid(f, 2, Color.GRAY).gen();
						if (p1 == null || p2 == null) {
							System.out.println("\t- \u001B[1;33mNot implemented\u001B[0m");
							continue;
						}
						double elf1 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p1));
						double elf2 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p1));
						double elf3 = MetricAggregator.MINIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p2)) / 2;
						double elf4 = MetricAggregator.MAXIMUM.aggregate(Metric.EDGE_LENGTH.iterator(p2)) / 2;
						double[] mtx = {elf1, elf2, elf3, elf4};
						System.out.print("\t- Edges:");
						for (double m : mtx) System.out.print(((1 == (float)m) ? " \u001B[1;32m" : " \u001B[1;31m") + (float)m + "\u001B[0m");
						TreeMap<Float,Float> angles = new TreeMap<Float,Float>();
						for (Polyhedron.Face face : p2.faces) {
							for (int i = 0, n = face.vertices.size(); i < n; i++) {
								Point3D vp = face.vertices.get(i).point;
								Point3D np = face.vertices.get((i + 1) % n).point;
								Point3D pp = face.vertices.get((i + n - 1) % n).point;
								Float key = (float)vp.angle(pp, np);
								Float value = angles.get(key);
								angles.put(key, ((value != null) ? (value + 1) : 1));
							}
						}
						System.out.print("\t- Angles:");
						for (Map.Entry<Float,Float> e : angles.entrySet()) {
							System.out.print((validAngles.contains(e.getKey()) ? " \u001B[1;32m" : " \u001B[1;31m") + e.getValue() + "×" + e.getKey() + "°\u001B[0m");
						}
						System.out.println();
					}
					return null;
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