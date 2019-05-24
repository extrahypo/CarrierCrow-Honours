package matt.honours.sms.asynctasks;

import java.util.Calendar;

import matt.honours.R;
import matt.honours.Utils.SmsUtils;
import matt.honours.database.DatabaseHelper;
import matt.honours.model.AsyncTaskData;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class NonEncryptedMsgAsyncTask extends AsyncTask<AsyncTaskData, Void, Boolean>{

	private Context context;
	private DatabaseHelper db;
	private FragmentManager manager;
	
	@Override
	protected Boolean doInBackground(AsyncTaskData... params) {
		String phone = params[0].params[0];
		String msg = params[0].params[1];
		String threadid = params[0].params[2];
		String msgid = params[0].params[3];
		context = params[0].context;
		db = DatabaseHelper.getInstance(context);
		manager = params[0].manager;
		insertMsg(phone, msg, threadid, msgid);
		SmsUtils.notify(phone, msg, context);
		return true;
	}

	private void insertMsg(String phone, String msg, String threadid, String msgid) {
		ContentValues values = new ContentValues();
		values.put(context.getString(R.string.db_threadId), threadid);
		values.put(context.getString(R.string.db_messageId), msgid);
		values.put(context.getString(R.string.db_msg), msg);
		values.put(context.getString(R.string.db_read), 0);
		values.put(context.getString(R.string.db_from), 1);
		values.put(context.getString(R.string.db_time), Calendar.getInstance().getTimeInMillis());
		values.put(context.getString(R.string.db_phone), phone);
		db.insertConversation(values);
	}
	
	@Override
	public void onPostExecute(Boolean done){
		SmsUtils.updateListFragments(manager);		
	}
}
