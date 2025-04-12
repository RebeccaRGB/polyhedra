package com.kreative.polyhedra;

import java.io.Serializable;

public class Cursor3D implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	
	private double x = 0, y = 0, z = 0;
	private double azimuth = 0, cr = 0;
	private double elevation = 0, sr = 0;
	
	public void moveXYZ(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
		this.azimuth = atan2(z, x); this.cr = hypot(z, x);
		this.elevation = atan2(y, cr); this.sr = hypot(z, y, x);
	}
	
	public void moveRYA(double cr, double y, double azimuth) {
		this.cr = cr; this.y = y; this.azimuth = azimuth;
		this.elevation = atan2(y, cr); this.sr = hypot(y, cr);
		this.x = cr * cos(azimuth); this.z = cr * sin(azimuth);
	}
	
	public void moveREA(double sr, double elevation, double azimuth) {
		this.sr = sr; this.elevation = elevation; this.azimuth = azimuth;
		this.cr = sr * cos(elevation); this.y = sr * sin(elevation);
		this.x = cr * cos(azimuth); this.z = cr * sin(azimuth);
	}
	
	public void deltaXYZ(double dx, double dy, double dz) {
		this.x += dx; this.y += dy; this.z += dz;
		if (dz != 0 || dx != 0) {
			this.azimuth = atan2(z, x);
			this.cr = hypot(z, x);
		}
		if (dz != 0 || dy != 0 || dx != 0) {
			this.elevation = atan2(y, cr);
			this.sr = hypot(z, y, x);
		}
	}
	
	public void deltaRYA(double dcr, double dy, double dazim) {
		this.cr += dcr; this.y += dy; this.azimuth += dazim;
		if (dy != 0 || dcr != 0) {
			this.elevation = atan2(y, cr);
			this.sr = hypot(y, cr);
		}
		if (dcr != 0 || dazim != 0) {
			this.x = cr * cos(azimuth);
			this.z = cr * sin(azimuth);
		}
	}
	
	public void deltaREA(double dsr, double delev, double dazim) {
		this.sr += dsr; this.elevation += delev; this.azimuth += dazim;
		if (dsr != 0 || delev != 0) {
			this.cr = sr * cos(elevation);
			this.y = sr * sin(elevation);
		}
		if (dsr != 0 || delev != 0 || dazim != 0) {
			this.x = cr * cos(azimuth);
			this.z = cr * sin(azimuth);
		}
	}
	
	public Point3D position() {
		return new Point3D(x, y, z);
	}
	
	public Cursor3D clone() {
		Cursor3D c = new Cursor3D();
		c.x = this.x; c.y = this.y; c.z = this.z;
		c.azimuth = this.azimuth; c.cr = this.cr;
		c.elevation = this.elevation; c.sr = this.sr;
		return c;
	}
	
	public void restore(Cursor3D c) {
		this.x = c.x; this.y = c.y; this.z = c.z;
		this.azimuth = c.azimuth; this.cr = c.cr;
		this.elevation = c.elevation; this.sr = c.sr;
	}
	
	private static double sin(double a) {
		long q = Math.round(a / 90);
		if (a == q * 90) {
			switch (((int)q) & 3) {
				case 1: return 1;
				case 3: return -1;
				default: return 0;
			}
		}
		return Math.sin(Math.toRadians(a));
	}
	
	private static double cos(double a) {
		long q = Math.round(a / 90);
		if (a == q * 90) {
			switch (((int)q) & 3) {
				case 0: return 1;
				case 2: return -1;
				default: return 0;
			}
		}
		return Math.cos(Math.toRadians(a));
	}
	
	private static double atan2(double y, double x) {
		if (y == 0) return (x < 0) ? 180 : 0;
		if (x == 0) return (y < 0) ? -90 : 90;
		return Math.toDegrees(Math.atan2(y,x));
	}
	
	private static double hypot(double x, double y) {
		return Math.hypot(x, y);
	}
	
	private static double hypot(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}
}