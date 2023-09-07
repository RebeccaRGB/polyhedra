package com.kreative.polyhedra.viewer;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import com.kreative.polyhedra.Polyhedron;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class ViewerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final double DEFAULT_ROTX = Math.PI / 6;
	private static final double DEFAULT_ZOOM = 0.4;
	
	private Polyhedron polyhedron;
	private boolean vertVisible = true;
	private boolean edgeVisible = true;
	private boolean faceVisible = true;
	private float vertRadius = 0.10f;
	private float edgeRadius = 0.05f;
	private Appearance vertAppearance = null;
	private Appearance edgeAppearance = null;
	private Appearance faceAppearance = null;
	private Node vertNode;
	private Node edgeNode;
	private Node faceNode;
	
	private final Group geometryGroup;
	private final TransformGroup autoRotateGroup;
	private final Alpha rotationAlpha;
	private final TransformGroup manualRotateGroup;
	private final TransformGroup scaleGroup;
	private final TransformGroup panGroup;
	
	public ViewerPanel(Polyhedron p) {
		super(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		add(canvas, BorderLayout.CENTER);
		
		geometryGroup = new Group();
		geometryGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		geometryGroup.setCapability(Group.ALLOW_CHILDREN_READ);
		geometryGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
		setPolyhedron(p);
		
		autoRotateGroup = new TransformGroup();
		autoRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		autoRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		autoRotateGroup.addChild(geometryGroup);
		rotationAlpha = new Alpha(-1, 4000);
		RotationInterpolator ri = new RotationInterpolator(rotationAlpha, autoRotateGroup);
		ri.setSchedulingBounds(new BoundingSphere());
		autoRotateGroup.addChild(ri);
		KeyPauseResumeBehavior prb = new KeyPauseResumeBehavior(rotationAlpha);
		prb.setSchedulingBounds(new BoundingSphere());
		autoRotateGroup.addChild(prb);
		
		manualRotateGroup = new TransformGroup();
		manualRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		manualRotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		manualRotateGroup.addChild(autoRotateGroup);
		Transform3D rotate = new Transform3D();
		rotate.rotX(DEFAULT_ROTX);
		manualRotateGroup.setTransform(rotate);
		MouseRotate rotateBehavior = new MouseRotate(canvas);
		rotateBehavior.setSchedulingBounds(new BoundingSphere());
		rotateBehavior.setTransformGroup(manualRotateGroup);
		manualRotateGroup.addChild(rotateBehavior);
		
		scaleGroup = new TransformGroup();
		scaleGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		scaleGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		scaleGroup.addChild(manualRotateGroup);
		Transform3D scale = new Transform3D();
		scale.setScale(DEFAULT_ZOOM);
		scaleGroup.setTransform(scale);
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
	}
	
	public Polyhedron getPolyhedron() { return polyhedron; }
	public boolean getVerticesVisible() { return vertVisible; }
	public boolean getEdgesVisible() { return edgeVisible; }
	public boolean getFacesVisible() { return faceVisible; }
	public float getVertexRadius() { return vertRadius; }
	public float getEdgeRadius() { return edgeRadius; }
	public Appearance getVertexAppearance() { return vertAppearance; }
	public Appearance getEdgeAppearance() { return edgeAppearance; }
	public Appearance getFaceAppearance() { return faceAppearance; }
	
	public void setPolyhedron(Polyhedron p) { polyhedron = p; build(true, true, true); }
	public void setVerticesVisible (boolean v) { vertVisible = v; build(false, false, false); }
	public void setEdgesVisible(boolean v) { edgeVisible = v; build(false, false, false); }
	public void setFacesVisible(boolean v) { faceVisible = v; build(false, false, false); }
	public void setVertexRadius(float r) { vertRadius = r; build(true, false, false); }
	public void setEdgeRadius(float r) { edgeRadius = r; build(false, true, false); }
	public void setVertexAppearance(Appearance a) { vertAppearance = a; build(true, false, false); }
	public void setEdgeAppearance(Appearance a) { edgeAppearance = a; build(false, true, false); }
	public void setFaceAppearance(Appearance a) { faceAppearance = a; build(false, false, true); }
	
	private void build(boolean vertices, boolean edges, boolean faces) {
		if (polyhedron == null) {
			vertNode = null;
			edgeNode = null;
			faceNode = null;
			geometryGroup.removeAllChildren();
		} else {
			if (vertices) vertNode = Convert.vertices(polyhedron, vertRadius, vertAppearance);
			if (edges) edgeNode = Convert.edges(polyhedron, edgeRadius, edgeAppearance);
			if (faces) faceNode = Convert.faces(polyhedron, faceAppearance);
			geometryGroup.removeAllChildren();
			if (vertVisible) geometryGroup.addChild(vertNode);
			if (edgeVisible) geometryGroup.addChild(edgeNode);
			if (faceVisible) geometryGroup.addChild(faceNode);
		}
	}
	
	public boolean isAnimationPaused() { return rotationAlpha.isPaused(); }
	public void pauseAnimation() { rotationAlpha.pause(); }
	public void resumeAnimation() { rotationAlpha.resume(); }
}