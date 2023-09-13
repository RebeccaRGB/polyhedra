package com.kreative.polyhedra;

public abstract class PointTransform3D {
	public abstract boolean isReflection();
	public abstract Point3D transform(double x, double y, double z);
	public abstract Point3D transform(Point3D point);
}