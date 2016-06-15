/**
 * Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic.
 *
 * nusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.qos.logback.classic.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class ThresholdLoggerFilter extends Filter<ILoggingEvent> {
	private Level level;
	private String logger;

	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!isStarted()) {
			return FilterReply.NEUTRAL;
		}

		if (!event.getLoggerName().startsWith(logger))
			return FilterReply.NEUTRAL;

		if (event.getLevel().isGreaterOrEqual(level)) {
			return FilterReply.NEUTRAL;
		} else {
			return FilterReply.DENY;
		}
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public void start() {
		if (this.level != null && this.logger != null) {
			super.start();
		}
	}
}
