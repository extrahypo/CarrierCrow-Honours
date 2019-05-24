package matt.honours;

import matt.honours.fragments.ContactListFragment;
import matt.honours.model.AsyncTaskData;
import matt.honours.sms.asynctasks.NewSmsSenderAsyncTask;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

//TODO check whats going on with the button on orientation change
public class NewConvoActivity extends NavigationEnabledActivity {

	private static final String TAG = "NewConoActivity";

	private FragmentManager	fragment_manager;
	private static final String  CONTACT_LIST_TAG = "ContactListFragment";
	private ContactListFragment contactList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_convo_activity2);
		if(findViewById(R.id.newconvo_container) != null){
			if(savedInstanceState != null){
				return;
			}
			fragment_manager = getFragmentManager();
			addFragments();
			contactList.setOtherValues(true, (EditText) findViewById(R.id.new_convo_phone_num));
			configureButtons();
		}	
	}
	
	

	private void addFragments() {
		contactList = (ContactListFragment) fragment_manager.findFragmentByTag(CONTACT_LIST_TAG);
		
		FragmentTransaction ft = fragment_manager.beginTransaction();
		
		if(contactList == null){
			contactList = new ContactListFragment();
			ft.add(R.id.new_convo_contanier,contactList, CONTACT_LIST_TAG);
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
	
	
	
	private void configureActionBar() {
		Log.d(TAG, "configureActionBar()");
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	}
	
	private void configureButtons() {
		Button contact = (Button) findViewById(R.id.choose_contact);
		contact.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				findViewById(R.id.new_convo_contanier).setVisibility(View.VISIBLE);
				findViewById(R.id.choose_contact).setVisibility(View.GONE);				
			}			
		});
		
		Button send = (Button) findViewById(R.id.new_convo_send);
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				EditText number = (EditText) findViewById(R.id.new_convo_phone_num);				
				if(number.getText().toString().matches("")){
					//do nothing
				} else {
					EditText msg = (EditText) findViewById(R.id.new_convo_msg);
					sendMessage(number.getText().toString(), msg.getText().toString());
				}				
			}
					
		});
	}
	
	private void sendMessage(String ph, String msg) {
		try{
			NewSmsSenderAsyncTask task = new NewSmsSenderAsyncTask();
			task.execute(new AsyncTaskData[] {new AsyncTaskData(new String[]{ph, msg}, this, fragment_manager)});
			Log.d(TAG, ph);
			startConvoThread(ph);
		} catch(Exception e){
			 Toast.makeText(getApplicationContext(),"SMS failed, please try again later!",
					 	Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
	}

	private void startConvoThread(String ph) {
		Intent i = new Intent(this, ConversationThreadActivity.class);
		i.putExtra(getString(R.string.db_phone), ph);
		i.putExtra(getString(R.string.db_threadId), "");
		startActivity(i);		
	}
}
