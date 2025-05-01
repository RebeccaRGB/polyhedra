package com.kreative.polyhedra;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Format {
	OFF(".off", "off") {
		public Polyhedron read(InputStream in) {
			return new OFFReader(in).readPolyhedron();
		}
		public void write(Polyhedron p, OutputStream out) {
			new OFFWriter(out).writePolyhedron(p);
		}
	},
	MCCOOEY(".txt", "txt", "mccooey", "dmccooey") {
		public Polyhedron read(InputStream in) {
			return new McCooeyReader(in).readPolyhedron(Color.gray);
		}
		public void write(Polyhedron p, OutputStream out) {
			new McCooeyWriter(out).writePolyhedron(p);
		}
	},
	PATH3D(".3vp", "3vp", "path", "path3d") {
		public Polyhedron read(InputStream in) {
			return new Path3DReader(in).readPolyhedron();
		}
		public void write(Polyhedron p, OutputStream out) {
			new Path3DWriter(out).writePolyhedron(p);
		}
	},
	OBJ(".obj", "obj", "wavefront") {
		public Polyhedron read(InputStream in) {
			return new ObjReader(in).readPolyhedron(Color.gray);
		}
		public void write(Polyhedron p, OutputStream out) {
			new ObjWriter(out).writePolyhedron(p);
		}
	};
	
	private final List<String> names;
	private Format(String... names) {
		this.names = Arrays.asList(names);
	}
	
	public abstract Polyhedron read(InputStream in);
	public abstract void write(Polyhedron p, OutputStream out);
	
	public final String getExtension() {
		return names.get(0);
	}
	
	public final List<String> getNames() {
		return Collections.unmodifiableList(names);
	}
	
	public static Format forFile(File file) {
		if (file != null) {
			String name = file.getName();
			int o = name.lastIndexOf('.');
			String ext = name.substring(o + 1);
			return forName(ext);
		}
		return null;
	}
	
	public static Format forName(String name) {
		if (name != null) {
			name = name.toLowerCase();
			for (Format format : values()) {
				if (format.names.contains(name)) {
					return format;
				}
			}
		}
		return null;
	}
}