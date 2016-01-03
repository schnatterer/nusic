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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link FileUtil}.
 * 
 * @author schnatterer
 *
 */
public class FileUtilTest {

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	/**
	 * Positive test for {@link FileUtil#deleteFilesOlderThan(File, int)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeleteFilesOlderThan() throws IOException {
		// Delete everything older than x days
		int daysThreshold = 2;

		/* Test files */
		// 1970
		File fileReallyOld = createFile("reallyOld.txt", new Date(0l));
		// One day older than threshold
		File fileJustTooOld = createFile("justTooOld.txt",
				DateUtils.addDays(new Date(), -(daysThreshold + 1)));
		// Future file, keep it
		createFile("future.txt", DateUtils.addDays(new Date(), 1));

		// Call method
		List<File> deletedFiles = FileUtil.deleteFilesOlderThan(
				testFolder.getRoot(), daysThreshold, null);

		assertThat("File not deleted", deletedFiles,
				containsInAnyOrder(fileReallyOld, fileJustTooOld));
		assertEquals("Too much files deleted", 2, deletedFiles.size());
	}

	/**
	 * Positive test for {@link FileUtil#deleteFilesOlderThan(File, int)} where
	 * no files should be deleted.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeleteFilesOlderThanNoFiles() throws IOException {
		// Delete everything older than x days
		int daysThreshold = 2;

		/* Test files */
		// Future file, keep it
		createFile("future.txt", DateUtils.addDays(new Date(), 1));

		// Call method
		List<File> deletedFiles = FileUtil.deleteFilesOlderThan(
				testFolder.getRoot(), daysThreshold, null);

		assertThat(deletedFiles, empty());
	}

	/**
	 * Negative test for {@link FileUtil#deleteFilesOlderThan(File, int)}
	 * passing a file instead of a folder.
	 * 
	 * @throws IOException
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteFilesOlderThanNotFolder() throws IOException {
		FileUtil.deleteFilesOlderThan(testFolder.newFile(), 42, null);
	}

	/**
	 * Negative test for {@link FileUtil#deleteFilesOlderThan(File, int)}
	 * passing a <code>null</code> file instead of a folder.
	 * 
	 * @throws IOException
	 */
	@Test(expected = NullPointerException.class)
	public void testDeleteFilesOlderThanNullFolder() {
		FileUtil.deleteFilesOlderThan(null, 42, null);
	}

	/**
	 * Negative test for {@link FileUtil#deleteFilesOlderThan(File, int)} where
	 * a file fails to delete.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testDeleteFilesOlderThanFileFails() throws IOException {
		// Delete everything older than x days
		int daysThreshold = 2;

		// Mocked file that fails to delete
		File mockedFile = mock(File.class);
		when(mockedFile.lastModified()).thenReturn(new Date(0l).getTime());
		when(mockedFile.delete()).thenReturn(false);
		// One day older than threshold
		File fileJustTooOld = createFile("justTooOld.txt",
				DateUtils.addDays(new Date(), -(daysThreshold + 1)));

		// Make mocked directory return our files
		File mockedDirectory = mock(File.class);
		when(mockedDirectory.isDirectory()).thenReturn(true);
		when(mockedDirectory.listFiles(any(FileFilter.class))).thenReturn(
				new File[] { mockedFile, fileJustTooOld });

		List<File> failed = new LinkedList<File>();
		List<File> deletedFiles = FileUtil.deleteFilesOlderThan(
				mockedDirectory, daysThreshold, failed);
		// Assert that failed file is on list
		assertThat("File expected to fail not on failed list", failed,
				containsInAnyOrder(mockedFile));
		assertEquals("Too much files on failed list", 1, failed.size());

		// Assert that other file was deleted
		assertThat("File not deleted", deletedFiles,
				containsInAnyOrder(fileJustTooOld));
		assertEquals("Too much files deleted", 1, deletedFiles.size());
	}

	private File createFile(String fileName, Date lastModified)
			throws IOException {
		File tempFile = testFolder.newFile(fileName);
		tempFile.setLastModified(lastModified.getTime());
		return tempFile;
	}

}
