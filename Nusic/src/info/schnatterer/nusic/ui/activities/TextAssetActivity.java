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
package info.schnatterer.nusic.ui.activities;

import info.schnatterer.nusic.R;
import info.schnatterer.nusic.application.NusicApplication;
import info.schnatterer.nusic.ui.util.TextUtil;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

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
public class TextAssetActivity extends SherlockFragmentActivity {
	/**
	 * Key to the creating intent's extras that contains the path (as
	 * {@link String}) to the asset that is loaded.
	 */
	public static final String EXTRA_ASSET_NAME = NusicApplication.getContext()
			.getString(R.string.extra_asset_name);
	/**
	 * Key to the creating intent's extras that contains the title, also known
	 * as label (as {@link String}) of the activity.
	 */
	public static final String EXTRA_TITLE = NusicApplication.getContext()
			.getString(R.string.extra_activity_title);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.simple_textview_layout);

		String title = getIntent().getStringExtra(EXTRA_TITLE);
		if (title != null) {
			setTitle(title);
		}

		TextView textView = (TextView) findViewById(R.id.renderRawHtmlTextView);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		String assetPath = getIntent().getStringExtra(EXTRA_ASSET_NAME);
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
