/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.security.SafeComponent;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.nio.ByteBuffer;

/**
 * Super class of all events that originates from the keyboard.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.1
 */
public abstract class KeyEvent extends Event {
	private static final long serialVersionUID = 1L;
	private final java.awt.event.KeyEvent source;

	/**
	 * Called by the game to create a new KeyEvent.
	 *
	 * @param source the source key event originating from the AWT.
	 */
	public KeyEvent(java.awt.event.KeyEvent source) {
		this.source = source;
	}

	/**
	 * Do not call this method!
	 * <p>
	 * This method is used by the game to determine the type of the source key
	 * event that occurred in the AWT.
	 *
	 * @return the source key event that originates from the AWT.
	 */
	public java.awt.event.KeyEvent getSourceEvent() {
		return source;
	}

	protected static class SerializableHelper implements ISerializableHelper {

		private static void serializeDetails(RbSerializer serializer, ByteBuffer buffer, java.awt.event.KeyEvent event) {
			serializer.serialize(buffer, event.getKeyChar());
			serializer.serialize(buffer, event.getKeyCode());
			serializer.serialize(buffer, event.getKeyLocation());
			serializer.serialize(buffer, event.getID());
			serializer.serialize(buffer, event.getModifiersEx());
			serializer.serialize(buffer, event.getWhen());
		}

		private static java.awt.event.KeyEvent deserializeDetails(ByteBuffer buffer) {
			char keyChar = buffer.getChar();
			int keyCode = buffer.getInt();
			int keyLocation = buffer.getInt();
			int id = buffer.getInt();
			int modifiersEx = buffer.getInt();
			long when = buffer.getLong();

			return new java.awt.event.KeyEvent(SafeComponent.getSafeEventComponent(), id, when, modifiersEx, keyCode, keyChar, keyLocation);
		}

		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_CHAR + RbSerializer.SIZEOF_INT
					+ RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_LONG + RbSerializer.SIZEOF_INT + RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			if (object instanceof KeyTypedEvent) {
				serializeDetails(serializer, buffer, ((KeyTypedEvent) object).getSourceEvent());
			} else if (object instanceof KeyPressedEvent) {
				serializeDetails(serializer, buffer, ((KeyPressedEvent) object).getSourceEvent());
			} else if (object instanceof KeyReleasedEvent) {
				serializeDetails(serializer, buffer, ((KeyReleasedEvent) object).getSourceEvent());
			} else {
				throw new IllegalArgumentException("Unsupported event type: " + object.getClass().getName());
			}

		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			java.awt.event.KeyEvent event = deserializeDetails(buffer);

			// Determine the correct event type
			switch (event.getID()) {
				case java.awt.event.KeyEvent.KEY_TYPED:
					return new KeyTypedEvent(event);
				case java.awt.event.KeyEvent.KEY_PRESSED:
					return new KeyPressedEvent(event);
				case java.awt.event.KeyEvent.KEY_RELEASED:
					return new KeyReleasedEvent(event);
				default:
					throw new IllegalArgumentException("Unknown key event type: " + event.getID());
			}
		}
	}
}
