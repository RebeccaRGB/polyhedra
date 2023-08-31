package com.kreative.polyhedra.viewer;

import java.awt.Color;
import java.util.Arrays;
import com.kreative.polyhedra.Point3D;
import com.kreative.polyhedra.Polyhedron;

public class ColorCube extends Polyhedron {
	public ColorCube() {
		this(1);
	}
	
	public ColorCube(double scale) {
		super(
			Arrays.asList(
				new Point3D(scale, scale, scale),
				new Point3D(scale, scale, -scale),
				new Point3D(scale, -scale, scale),
				new Point3D(scale, -scale, -scale),
				new Point3D(-scale, scale, scale),
				new Point3D(-scale, scale, -scale),
				new Point3D(-scale, -scale, scale),
				new Point3D(-scale, -scale, -scale)
			),
			Arrays.asList(
				Arrays.asList(2, 0, 4, 6),
				Arrays.asList(6, 4, 5, 7),
				Arrays.asList(7, 5, 1, 3),
				Arrays.asList(3, 1, 0, 2),
				Arrays.asList(0, 1, 5, 4),
				Arrays.asList(3, 2, 6, 7)
			),
			Arrays.asList(
				Color.RED,
				Color.YELLOW,
				Color.GREEN,
				Color.BLUE,
				Color.MAGENTA,
				Color.CYAN
			)
		);
	}
}