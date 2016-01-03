/**
 * ﻿Copyright (C) 2013 Johannes Schnatterer
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This file is part of nusic-util.
 *
 * nusic-util is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * nusic-util is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with nusic-util.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.schnatterer.nusic.util;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.time.DateUtils;

/**
 * File utilities.
 * 
 * @author schnatterer
 *
 */
public final class FileUtil {

	private FileUtil() {
	}

	/**
	 * Deletes files older than a specific amount of days from the current date.
	 * 
	 * @param directory
	 *            directory where to look for the files.
	 * @param days
	 *            amount of days before the current date. Files older than that
	 *            are deleted
	 * @param failedFiles
	 *            (optional, can be <code>null</code>) a list of files that
	 *            where supposed to be deleted but failed to be deleted
	 * @return a list of successfully deleted files
	 */
	public static List<File> deleteFilesOlderThan(File directory, int days,
			List<File> failedFiles) {
		List<File> deleted = new LinkedList<File>();

		Collection<File> filesToDelete = FileUtils.listFiles(directory,
				new AgeFileFilter(DateUtils.addDays(new Date(), -days)),
				TrueFileFilter.TRUE);

		for (File fileToDelete : filesToDelete) {
			boolean success = fileToDelete.delete();
			if (success) {
				deleted.add(fileToDelete);
			} else if (failedFiles != null) {
				failedFiles.add(fileToDelete);
			}
		}
		return deleted;
	}
}
