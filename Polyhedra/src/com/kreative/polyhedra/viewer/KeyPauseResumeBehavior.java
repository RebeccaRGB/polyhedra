package com.kreative.polyhedra.viewer;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import javax.media.j3d.Alpha;
import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;

public class KeyPauseResumeBehavior extends Behavior {
	private final Alpha alpha;
	private final WakeupCondition condition;
	
	public KeyPauseResumeBehavior(Alpha alpha) {
		this.alpha = alpha;
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
						if (e.getID() == KeyEvent.KEY_PRESSED) {
							if (e.getKeyCode() == KeyEvent.VK_SPACE) {
								if (alpha.isPaused()) alpha.resume();
								else alpha.pause();
							}
						}
					}
				}
			}
		}
		wakeupOn(condition);
	}
}