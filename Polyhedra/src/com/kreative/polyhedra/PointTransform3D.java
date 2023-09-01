package com.kreative.polyhedra;

public abstract class PointTransform3D {
	public abstract Point3D transform(double x, double y, double z);
	public abstract Point3D transform(Point3D point);
	
	public static final Normalize NORMALIZE = new Normalize();
	public static class Normalize extends PointTransform3D {
		private Normalize() {}
		public Point3D transform(double x, double y, double z) {
			double m = Math.sqrt(x * x + y * y + z * z);
			if (m != 0) { x /= m; y /= m; z /= m; }
			return new Point3D(x, y, z);
		}
		public Point3D transform(Point3D point) {
			return point.normalize();
		}
	}
	
	public static class Scale extends PointTransform3D {
		private final double sx, sy, sz;
		public Scale(double scale) {
			this.sx = scale; this.sy = scale; this.sz = scale;
		}
		public Scale(double sx, double sy, double sz) {
			this.sx = sx; this.sy = sy; this.sz = sz;
		}
		public Point3D transform(double x, double y, double z) {
			return new Point3D(x * sx, y * sy, z * sz);
		}
		public Point3D transform(Point3D point) {
			return new Point3D(point.getX() * sx, point.getY() * sy, point.getZ() * sz);
		}
	}
	
	public static class Translate extends PointTransform3D {
		private final double tx, ty, tz;
		public Translate(double tx, double ty, double tz) {
			this.tx = tx; this.ty = ty; this.tz = tz;
		}
		public Point3D transform(double x, double y, double z) {
			return new Point3D(x + tx, y + ty, z + tz);
		}
		public Point3D transform(Point3D point) {
			return new Point3D(point.getX() + tx, point.getY() + ty, point.getZ() + tz);
		}
	}
}