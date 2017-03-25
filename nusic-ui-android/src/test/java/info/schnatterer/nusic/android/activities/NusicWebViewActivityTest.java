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
package info.schnatterer.nusic.android.activities;

import android.content.Intent;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import info.schnatterer.nusic.ui.BuildConfig;
import info.schnatterer.nusic.ui.R;
import roboguice.RoboGuice;

@RunWith(RobolectricTestRunner.class)
// Force usage of debug manifest, or else release builds will fail...
@Config(constants = BuildConfig.class, manifest = "../debug/AndroidManifest.xml")
public class NusicWebViewActivityTest {

    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    private String expectedUrl = "someUrl";
    private String expectedSubject = "the subject";

    @Test
    public void createShareIntent() throws Exception {
        Intent intent = new Intent()
            .putExtra(NusicWebViewActivity.EXTRA_URL, expectedUrl)
            .putExtra(NusicWebViewActivity.EXTRA_SUBJECT, expectedSubject);
        NusicWebViewActivity activity = Robolectric.buildActivity(NusicWebViewActivity.class, intent).create().get();
        Intent shareIntent = activity.createShareIntent();
        Assert.assertEquals("subject", expectedSubject, shareIntent.getStringExtra(Intent.EXTRA_SUBJECT));
        Assert.assertEquals("url",
            expectedUrl + "\n" + RuntimeEnvironment.application.getString(R.string.NusicWebViewActivity_sharedViaNusic),
            shareIntent.getStringExtra(Intent.EXTRA_TEXT));
    }

    @Test
    public void createShareIntentNoExtras() throws Exception {
        NusicWebViewActivity activity = Robolectric.setupActivity(NusicWebViewActivity.class);
        Intent shareIntent = activity.createShareIntent();
        Assert.assertEquals("subject", "", shareIntent.getStringExtra(Intent.EXTRA_SUBJECT));
        Assert.assertEquals("url", "", shareIntent.getStringExtra(Intent.EXTRA_TEXT));
    }

    // TODO assert onCreate(), email and web
}
