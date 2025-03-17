/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;

import robocode.AdvancedRobot;
import robocode.util.Utils;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.HashSet;
import java.util.Set;

import static java.awt.event.KeyEvent.*;

public class Interactive_v2 extends AdvancedRobot {

	int aimX, aimY;
	int firePower;

	private enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT
	}

	private final Set<Direction> directions = new HashSet<>();

	public void run() {
		setColors(Color.BLACK, Color.WHITE, Color.RED);
		for (;;) { // Infinite loop for continuous robot behavior
			setAhead(distanceToMove());
			setTurnRight(angleToTurnInDegrees());
			double angle = normalAbsoluteAngle(Math.atan2(aimX - getX(), aimY - getY()));
			setTurnGunRightRadians(normalRelativeAngle(angle - getGunHeadingRadians()));
			if (firePower > 0) {
				setFire(firePower);
			}
			execute();
		}
	}

	public void onKeyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case VK_UP:
			case VK_W:
				directions.add(Direction.UP);
				break;
			case VK_DOWN:
			case VK_S:
				directions.add(Direction.DOWN);
				break;
			case VK_RIGHT:
			case VK_D:
				directions.add(Direction.RIGHT);
				break;
			case VK_LEFT:
			case VK_A:
				directions.add(Direction.LEFT);
				break;
		}
	}

	public void onKeyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case VK_UP:
			case VK_W:
				directions.remove(Direction.UP);
				break;
			case VK_DOWN:
			case VK_S:
				directions.remove(Direction.DOWN);
				break;
			case VK_RIGHT:
			case VK_D:
				directions.remove(Direction.RIGHT);
				break;
			case VK_LEFT:
			case VK_A:
				directions.remove(Direction.LEFT);
				break;
		}
	}

	public void onMouseWheelMoved(MouseWheelEvent e) {
		// Do nothing
	}

	public void onMouseMoved(MouseEvent e) {
		updateAimCoordinates(e);
	}

	public void onMousePressed(MouseEvent e) {
		handleMousePress(e);
	}

	public void onMouseReleased(MouseEvent e) {
		firePower = 0;
	}

	public void onPaint(Graphics2D g) {
		g.setColor(Color.RED);
		g.drawOval(aimX - 15, aimY - 15, 30, 30);
		g.drawLine(aimX, aimY - 4, aimX, aimY + 4);
		g.drawLine(aimX - 4, aimY, aimX + 4, aimY);
	}

	private double angleToTurnInDegrees() {
		if (directions.isEmpty()) {
			return 0;
		}
		return Utils.normalRelativeAngleDegrees(desiredDirection() - getHeading());
	}

	private double distanceToMove() {
		if (directions.isEmpty()) {
			return 0;
		}
		if (Math.abs(angleToTurnInDegrees()) > 45) {
			return 5;
		}
		return Double.POSITIVE_INFINITY;
	}

	private double desiredDirection() {
		if (directions.contains(Direction.UP)) {
			if (directions.contains(Direction.RIGHT)) {
				return 45;
			}
			if (directions.contains(Direction.LEFT)) {
				return 315;
			}
			return 0;
		}
		if (directions.contains(Direction.DOWN)) {
			if (directions.contains(Direction.RIGHT)) {
				return 135;
			}
			if (directions.contains(Direction.LEFT)) {
				return 225;
			}
			return 180;
		}
		if (directions.contains(Direction.RIGHT)) {
			return 90;
		}
		if (directions.contains(Direction.LEFT)) {
			return 270;
		}
		return 0;
	}

	private void updateAimCoordinates(MouseEvent e) {
		this.aimX = e.getX();
		this.aimY = e.getY();
	}

	private void handleMousePress(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON3:
				this.firePower = 3;
				setBulletColor(Color.RED);
				break;
			case MouseEvent.BUTTON2:
				this.firePower = 2;
				setBulletColor(Color.ORANGE);
				break;
			default:
				this.firePower = 1;
				setBulletColor(Color.YELLOW);
				break;
		}
	}

	public void setBulletColor(Color color) {
		super.setBulletColor(color);
	}
}