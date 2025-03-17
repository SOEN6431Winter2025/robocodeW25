/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.serialization;

import net.sf.robocode.peer.BulletCommand;
import net.sf.robocode.peer.DebugProperty;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.peer.TeamMessage;
import net.sf.robocode.robotpaint.Graphics2DSerialized;
import net.sf.robocode.security.HiddenAccess;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import robocode.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;

/**
 * @author Pavel Savara (original)
 */
public class RbSerializerTest {

	private Exception exception = null;

	@BeforeClass
	public static void init() {
		if (!new File("").getAbsolutePath().endsWith("robocode.core")) {
			throw new Error("Please run test with current directory in 'robocode.core'");
		}

		// Disable engine classloader for this test
		System.setProperty("NOSECURITY", "true");
		System.setProperty("TESTING", "true");
		HiddenAccess.initContainer();
	}

	@AfterClass
	public static void cleanup() {
		System.setProperty("NOSECURITY", "false");
	}

	@Test
	public void empty() throws IOException {
		ExecCommands ec = new ExecCommands();
		ec.setBodyTurnRemaining(150.123);
		ec.setTryingToPaint(true);

		ExecCommands ec2 = serializeAndDeserializeExecCommands(ec);

		assertNear(ec2.getBodyTurnRemaining(), ec.getBodyTurnRemaining());
		Assert.assertTrue(ec2.isTryingToPaint());
	}

	@Test
	public void withBullets() throws IOException {
		ExecCommands ec = new ExecCommands();
		ec.setBodyTurnRemaining(150.123);
		ec.getBullets().add(new BulletCommand(1.0, true, 0.9354, 11));
		ec.getBullets().add(new BulletCommand(1.0, false, 0.9454, 12));
		ec.getBullets().add(new BulletCommand(1.0, true, 0.9554, -128));

		ExecCommands ec2 = serializeAndDeserializeExecCommands(ec);

		assertNear(ec2.getBodyTurnRemaining(), ec.getBodyTurnRemaining());
		assertNear(ec2.getBullets().get(0).getPower(), 1.0);
		Assert.assertFalse(ec2.getBullets().get(1).isFireAssistValid());
		Assert.assertTrue(ec2.getBullets().get(2).isFireAssistValid());
		Assert.assertEquals(ec2.getBullets().get(2).getBulletId(), -128);
	}

	@Test
	public void withMessages() throws IOException {
		ExecCommands ec = new ExecCommands();
		ec.setBodyTurnRemaining(150.123);
		ec.getBullets().add(new BulletCommand(1.0, true, 0.9354, 11));

		final byte[] data = new byte[20];
		data[10] = 10;
		ec.getTeamMessages().add(new TeamMessage("Foo", "Bar", data));
		ec.getTeamMessages().add(new TeamMessage("Foo", "Bar", null));

		ExecCommands ec2 = serializeAndDeserializeExecCommands(ec);

		Assert.assertEquals(ec2.getTeamMessages().get(0).message[0], 0);
		Assert.assertEquals(ec2.getTeamMessages().get(0).message[10], 10);
		Assert.assertEquals(ec2.getTeamMessages().get(0).sender, "Foo");
		Assert.assertEquals(ec2.getTeamMessages().get(0).recipient, "Bar");
		Assert.assertNull(ec2.getTeamMessages().get(1).message);
	}

	@Test
	public void withProperties() throws IOException {
		ExecCommands ec = new ExecCommands();
		ec.setBodyTurnRemaining(150.123);
		ec.getBullets().add(new BulletCommand(1.0, true, 0.9354, 11));
		ec.getTeamMessages().add(new TeamMessage("Foo", "Bar", null));
		ec.getDebugProperties().add(new DebugProperty("UTF8 Native characters", "Příliš žluťoučký kůň úpěl ďábelské ódy."));

		ExecCommands ec2 = serializeAndDeserializeExecCommands(ec);

		Assert.assertEquals(ec2.getDebugProperties().get(0).getKey(), "UTF8 Native characters");
		Assert.assertEquals(ec2.getDebugProperties().get(0).getValue(), "Příliš žluťoučký kůň úpěl ďábelské ódy.");
	}

	@Test
	public void graphics() throws InterruptedException {
		final Graphics2DSerialized sg = new Graphics2DSerialized();

		sg.setPaintingEnabled(true);
		sg.setBackground(Color.GREEN);
		sg.setColor(Color.RED);
		sg.draw(new Arc2D.Double(Arc2D.PIE));

		sg.setColor(Color.BLUE);
		sg.draw(new Line2D.Double(99, 98, 78, 3));

		sg.setColor(Color.YELLOW);
		sg.draw(new Rectangle2D.Double(20, 20, 30, 50));

		sg.setColor(Color.BLACK);
		sg.drawLine(99, 3, 78, 3);
		sg.drawRect(90, 20, 30, 50);

		sg.setColor(Color.CYAN);
		sg.setStroke(new BasicStroke(1, 2, BasicStroke.JOIN_ROUND, 4, null, 0));
		sg.fill(new Rectangle2D.Double(20, 70, 30, 50));
		sg.fill(new Ellipse2D.Double(70, 70, 30, 50));

		sg.setColor(Color.MAGENTA);
		sg.fill(new RoundRectangle2D.Double(110, 70, 30, 50, 13.5, 16.1));

		exception = null;

		Canvas d = new Canvas() {
			@Override
			public void paint(Graphics g) {
				synchronized (this) {
					try {
						sg.processTo((Graphics2D) g);
					} catch (Exception e) {
						exception = e;
					}
				}
			}
		};

		d.setSize(200, 200);
		if (!GraphicsEnvironment.isHeadless()) {
			JFrame f = new JFrame();
			f.add(d);
			f.pack();
			f.setVisible(true);
			Thread.sleep(100);
			f.setVisible(false);
		}

		Assert.assertNull("Exception occurred: " + exception, exception);
	}

	private ExecCommands serializeAndDeserializeExecCommands(ExecCommands ec) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		RbSerializer rbs = new RbSerializer();

		rbs.serialize(out, RbSerializer.ExecCommands_TYPE, ec);
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return (ExecCommands) rbs.deserialize(in);
	}

	public static void assertNear(double v1, double v2) {
		Assert.assertEquals(v1, v2, Utils.NEAR_DELTA);
	}
}
