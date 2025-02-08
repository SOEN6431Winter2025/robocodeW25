/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;


/**
 * @author Pavel Savara (original)
 * fixing the issue "Make instance a static final constant or non-public and provide accessors if needed"
 */
public abstract class ContainerBase {
	private static ContainerBase instance;

	public static ContainerBase getInstance(){
		return instance;
	}

	public static void setInstance(ContainerBase containerBase){
		instance = containerBase;
	}

	protected abstract <T> T getBaseComponent(java.lang.Class<T> tClass);

	public static <T> T getComponent(java.lang.Class<T> tClass) {
		return instance == null ? null : instance.getBaseComponent(tClass);
	}
}
