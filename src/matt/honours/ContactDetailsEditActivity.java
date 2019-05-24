package matt.honours;

import matt.honours.database.DatabaseProvider;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class ContactDetailsEditActivity extends NavigationEnabledActivity {

	private static final String TAG = "ContactsDetailsEditActivity";
	private static final String WHERE = "contactID = ?";
	//DBTools dbTools = new DBTools(this);
	private static final String[]		CONTACTS_PROJECTION	= new String[] { BaseColumns._ID,
		"contactID", "firstName", "lastName", "phone", "email", "address", "notes", "threadID"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_details_edit_activity);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		String cID = getIntent().getStringExtra(getString(R.string.db_contactId));
		Log.d("CONTACTID", cID);
		configureDetails(cID);
	}
	
	private void configureDetails(String id){
		Cursor values = this.getContentResolver().query(DatabaseProvider.CONTACT_SGL_URI, 
				CONTACTS_PROJECTION, WHERE, new String[] {id}, null);
		values.moveToFirst();
		EditText fname = (EditText) findViewById(R.id.contact_edit_fname);
		EditText phone = (EditText) findViewById(R.id.contact_edit_phone);
		TextView contactid = (TextView) findViewById(R.id.contact_edit_id);
		EditText lname = (EditText) findViewById(R.id.contact_edit_lname);
		EditText email = (EditText) findViewById(R.id.contact_edit_email);
		EditText address = (EditText) findViewById(R.id.contact_edit_address);
		EditText notes = (EditText) findViewById(R.id.contact_edit_notes);
		fname.setText(values.getString(2));
		phone.setText(values.getString(4));
		contactid.setText(id);
		lname.setText(values.getString(3));
		email.setText(values.getString(5));
		address.setText(values.getString(6));
		notes.setText(values.getString(7));
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
		case R.id.action_delete:
			deleteContact();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteContact() {
		new AlertDialog.Builder(this)
		.setTitle("Delete Entry")
		.setMessage("Are you sure you want to delete this contact?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
			TextView contactid = (TextView) findViewById(R.id.contact_edit_id);
            //dbTools.deleteContact(contactid.getText().toString());
            ContactDetailsEditActivity.this.getContentResolver().delete(DatabaseProvider.CONTACTS_CONTENT_URI,
            		WHERE, new String[] {contactid.getText().toString()});
            callContactActivity();
			}
		})
		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
            // do nothing
			}
		})
		.setIcon(android.R.drawable.ic_dialog_alert)
		.show();
	}
	
	private void saveContact() {
		EditText fname = (EditText) findViewById(R.id.contact_edit_fname);
		EditText phone = (EditText) findViewById(R.id.contact_edit_phone);
	
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
			TextView contactid = (TextView) findViewById(R.id.contact_edit_id);
			EditText lname = (EditText) findViewById(R.id.contact_edit_lname);
			EditText email = (EditText) findViewById(R.id.contact_edit_email);
			EditText address = (EditText) findViewById(R.id.contact_edit_address);
			EditText notes = (EditText) findViewById(R.id.contact_edit_notes);
		
		ContentValues queryValues = new ContentValues();
		queryValues.put(getString(R.string.db_contactId), contactid.getText().toString());
		queryValues.put(getString(R.string.db_fname), fname.getText().toString());
		queryValues.put(getString(R.string.db_lname), lname.getText().toString());
		queryValues.put(getString(R.string.db_phone), phone.getText().toString());
		queryValues.put(getString(R.string.db_email), email.getText().toString());
		queryValues.put(getString(R.string.db_address), address.getText().toString());
		queryValues.put(getString(R.string.db_notes), notes.getText().toString());
		//dbTools.updateContact(queryValues);	
		this.getContentResolver().update(DatabaseProvider.CONTACT_SGL_URI, queryValues, 
				WHERE, new String[] {contactid.getText().toString()});
		callContactActivity();
	}
}

public void callContactActivity(){
	NavUtils.navigateUpFromSameTask(this);
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		
		menu.findItem(R.id.action_phone).setVisible(false);
		menu.findItem(R.id.action_contacts).setVisible(false);
		menu.findItem(R.id.action_new_plus).setVisible(false);
		menu.findItem(R.id.action_save).setVisible(true);
		menu.findItem(R.id.action_delete).setVisible(true);
		menu.findItem(R.id.action_delete_convo).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}
	

}
