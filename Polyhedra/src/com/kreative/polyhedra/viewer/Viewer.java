package com.kreative.polyhedra.viewer;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import com.kreative.polyhedra.OFFReader;
import com.kreative.polyhedra.Polyhedron;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class Viewer extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final Group geometryGroup;
	private final TransformGroup scaleGroup;
	private final TransformGroup autoRotateGroup;
	private final TransformGroup manualRotateGroup;
	private final TransformGroup panGroup;
	
	public Viewer(Node... nodes) {
		JPanel content = new JPanel(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		content.add(canvas, BorderLayout.CENTER);
		setContentPane(content);
		setTitle("Polyhedron Viewer");
		
		geometryGroup = new Group();
		geometryGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		geometryGroup.setCapability(Group.ALLOW_CHILDREN_READ);
		geometryGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		for (Node node : nodes) geometryGroup.addChild(node);
		
		autoRotateGroup = new TransformGroup();
		autoRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		autoRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		autoRotateGroup.addChild(geometryGroup);
		Alpha ra = new Alpha(-1, 4000);
		RotationInterpolator ri = new RotationInterpolator(ra, autoRotateGroup);
		ri.setSchedulingBounds(new BoundingSphere());
		autoRotateGroup.addChild(ri);
		
		manualRotateGroup = new TransformGroup();
		manualRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		manualRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		manualRotateGroup.addChild(autoRotateGroup);
		MouseRotate rotateBehavior = new MouseRotate(canvas);
		rotateBehavior.setSchedulingBounds(new BoundingSphere());
		rotateBehavior.setTransformGroup(manualRotateGroup);
		manualRotateGroup.addChild(rotateBehavior);
		
		scaleGroup = new TransformGroup();
		scaleGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scaleGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		scaleGroup.addChild(manualRotateGroup);
		MouseWheelZoom scaleBehavior = new MouseWheelZoom(canvas, MouseWheelZoom.INVERT_INPUT);
		scaleBehavior.setSchedulingBounds(new BoundingSphere());
		scaleBehavior.setTransformGroup(scaleGroup);
		scaleGroup.addChild(scaleBehavior);
		
		panGroup = new TransformGroup();
		panGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		panGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		panGroup.addChild(scaleGroup);
		MouseTranslate panBehavior = new MouseTranslate(canvas);
		panBehavior.setSchedulingBounds(new BoundingSphere());
		panBehavior.setTransformGroup(panGroup);
		panGroup.addChild(panBehavior);
		
		AmbientLight al = new AmbientLight(new Color3f(1, 1, 1));
		al.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		
		DirectionalLight dl = new DirectionalLight(new Color3f(1, 1, 1), new Vector3f(-1, 0, -1));
		dl.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		
		BranchGroup scene = new BranchGroup();
		scene.addChild(dl);
		scene.addChild(al);
		scene.addChild(panGroup);
		scene.compile();
		
		SimpleUniverse universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
		universe.addBranchGraph(scene);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(null);
	}
	
	public Viewer(Polyhedron p) {
		this(
			Convert.vertices(p, 0.1f, null),
			Convert.edges(p, 0.05f, null),
			Convert.faces(p, null)
		);
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
					in.close();
					if (p == null) System.err.println("Error: No polyhedron found in " + arg);
					else new Viewer(p).setVisible(true);
				} catch (IOException e) {
					System.err.println("Error: No polyhedron found in " + arg + ": " + e);
				}
			}
		}
	}
}