package matt.honours;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@SuppressLint("Registered")
public abstract class NavigationEnabledActivity extends Activity {
	
	private static final String TAG = "NavigationEnabledActivity";
	//protected ArrayAdapter<String> action_bar_nav_adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart()");
		super.onStart();
		setUpActionBar();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu()");
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	
	protected void setUpActionBar() {
		Log.d(TAG, "setUpActionBar()");
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(false);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Log.d(TAG, "Settings action");
			return super.onOptionsItemSelected(item);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
}
