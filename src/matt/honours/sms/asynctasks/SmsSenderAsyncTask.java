package matt.honours.sms.asynctasks;

import java.util.Calendar;

import javax.crypto.SecretKey;

import matt.honours.Utils.SmsUtils;
import matt.honours.cryptography.EncryptDecrypt.EncryptDecrypt;
import matt.honours.cryptography.keys.KeyConversion;
import matt.honours.cryptography.keys.KeyGeneration;
import matt.honours.database.DatabaseHelper;
import matt.honours.model.AsyncTaskData;
import matt.honours.model.SmsMessages;
import matt.honours.sms.SmsSender;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SmsSenderAsyncTask extends AsyncTask<AsyncTaskData, Void, Boolean> {

	private Context context;
	private DatabaseHelper db;
	private FragmentManager manager;
	
	@Override
	protected Boolean doInBackground(AsyncTaskData... params) {
		String phone = params[0].params[0];
		String message = params[0].params[1];
		String threadid = params[0].params[2];
		context = params[0].context;
		db = DatabaseHelper.getInstance(context);
		manager = params[0].manager;	
		sendMessage(threadid, phone, message);
		//checkTimes(threadid, phone);
		return true;
	}

	private void checkTimes(String threadid, String phone) {
		Long[] times = db.getTimes(threadid);
		if(times[0] != null && times[1] != null){
			Calendar c1 = Calendar.getInstance();
			c1.setTimeInMillis(times[0]);
			Calendar c2 = Calendar.getInstance();
			c2.setTimeInMillis(times[1]);
			boolean aesTime = SmsUtils.compareDateM(c1, Calendar.getInstance());
			boolean publicTime = SmsUtils.compareDateY(c2, Calendar.getInstance());
			int i = 0;
			if(publicTime){
				i = 9;
			} else if(aesTime){
				i = 6;
			}
			if(i != 0){
				KeyExchangeAsyncTask task = new KeyExchangeAsyncTask();
				task.execute(new AsyncTaskData[] { new AsyncTaskData(new String[]{phone, "", Integer.toString(i),
					threadid}, context, manager)});
			}
		}
		
	}

	private void sendMessage(String id, String phone, String message) {

		int a = db.startKeyExchange(id);
		
		Log.d("ASYNCTASK", Integer.toString(a));
		String msgID =  db.getNewMessageId(id);
		Calendar now = Calendar.getInstance();
		ContentValues queryValues = new ContentValues();
		queryValues.put("threadID", id);
		queryValues.put("message", message);
		queryValues.put("messageID", msgID);
		queryValues.put("time", now.getTimeInMillis());
		queryValues.put("fromCon", 0);
		queryValues.put("read", 1);
		queryValues.put("phone", phone);
		db.insertConversation(queryValues);		
		
		if(a == 1){
			db.insertPending(new SmsMessages(id, msgID, message, null, 0, 0, phone));
			KeyExchangeAsyncTask task = new KeyExchangeAsyncTask();
			task.execute(new AsyncTaskData[]{new AsyncTaskData(new String[]{phone, "", "0", id},
					context, null)});
		} else if(a == 0){
			String header = "_e:";	
			String[] s = db.getKeyInfoConIV(id);
			SecretKey k = null;
			if(s[0] == null){
				k = KeyGeneration.getNewKeys(); 
				db.updateKey("keyUser", id, KeyConversion.secretKeyToString(k));
			} else {
				k = KeyConversion.stringToSecretKey(s[0]);
			}
			if(message.length() < 100){
				String e = EncryptDecrypt.encryptString(message, k, s[1]);
				db.updateKey("ivVector", id, e);
				String text = header + e;	
				SmsSender.sendMsg(phone, text, header);
			} else {
				sendMultiMessages(id, phone, message, s[1], k, header);
			}
			
		} else {	
			SmsSender.sendMsg(phone, message, "");
		}
	}
	
	private void sendMultiMessages(String id, String phone, String message, String iv,
			SecretKey k, String header) {
		String toEncrypt = message.substring(0, 99);
		db.updateKey("ivVector", id, iv);
		String e = EncryptDecrypt.encryptString(toEncrypt, k, iv);
		SmsSender.sendMsg(phone, header + e, header);
		if(message.length() > 100){
			sendMultiMessages(id, phone, message.substring(100), e, k, header);
		}
	}

	@Override
	public void onPostExecute(Boolean done){
		SmsUtils.updateListFragments(manager);		
	}

}
