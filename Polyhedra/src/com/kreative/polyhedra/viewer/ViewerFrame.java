package com.kreative.polyhedra.viewer;

import javax.media.j3d.Appearance;
import javax.swing.JFrame;
import com.kreative.polyhedra.Polyhedron;

public class ViewerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private final ViewerPanel viewerPanel;
	
	public ViewerFrame(Polyhedron p) {
		super("Polyhedron Viewer");
		viewerPanel = new ViewerPanel(p);
		setContentPane(viewerPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(null);
	}
	
	public Polyhedron getPolyhedron() { return viewerPanel.getPolyhedron(); }
	public boolean getVerticesVisible() { return viewerPanel.getVerticesVisible(); }
	public boolean getEdgesVisible() { return viewerPanel.getEdgesVisible(); }
	public boolean getFacesVisible() { return viewerPanel.getFacesVisible(); }
	public float getVertexRadius() { return viewerPanel.getVertexRadius(); }
	public float getEdgeRadius() { return viewerPanel.getEdgeRadius(); }
	public Appearance getVertexAppearance() { return viewerPanel.getVertexAppearance(); }
	public Appearance getEdgeAppearance() { return viewerPanel.getEdgeAppearance(); }
	public Appearance getFaceAppearance() { return viewerPanel.getFaceAppearance(); }
	
	public void setPolyhedron(Polyhedron p) { viewerPanel.setPolyhedron(p); }
	public void setVerticesVisible (boolean v) { viewerPanel.setVerticesVisible(v); }
	public void setEdgesVisible(boolean v) { viewerPanel.setEdgesVisible(v); }
	public void setFacesVisible(boolean v) { viewerPanel.setFacesVisible(v); }
	public void setVertexRadius(float r) { viewerPanel.setVertexRadius(r); }
	public void setEdgeRadius(float r) { viewerPanel.setEdgeRadius(r); }
	public void setVertexAppearance(Appearance a) { viewerPanel.setVertexAppearance(a); }
	public void setEdgeAppearance(Appearance a) { viewerPanel.setEdgeAppearance(a); }
	public void setFaceAppearance(Appearance a) { viewerPanel.setFaceAppearance(a); }
	
	public boolean isAnimationPaused() { return viewerPanel.isAnimationPaused(); }
	public void pauseAnimation() { viewerPanel.pauseAnimation(); }
	public void resumeAnimation() { viewerPanel.resumeAnimation(); }
}