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

import info.schnatterer.nusic.android.util.TextUtil;
import info.schnatterer.nusic.ui.R;
import roboguice.activity.RoboActionBarActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Activity that loads a text from an asset file and displays it in a text view.
 * If the asset file ends in <code>.htm</code> or <code>.html</code> (case
 * insensitive) it is rendered as HTML.
 * 
 * The assets that is displayed can be passed to the activity using the
 * {@link #EXTRA_ASSET_NAME} {@link String}. In addition the title of the
 * activity (also called label) can be set explicitly by passing the
 * {@link #EXTRA_TITLE} {@link String}.
 * 
 * @author schnatterer
 */
public class TextAssetActivity extends RoboActionBarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_textview_layout);
		TextView textView = (TextView) findViewById(R.id.renderRawHtmlTextView);
		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		String title = getIntent().getStringExtra(
				getString(R.string.extra_activity_title));
		if (title != null) {
			setTitle(title);
		}

		textView.setMovementMethod(LinkMovementMethod.getInstance());

		/*
		 * This results in not clickable HTML links However, without this
		 * non-html URLs are not auto linked. So: use <a href="http://..">xz</a>
		 * syntax!
		 */
		String assetPath = getIntent().getStringExtra(
				getString(R.string.extra_asset_name));
		CharSequence text = TextUtil.loadTextFromAsset(this, assetPath);
		if (text != null) {
			textView.setText(text);
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
