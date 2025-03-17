/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;


import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;

import java.awt.*;


/**
 * SpinBot - a sample robot by Mathew Nelson.
 * <p>
 * Moves in a circle, firing hard when an enemy is detected.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class SpinBot extends AdvancedRobot {

	public void run() {
		setColors();

		while (true) {
			setTurnRight(10000);
			setMaxVelocity(5);
			ahead(10000);
		}
	}

	/**
	 * Sets the robot's colors
	 */
	private void setColors() {
		setBodyColor(Color.blue);
		setGunColor(Color.blue);
		setRadarColor(Color.black);
		setScanColor(Color.yellow);
	}

	/**
	 * onScannedRobot: Fire hard!
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(3);
	}

	/**
	 * onHitRobot: Fire if close, turn if we caused the collision.
	 */
	public void onHitRobot(HitRobotEvent e) {
		if (Math.abs(e.getBearing()) < 10) {
			fire(3);
		}
		if (e.isMyFault()) {
			turnRight(10);
		}
	}
}