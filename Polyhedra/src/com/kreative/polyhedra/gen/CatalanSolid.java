package com.kreative.polyhedra.gen;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;
import com.kreative.polyhedra.PolyhedronGen;
import com.kreative.polyhedra.PolyhedronOp;
import com.kreative.polyhedra.op.ConvexHull;
import com.kreative.polyhedra.op.Scale;

public class CatalanSolid extends PolyhedronGen {
	private static final PointCloud.SetUnion UNION = new PointCloud.SetUnion();
	private static final PointCloud.PermuteSign EVERY_SIGN_CHANGE = new PointCloud.PermuteSign("xyzeo");
	private static final PointCloud.PermuteSign EVEN_SIGN_CHANGE = new PointCloud.PermuteSign("xyze");
	private static final PointCloud.PermuteSign ODD_SIGN_CHANGE = new PointCloud.PermuteSign("xyzo");
	private static final PointCloud.PermuteSign XY_SIGN_CHANGE = new PointCloud.PermuteSign("xyeo");
	private static final PointCloud.PermuteSign X_SIGN_CHANGE = new PointCloud.PermuteSign("xeo");
	private static final PointCloud.PermuteOrder EVERY_ORDER = new PointCloud.PermuteOrder("eo");
	private static final PointCloud.PermuteOrder EVEN_ORDER = new PointCloud.PermuteOrder("e");
	private static final PointCloud.PermuteOrder ODD_ORDER = new PointCloud.PermuteOrder("o");
	private static final PointCloud.VectorNegate REFLECT = new PointCloud.VectorNegate();
	
	public static enum FormSpecifier {
		TRIAKIS_TETRAHEDRON (
			0.95940322360024695434, // sqrt(22)*9/44
			1.0606601717798212866, // sqrt(2)*3/4
			1.1022703842524301442, // sqrt(6)*9/20
			1.8371173070873835736, // sqrt(6)*3/4
			1.8,
			3.0,
			"triakistetrahedron", "tikit", "tkt", "du2", "1"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(1.0606601717798214, 1.0606601717798214, 1.0606601717798214); // sqrt(2)*3/4
				Point3D p2 = new Point3D(0.6363961030678928, 0.6363961030678928, 0.6363961030678928); // sqrt(2)*9/20
				return UNION.op(EVEN_SIGN_CHANGE.op(Arrays.asList(p1)), ODD_SIGN_CHANGE.op(Arrays.asList(p2)));
			}
		},
		RHOMBIC_DODECAHEDRON (
			0.75,
			0.86602540378443864676, // sqrt(3)/2
			0.91855865354369178682, // sqrt(6)*3/8
			1.0606601717798212866, // sqrt(2)*3/4
			0.91855865354369178682, // sqrt(6)*3/8
			"rhombicdodecahedron", "rad", "rd", "du7", "2"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(0.5303300858899106433, 0.5303300858899106433, 0.5303300858899106433); // sqrt(2)*3/8
				Point3D p2 = new Point3D(1.0606601717798212866, 0, 0); // sqrt(2)*3/4
				return UNION.op(EVERY_SIGN_CHANGE.op(Arrays.asList(p1)), EVERY_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p2))));
			}
		},
		TETRAKIS_HEXAHEDRON (
			1.4230249470757706994, // sqrt(10)*9/20
			1.5,
			1.5909902576697319299, // sqrt(2)*9/8
			1.8371173070873835736, // sqrt(6)*3/4
			1.5909902576697319299, // sqrt(2)*9/8
			2.1213203435596425732, // sqrt(2)*3/2
			"tetrakishexahedron", "tekah", "tkh", "du8", "3"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(1.0606601717798214, 1.0606601717798214, 1.0606601717798214); // sqrt(2)*3/4
				Point3D p2 = new Point3D(1.5909902576697321, 0, 0); // sqrt(2)*9/8
				return UNION.op(EVERY_SIGN_CHANGE.op(Arrays.asList(p1)), EVERY_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p2))));
			}
		},
		TRIAKIS_OCTAHEDRON (
			1.6382813268065143234, // sqrt((sqrt(2)*16+23)*17)/17
			1.7071067811865475244, // sqrt(2)/2+1
			1.7320508075688772935, // sqrt(3)
			2.4142135623730950488, // sqrt(2)+1
			2.0,
			3.4142135623730950488, // sqrt(2)+2
			"triakisoctahedron", "tikko", "tko", "du9", "4"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(2.4142135623730950488, 0, 0); // sqrt(2)+1
				Point3D p2 = new Point3D(1, 1, 1);
				return UNION.op(EVERY_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p1))), EVERY_SIGN_CHANGE.op(Arrays.asList(p2)));
			}
		},
		DELTOIDAL_ICOSITETRAHEDRON (
			1.2202629537976100741, // sqrt((sqrt(2)*4+7)*34)/17
			1.3065629648763765279, // sqrt((sqrt(2)+2)*2)/2
			1.3396704247226696103, // (sqrt(3)*4+sqrt(6))/7
			1.4142135623730950488, // sqrt(2)
			0.83718607580427642316, // sqrt(10−sqrt(2))*2/7
			1.0823922002923939688, // sqrt((2−sqrt(2))*2)
			"deltoidalicositetrahedron", "strombicicositetrahedron", "smalllancealdisdodecahedron",
			"sladid", "dit", "sit", "sldd", "du10", "5"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(1.4142135623730950488, 0, 0); // sqrt(2)
				Point3D p2 = new Point3D(1, 1, 0);
				Point3D p3 = new Point3D(0.7734590803390136, 0.7734590803390136, 0.7734590803390136); // (sqrt(2)+4)/7
				List<Point3D> lp1 = EVERY_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p1)));
				List<Point3D> lp2 = EVERY_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p2)));
				List<Point3D> lp3 = EVERY_SIGN_CHANGE.op(Arrays.asList(p3));
				return UNION.op(UNION.op(lp1, lp2), lp3);
			}
		},
		DISDYAKIS_DODECAHEDRON (
			2.2097412102566332828, // sqrt((sqrt(2)*8+15)*1746)/97
			2.2630334384537146236, // sqrt((sqrt(2)+2)*6)/2
			2.3203772410170407352, // (sqrt(2)+4)*3/7
			2.4494897427831780982, // sqrt(6)
			2.6754174373368364913, // (sqrt(2)*3+2)*3/7
			1.4500488186822163018, // sqrt((10−sqrt(2))*3)*2/7
			1.9397429472460411059, // sqrt((sqrt(2)+2)*6)*3/7
			2.3644524131865197592, // sqrt((sqrt(2)+10)*6)*2/7
			"disdyakisdodecahedron", "smalldisdyakisdodecahedron",
			"siddykid", "ddkd", "sddkd", "du11", "6"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(2.675417437336837, 0, 0); // (sqrt(2)*3+2)*3/7
				Point3D p2 = new Point3D(1.6407544820340816, 1.6407544820340816, 0); // (sqrt(2)*2+1)*3/7
				Point3D p3 = new Point3D(1.4142135623730951, 1.4142135623730951, 1.4142135623730951); // sqrt(2)
				List<Point3D> lp1 = EVERY_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p1)));
				List<Point3D> lp2 = EVERY_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p2)));
				List<Point3D> lp3 = EVERY_SIGN_CHANGE.op(Arrays.asList(p3));
				return UNION.op(UNION.op(lp1, lp2), lp3);
			}
		},
		PENTAGONAL_ICOSITETRAHEDRON_LAEVO (
			1.1576617909555498021, // sqrt(42*(78+cbrt(66*(6039+49*sqrt(33)))+cbrt(66*(6039−49*sqrt(33)))))/84
			1.2472231679936432518, // sqrt(3*(7+cbrt(199+3*sqrt(33))+cbrt(199−3*sqrt(33))))/6
			1.2820358469890142117, // sqrt(2*(6+cbrt(6*(9+sqrt(33)))+cbrt(6*(9−sqrt(33)))))/4
			1.3614101519264425345, // sqrt(6*(14+cbrt(2*(1777+33*sqrt(33)))+cbrt(2*(1777−33*sqrt(33)))))/12
			0.59346535597198731050, // sqrt(6*(4−cbrt(2*(13+3*sqrt(33)))−cbrt(2*(13−3*sqrt(33)))))/6
			0.84250916244486046725, // sqrt(3*(4+cbrt(19+3*sqrt(33))+cbrt(19−3*sqrt(33))))/6
			"pentagonalicositetrahedronlaevo", "petaloiddisdodecahedronlaevo",
			"pentagonalicositetrahedron", "petaloiddisdodecahedron",
			"elpedid", "lpit", "lpdd", "7",
			"pedid", "pit", "pdd", "du12"
		) {
			public List<Point3D> points() {
				double c0 = 1.36141015192644253450; // sqrt(6*(14+cbrt(2*(1777+33*sqrt(33)))+cbrt(2*(1777-33*sqrt(33)))))/12
				double c1 = 0.21879664300048044102; // sqrt(6*(cbrt(6*(9+sqrt(33)))+cbrt(6*(9-sqrt(33)))-6))/12
				double c2 = 0.74018374136985722281; // sqrt(6*(6+cbrt(6*(9+sqrt(33)))+cbrt(6*(9-sqrt(33)))))/12
				double c3 = 1.02365617811269018236; // sqrt(6*(18+cbrt(6*(9+sqrt(33)))+cbrt(6*(9-sqrt(33)))))/12
				Point3D p1 = new Point3D(c0, 0, 0);
				Point3D p2 = new Point3D(c2, c2, c2);
				Point3D p3 = new Point3D(c1, c2, c3);
				List<Point3D> lp1 = EVERY_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p1)));
				List<Point3D> lp2 = EVERY_SIGN_CHANGE.op(Arrays.asList(p2));
				List<Point3D> lp3 = EVEN_ORDER.op(EVEN_SIGN_CHANGE.op(Arrays.asList(p3)));
				List<Point3D> lp4 = ODD_ORDER.op(ODD_SIGN_CHANGE.op(Arrays.asList(p3)));
				return UNION.op(UNION.op(lp1, lp2), UNION.op(lp3, lp4));
			}
		},
		PENTAGONAL_ICOSITETRAHEDRON_DEXTRO (
			1.1576617909555498021, // sqrt(42*(78+cbrt(66*(6039+49*sqrt(33)))+cbrt(66*(6039−49*sqrt(33)))))/84
			1.2472231679936432518, // sqrt(3*(7+cbrt(199+3*sqrt(33))+cbrt(199−3*sqrt(33))))/6
			1.2820358469890142117, // sqrt(2*(6+cbrt(6*(9+sqrt(33)))+cbrt(6*(9−sqrt(33)))))/4
			1.3614101519264425345, // sqrt(6*(14+cbrt(2*(1777+33*sqrt(33)))+cbrt(2*(1777−33*sqrt(33)))))/12
			0.59346535597198731050, // sqrt(6*(4−cbrt(2*(13+3*sqrt(33)))−cbrt(2*(13−3*sqrt(33)))))/6
			0.84250916244486046725, // sqrt(3*(4+cbrt(19+3*sqrt(33))+cbrt(19−3*sqrt(33))))/6
			"pentagonalicositetrahedrondextro", "petaloiddisdodecahedrondextro",
			"arpedid", "rpit", "rpdd", "8"
		) {
			public List<Point3D> points() {
				return REFLECT.op(PENTAGONAL_ICOSITETRAHEDRON_LAEVO.points());
			}
		},
		RHOMBIC_TRIACONTAHEDRON (
			1.4635254915624211362, // (sqrt(5)*3+5)/8
			1.5388417685876267013, // sqrt(sqrt(5)*2+5)/2
			1.5666546730064754191, // (sqrt(3)*5+sqrt(15))/8
			1.7204774005889669228, // sqrt((sqrt(5)*2+5)*5)/4
			1.0633135104400499152, // sqrt((sqrt(5)+5)*10)/8
			"rhombictriacontahedron", "rhote", "rt", "rtc", "du24", "9"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(0.9045084971874737, 0.9045084971874737, 0.9045084971874737); // (sqrt(5)+5)/8
				Point3D p2 = new Point3D(1.4635254915624211, 0.9045084971874737, 0); // (sqrt(5)*3+5)/8; (sqrt(5)+5)/8
				Point3D p3 = new Point3D(0.5590169943749474, 1.4635254915624211, 0); // sqrt(5)/4; (sqrt(5)*3+5)/8
				return UNION.op(EVERY_SIGN_CHANGE.op(Arrays.asList(p1)), EVEN_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p2, p3))));
			}
		},
		PENTAKIS_DODECAHEDRON (
			2.3771316059838161118, // sqrt((sqrt(5)*6+17)*109)*9/218
			2.4270509831248422723, // (sqrt(5)+1)*3/4
			2.5309268686270615215, // sqrt(sqrt(5)*22+65)*9/38
			2.5980762113533159403, // sqrt(3)*3/2
			1.6446959786840112913, // (sqrt(5)*2−1)*9/19
			1.8541019662496845446, // (sqrt(5)−1)*3/2
			"pentakisdodecahedron", "pakid", "pkd", "du25", "10"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(0.92705098312484227231, 2.4270509831248422723, 0); // (sqrt(5)-1)*3/4; (sqrt(5)+1)*3/4
				Point3D p2 = new Point3D(2.1529349866775070571, 1.3305869973355014114, 0); // (sqrt(5)*5+7)*9/76; (sqrt(5)+9)*9/76
				Point3D p3 = new Point3D(1.5, 1.5, 1.5);
				return UNION.op(EVEN_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p1, p2))), EVERY_SIGN_CHANGE.op(Arrays.asList(p3)));
			}
		},
		TRIAKIS_ICOSAHEDRON (
			2.8852583129200411870, // sqrt((sqrt(5)*18+41)*61)*5/122
			2.9270509831248422723, // (sqrt(5)*3+5)/4
			2.9413907079821512843, // (sqrt(3)*3+sqrt(15)*2)*5/22
			3.44095480117793384552, // sqrt((sqrt(5)*2+5)*5)/2
			2.09910635852267947646, // (sqrt(5)+7)*5/22
			3.6180339887498948482, // (sqrt(5)+5)/2
			"triakisicosahedron", "tiki", "tki", "du26", "11"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(2.9270509831248422723, 1.8090169943749474241, 0); // (sqrt(5)*3+5)/4; (sqrt(5)+5)/4
				Point3D p2 = new Point3D(1.0495531792613397382, 2.7477658963066986911, 0); // (sqrt(5)+7)*5/44; (sqrt(5)*5+13)*5/44
				Point3D p3 = new Point3D(1.6982127170453589529, 1.6982127170453589529, 1.6982127170453589529); // (sqrt(5)*2+3)*5/22
				return UNION.op(EVEN_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p1, p2))), EVERY_SIGN_CHANGE.op(Arrays.asList(p3)));
			}
		},
		DELTOIDAL_HEXECONTAHEDRON (
			2.1209910195184334175, // sqrt((sqrt(5)*8+19)*205)/41
			2.1762508994828215111, // sqrt((sqrt(5)*2+5)*2)/2
			2.1956534020612776371, // (sqrt(3)*5+sqrt(15)*4)/11
			2.2360679774997896964, // sqrt(5)
			2.2939698674519558970, // sqrt((sqrt(5)*2+5)*5)/3
			0.80499198439381116988, // sqrt((85−sqrt(5)*31)*5)/11
			1.2391601148672816338, // sqrt((5−sqrt(5))*5)/3
			"deltoidalhexecontahedron", "strombichexecontahedron", "smalllancealditriacontahedron",
			"sladit", "dhc", "shc", "sldt", "du27", "12"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(2.2360679774997896964, 0, 0); // sqrt(5)
				Point3D p2 = new Point3D(0.78345763534089953165, 2.0511187180680957849, 0); // (sqrt(5)+15)/22; (sqrt(5)*9+25)/22
				Point3D p3 = new Point3D(1.9513673220832281815, 1.2060113295832982827, 0); // (sqrt(5)*3+5)/6; (sqrt(5)+5)/6
				Point3D p4 = new Point3D(0.6909830056250525759, 1.1180339887498948482, 1.8090169943749474241); // (5-sqrt(5))/4; sqrt(5)/2; (sqrt(5)+5)/4
				Point3D p5 = new Point3D(1.26766108272719625324, 1.26766108272719625324, 1.26766108272719625324); // (sqrt(5)*4+5)/11
				List<Point3D> lp1 = EVEN_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p1)));
				List<Point3D> lp2 = EVEN_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p2, p3)));
				List<Point3D> lp3 = EVEN_ORDER.op(EVERY_SIGN_CHANGE.op(Arrays.asList(p4)));
				List<Point3D> lp4 = EVERY_SIGN_CHANGE.op(Arrays.asList(p5));
				return UNION.op(UNION.op(lp1, lp2), UNION.op(lp3, lp4));
			}
		},
		DISDYAKIS_TRIACONTAHEDRON (
			3.73664645608314244845, // sqrt((sqrt(5)*16+39)*10845)/241
			3.7693771279217166027, // sqrt((sqrt(5)*2+5)*6)/2
			3.8029832481815887597, // (sqrt(5)*4+5)*3/11
			3.8729833462074168852, // sqrt(15)
			4.1291457614135206146, // sqrt((sqrt(5)*2+5)*5)*3/5
			1.3942870166557737040, // sqrt((85−sqrt(5)*31)*15)/11
			2.19017447980650378252, // sqrt((sqrt(5)*19+65)*15)*3/55
			2.5755459331956214849, // sqrt((5−sqrt(5))*15)*2/5
			"disdyakistriacontahedron", "smalldisdyakistriacontahedron",
			"siddykit", "ddkt", "ddktc", "sddkt", "sddktc", "du28", "13"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(3.8029832481815887597, 0, 0); // (sqrt(5)*4+5)*3/11
				Point3D p2 = new Point3D(1.3819660112501051518, 3.6180339887498948482, 0); // (5-sqrt(5))/2; (sqrt(5)+5)/2
				Point3D p3 = new Point3D(3.5124611797498107268, 2.1708203932499369089, 0); // (sqrt(5)*9+15)/10; (sqrt(5)+5)*3/10
				Point3D p4 = new Point3D(1.1751864530113492975, 1.9014916240907943799, 3.0766780771021436773); // (sqrt(5)+15)*3/44; (sqrt(5)*4+5)*3/22; (sqrt(5)*27+75)/44
				Point3D p5 = new Point3D(2.2360679774997896964, 2.2360679774997896964, 2.2360679774997896964); // sqrt(5)
				List<Point3D> lp1 = EVEN_ORDER.op(X_SIGN_CHANGE.op(Arrays.asList(p1)));
				List<Point3D> lp2 = EVEN_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p2, p3)));
				List<Point3D> lp3 = EVEN_ORDER.op(EVERY_SIGN_CHANGE.op(Arrays.asList(p4)));
				List<Point3D> lp4 = EVERY_SIGN_CHANGE.op(Arrays.asList(p5));
				return UNION.op(UNION.op(lp1, lp2), UNION.op(lp3, lp4));
			}
		},
		PENTAGONAL_HEXECONTAHEDRON_LAEVO (
			// phi = (sqrt(5)+1)/2
			// xi = cbrt((phi+sqrt(phi−5/27))/2)+cbrt((phi−sqrt(phi−5/27))/2)
			2.0398731549542789999, // xi*sqrt(209*((xi^2)*(104*phi−7)+xi*(52+153*phi)+(195−phi)))/418
			2.0970538352520879924, // phi*sqrt(xi*(xi+phi)+1)/2
			2.1172098986276657420, // sqrt(3*(xi*phi+1+phi+(1/xi)))/2
			2.2200006991613182111, // sqrt((xi^2)*(1009+1067*phi)+xi*(1168+2259*phi)+(1097+941*phi))/62
			0.58289953474498241442, // 1/xi
			1.0199882470228458983, // (xi*(2+7*phi)+(5*phi−3)+2*(8−3*phi)/xi)/31
			"pentagonalhexecontahedronlaevo", "smallpetaloidditriacontahedronlaevo",
			"pentagonalhexecontahedron", "smallpetaloidditriacontahedron",
			"elsapedit", "lphc", "lspdt", "14",
			"sapedit", "phc", "spdt", "du29"
		) {
			public List<Point3D> points() {
				Point3D p1 = new Point3D(0.19289371135235902211, 0.21848337012732122437, -2.0970538352520879924); // phi*sqrt(3-(xi^2))/2, phi*sqrt((xi-1-(1/xi))*phi)/(2*xi), phi*sqrt(xi*(xi+phi)+1)/2
				Point3D p2 = new Point3D(0.37482165811456229527, 1.13706613386050418841, -1.7461864409858263457); // phi*sqrt((xi-1-(1/xi))*phi)/2; sqrt(2+3*phi-2*xi+(3/xi))/2; phi*sqrt((xi^2)+xi)/2
				Point3D p3 = new Point3D(0.56771536946692131737, 0.82495755267627584627, 1.86540131081769566577); // (xi^2)*phi*sqrt(3-(xi^2))/2; sqrt((xi+2)*phi+2)/(2*xi); (phi^3)*sqrt(xi*(xi+phi)+1)/(2*(xi^2))
				Point3D p4 = new Point3D(0.72833517695719147736, 1.27209628257581214614, 1.52770307085850512137); // phi*sqrt(1-xi+(1+phi)/xi)/2; phi*sqrt((xi^2)+xi+1+phi)/(2*xi); phi*sqrt((xi^2)+2*xi*phi+2)/(2*xi)
				Point3D p5 = new Point3D(0.92122888830955049947, 0.95998770139158380399, -1.6469179406903744414); // sqrt(-(xi^2)*(2+phi)+xi*(1+3*phi)+4)/2; (1+phi)*sqrt(1+(1/xi))/(2*xi); sqrt((xi^2)*(1+2*phi)-phi)/2
				Point3D p6 = new Point3D(1.22237170490362309266, 1.22237170490362309266, 1.22237170490362309266); // phi*sqrt(xi*(xi+phi)+1)/(2*xi)
				Point3D p7 = new Point3D(0.75546726051659557971, 1.97783896542021867237, 0); // sqrt(xi*(xi+phi)+1)/(2*xi); (phi^2)*sqrt(xi*(xi+phi)+1)/(2*xi)
				Point3D p8 = new Point3D(1.88844538928366915418, 1.16712343647533397917, 0); // sqrt((xi^2)*(617+842*phi)+xi*(919+1589*phi)+(627+784*phi))/62; sqrt((xi^2)*(392+225*phi)+xi*(249+670*phi)+(470+157*phi))/62
				List<Point3D> lp1 = EVEN_ORDER.op(EVEN_SIGN_CHANGE.op(Arrays.asList(p1, p2, p3, p4, p5)));
				List<Point3D> lp2 = EVERY_SIGN_CHANGE.op(Arrays.asList(p6));
				List<Point3D> lp3 = EVEN_ORDER.op(XY_SIGN_CHANGE.op(Arrays.asList(p7, p8)));
				return UNION.op(lp1, UNION.op(lp2, lp3));
				// Math is hard. Let's go shopping.
			}
		},
		PENTAGONAL_HEXECONTAHEDRON_DEXTRO (
			// phi = (sqrt(5)+1)/2
			// xi = cbrt((phi+sqrt(phi−5/27))/2)+cbrt((phi−sqrt(phi−5/27))/2)
			2.0398731549542789999, // xi*sqrt(209*((xi^2)*(104*phi−7)+xi*(52+153*phi)+(195−phi)))/418
			2.0970538352520879924, // phi*sqrt(xi*(xi+phi)+1)/2
			2.1172098986276657420, // sqrt(3*(xi*phi+1+phi+(1/xi)))/2
			2.2200006991613182111, // sqrt((xi^2)*(1009+1067*phi)+xi*(1168+2259*phi)+(1097+941*phi))/62
			0.58289953474498241442, // 1/xi
			1.0199882470228458983, // (xi*(2+7*phi)+(5*phi−3)+2*(8−3*phi)/xi)/31
			"pentagonalhexecontahedrondextro", "smallpetaloidditriacontahedrondextro",
			"arsapedit", "rphc", "rspdt", "15"
		) {
			public List<Point3D> points() {
				return REFLECT.op(PENTAGONAL_HEXECONTAHEDRON_LAEVO.points());
			}
		};
		public final double inradiusFactor;
		public final double midradiusFactor;
		public final double smallCircumradiusFactor;
		public final double mediumCircumradiusFactor;
		public final double largeCircumradiusFactor;
		public final double shortEdgeLengthFactor;
		public final double mediumEdgeLengthFactor;
		public final double longEdgeLengthFactor;
		private final List<String> names;
		private FormSpecifier(double irf, double mrf, double scrf, double lcrf, double elf, String... names) {
			this.inradiusFactor = irf;
			this.midradiusFactor = mrf;
			this.smallCircumradiusFactor = scrf;
			this.mediumCircumradiusFactor = (scrf + lcrf) / 2;
			this.largeCircumradiusFactor = lcrf;
			this.shortEdgeLengthFactor = elf;
			this.mediumEdgeLengthFactor = elf;
			this.longEdgeLengthFactor = elf;
			this.names = Arrays.asList(names);
		}
		private FormSpecifier(double irf, double mrf, double scrf, double lcrf, double self, double lelf, String... names) {
			this.inradiusFactor = irf;
			this.midradiusFactor = mrf;
			this.smallCircumradiusFactor = scrf;
			this.mediumCircumradiusFactor = (scrf + lcrf) / 2;
			this.largeCircumradiusFactor = lcrf;
			this.shortEdgeLengthFactor = self;
			this.mediumEdgeLengthFactor = (self + lelf) / 2;
			this.longEdgeLengthFactor = lelf;
			this.names = Arrays.asList(names);
		}
		private FormSpecifier(double irf, double mrf, double scrf, double mcrf, double lcrf, double self, double lelf, String... names) {
			this.inradiusFactor = irf;
			this.midradiusFactor = mrf;
			this.smallCircumradiusFactor = scrf;
			this.mediumCircumradiusFactor = mcrf;
			this.largeCircumradiusFactor = lcrf;
			this.shortEdgeLengthFactor = self;
			this.mediumEdgeLengthFactor = (self + lelf) / 2;
			this.longEdgeLengthFactor = lelf;
			this.names = Arrays.asList(names);
		}
		private FormSpecifier(double irf, double mrf, double scrf, double mcrf, double lcrf, double self, double melf, double lelf, String... names) {
			this.inradiusFactor = irf;
			this.midradiusFactor = mrf;
			this.smallCircumradiusFactor = scrf;
			this.mediumCircumradiusFactor = mcrf;
			this.largeCircumradiusFactor = lcrf;
			this.shortEdgeLengthFactor = self;
			this.mediumEdgeLengthFactor = melf;
			this.longEdgeLengthFactor = lelf;
			this.names = Arrays.asList(names);
		}
		public abstract List<Point3D> points();
		public static FormSpecifier forIndex(int index) {
			switch (index) {
				case  1: return TRIAKIS_TETRAHEDRON;
				case  2: return RHOMBIC_DODECAHEDRON;
				case  3: return TETRAKIS_HEXAHEDRON;
				case  4: return TRIAKIS_OCTAHEDRON;
				case  5: return DELTOIDAL_ICOSITETRAHEDRON;
				case  6: return DISDYAKIS_DODECAHEDRON;
				case  7: return PENTAGONAL_ICOSITETRAHEDRON_LAEVO;
				case  8: return PENTAGONAL_ICOSITETRAHEDRON_DEXTRO;
				case  9: return RHOMBIC_TRIACONTAHEDRON;
				case 10: return PENTAKIS_DODECAHEDRON;
				case 11: return TRIAKIS_ICOSAHEDRON;
				case 12: return DELTOIDAL_HEXECONTAHEDRON;
				case 13: return DISDYAKIS_TRIACONTAHEDRON;
				case 14: return PENTAGONAL_HEXECONTAHEDRON_LAEVO;
				case 15: return PENTAGONAL_HEXECONTAHEDRON_DEXTRO;
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
		INRADIUS {
			public double toScale(FormSpecifier form, double radius) { return radius / form.inradiusFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.inradiusFactor / scale; }
		},
		MIDRADIUS {
			public double toScale(FormSpecifier form, double radius) { return radius / form.midradiusFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.midradiusFactor / scale; }
		},
		SMALL_CIRCUMRADIUS {
			public double toScale(FormSpecifier form, double radius) { return radius / form.smallCircumradiusFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.smallCircumradiusFactor / scale; }
		},
		MEDIUM_CIRCUMRADIUS {
			public double toScale(FormSpecifier form, double radius) { return radius / form.mediumCircumradiusFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.mediumCircumradiusFactor / scale; }
		},
		LARGE_CIRCUMRADIUS {
			public double toScale(FormSpecifier form, double radius) { return radius / form.largeCircumradiusFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.largeCircumradiusFactor / scale; }
		},
		SHORT_EDGE_LENGTH {
			public double toScale(FormSpecifier form, double radius) { return radius / form.shortEdgeLengthFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.shortEdgeLengthFactor / scale; }
		},
		MEDIUM_EDGE_LENGTH {
			public double toScale(FormSpecifier form, double radius) { return radius / form.mediumEdgeLengthFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.mediumEdgeLengthFactor / scale; }
		},
		LONG_EDGE_LENGTH {
			public double toScale(FormSpecifier form, double radius) { return radius / form.longEdgeLengthFactor; }
			public double fromScale(FormSpecifier form, double scale) { return form.longEdgeLengthFactor / scale; }
		},
		DUAL_EDGE_LENGTH {
			public double toScale(FormSpecifier form, double length) { return length; }
			public double fromScale(FormSpecifier form, double scale) { return scale; }
		};
		public abstract double toScale(FormSpecifier form, double size);
		public abstract double fromScale(FormSpecifier form, double scale);
	}
	
	private final PolyhedronGen gen;
	private final PolyhedronOp op1;
	private final PolyhedronOp op2;
	
	public CatalanSolid(FormSpecifier form, SizeSpecifier spec, double size, Color color) {
		this.gen = new PointCloud(form.points());
		this.op1 = new ConvexHull(color);
		this.op2 = new Scale(spec.toScale(form, size));
	}
	
	public Polyhedron gen() {
		return op2.op(op1.op(gen.gen()));
	}
	
	public static class Factory extends PolyhedronGen.Factory<CatalanSolid> {
		public String name() { return "CatalanSolid"; }
		
		public CatalanSolid parse(String[] args) {
			FormSpecifier form = FormSpecifier.TRIAKIS_TETRAHEDRON;
			SizeSpecifier spec = SizeSpecifier.LARGE_CIRCUMRADIUS;
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
				} else if (arg.equalsIgnoreCase("-i") && argi < args.length) {
					spec = SizeSpecifier.INRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-m") && argi < args.length) {
					spec = SizeSpecifier.MIDRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-p") && argi < args.length) {
					spec = SizeSpecifier.SMALL_CIRCUMRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-q") && argi < args.length) {
					spec = SizeSpecifier.MEDIUM_CIRCUMRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-r") && argi < args.length) {
					spec = SizeSpecifier.LARGE_CIRCUMRADIUS;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-a") && argi < args.length) {
					spec = SizeSpecifier.SHORT_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-b") && argi < args.length) {
					spec = SizeSpecifier.MEDIUM_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-e") && argi < args.length) {
					spec = SizeSpecifier.LONG_EDGE_LENGTH;
					size = parseDouble(args[argi++], size);
				} else if (arg.equalsIgnoreCase("-d") && argi < args.length) {
					spec = SizeSpecifier.DUAL_EDGE_LENGTH;
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
			return new CatalanSolid(form, spec, size, c);
		}
		
		public Option[] options() {
			final String toc = (
				"which Catalan solid (by index or name)"
				+ "\n\t\t 1  dU2   tkt   triakis tetrahedron"
				+ "\n\t\t 2  dU7   rd    rhombic dodecahedron"
				+ "\n\t\t 3  dU8   tkh   tetrakis hexahedron"
				+ "\n\t\t 4  dU9   tko   triakis octahedron"
				+ "\n\t\t 5  dU10  dit   deltoidal icositetrahedron"
				+ "\n\t\t 6  dU11  ddkd  disdyakis dodecahedron"
				+ "\n\t\t 7  dU12  lpit  pentagonal icositetrahedron (laevo)"
				+ "\n\t\t 8  dU12  rpit  pentagonal icositetrahedron (dextro)"
				+ "\n\t\t 9  dU24  rt    rhombic triacontahedron"
				+ "\n\t\t10  dU25  pkd   pentakis dodecahedron"
				+ "\n\t\t11  dU26  tki   triakis icosahedron"
				+ "\n\t\t12  dU27  dhc   deltoidal hexecontahedron"
				+ "\n\t\t13  dU28  ddkt  disdyakis triacontahedron"
				+ "\n\t\t14  dU29  lphc  pentagonal hexecontahedron (laevo)"
				+ "\n\t\t15  dU29  rphc  pentagonal hexecontahedron (dextro)"
			);
			return new Option[] {
				new Option("n", Type.INT, toc),
				new Option("p", Type.REAL, "radius of smallest circumscribed sphere", "q","r","m","i","a","b","e","d"),
				new Option("q", Type.REAL, "radius of median circumscribed sphere", "p","r","m","i","a","b","e","d"),
				new Option("r", Type.REAL, "radius of largest circumscribed sphere", "p","q","m","i","a","b","e","d"),
				new Option("m", Type.REAL, "radius of sphere tangent to edges", "p","q","r","i","a","b","e","d"),
				new Option("i", Type.REAL, "radius of inscribed sphere", "p","q","r","m","a","b","e","d"),
				new Option("a", Type.REAL, "shortest edge length", "p","q","r","m","i","b","e","d"),
				new Option("b", Type.REAL, "median edge length", "p","q","r","m","i","a","e","d"),
				new Option("e", Type.REAL, "longest edge length", "p","q","r","m","i","a","b","d"),
				new Option("d", Type.REAL, "edge length of dual polyhedron", "p","q","r","m","i","a","b","e"),
				new Option("c", Type.COLOR, "color"),
			};
		}
	}
	
	public static void main(String[] args) {
		new Factory().main(args);
	}
}