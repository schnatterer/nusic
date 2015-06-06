/* Copyright (C) 2014 Johannes Schnatterer
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
package info.schnatterer.nusic.android.activities;

import info.schnatterer.nusic.android.util.TextUtil;
import info.schnatterer.nusic.ui.R;
import roboguice.activity.RoboSherlockFragmentActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.view.MenuItem;

/**
 * Activity that loads a website from an URL and displays it in a text view.
 * 
 * The URI to the website that is displayed can be passed to the activity using
 * {@link Intent#setData(android.net.Uri)}.<br/>
 * <br/>
 * This will also work for email links (<code>mailto:</code>).URLs like<br/>
 * <code>mailto:x@y.z?subject=@string/someResource&amp;body=Your message here</code>
 * <br/>
 * will result in an intent {@link Intent#ACTION_SENDTO}, i.e. the user's
 * preferred email app is initialized with the parameters passed to this
 * activity. Note that any <code>@string/</code> parameters are localized before
 * starting the new activity.
 * 
 * @author schnatterer
 */
public class NusicWebView extends RoboSherlockFragmentActivity {

	/** "Protocol" prefix of a link for E-mails. */
	private static final String MAILTO_LINK = "mailto:";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		WebView webView = (WebView) findViewById(R.id.webview);

		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String url = getIntent().getData().toString();
		if (url.startsWith(MAILTO_LINK)) {
			Intent send = new Intent(Intent.ACTION_SENDTO);
			Uri uri = Uri.parse(TextUtil.replaceResourceStrings(this, url));

			send.setData(uri);
			startActivity(send);
			finish();
		} else {
			/* Activate JavaScript */
			// webView.getSettings().setJavaScriptEnabled(true);

			// webView.getSettings().setLoadWithOverviewMode(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.getSettings().setBuiltInZoomControls(true);

			webView.loadUrl(url);

			/* Prevent WebView from Opening the Browser on external links */
			webView.setWebViewClient(new InsideWebViewClient());
		}
	}

	/* Class that prevents opening the Browser */
	private class InsideWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// When the back arrow in the header (left of the icon) is clicked,
			// "go back one activity"
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
