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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import roboguice.RoboGuice;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, packageName = "info.schnatterer.nusic")
public class NusicWebViewTest {

    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    @Test
    public void onCreate() throws Exception {
        //Intent intent = new Intent(Intent.ACTION_VIEW);
        //Activity activity = Robolectric.buildActivity(NusicWebViewForTest.class).withIntent(intent).create().get();
        // TODO Fails because does not find resources. Why must android testing be so complicated?
    }

    private static class NusicWebViewForTest extends NusicWebView {
        @Override
        public Context getApplicationContext() {
            return RuntimeEnvironment.application;
        }
    }
}
