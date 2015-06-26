/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-ui-android.
 *
 * nusic-ui-android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-ui-android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-ui-android.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.android.util;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public final class Log {

	private Log() {
	}

	/**
	 * Set the log level of the root logger. <b>Note</b>: This depends on the
	 * actual logging framework.
	 * 
	 * @param level
	 *            the log level to set
	 */
	public static void setRootLogLevel(String level) {
		Logger root = (Logger) LoggerFactory
				.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		root.info("root.getLevel(): {}", root.getLevel().toString());
		root.info("Setting level to {}", level);
		root.setLevel(Level.toLevel(level));
	}
}
