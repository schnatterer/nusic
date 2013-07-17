package info.schnatterer.newsic.activities;

import info.schnatterer.newsic.Application;
import info.schnatterer.newsic.R;
import info.schnatterer.newsic.model.Artist;
import info.schnatterer.newsic.tasks.LoadNewRelasesTask;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	private LoadNewRelasesTask loadNewRelasesTask = null;

	// private List<Artist> releases = new LinkedList<Artist>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ListView releasesListView = (ListView) findViewById(R.id.releasesListView);
		releasesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Object o = releasesListView.getItemAtPosition(position);
				Artist artist = (Artist) o;
				Application.toast("Selected :" + " " + artist.getArtistName());
			}

		});

		if (Application.isOnline()) {
			loadNewRelasesTask = new LoadNewRelasesTask(this, releasesListView);
			loadNewRelasesTask.execute();
		} else {
			Application.toast(getString(R.string.MainActivity_notOnline));
		}
	}

//	@Override
//	public Object onRetainNonConfigurationInstance() {
//		if (loadNewRelasesTask != null) {
//			loadNewRelasesTask.detach();
//			return (loadNewRelasesTask);
//		} else {
//			super.onRetainNonConfigurationInstance();
//		}
//	}

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_main);
	// // if (BuildConfig.DEBUG) {
	// // Log.d(Constants.LOG, "onCreated called");
	// // }
	//
	// final ListView listview = (ListView) findViewById(R.id.releasesListView);
	// String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
	// "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
	// "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
	// "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
	// "Android", "iPhone", "WindowsMobile" };
	//
	// final ArrayList<String> list = new ArrayList<String>();
	// for (int i = 0; i < values.length; ++i) {
	// list.add(values[i]);
	// }
	// final StableArrayAdapter adapter = new StableArrayAdapter(this,
	// android.R.layout.simple_list_item_1, list);
	// listview.setAdapter(adapter);
	//
	// // listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
	// {
	// // @Override
	// // public void onItemClick(AdapterView<?> parent, final View view,
	// // int position, long id) {
	// // final String item = (String) parent.getItemAtPosition(position);
	// // view.animate().setDuration(2000).alpha(0)
	// // .withEndAction(new Runnable() {
	// // @Override
	// // public void run() {
	// // list.remove(item);
	// // adapter.notifyDataSetChanged();
	// // view.setAlpha(1);
	// // }
	// // });
	// // }
	// //
	// // });
	// }
	//
	// private class StableArrayAdapter extends ArrayAdapter<String> {
	//
	// HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	//
	// public StableArrayAdapter(Context context, int textViewResourceId,
	// List<String> objects) {
	// super(context, textViewResourceId, objects);
	// for (int i = 0; i < objects.size(); ++i) {
	// mIdMap.put(objects.get(i), i);
	// }
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// String item = getItem(position);
	// return mIdMap.get(item);
	// }
	//
	// @Override
	// public boolean hasStableIds() {
	// return true;
	// }
	//
	// }
	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

}
