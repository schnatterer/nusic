package info.schnatterer.nusic.ui.activities;

import info.schnatterer.nusic.R;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class NusicWebView extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the back arrow in the header (left of the icon)
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_web_view);

		WebView webView = (WebView) findViewById(R.id.webview);
		/* Activate JavaScript */
		// webView.getSettings().setJavaScriptEnabled(true);

		// webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView.loadUrl(getIntent().getData().toString());

		/* Prevent WebView from Opening the Browser */
		webView.setWebViewClient(new InsideWebViewClient());
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
