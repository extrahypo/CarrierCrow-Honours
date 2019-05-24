package matt.honours;

import matt.honours.database.DatabaseHelper;
import matt.honours.fragments.ThreadListFragment;
import matt.honours.model.AsyncTaskData;
import matt.honours.sms.asynctasks.KeyExchangeAsyncTask;
import matt.honours.sms.asynctasks.SmsSenderAsyncTask;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ConversationThreadActivity extends NavigationEnabledActivity {

	private static final String TAG = "ConversationThreadActivity";
	public String thread_id = "";

	private EditText text;
	private CheckBox encrypt_msg;

	private DatabaseHelper db;
	
	private FragmentManager	fragment_manager;
	private ThreadListFragment convoList;
	private static final String  CONVO_LIST_TAG = "ThreadListFragment";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
		setContentView(R.layout.conversation_thread);
		Log.d(TAG, "onCreate()");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		db = DatabaseHelper.getInstance(this.getApplicationContext());
		text = (EditText) findViewById(R.id.convo_thread_msg);
		encrypt_msg = (CheckBox) findViewById(R.id.thread_encrypt);

		Bundle b = getIntent().getExtras();
		if(b == null){
			Log.d(TAG, "Bundle is null");
		}

		thread_id = b.get(getString(R.string.db_threadId)).toString();
		
		if(thread_id == null || thread_id.equals("")){
		
				String ph = b.get(getString(R.string.db_phone)).toString();
				Log.d(TAG, ph);
				Cursor c = db.getThreadIdByPhone(ph);
				Log.d(TAG, ph);
				if(c.moveToFirst()){
					thread_id = c.getString(0);
				}
				Log.d(TAG, "onCreate()");
		}
		Log.d(TAG, "onCreate()");
	
		configureUIItems();
		fragment_manager = getFragmentManager();
		addFragments();

		configureButtons();
		Log.d(TAG, "onCreate()");
		} catch (Exception e){
			Log.e(TAG, "ERROR");
			e.printStackTrace();
		}
	}	

	private void addFragments() {

		convoList = (ThreadListFragment) fragment_manager.findFragmentByTag(CONVO_LIST_TAG);
		
		FragmentTransaction ft = fragment_manager.beginTransaction();
		
		if(convoList == null){
			convoList = new ThreadListFragment();
			ft.add(R.id.convo_thread_list_contanier, convoList, CONVO_LIST_TAG);
		}
		
		ft.commit();
		fragment_manager.executePendingTransactions();
	}
	
	private void configureUIItems() {
		db.setThreadRead(thread_id);
		Cursor contact = db.getContactByThreadId(thread_id);
		TextView textview = (TextView) findViewById(R.id.convo_thread_name);
		if(!contact.moveToFirst()){
			Cursor phone = db.getPhoneByThreadID(thread_id);
			if(phone.moveToFirst()){
				textview.setText(phone.getString(0));
			} else {
				textview.setText("Unknown");
			}
		} else {
			String lname = contact.getString(contact.getColumnIndex(getString(R.string.db_lname)));
			if(lname != null && !lname.equals("")){
				textview.setText(contact.getString(contact.getColumnIndex(getString(R.string.db_fname))) + " " +
						lname);
			} else {
				textview.setText(contact.getString(contact.getColumnIndex(getString(R.string.db_fname))));
			}			
		}
		
		int encrypt = db.getEncryption(thread_id);
		encrypt_msg = (CheckBox) findViewById(R.id.thread_encrypt);
		if(encrypt == 0){
			encrypt_msg.setChecked(false);
		} else {
			encrypt_msg.setChecked(true);
		}
		
	}

	private void configureButtons() {
		Log.d(TAG, "configureButtons()");
		Button send = (Button) findViewById(R.id.convo_thread_send);
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				sendMsg();
			}

			private void sendMsg() {
				String value = text.getText().toString();
				if(!value.equals("") || value != null){
					Cursor phone = db.getPhoneByThreadID(thread_id);
					if(phone.moveToFirst()){
						try{
							String ph = phone.getString(0);
							//SmsSender.sendMessage(phone, value.getString(), db, ConversationThreadActivity.this);
							SmsSenderAsyncTask task = new SmsSenderAsyncTask();
							task.execute(new AsyncTaskData[] {new AsyncTaskData(new String[]{ph, value, thread_id},
									ConversationThreadActivity.this, fragment_manager)});
							Log.d(TAG, ph);
							clearValues();
							//updateList();							
						} catch(Exception e){
							 Toast.makeText(getApplicationContext(),"SMS failed, please try again later!",
									 	Toast.LENGTH_LONG).show();
							e.printStackTrace();
						}
					}					
				}				
			}

			private void clearValues() {
				text.setText("");
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
			}
			
		});
		
		encrypt_msg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int e = 0;
				if(encrypt_msg.isChecked()){
					e = 1;
				}
				db.setEncryption(thread_id, e);
				db.close();
			}
			
		});
		
	}


	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		configureActionBar();	
		convoList.update();
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
		case R.id.action_delete_convo:
			deleteConvo();
			return true;
		case R.id.action_reset_keys:
			resetKeys();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void resetKeys() {
		// TODO Auto-generated method stub
		if(db.encryptionExists(thread_id)){
			new AlertDialog.Builder(this)
			.setTitle("Reset Keys")
			.setMessage("Are you sure you wish to reset the encryption keys?")
			.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					reset("9");
					
				}
			})
			.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
				}
			})
			.setIcon(android.R.drawable.ic_dialog_alert)
			.show();
		} else{
			db.insertEncryption(thread_id);
			reset("0");
		}
		
	}

	private void reset(String i){
		Cursor phone = db.getPhoneByThreadID(thread_id);
		if(phone.moveToFirst()){
			String ph = phone.getString(0);
			KeyExchangeAsyncTask task = new KeyExchangeAsyncTask();
			task.execute(new AsyncTaskData[] { new AsyncTaskData(new String[]{ph, "", i,
					thread_id}, ConversationThreadActivity.this, fragment_manager)});
		}
	}

	private void deleteConvo() {
		new AlertDialog.Builder(this)
		.setTitle("Delete Conversation")
		.setMessage("Are you sure you wish to delete this conversation?")
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				db.deleteConvo(thread_id);
				NavUtils.navigateUpFromSameTask(ConversationThreadActivity.this);
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.d(TAG, "onPrepareOptionsMenu()");
		
		menu.findItem(R.id.action_phone).setVisible(false);
		menu.findItem(R.id.action_contacts).setVisible(false);
		menu.findItem(R.id.action_new_plus).setVisible(false);
		menu.findItem(R.id.action_save).setVisible(false);
		menu.findItem(R.id.action_delete).setVisible(false);
		menu.findItem(R.id.action_delete_convo).setVisible(true);
		menu.findItem(R.id.action_reset_keys).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	private void configureActionBar() {
		Log.d(TAG, "configureActionBar()");
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
	}
}
