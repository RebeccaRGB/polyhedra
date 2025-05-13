package com.kreative.polyhedra;

import java.util.ArrayList;
import java.util.Arrays;

public class Point3D {
	public static final Point3D ZERO = new Point3D(0, 0, 0);
	public static final Point3D X = new Point3D(1, 0, 0);
	public static final Point3D Y = new Point3D(0, 1, 0);
	public static final Point3D Z = new Point3D(0, 0, 1);
	
	private final double x, y, z;
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D add(double x, double y, double z) {
		return new Point3D(this.x + x, this.y + y, this.z + z);
	}
	
	public Point3D add(Point3D point) {
		return new Point3D(this.x + point.x, this.y + point.y, this.z + point.z);
	}
	
	public double angle(double x, double y, double z) {
		double n = this.dotProduct(x, y, z);
		double d = Math.sqrt(this.magnitudeSq() * (x * x + y * y + z * z));
		return Math.toDegrees(Math.acos(n / d));
	}
	
	public double angle(Point3D point) {
		double n = this.dotProduct(point);
		double d = Math.sqrt(this.magnitudeSq() * point.magnitudeSq());
		return Math.toDegrees(Math.acos(n / d));
	}
	
	public double angle(Point3D p1, Point3D p2) {
		return p1.subtract(this).angle(p2.subtract(this));
	}
	
	public Point3D angleBisector(double x, double y, double z) {
		double m1 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		double m2 = Math.sqrt(x * x + y * y + z * z);
		x = ((m1 == 0) ? 0 : (this.x / m1)) + ((m2 == 0) ? 0 : (x / m2));
		y = ((m1 == 0) ? 0 : (this.y / m1)) + ((m2 == 0) ? 0 : (y / m2));
		z = ((m1 == 0) ? 0 : (this.z / m1)) + ((m2 == 0) ? 0 : (z / m2));
		double m = Math.sqrt(x * x + y * y + z * z);
		return (m == 0) ? ZERO : new Point3D(x / m, y / m, z / m);
	}
	
	public Point3D angleBisector(Point3D point) {
		double m1 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		double m2 = Math.sqrt(point.x * point.x + point.y * point.y + point.z * point.z);
		double x = ((m1 == 0) ? 0 : (this.x / m1)) + ((m2 == 0) ? 0 : (point.x / m2));
		double y = ((m1 == 0) ? 0 : (this.y / m1)) + ((m2 == 0) ? 0 : (point.y / m2));
		double z = ((m1 == 0) ? 0 : (this.z / m1)) + ((m2 == 0) ? 0 : (point.z / m2));
		double m = Math.sqrt(x * x + y * y + z * z);
		return (m == 0) ? ZERO : new Point3D(x / m, y / m, z / m);
	}
	
	public Point3D angleBisector(Point3D p1, Point3D p2, double magnitude) {
		if (magnitude == 0) return this;
		Point3D b = p1.subtract(this).angleBisector(p2.subtract(this));
		return this.add(b.multiply(magnitude));
	}
	
	public double angleRad(double x, double y, double z) {
		double n = this.dotProduct(x, y, z);
		double d = Math.sqrt(this.magnitudeSq() * (x * x + y * y + z * z));
		return Math.acos(n / d);
	}
	
	public double angleRad(Point3D point) {
		double n = this.dotProduct(point);
		double d = Math.sqrt(this.magnitudeSq() * point.magnitudeSq());
		return Math.acos(n / d);
	}
	
	public double angleRad(Point3D p1, Point3D p2) {
		return p1.subtract(this).angleRad(p2.subtract(this));
	}
	
	public static Point3D average(Point3D... points) {
		return average(Arrays.asList(points));
	}
	
	public static Point3D average(Iterable<Point3D> points) {
		double x = 0;
		double y = 0;
		double z = 0;
		int count = 0;
		for (Point3D point : points) {
			if (point != null) {
				x += point.x;
				y += point.y;
				z += point.z;
				count++;
			}
		}
		if (count == 0) return ZERO;
		return new Point3D(x / count, y / count, z / count);
	}
	
	public static double averageMagnitude(Point3D... points) {
		return averageMagnitude(Arrays.asList(points));
	}
	
	public static double averageMagnitude(Iterable<Point3D> points) {
		double m = 0;
		int count = 0;
		for (Point3D point : points) {
			if (point != null) {
				m += point.magnitude();
				count++;
			}
		}
		if (count == 0) return 0;
		return m / count;
	}
	
	public Point3D crossProduct(double x, double y, double z) {
		return new Point3D(
			this.y * z - this.z * y,
			this.z * x - this.x * z,
			this.x * y - this.y * x
		);
	}
	
	public Point3D crossProduct(Point3D vector) {
		return new Point3D(
			this.y * vector.z - this.z * vector.y,
			this.z * vector.x - this.x * vector.z,
			this.x * vector.y - this.y * vector.x
		);
	}
	
	public double distance(double x, double y, double z) {
		double dx = this.x - x;
		double dy = this.y - y;
		double dz = this.z - z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public double distance(Point3D point) {
		double dx = this.x - point.x;
		double dy = this.y - point.y;
		double dz = this.z - point.z;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public double distanceSq(double x, double y, double z) {
		double dx = this.x - x;
		double dy = this.y - y;
		double dz = this.z - z;
		return (dx * dx + dy * dy + dz * dz);
	}
	
	public double distanceSq(Point3D point) {
		double dx = this.x - point.x;
		double dy = this.y - point.y;
		double dz = this.z - point.z;
		return (dx * dx + dy * dy + dz * dz);
	}
	
	public double distanceToLine(Point3D p1, Point3D p2) {
		Point3D v1 = this.subtract(p1);
		Point3D v2 = p2.subtract(p1);
		Point3D v3 = v1.crossProduct(v2);
		return v3.magnitude() / v2.magnitude();
	}
	
	public double distanceToPlane(Point3D p, Point3D normal) {
		return Math.abs(this.subtract(p).dotProduct(normal.normalize()));
	}
	
	public Point3D divide(double factor) {
		return new Point3D(x / factor, y / factor, z / factor);
	}
	
	public Point3D divide(double xfactor, double yfactor, double zfactor) {
		return new Point3D(x / xfactor, y / yfactor, z / zfactor);
	}
	
	public double dotProduct(double x, double y, double z) {
		return this.x * x + this.y * y + this.z * z;
	}
	
	public double dotProduct(Point3D vector) {
		return this.x * vector.x + this.y * vector.y + this.z * vector.z;
	}
	
	public boolean equals(Object obj) {
		return (
			(obj instanceof Point3D)
			&& this.x == ((Point3D)obj).x
			&& this.y == ((Point3D)obj).y
			&& this.z == ((Point3D)obj).z
		);
	}
	
	public boolean equals(Point3D point, double epsilon) {
		return (
			Math.abs(this.x - point.x) <= epsilon &&
			Math.abs(this.y - point.y) <= epsilon &&
			Math.abs(this.z - point.z) <= epsilon
		);
	}
	
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	
	public int hashCode() {
		long x = (this.x == 0) ? 0 : Double.doubleToLongBits(this.x);
		long y = (this.y == 0) ? 0 : Double.doubleToLongBits(this.y);
		long z = (this.z == 0) ? 0 : Double.doubleToLongBits(this.z);
		long h = (x << 2) + (y << 14) + (z << 26) + (z >> 38) + (y >> 50) + (x >> 62);
		return (int)h;
	}
	
	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public double magnitudeSq() {
		return (x * x + y * y + z * z);
	}
	
	public static Point3D max(Point3D... points) {
		return max(Arrays.asList(points));
	}
	
	public static Point3D max(Iterable<Point3D> points) {
		double x = Double.NaN;
		double y = Double.NaN;
		double z = Double.NaN;
		boolean first = true;
		for (Point3D point : points) {
			if (point != null) {
				if (first || point.x > x) x = point.x;
				if (first || point.y > y) y = point.y;
				if (first || point.z > z) z = point.z;
				first = false;
			}
		}
		return new Point3D(x, y, z);
	}
	
	public static double maxMagnitude(Point3D... points) {
		return maxMagnitude(Arrays.asList(points));
	}
	
	public static double maxMagnitude(Iterable<Point3D> points) {
		double m = Double.NaN;
		boolean first = true;
		for (Point3D point : points) {
			if (point != null) {
				double pointm = point.magnitude();
				if (first || pointm > m) m = pointm;
				first = false;
			}
		}
		return m;
	}
	
	public Point3D midpoint(double x, double y, double z) {
		return new Point3D((this.x + x) / 2, (this.y + y) / 2, (this.z + z) / 2);
	}
	
	public Point3D midpoint(Point3D point) {
		return new Point3D((this.x + point.x) / 2, (this.y + point.y) / 2, (this.z + point.z) / 2);
	}
	
	public static Point3D min(Point3D... points) {
		return min(Arrays.asList(points));
	}
	
	public static Point3D min(Iterable<Point3D> points) {
		double x = Double.NaN;
		double y = Double.NaN;
		double z = Double.NaN;
		boolean first = true;
		for (Point3D point : points) {
			if (point != null) {
				if (first || point.x < x) x = point.x;
				if (first || point.y < y) y = point.y;
				if (first || point.z < z) z = point.z;
				first = false;
			}
		}
		return new Point3D(x, y, z);
	}
	
	public static double minMagnitude(Point3D... points) {
		return minMagnitude(Arrays.asList(points));
	}
	
	public static double minMagnitude(Iterable<Point3D> points) {
		double m = Double.NaN;
		boolean first = true;
		for (Point3D point : points) {
			if (point != null) {
				double pointm = point.magnitude();
				if (first || pointm < m) m = pointm;
				first = false;
			}
		}
		return m;
	}
	
	public Point3D multiply(double factor) {
		return new Point3D(x * factor, y * factor, z * factor);
	}
	
	public Point3D multiply(double xfactor, double yfactor, double zfactor) {
		return new Point3D(x * xfactor, y * yfactor, z * zfactor);
	}
	
	public Point3D negate() {
		return new Point3D(-x, -y, -z);
	}
	
	public Point3D normal(Point3D... points) {
		return normal(Arrays.asList(points));
	}
	
	public Point3D normal(Iterable<Point3D> points) {
		ArrayList<Point3D> vectors = new ArrayList<Point3D>();
		for (Point3D point : points) vectors.add(point.subtract(this));
		ArrayList<Point3D> normals = new ArrayList<Point3D>();
		for (int i = 0, n = vectors.size(); i < n; i++) {
			Point3D vec1 = vectors.get(i);
			if (vec1.magnitude() == 0) continue;
			Point3D vec2 = vectors.get((i + 1) % n);
			if (vec2.magnitude() == 0) continue;
			normals.add(vec1.crossProduct(vec2).normalize());
		}
		if (normals.isEmpty()) return ZERO;
		return average(normals).normalize();
	}
	
	public Point3D normalize() {
		double m = Math.sqrt(x * x + y * y + z * z);
		return (m == 0) ? this : new Point3D(x / m, y / m, z / m);
	}
	
	public Point3D normalize(double magnitude) {
		if (magnitude == 0) return ZERO;
		double m = Math.sqrt(x * x + y * y + z * z);
		return (m == 0) ? this : new Point3D(x * magnitude / m, y * magnitude / m, z * magnitude / m);
	}
	
	public Point3D partition(double x, double y, double z, double a, double b) {
		if (a == b) return midpoint(x, y, z);
		if (a == 0) return this;
		if (b == 0) return new Point3D(x, y, z);
		x = (this.x * b + x * a) / (b + a);
		y = (this.y * b + y * a) / (b + a);
		z = (this.z * b + z * a) / (b + a);
		return new Point3D(x, y, z);
	}
	
	public Point3D partition(Point3D point, double a, double b) {
		if (a == b) return midpoint(point);
		if (a == 0) return this;
		if (b == 0) return point;
		double x = (this.x * b + point.x * a) / (b + a);
		double y = (this.y * b + point.y * a) / (b + a);
		double z = (this.z * b + point.z * a) / (b + a);
		return new Point3D(x, y, z);
	}
	
	public Point3D subtract(double x, double y, double z) {
		return new Point3D(this.x - x, this.y - y, this.z - z);
	}
	
	public Point3D subtract(Point3D point) {
		return new Point3D(this.x - point.x, this.y - point.y, this.z - point.z);
	}
	
	public String toString(String prefix, String delim, String suffix) {
		return prefix + x + delim + y + delim + z + suffix;
	}
	
	public String toString() {
		return toString("(", ", ", ")");
	}
}