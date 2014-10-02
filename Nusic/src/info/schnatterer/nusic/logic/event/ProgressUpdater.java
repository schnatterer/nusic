/* Copyright (C) 2013 Johannes Schnatterer
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

 * nusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with nusic.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.logic.event;

import java.util.Set;

/**
 * Helper class for updating the progress.
 * 
 * To use it, just create an inner class setting the generic type parameters
 * needed.
 * 
 * @author schnatterer
 * 
 * @param <PROCESSED_ENTITY>
 * @param <RESULT_ENTITY>
 */
public class ProgressUpdater<PROCESSED_ENTITY, RESULT_ENTITY> {

	private Set<ProgressListener<PROCESSED_ENTITY, RESULT_ENTITY>> listenerList;
	private int max = 0;

	public ProgressUpdater(
			Set<ProgressListener<PROCESSED_ENTITY, RESULT_ENTITY>> listenerList) {
		this.listenerList = listenerList;
	}

	public void progressStarted(int nEntities) {
		max = nEntities;
		for (ProgressListener<PROCESSED_ENTITY, RESULT_ENTITY> progressListener : listenerList) {
			progressListener.onProgressStarted(nEntities);
		}
	}

	/**
	 * Notifies all {@link ProgressListener}s.
	 * 
	 * @param artist
	 * @param progress
	 * @param potentialException
	 */
	public void progress(PROCESSED_ENTITY entity, int progress,
			Throwable potentialException) {
		for (ProgressListener<PROCESSED_ENTITY, RESULT_ENTITY> progressListener : listenerList) {
			progressListener.onProgress(entity, progress, max,
					potentialException);
		}
	}

	public void progressFinished(RESULT_ENTITY result) {
		for (ProgressListener<PROCESSED_ENTITY, RESULT_ENTITY> progressListener : listenerList) {
			progressListener.onProgressFinished(result);
		}
	}

	public void progressFailed(PROCESSED_ENTITY entity, int progress,
			Throwable potentialException, RESULT_ENTITY resultOnFailure) {
		for (ProgressListener<PROCESSED_ENTITY, RESULT_ENTITY> progressListener : listenerList) {
			progressListener.onProgressFailed(entity, progress, max,
					resultOnFailure, potentialException);
		}
	}
}
