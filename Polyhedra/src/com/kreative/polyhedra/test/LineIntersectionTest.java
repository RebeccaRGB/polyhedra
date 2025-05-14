package com.kreative.polyhedra.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class LineIntersectionTest {
	private static boolean linesIntersect(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
		// return true if two line segments intersect
		double s1x = p1.getX() - p0.getX();
		double s1y = p1.getY() - p0.getY();
		double s2x = p3.getX() - p2.getX();
		double s2y = p3.getY() - p2.getY();
		double det = s1x * s2y - s2x * s1y;
		if (det == 0) return false; // lines are parallel or colinear
		double t1 = ((p0.getY() - p2.getY()) * s1x - (p0.getX() - p2.getX()) * s1y) / det;
		double t2 = ((p0.getY() - p2.getY()) * s2x - (p0.getX() - p2.getX()) * s2y) / det;
		return (t1 > 0 && t1 < 1 && t2 > 0 && t2 < 1);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final Point2D[] points = {
					new Point2D.Double(10, 10),
					new Point2D.Double(90, 90),
					new Point2D.Double(10, 90),
					new Point2D.Double(90, 10)
				};
				final JComponent panel = new JComponent() {
					private static final long serialVersionUID = 1L;
					protected void paintComponent(Graphics g) {
						Graphics2D g2 = (Graphics2D)g;
						g2.setColor(Color.red);
						g2.draw(new Line2D.Double(points[0], points[1]));
						g2.fill(new Ellipse2D.Double(points[0].getX()-2.5, points[0].getY()-2.5, 5, 5));
						g2.fill(new Ellipse2D.Double(points[1].getX()-2.5, points[1].getY()-2.5, 5, 5));
						g2.setColor(Color.blue);
						g2.draw(new Line2D.Double(points[2], points[3]));
						g2.fill(new Ellipse2D.Double(points[2].getX()-2.5, points[2].getY()-2.5, 5, 5));
						g2.fill(new Ellipse2D.Double(points[3].getX()-2.5, points[3].getY()-2.5, 5, 5));
					}
				};
				final JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setContentPane(panel);
				frame.setTitle(linesIntersect(points[0], points[1], points[2], points[3]) ? "Intersect" : "Don't Intersect");
				frame.setSize(500, 500);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
				
				final Random random = new Random();
				final Thread thread = new Thread() {
					public void run() {
						while (!Thread.interrupted()) {
							try { Thread.sleep(1000); }
							catch (InterruptedException e) { return; }
							for (int i = 0; i < points.length; i++) {
								points[i] = new Point2D.Double(random.nextDouble()*panel.getWidth(), random.nextDouble()*panel.getHeight());
							}
							panel.repaint();
							frame.setTitle(linesIntersect(points[0], points[1], points[2], points[3]) ? "Intersect" : "Don't Intersect");
						}
					}
				};
				thread.start();
			}
		});
	}
}