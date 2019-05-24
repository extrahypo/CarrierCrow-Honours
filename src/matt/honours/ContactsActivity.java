package matt.honours;

import matt.honours.fragments.ContactListFragment;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ContactsActivity extends NavigationEnabledActivity {

	private static final String TAG = "ContactsActivity";

	private FragmentManager	fragment_manager;
	private static final String  CONTACT_LIST_TAG = "ContactListFragment";
	private ContactListFragment contactList = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

			if(savedInstanceState != null){
				
			}
			setContentView(R.layout.contacts_activity2);
			fragment_manager = getFragmentManager();
			addFragments();
	}
	
	private void addFragments() {

		contactList = (ContactListFragment) fragment_manager.findFragmentByTag(CONTACT_LIST_TAG);
		
		FragmentTransaction ft = fragment_manager.beginTransaction();
		
		if(contactList == null){
			contactList = new ContactListFragment();
			ft.add(R.id.contact_container2,contactList, CONTACT_LIST_TAG);
		}
		
		ft.commit();
		fragment_manager.executePendingTransactions();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		configureActionBar();
	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case R.id.action_new_plus:
			switchActivities();
			return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	private void switchActivities() {
		Intent intent = new Intent(this, ContactDetailsActivity.class);
		startActivity(intent);
	}

	private void configureActionBar() {
		Log.d(TAG, "configureActionBar()");
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	}
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		
		menu.findItem(R.id.action_phone).setVisible(false);
		menu.findItem(R.id.action_contacts).setVisible(false);
		menu.findItem(R.id.action_new_plus).setVisible(true);
		menu.findItem(R.id.action_save).setVisible(false);
		menu.findItem(R.id.action_delete).setVisible(false);
		menu.findItem(R.id.action_delete_convo).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}


}
