package com.kreative.polyhedra.viewer;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import javax.media.j3d.Alpha;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.kreative.polyhedra.OFFReader;
import com.kreative.polyhedra.Polyhedron;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Viewer extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public Viewer(Shape3D s) {
		JPanel content = new JPanel(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		content.add(canvas, BorderLayout.CENTER);
		setContentPane(content);
		setTitle("Polyhedron Viewer");
		
		TransformGroup txg = new TransformGroup();
		txg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		txg.addChild(s);
		
		Alpha ra = new Alpha(-1, 4000);
		RotationInterpolator ri = new RotationInterpolator(ra, txg);
		ri.setSchedulingBounds(new BoundingSphere());
		txg.addChild(ri);
		
		Transform3D rotate = new Transform3D();
		Transform3D tempRotate = new Transform3D();
		Transform3D tempScale = new Transform3D();
		rotate.rotX(Math.PI/4);
		tempRotate.rotY(Math.PI/5);
		rotate.mul(tempRotate);
		tempScale.setScale(0.5);
		rotate.mul(tempScale);
		TransformGroup objRotate = new TransformGroup(rotate);
		objRotate.addChild(txg);
		
		BranchGroup scene = new BranchGroup();
		scene.addChild(objRotate);
		scene.compile();
		
		SimpleUniverse universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(scene);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(null);
	}
	
	public Viewer(Polyhedron p) {
		this(Convert.faces(p));
	}
	
	public static void main(String[] args) {
		if (args.length == 0) {
			new Viewer(new ColorCube(0.4)).setVisible(true);
		} else {
			for (String arg : args) {
				if (arg.equals("-")) {
					OFFReader reader = new OFFReader(System.in);
					Polyhedron p = reader.readPolyhedron();
					if (p == null) System.err.println("Error: No polyhedron found in standard input");
					else new Viewer(p).setVisible(true);
				} else try {
					FileInputStream in = new FileInputStream(arg);
					OFFReader reader = new OFFReader(in);
					Polyhedron p = reader.readPolyhedron();
					if (p == null) System.err.println("Error: No polyhedron found in " + arg);
					else new Viewer(p).setVisible(true);
				} catch (IOException e) {
					System.err.println("Error: No polyhedron found in " + arg + ": " + e);
				}
			}
		}
	}
}