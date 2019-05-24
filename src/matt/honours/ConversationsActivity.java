package matt.honours;


import matt.honours.Utils.SmsUtils;
import matt.honours.fragments.ConversationsListFragment;
import matt.honours.model.AsyncTaskData;
import matt.honours.sms.asynctasks.TestEncryption;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.MenuItem;

public class ConversationsActivity extends NavigationEnabledActivity {

	private static final String TAG = "ConversationsActivity";

	private FragmentManager	fragment_manager;
	private static final String  CONVO_LIST_TAG = "ConversationListFragment";
	private ConversationsListFragment convoList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.conversations_activity2);
		if(savedInstanceState != null){

		}
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(1);
		fragment_manager = getFragmentManager();
		addFragments();
		TestEncryption task = new TestEncryption();
		task.execute(new AsyncTaskData[]{new AsyncTaskData(new String[]{"keys"}, this, null)});
	}
	

	private void addFragments() {
		convoList = (ConversationsListFragment) fragment_manager.findFragmentByTag(CONVO_LIST_TAG);
		
		FragmentTransaction ft = fragment_manager.beginTransaction();
		
		if(convoList == null){
			convoList = new ConversationsListFragment();
			ft.add(R.id.convo_list_container, convoList, CONVO_LIST_TAG);
		}
		
		ft.commit();
		fragment_manager.executePendingTransactions();
	}

	@Override
	protected void onResume(){
		super.onResume();
		if(isDefault(this)){
			//do nothing
		} else {
			isDefaultApp(this);
		}	
		convoList.update();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_contacts:
			Intent intent = new Intent(this, ContactsActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_new_plus:
			newConvoActivity();
			return true;
		case R.id.action_phone:
			Intent intentPH = new Intent(this, PhoneActivity.class);
			startActivity(intentPH);
			return true;
		case R.id.action_delete_convo:
			Log.d(TAG, "Delete Convo action");
			deleteConvo();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}


	private void deleteConvo() {
		// TODO Auto-generated method stub
		
	}

	private void newConvoActivity() {
		Intent intent = new Intent(getApplication(), NewConvoActivity.class);
		startActivity(intent);		
	}	
	
	private void isDefaultApp(final Context context){

        if (!isDefault(context)) {
            // App is not default.
            // Show the "not currently set as the default SMS app" interface
        	new AlertDialog.Builder(this)
    		.setTitle("Default Application")
    		.setMessage("This application is not the default SMS application." +
    		"Do you wish to change it to the default SMS application?")
    		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) { 
    				setDefault(context);
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
        Log.d(TAG, Telephony.Sms.getDefaultSmsPackage(this));
	}
	
	private boolean isDefault(Context context){
		if(SmsUtils.hasKitKat())
			return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
		return true;
	}
	
	private void setDefault(Context context){
		if(SmsUtils.hasKitKat()){
			Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
			intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
			context.startActivity(intent);
		}
	}
}
