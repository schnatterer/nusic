/**
 * Copyright (C) 2013 Johannes Schnatterer
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
package info.schnatterer.nusic.android.activities;

import info.schnatterer.nusic.android.util.Logs;
import info.schnatterer.nusic.android.util.TextUtil;
import info.schnatterer.nusic.ui.R;

import java.io.File;
import java.util.ArrayList;

import roboguice.activity.RoboActionBarActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;

/**
 * Sends an intent that contains all available log files. It is supposed to be
 * consumed by an emailer app. Note that you need to set up the
 * {@link FileProvider} in your android manifest. For example: <br/>
 * 
 * <pre>
 *    &lt;!-- Expose log files for email clients --&gt;
 *    &lt;provider
 *         android:name=&quot;android.support.v4.content.FileProvider&quot;
 *         android:authorities=&quot;@string/authority_log_file_provider&quot;
 *         android:exported=&quot;false&quot;
 *         android:grantUriPermissions=&quot;true&quot; &gt;
 *         &lt;meta-data
 *             android:name=&quot;android.support.FILE_PROVIDER_PATHS&quot;
 *             android:resource=&quot;@xml/logpath&quot; /&gt;
 *     &lt;/provider&gt;
 * </pre>
 * 
 * @author schnatterer
 *
 */
public class SendLogActivity extends RoboActionBarActivity {

	/** Subject to use. */
	private static final String SUBJECT = "[nusic @string/versionName] - Issue";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* Get URIs for log files using android.support.v4.content.FileProvider */
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (final File fileEntry : Logs.getLogFiles(this)) {
			// Don't recurse!
			if (!fileEntry.isDirectory()) {
				// Create content provider URI
				uris.add(FileProvider.getUriForFile(this,
						getString(R.string.authority_log_file_provider),
						fileEntry));
			}
		}

		final Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
		email.setType("message/rfc822");
		email.putExtra(Intent.EXTRA_EMAIL,
				new String[] { getString(R.string.app_contact) });
		email.putExtra(Intent.EXTRA_SUBJECT,
				TextUtil.replaceResourceStrings(this, SUBJECT));
		email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		email.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		startActivity(email);

		finish();
	}
}
