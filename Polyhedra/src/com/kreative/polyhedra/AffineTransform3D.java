package com.kreative.polyhedra;

import java.awt.geom.NoninvertibleTransformException;
import java.util.Arrays;

public class AffineTransform3D extends PointTransform3D {
	public static final AffineTransform3D IDENTITY = new AffineTransform3D(1,0,0,0,1,0,0,0,1,0,0,0);
	
	private static int qsin(int numQuadrants) {
		switch (numQuadrants & 3) {
			case 1: return 1;
			case 3: return -1;
			default: return 0;
		}
	}
	
	private static int qcos(int numQuadrants) {
		switch (numQuadrants & 3) {
			case 0: return 1;
			case 2: return -1;
			default: return 0;
		}
	}
	
	public static AffineTransform3D getQuadrantRotateXInstance(int numQuadrants) {
		return new AffineTransform3D(
			1, 0, 0,
			0, qcos(numQuadrants), qsin(numQuadrants),
			0, -qsin(numQuadrants), qcos(numQuadrants),
			0, 0, 0
		);
	}
	
	public static AffineTransform3D getQuadrantRotateYInstance(int numQuadrants) {
		return new AffineTransform3D(
			qcos(numQuadrants), 0, -qsin(numQuadrants),
			0, 1, 0,
			qsin(numQuadrants), 0, qcos(numQuadrants),
			0, 0, 0
		);
	}
	
	public static AffineTransform3D getQuadrantRotateZInstance(int numQuadrants) {
		return new AffineTransform3D(
			qcos(numQuadrants), qsin(numQuadrants), 0,
			-qsin(numQuadrants), qcos(numQuadrants), 0,
			0, 0, 1,
			0, 0, 0
		);
	}
	
	public static AffineTransform3D getRotateXInstance(double theta) {
		return new AffineTransform3D(
			1, 0, 0,
			0, Math.cos(theta), Math.sin(theta),
			0, -Math.sin(theta), Math.cos(theta),
			0, 0, 0
		);
	}
	
	public static AffineTransform3D getRotateYInstance(double theta) {
		return new AffineTransform3D(
			Math.cos(theta), 0, -Math.sin(theta),
			0, 1, 0,
			Math.sin(theta), 0, Math.cos(theta),
			0, 0, 0
		);
	}
	
	public static AffineTransform3D getRotateZInstance(double theta) {
		return new AffineTransform3D(
			Math.cos(theta), Math.sin(theta), 0,
			-Math.sin(theta), Math.cos(theta), 0,
			0, 0, 1,
			0, 0, 0
		);
	}
	
	public static AffineTransform3D getScaleInstance(double sx, double sy, double sz) {
		return new AffineTransform3D(sx, 0, 0, 0, sy, 0, 0, 0, sz, 0, 0, 0);
	}
	
	public static AffineTransform3D getShearInstance(
		double shxy, double shxz, double shyx, double shyz, double shzx, double shzy
	) {
		return new AffineTransform3D(1, shyx, shzx, shxy, 1, shzy, shxz, shyz, 1, 0, 0, 0);
	}
	
	public static AffineTransform3D getTranslateInstance(double tx, double ty, double tz) {
		return new AffineTransform3D(1, 0, 0, 0, 1, 0, 0, 0, 1, tx, ty, tz);
	}
	
	private final double m00, m01, m02, m03;
	private final double m10, m11, m12, m13;
	private final double m20, m21, m22, m23;
	
	public AffineTransform3D(
		double m00, double m10, double m20,
		double m01, double m11, double m21,
		double m02, double m12, double m22,
		double m03, double m13, double m23
	) {
		this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
		this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
		this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
	}
	
	public AffineTransform3D(double[] flatmatrix) {
		this.m00 = (flatmatrix.length > 0) ? flatmatrix[0] : 1;
		this.m10 = (flatmatrix.length > 1) ? flatmatrix[1] : 0;
		this.m20 = (flatmatrix.length > 2) ? flatmatrix[2] : 0;
		this.m01 = (flatmatrix.length > 3) ? flatmatrix[3] : 0;
		this.m11 = (flatmatrix.length > 4) ? flatmatrix[4] : 1;
		this.m21 = (flatmatrix.length > 5) ? flatmatrix[5] : 0;
		this.m02 = (flatmatrix.length > 6) ? flatmatrix[6] : 0;
		this.m12 = (flatmatrix.length > 7) ? flatmatrix[7] : 0;
		this.m22 = (flatmatrix.length > 8) ? flatmatrix[8] : 1;
		this.m03 = (flatmatrix.length > 9) ? flatmatrix[9] : 0;
		this.m13 = (flatmatrix.length > 10) ? flatmatrix[10] : 0;
		this.m23 = (flatmatrix.length > 11) ? flatmatrix[11] : 0;
	}
	
	public AffineTransform3D concatenate(AffineTransform3D tx) {
		return new AffineTransform3D(
			this.m00 * tx.m00 + this.m01 * tx.m10 + this.m02 * tx.m20,
			this.m10 * tx.m00 + this.m11 * tx.m10 + this.m12 * tx.m20,
			this.m20 * tx.m00 + this.m21 * tx.m10 + this.m22 * tx.m20,
			this.m00 * tx.m01 + this.m01 * tx.m11 + this.m02 * tx.m21,
			this.m10 * tx.m01 + this.m11 * tx.m11 + this.m12 * tx.m21,
			this.m20 * tx.m01 + this.m21 * tx.m11 + this.m22 * tx.m21,
			this.m00 * tx.m02 + this.m01 * tx.m12 + this.m02 * tx.m22,
			this.m10 * tx.m02 + this.m11 * tx.m12 + this.m12 * tx.m22,
			this.m20 * tx.m02 + this.m21 * tx.m12 + this.m22 * tx.m22,
			this.m00 * tx.m03 + this.m01 * tx.m13 + this.m02 * tx.m23 + this.m03,
			this.m10 * tx.m03 + this.m11 * tx.m13 + this.m12 * tx.m23 + this.m13,
			this.m20 * tx.m03 + this.m21 * tx.m13 + this.m22 * tx.m23 + this.m23
		);
	}
	
	public Point3D deltaTransform(double x, double y, double z) {
		double x1 = m00 * x + m01 * y + m02 * z;
		double y1 = m10 * x + m11 * y + m12 * z;
		double z1 = m20 * x + m21 * y + m22 * z;
		return new Point3D(x1, y1, z1);
	}
	
	public Point3D deltaTransform(Point3D point) {
		double x1 = m00 * point.getX() + m01 * point.getY() + m02 * point.getZ();
		double y1 = m10 * point.getX() + m11 * point.getY() + m12 * point.getZ();
		double z1 = m20 * point.getX() + m21 * point.getY() + m22 * point.getZ();
		return new Point3D(x1, y1, z1);
	}
	
	public boolean equals(Object obj) {
		return (
			(obj instanceof AffineTransform3D)
			&& this.m00 == ((AffineTransform3D)obj).m00
			&& this.m01 == ((AffineTransform3D)obj).m01
			&& this.m02 == ((AffineTransform3D)obj).m02
			&& this.m03 == ((AffineTransform3D)obj).m03
			&& this.m10 == ((AffineTransform3D)obj).m10
			&& this.m11 == ((AffineTransform3D)obj).m11
			&& this.m12 == ((AffineTransform3D)obj).m12
			&& this.m13 == ((AffineTransform3D)obj).m13
			&& this.m20 == ((AffineTransform3D)obj).m20
			&& this.m21 == ((AffineTransform3D)obj).m21
			&& this.m22 == ((AffineTransform3D)obj).m22
			&& this.m23 == ((AffineTransform3D)obj).m23
		);
	}
	
	public boolean equals(AffineTransform3D tx, double epsilon) {
		return (
			(tx != null)
			&& Math.abs(this.m00 - tx.m00) < epsilon
			&& Math.abs(this.m01 - tx.m01) < epsilon
			&& Math.abs(this.m02 - tx.m02) < epsilon
			&& Math.abs(this.m03 - tx.m03) < epsilon
			&& Math.abs(this.m10 - tx.m10) < epsilon
			&& Math.abs(this.m11 - tx.m11) < epsilon
			&& Math.abs(this.m12 - tx.m12) < epsilon
			&& Math.abs(this.m13 - tx.m13) < epsilon
			&& Math.abs(this.m20 - tx.m20) < epsilon
			&& Math.abs(this.m21 - tx.m21) < epsilon
			&& Math.abs(this.m22 - tx.m22) < epsilon
			&& Math.abs(this.m23 - tx.m23) < epsilon
		);
	}
	
	public double getDeterminant() {
		double s0 = m11 * m22 - m12 * m21;
		double s1 = m12 * m20 - m10 * m22;
		double s2 = m10 * m21 - m11 * m20;
		return m00 * s0 + m01 * s1 + m02 * s2;
	}
	
	public double[] getMatrix(double[] flatmatrix) {
		if (flatmatrix == null) flatmatrix = new double[12];
		if (flatmatrix.length > 0) flatmatrix[0] = m00;
		if (flatmatrix.length > 1) flatmatrix[1] = m10;
		if (flatmatrix.length > 2) flatmatrix[2] = m20;
		if (flatmatrix.length > 3) flatmatrix[3] = m01;
		if (flatmatrix.length > 4) flatmatrix[4] = m11;
		if (flatmatrix.length > 5) flatmatrix[5] = m21;
		if (flatmatrix.length > 6) flatmatrix[6] = m02;
		if (flatmatrix.length > 7) flatmatrix[7] = m12;
		if (flatmatrix.length > 8) flatmatrix[8] = m22;
		if (flatmatrix.length > 9) flatmatrix[9] = m03;
		if (flatmatrix.length > 10) flatmatrix[10] = m13;
		if (flatmatrix.length > 11) flatmatrix[11] = m23;
		return flatmatrix;
	}
	
	public double getScaleX() { return m00; }
	public double getScaleY() { return m11; }
	public double getScaleZ() { return m22; }
	
	public double getShearXY() { return m01; }
	public double getShearXZ() { return m02; }
	public double getShearYX() { return m10; }
	public double getShearYZ() { return m12; }
	public double getShearZX() { return m20; }
	public double getShearZY() { return m21; }
	
	public double getTranslateX() { return m03; }
	public double getTranslateY() { return m13; }
	public double getTranslateZ() { return m23; }
	
	public int hashCode() {
		return Arrays.asList(
			Double.valueOf(m00), Double.valueOf(m01), Double.valueOf(m02), Double.valueOf(m03),
			Double.valueOf(m10), Double.valueOf(m11), Double.valueOf(m12), Double.valueOf(m13),
			Double.valueOf(m20), Double.valueOf(m21), Double.valueOf(m22), Double.valueOf(m23)
		).hashCode();
	}
	
	public Point3D inverseTransform(double x, double y, double z) throws NoninvertibleTransformException {
		double s0 = m11 * m22 - m12 * m21;
		double s1 = m12 * m20 - m10 * m22;
		double s2 = m10 * m21 - m11 * m20;
		double det = m00 * s0 + m01 * s1 + m02 * s2;
		if (det == 0) throw new NoninvertibleTransformException(toString());
		double s3 = m02 * m21 - m01 * m22;
		double s4 = m00 * m22 - m02 * m20;
		double s5 = m01 * m20 - m00 * m21;
		double s6 = m01 * m12 - m02 * m11;
		double s7 = m02 * m10 - m00 * m12;
		double s8 = m00 * m11 - m01 * m10;
		double m00 = s0 / det, m10 = s1 / det, m20 = s2 / det;
		double m01 = s3 / det, m11 = s4 / det, m21 = s5 / det;
		double m02 = s6 / det, m12 = s7 / det, m22 = s8 / det;
		double m03 = (this.m03 * s0 + this.m13 * s3 + this.m23 * s6) / -det;
		double m13 = (this.m03 * s1 + this.m13 * s4 + this.m23 * s7) / -det;
		double m23 = (this.m03 * s2 + this.m13 * s5 + this.m23 * s8) / -det;
		double x1 = m00 * x + m01 * y + m02 * z + m03;
		double y1 = m10 * x + m11 * y + m12 * z + m13;
		double z1 = m20 * x + m21 * y + m22 * z + m23;
		return new Point3D(x1, y1, z1);
	}
	
	public Point3D inverseTransform(Point3D point) throws NoninvertibleTransformException {
		double s0 = m11 * m22 - m12 * m21;
		double s1 = m12 * m20 - m10 * m22;
		double s2 = m10 * m21 - m11 * m20;
		double det = m00 * s0 + m01 * s1 + m02 * s2;
		if (det == 0) throw new NoninvertibleTransformException(toString());
		double s3 = m02 * m21 - m01 * m22;
		double s4 = m00 * m22 - m02 * m20;
		double s5 = m01 * m20 - m00 * m21;
		double s6 = m01 * m12 - m02 * m11;
		double s7 = m02 * m10 - m00 * m12;
		double s8 = m00 * m11 - m01 * m10;
		double m00 = s0 / det, m10 = s1 / det, m20 = s2 / det;
		double m01 = s3 / det, m11 = s4 / det, m21 = s5 / det;
		double m02 = s6 / det, m12 = s7 / det, m22 = s8 / det;
		double m03 = (this.m03 * s0 + this.m13 * s3 + this.m23 * s6) / -det;
		double m13 = (this.m03 * s1 + this.m13 * s4 + this.m23 * s7) / -det;
		double m23 = (this.m03 * s2 + this.m13 * s5 + this.m23 * s8) / -det;
		double x1 = m00 * point.getX() + m01 * point.getY() + m02 * point.getZ() + m03;
		double y1 = m10 * point.getX() + m11 * point.getY() + m12 * point.getZ() + m13;
		double z1 = m20 * point.getX() + m21 * point.getY() + m22 * point.getZ() + m23;
		return new Point3D(x1, y1, z1);
	}
	
	public AffineTransform3D invert() throws NoninvertibleTransformException {
		double s0 = m11 * m22 - m12 * m21;
		double s1 = m12 * m20 - m10 * m22;
		double s2 = m10 * m21 - m11 * m20;
		double det = m00 * s0 + m01 * s1 + m02 * s2;
		if (det == 0) throw new NoninvertibleTransformException(toString());
		double s3 = m02 * m21 - m01 * m22;
		double s4 = m00 * m22 - m02 * m20;
		double s5 = m01 * m20 - m00 * m21;
		double s6 = m01 * m12 - m02 * m11;
		double s7 = m02 * m10 - m00 * m12;
		double s8 = m00 * m11 - m01 * m10;
		return new AffineTransform3D(
			s0 / det, s1 / det, s2 / det,
			s3 / det, s4 / det, s5 / det,
			s6 / det, s7 / det, s8 / det,
			(m03 * s0 + m13 * s3 + m23 * s6) / -det,
			(m03 * s1 + m13 * s4 + m23 * s7) / -det,
			(m03 * s2 + m13 * s5 + m23 * s8) / -det
		);
	}
	
	public boolean isIdentity() {
		return (
			m00 == 1 && m01 == 0 && m02 == 0 && m03 == 0 &&
			m10 == 0 && m11 == 1 && m12 == 0 && m13 == 0 &&
			m20 == 0 && m21 == 0 && m22 == 1 && m23 == 0
		);
	}
	
	public boolean isIdentity(double epsilon) {
		return equals(IDENTITY, epsilon);
	}
	
	public AffineTransform3D preConcatenate(AffineTransform3D tx) {
		return new AffineTransform3D(
			tx.m00 * this.m00 + tx.m01 * this.m10 + tx.m02 * this.m20,
			tx.m10 * this.m00 + tx.m11 * this.m10 + tx.m12 * this.m20,
			tx.m20 * this.m00 + tx.m21 * this.m10 + tx.m22 * this.m20,
			tx.m00 * this.m01 + tx.m01 * this.m11 + tx.m02 * this.m21,
			tx.m10 * this.m01 + tx.m11 * this.m11 + tx.m12 * this.m21,
			tx.m20 * this.m01 + tx.m21 * this.m11 + tx.m22 * this.m21,
			tx.m00 * this.m02 + tx.m01 * this.m12 + tx.m02 * this.m22,
			tx.m10 * this.m02 + tx.m11 * this.m12 + tx.m12 * this.m22,
			tx.m20 * this.m02 + tx.m21 * this.m12 + tx.m22 * this.m22,
			tx.m00 * this.m03 + tx.m01 * this.m13 + tx.m02 * this.m23 + tx.m03,
			tx.m10 * this.m03 + tx.m11 * this.m13 + tx.m12 * this.m23 + tx.m13,
			tx.m20 * this.m03 + tx.m21 * this.m13 + tx.m22 * this.m23 + tx.m23
		);
	}
	
	public AffineTransform3D quadrantRotateX(int numQuadrants) {
		return concatenate(getQuadrantRotateXInstance(numQuadrants));
	}
	
	public AffineTransform3D quadrantRotateY(int numQuadrants) {
		return concatenate(getQuadrantRotateYInstance(numQuadrants));
	}
	
	public AffineTransform3D quadrantRotateZ(int numQuadrants) {
		return concatenate(getQuadrantRotateZInstance(numQuadrants));
	}
	
	public AffineTransform3D rotateX(double theta) {
		return concatenate(getRotateXInstance(theta));
	}
	
	public AffineTransform3D rotateY(double theta) {
		return concatenate(getRotateYInstance(theta));
	}
	
	public AffineTransform3D rotateZ(double theta) {
		return concatenate(getRotateZInstance(theta));
	}
	
	public AffineTransform3D scale(double sx, double sy, double sz) {
		return concatenate(getScaleInstance(sx, sy, sz));
	}
	
	public AffineTransform3D shear(
		double shxy, double shxz, double shyx, double shyz, double shzx, double shzy
	) {
		return concatenate(getShearInstance(shxy, shxz, shyx, shyz, shzx, shzy));
	}
	
	public String toString(String prefix, String delim, String suffix) {
		return (
			prefix
			+ m00 + delim + m10 + delim + m20 + delim
			+ m01 + delim + m11 + delim + m21 + delim
			+ m02 + delim + m12 + delim + m22 + delim
			+ m03 + delim + m13 + delim + m23 +
			suffix
		);
	}
	
	public String toString() {
		return toString("(", ", ", ")");
	}
	
	public AffineTransform3D translate(double tx, double ty, double tz) {
		return concatenate(getTranslateInstance(tx, ty, tz));
	}
	
	public Point3D transform(double x, double y, double z) {
		double x1 = m00 * x + m01 * y + m02 * z + m03;
		double y1 = m10 * x + m11 * y + m12 * z + m13;
		double z1 = m20 * x + m21 * y + m22 * z + m23;
		return new Point3D(x1, y1, z1);
	}
	
	public Point3D transform(Point3D point) {
		double x1 = m00 * point.getX() + m01 * point.getY() + m02 * point.getZ() + m03;
		double y1 = m10 * point.getX() + m11 * point.getY() + m12 * point.getZ() + m13;
		double z1 = m20 * point.getX() + m21 * point.getY() + m22 * point.getZ() + m23;
		return new Point3D(x1, y1, z1);
	}
}