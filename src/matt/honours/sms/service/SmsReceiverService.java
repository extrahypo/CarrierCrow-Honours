package matt.honours.sms.service;

import matt.honours.R;
import matt.honours.Utils.SmsUtils;
import matt.honours.cryptography.EncryptDecrypt.EncryptDecrypt;
import matt.honours.cryptography.keys.KeyConversion;
import matt.honours.database.DatabaseHelper;
import matt.honours.database.DatabaseProvider;
import matt.honours.model.AsyncTaskData;
import matt.honours.sms.SmsReceiver;
import matt.honours.sms.asynctasks.KeyExchangeAsyncTask;
import matt.honours.sms.asynctasks.NonEncryptedMsgAsyncTask;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony.Sms.Intents;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiverService extends IntentService {

	private static final String TAG 				= "SmsReceiverService";
	private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String ACTION_SMS_DELIVER 	= "android.provider.Telephony.SMS_DELIVER";
    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE 		= "application/vnd.wap.mms-message";
    private static final String NEW_KEY 			= "_k:";
    private static final String ENC_MSG 			= "_e:";
    private static final String AES_KEY 			= "_a:";
    private static final int 	KEY_LENGTH 			= NEW_KEY.length();
    public static final String 	MESSAGE_SENT_ACTION = "com.android.mms.transaction.MESSAGE_SENT";
	HeadlessSmsSendServicedService service 			= null;
	private Context context;
    private boolean serviceRestarted = false;
    private DatabaseHelper db;


	public SmsReceiverService() {
        super(TAG);
    }
	
	@Override
	public void onCreate() {
	    super.onCreate();
	    context = getApplicationContext();
	    db = DatabaseHelper.getInstance(context);
	    
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceRestarted = false;
        if ((flags & START_FLAG_REDELIVERY) !=0) {
            serviceRestarted = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

	@Override
	protected void onHandleIntent(Intent intent) {

        if (intent != null && !serviceRestarted) {
            final String action = intent.getAction();
            final String dataType = intent.getType();
            
            if (ACTION_SMS_DELIVER.equals(action) || ACTION_SMS_RECEIVED.equals(action)) {
                handleSmsReceived(intent);
            } else if (ACTION_MMS_RECEIVED.equals(action) && MMS_DATA_TYPE.equals(dataType)) {
                //handleMmsReceived(intent);
            } else if (MESSAGE_SENT_ACTION.equals(action)) {
                handleSmsSent(intent);
            } else {
                handleMessageReceived(intent);
            }
        }
        SmsReceiver.completeWakefulIntent(intent);            
     }

	private void handleMessageReceived(Intent intent) {
		// TODO Auto-generated method stub
		
	}

	private void handleSmsSent(Intent intent) {
		// TODO Auto-generated method stub
		
	}

	private void handleSmsReceived(Intent intent) {		
		Bundle bundle = intent.getExtras();
        if (bundle != null) {
            SmsMessage[] messages = null;
            if (SmsUtils.hasKitKat()) {
                messages = Intents.getMessagesFromIntent(intent);
            } else {
                messages = SmsUtils.getMessagesFromIntent(intent);
            }
            if (messages != null) {
     
            	String ph = "";
            	String body = "";
            	if(messages.length == 1){
            		ph = messages[0].getOriginatingAddress();
            		body = messages[0].getDisplayMessageBody();
            	} else {            		
            		for(SmsMessage sms : messages){
            			body += sms.getDisplayMessageBody();
            			if(ph.equals("")){
            				ph = sms.getOriginatingAddress();
            			}
            		}
            	}
            	
            	Log.d("SMSMSG", body);
            		if(ph.contains("+64")){
            			ph = "0" + ph.substring(3);
            		}
            		String thread_id = getMessageIds(ph);

            		if(body.length() >= KEY_LENGTH){
            			String subBody = body.substring(0, KEY_LENGTH);

            			boolean encrypt = db.encryptionExists(thread_id);

                		if(encrypt && subBody.equals(NEW_KEY)){
                			int q = db.getKeyState(thread_id);
                			
                			Log.d(TAG, "QS VAULE: " + Integer.toString(q));
                			if(q == -1){
                				nonEncrypted(thread_id, body, ph);
                			} else if(q == 4){
                				q = 8;
                			} else{  
                				q++;
                			}
                			Log.d(TAG, "QS VAULE: " + Integer.toString(q + 1));
                			KeyExchangeAsyncTask task = new KeyExchangeAsyncTask();
                			task.execute(new AsyncTaskData[] { new AsyncTaskData(new String[]{ph, body, Integer.toString(q),
                					thread_id}, this, null)});
                		} else if(subBody.equals(NEW_KEY)){
                			Log.d(TAG, "not right");
                			db.insertEncryption(thread_id);  
                			KeyExchangeAsyncTask task = new KeyExchangeAsyncTask();
                			task.execute(new AsyncTaskData[] { new AsyncTaskData(new String[]{ph, body, "1", 
                					thread_id}, this, null)});
                		} else if(encrypt && subBody.equals(ENC_MSG)){
                			decryptMsg(thread_id, body, ph);
                		} else if(subBody.equals(AES_KEY)){
                			KeyExchangeAsyncTask task = new KeyExchangeAsyncTask();
                			task.execute(new AsyncTaskData[] { new AsyncTaskData(new String[]{ph, body, "7",
                					thread_id}, this, null)});
                		}
                		else {
                			nonEncrypted(thread_id, body, ph);
                		}
            		} else {
            			nonEncrypted(thread_id, body, ph);
            		}
   				}
            }
	}
	

	private String getMessageIds(String phone){
		Cursor tidCursor = db.getThreadIdByPhone(phone);

		Log.d("MSGIDS", Integer.toString(tidCursor.getCount()));
		if(tidCursor.moveToFirst()){
			return tidCursor.getString(0);
		} else {
			String threadid = "1";
			tidCursor = db.getNewThreadId();
			if(tidCursor.moveToFirst()){
				threadid = tidCursor.getString(0); 
			}
			if(threadid == null){
				threadid = "1";
			}
			db.insertEncryption(threadid);
			Cursor contact = db.getContactByPhone(phone);

				if(contact.moveToFirst()){
					String contactid = contact.getString(0);
					ContentValues values = new ContentValues();
					values.put(context.getString(R.string.db_threadId), threadid);
					context.getContentResolver().update(DatabaseProvider.CONTACT_TID_URI, values,
							context.getString(R.string.db_contactId) + "= ?", new String[] {contactid});
				}	
				return threadid;		
		}		
	}
		
	private void nonEncrypted(String thread_id, String body, String ph){
		String msgid = db.getNewMessageId(thread_id);
		NonEncryptedMsgAsyncTask task = new NonEncryptedMsgAsyncTask();
		task.execute(new AsyncTaskData[] {new AsyncTaskData(new String[] {ph, body, thread_id, msgid}, context, 
				null)});
	}	

	private void decryptMsg(String thread_id, String body, String ph) {
		String[] keyInfo = db.getKeyInfoUserIV(thread_id);
		String sub = body.substring(KEY_LENGTH);
		db.updateKey("ivVector", thread_id, sub);
		String msg = EncryptDecrypt.decryptString(sub, KeyConversion.stringToSecretKey(keyInfo[0]), keyInfo[1]);
		nonEncrypted(thread_id, msg, ph);
	}

	
}
