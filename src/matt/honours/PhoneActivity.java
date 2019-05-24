package matt.honours;

import matt.honours.fragments.ContactListFragment;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PhoneActivity extends NavigationEnabledActivity {

	private static final String TAG = "PhoneActivity";	

	private EditText numField;

	private FragmentManager	fragment_manager;
	private static final String  CONTACT_LIST_TAG = "ContactListFragment";
	private ContactListFragment contactList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_book_activity);

		numField = (EditText) findViewById(R.id.phone_phonenum);
		
		//configureContacts();
		fragment_manager = getFragmentManager();
		addFragments();
		contactList.setOtherValues(true, numField);
		configureOther();		
	}
	
	private void addFragments() {

		contactList = (ContactListFragment) fragment_manager.findFragmentByTag(CONTACT_LIST_TAG);
		
		FragmentTransaction ft = fragment_manager.beginTransaction();
		
		if(contactList == null){
			contactList = new ContactListFragment();
			ft.add(R.id.phone_call_container,contactList, CONTACT_LIST_TAG);
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

	private void configureActionBar() {
		Log.d(TAG, "configureActionBar()");
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	}
	
	private void configureOther() {
		Log.d(TAG, "configureOther()");
		//DIAL BUTTON
		Button dial = (Button) findViewById(R.id.phone_call_dial);
		dial.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				String ph  = numField.getText().toString();	
				Log.d(TAG, ph);
				/*if(!ph.equals("")){
					HashMap<String, String> details = dbTools.getContactInfoByPhone(ph);
					Intent i = new Intent(getApplication(), PhoneCallActivity.class);
					if(details.isEmpty())
						i.putExtra("contactID", "");
					else
						i.putExtra("contactID", details.containsKey("contactID"));
					i.putExtra("phone", ph);
					startActivity(i);				
				}	*/
				//String uri = "tel:"+ ph;
				//Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(uri));		
				//startActivity(callIntent);
				
				Intent i = new Intent(Intent.ACTION_MAIN, null);
				i.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName cn = new ComponentName("matt.honours.voice", 
						"matt.honours.voice.PhoneActivity");
				i.setComponent(cn);
				i.putExtra("phone", numField.getText().toString());
				i.putExtra("name", "");
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
			}
			
		});		
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Log.d(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		
		menu.findItem(R.id.action_phone).setVisible(false);
		menu.findItem(R.id.action_contacts).setVisible(false);
		menu.findItem(R.id.action_new_plus).setVisible(false);
		menu.findItem(R.id.action_save).setVisible(false);
		menu.findItem(R.id.action_delete).setVisible(false);
		menu.findItem(R.id.action_delete_convo).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}

}
