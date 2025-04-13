package com.kreative.polyhedra.viewer;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;

public class KeyToggleVEFBehavior extends Behavior {
	private final ViewerPanel viewer;
	private final WakeupCondition condition;
	
	public KeyToggleVEFBehavior(ViewerPanel viewer) {
		this.viewer = viewer;
		this.condition = new WakeupOr(
			new WakeupCriterion[] {
				new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED)
			}
		);
	}
	
	public void initialize() {
		wakeupOn(condition);
	}
	
	@SuppressWarnings("rawtypes")
	public void processStimulus(Enumeration criteria) {
		while (criteria.hasMoreElements()) {
			Object criterion = criteria.nextElement();
			if (criterion instanceof WakeupOnAWTEvent) {
				WakeupOnAWTEvent wakeup = (WakeupOnAWTEvent)criterion;
				for (AWTEvent event : wakeup.getAWTEvent()) {
					if (event instanceof KeyEvent) {
						KeyEvent e = (KeyEvent)event;
						if (e.getID() == KeyEvent.KEY_PRESSED && !e.isControlDown() && !e.isMetaDown()) {
							switch (e.getKeyCode()) {
								case KeyEvent.VK_V:
									viewer.setVerticesVisible(!viewer.getVerticesVisible());
									break;
								case KeyEvent.VK_E:
									viewer.setEdgesVisible(!viewer.getEdgesVisible());
									break;
								case KeyEvent.VK_F:
									viewer.setFacesVisible(!viewer.getFacesVisible());
									break;
							}
						}
					}
				}
			}
		}
		wakeupOn(condition);
	}
}