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

		ExecCommands ec2 = serializeAndDeserialize(ec, RbSerializer.ExecCommands_TYPE);

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

		ExecCommands ec2 = serializeAndDeserialize(ec, RbSerializer.ExecCommands_TYPE);

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

		ExecCommands ec2 = serializeAndDeserialize(ec, RbSerializer.ExecCommands_TYPE);

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

		ExecCommands ec2 = serializeAndDeserialize(ec, RbSerializer.ExecCommands_TYPE);

		Assert.assertEquals(ec2.getDebugProperties().get(0).getKey(), "UTF8 Native characters");
		Assert.assertEquals(ec2.getDebugProperties().get(0).getValue(), "Příliš žluťoučký kůň úpěl ďábelské ódy.");
	}

	private <T> T serializeAndDeserialize(T object, int type) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		RbSerializer rbs = new RbSerializer();

		rbs.serialize(out, type, object);
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return (T) rbs.deserialize(in);
	}

	public static void assertNear(double v1, double v2) {
		Assert.assertEquals(v1, v2, Utils.NEAR_DELTA);
	}
}
