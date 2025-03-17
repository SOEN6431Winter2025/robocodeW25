/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package sample;

import robocode.AdvancedRobot;
import static robocode.util.Utils.normalAbsoluteAngle;
import static robocode.util.Utils.normalRelativeAngle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static java.awt.event.KeyEvent.*;

public class Interactive extends AdvancedRobot {

	int moveDirection;
	int turnDirection;
	double moveAmount;
	int aimX, aimY;
	int firePower;

	public void run() {
		setColors(Color.BLACK, Color.WHITE, Color.RED);

		for (;;) {
			setAhead(moveAmount * moveDirection);
			moveAmount = Math.max(0, moveAmount - 1);
			setTurnRight(45 * turnDirection);

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
				moveDirection = 1;
				moveAmount = Double.POSITIVE_INFINITY;
				break;
			case VK_DOWN:
			case VK_S:
				moveDirection = -1;
				moveAmount = Double.POSITIVE_INFINITY;
				break;
			case VK_RIGHT:
			case VK_D:
				turnDirection = 1;
				break;
			case VK_LEFT:
			case VK_A:
				turnDirection = -1;
				break;
		}
	}

	public void onKeyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case VK_UP:
			case VK_W:
			case VK_DOWN:
			case VK_S:
				moveDirection = 0;
				moveAmount = 0;
				break;
			case VK_RIGHT:
			case VK_D:
			case VK_LEFT:
			case VK_A:
				turnDirection = 0;
				break;
		}
	}

	public void onMouseWheelMoved(MouseWheelEvent e) {
		moveDirection = (e.getWheelRotation() < 0) ? 1 : -1;
		moveAmount += Math.abs(e.getWheelRotation()) * 5;
	}

	public void onMouseMoved(MouseEvent e) {
		MouseEventHelper.updateAimCoordinates(e, this);
	}

	public void onMousePressed(MouseEvent e) {
		MouseEventHelper.handleMousePress(e, this);
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

	public void setAimX(int aimX) {
		this.aimX = aimX;
	}

	public void setAimY(int aimY) {
		this.aimY = aimY;
	}

	public void setFirePower(int firePower) {
		this.firePower = firePower;
	}

	public void setBulletColor(Color color) {
		super.setBulletColor(color);
	}
}

class MouseEventHelper {

	public static void updateAimCoordinates(MouseEvent e, Interactive interactive) {
		interactive.setAimX(e.getX());
		interactive.setAimY(e.getY());
	}

	public static void handleMousePress(MouseEvent e, Interactive interactive) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON3:
				interactive.setFirePower(3);
				interactive.setBulletColor(Color.RED);
				break;
			case MouseEvent.BUTTON2:
				interactive.setFirePower(2);
				interactive.setBulletColor(Color.ORANGE);
				break;
			default:
				interactive.setFirePower(1);
				interactive.setBulletColor(Color.YELLOW);
				break;
		}
	}
}