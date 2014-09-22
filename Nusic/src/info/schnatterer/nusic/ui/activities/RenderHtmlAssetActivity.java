package info.schnatterer.nusic.ui.activities;

import info.schnatterer.nusic.Constants;
import info.schnatterer.nusic.R;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class RenderHtmlAssetActivity extends SherlockFragmentActivity {

	private static final String REGEX_ENDING_HTML = "^.*(\\.htm[l]?)$";
	// TODO define in constants?
	public static final String EXTRA_ASSET_NAME = "nusic.intent.extra.assetName";
	public static final String EXTRA_TITLE = "nusic.intent.extra.title";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_render_html_asset);

		String title = getIntent().getStringExtra(EXTRA_TITLE);
		if (title != null) {
			setTitle(title);
		}

		TextView textView = (TextView) findViewById(R.id.renderRawHtmlTextView);
		String assetPath = getIntent().getStringExtra(EXTRA_ASSET_NAME);
		if (assetPath != null) {
			InputStream is = null;
			try {
				is = getResources().getAssets().open(assetPath);
				if (assetPath.matches(REGEX_ENDING_HTML)) {
					textView.setText(Html.fromHtml(IOUtils.toString(is)
							.replaceFirst("<title>.*</title>", "")));
				} else {
					textView.setText(IOUtils.toString(is));
				}
			} catch (IOException e) {
				// TODO i18n
				textView.setText("Unable to load text file");
				Log.w(Constants.LOG, "Unable to load asset from path \""
						+ assetPath + "\"", e);
			} finally {
				IOUtils.closeQuietly(is);
			}
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
