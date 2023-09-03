package com.kreative.polyhedra;

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
	
	public Point3D divide(double factor) {
		return new Point3D(x / factor, y / factor, z / factor);
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
	
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	
	public int hashCode() {
		return Arrays.asList(
			Double.valueOf(x),
			Double.valueOf(y),
			Double.valueOf(z)
		).hashCode();
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
	
	public Point3D negate() {
		return new Point3D(-x, -y, -z);
	}
	
	public Point3D normalize() {
		double m = Math.sqrt(x * x + y * y + z * z);
		return (m == 0) ? this : new Point3D(x / m, y / m, z / m);
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