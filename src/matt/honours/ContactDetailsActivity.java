package matt.honours;

import matt.honours.database.DatabaseProvider;
import matt.honours.fragments.ContactsDetailsFragment;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

public class ContactDetailsActivity extends NavigationEnabledActivity {

	private static final String TAG = "ContactDetaisActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_details_activity);
		if(findViewById(R.id.contact_details_container) != null){
			if(savedInstanceState != null){
				return;
			}
			ContactsDetailsFragment details = new ContactsDetailsFragment(); 
			details.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(R.id.contact_details_container, details).commit();
		}
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.d(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_save:
			saveContact();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void saveContact() {
		EditText fname = (EditText) findViewById(R.id.contact_details_fname);
		EditText phone = (EditText) findViewById(R.id.contact_details_phone);
		
		if(fname.getText().toString().matches("") || phone.getText().toString().matches("")){
			new AlertDialog.Builder(this)
		    .setTitle("Save Error")
		    .setMessage("Contact must have a first name and phone number")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		     .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
		} else {
			EditText lname = (EditText) findViewById(R.id.contact_details_lname);
			EditText email = (EditText) findViewById(R.id.contact_details_email);
			EditText address = (EditText) findViewById(R.id.contact_details_address);
			EditText notes = (EditText) findViewById(R.id.contact_details_notes);
			
			ContentValues queryValues = new ContentValues();
			queryValues.put(getString(R.string.db_fname), fname.getText().toString());
			queryValues.put(getString(R.string.db_lname), lname.getText().toString());
			queryValues.put(getString(R.string.db_phone), phone.getText().toString());
			queryValues.put(getString(R.string.db_email), email.getText().toString());
			queryValues.put(getString(R.string.db_address), address.getText().toString());
			queryValues.put(getString(R.string.db_notes), notes.getText().toString());
			this.getContentResolver().insert(DatabaseProvider.CONTACTS_CONTENT_URI, queryValues);
			//dbTools.insertContact(queryValues);	
			this.callContactActivity();
		}
	}
	
	public void callContactActivity(){
		NavUtils.navigateUpFromSameTask(this);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		
		menu.findItem(R.id.action_phone).setVisible(false);
		menu.findItem(R.id.action_contacts).setVisible(false);
		menu.findItem(R.id.action_new_plus).setVisible(false);
		menu.findItem(R.id.action_save).setVisible(true);
		menu.findItem(R.id.action_delete).setVisible(false);
		menu.findItem(R.id.action_delete_convo).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}

}
