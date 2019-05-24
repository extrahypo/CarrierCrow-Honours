package matt.honours.sms.asynctasks;

import java.util.Calendar;

import matt.honours.Utils.SmsUtils;
import matt.honours.database.DatabaseHelper;
import matt.honours.model.AsyncTaskData;
import matt.honours.sms.SmsSender;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class NewSmsSenderAsyncTask extends AsyncTask<AsyncTaskData, Void, Boolean> {


	private Context context;
	private DatabaseHelper db;
	private FragmentManager manager;
	
	@Override
	protected Boolean doInBackground(AsyncTaskData... params) {
		String phone = params[0].params[0];
		String message = params[0].params[1];
		context = params[0].context;
		db = DatabaseHelper.getInstance(context);
		manager = params[0].manager;
		checkMessageType(phone, message);		
		return true;
	}

	private void checkMessageType(String phone, String message) {
	
		Cursor cursor = db.getContactByPhone(phone);
		Log.d("ASYNCTASK", Integer.toString(cursor.getCount()));
		if(!cursor.moveToFirst()){
			Cursor id_cursor = db.getThreadIdByPhone(phone);
			String id = "";
			if(id_cursor.moveToFirst()){
				id = cursor.getString(0);
			}
			if(id == null || id.equals(""))
				sendNewMessage(phone, message);
			else
				sendMessage(id, phone, message);
		}
		else{
			String threadid = cursor.getString(cursor.getColumnIndex("threadID"));
			
			if(threadid.equals("-1")){				
				threadid = newThreadID();
				updateContactThreadId(threadid, cursor);
			}
			
			sendMessage(threadid, phone, message);
		}		
	}

	private void sendMessage(String id, String phone, String message) {
		SmsSenderAsyncTask task  = new SmsSenderAsyncTask();
		task.execute(new AsyncTaskData[] { new AsyncTaskData(new String[] {phone, message, id},
				context, manager)});
	}

	private void sendNewMessage(String phone, String message) {

		//Get new thread id and time
		String thread_id = newThreadID();
		Calendar now = Calendar.getInstance();
		
		//Set content values
		ContentValues queryValues = new ContentValues();
		queryValues.put("threadID", thread_id);
		queryValues.put("messageID", "1");
		queryValues.put("message", message);
		queryValues.put("time", Long.toString(now.getTimeInMillis()));
		queryValues.put("fromCon", Integer.toString(0));
		queryValues.put("read", Integer.toString(1));
		queryValues.put("phone", phone);

		//Update contact threadid
		Cursor contact = db.getContactByPhone(phone);
		if(contact.moveToFirst()){
			updateContactThreadId(thread_id, contact);
		}
		
		//insert convo and send message
		db.insertConversation(queryValues);
		db.insertEncryption(thread_id);
		SmsSender.sendMsg(phone, message, "");
	}
	
	private String newThreadID(){
		Cursor tid = db.getNewThreadId();
		if(tid.moveToFirst()){
			return Integer.toString(tid.getInt(0) + 1);
		} else {
			return "1";
		}
	}
	
	private void updateContactThreadId(String threadid, Cursor contact){
		ContentValues values = new ContentValues();
		values.put("threadID", threadid);
		db.updateContactThreadId(contact.getString(contact.getColumnIndex("contactID")), values);
		//context.getContentResolver().update(DatabaseProvider.CONTACT_TID_URI, values, 
			//	"contactID = ?", new String[] {contact.getString(contact.getColumnIndex("contactID"))});
	}
	
	@Override
	public void onPostExecute(Boolean done){
		SmsUtils.updateListFragments(manager);		
	}

}
