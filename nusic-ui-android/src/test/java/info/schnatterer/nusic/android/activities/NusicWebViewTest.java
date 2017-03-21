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
    }

    private static class NusicWebViewForTest extends NusicWebView {
        @Override
        public Context getApplicationContext() {
            return RuntimeEnvironment.application;
        }
    }
}
