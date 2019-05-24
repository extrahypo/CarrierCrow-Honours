package matt.honours.sms.asynctasks;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
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

import org.spongycastle.jce.spec.IEKeySpec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;


public class KeyExchangeAsyncTask extends AsyncTask<AsyncTaskData, Void, Boolean> {

	private static final String NEW_KEY 		= "_k:";
	private static final String ENC_KEY 		= "_e:";
	private static final String AES_KEY 		= "_a:";
	private static final int 	NEW_KEY_LENGTH 	= NEW_KEY.length();
	private static final String TAG 			= "KeyExchangeAsyncTask";
	private DatabaseHelper db;
	private String thread_id;
	private Context context;
	
	@Override
	protected Boolean doInBackground(AsyncTaskData... params) {
		String phone = params[0].params[0];
		String message = params[0].params[1];
		int type = Integer.parseInt(params[0].params[2]);
		if(params[0].params.length == 4){
			thread_id = params[0].params[3];
		} else {
			getThreadId(phone);
		}		
		context = params[0].context;
		db = DatabaseHelper.getInstance(context);
		checkMessageType(phone, message, type);
		return true;
	}

	private void checkMessageType(String phone, String message, int type) {
		Log.d(TAG, "checkMessageType");
		switch (type) {
		case 0:
			sendKeyRequest(phone);
			break;
		case 1:
			receivePublicSendPublicKey(phone, message);
			break;
		case 2:
			receivePublicKeySendKey(phone, message);
			break;
		case 3:
			receiveContactKeySendKey(phone, message);
			break;
		case 4:
			receiveContactKey(phone, message);
			break;
		case 5: 
			sendPendingMessages(phone);
			break;
		case 6:
			sendNewAES(phone);
			break;
		case 7: 
			receiveNewAESSendNew(phone, message);
			break;
		case 9: 
			sendNewMasterKeys(phone);
			break;
		case 10:
			recevieNewMasterKeys(phone, message);
			break;
		}
	}
	

	//------------------------------------------------------------------------------------------
	//Key Sharing Methods
	
	
	private void recevieNewMasterKeys(String phone, String message) {
		db.resetEncryption(thread_id);
		receivePublicSendPublicKey(phone, message);
	}

	private void sendNewMasterKeys(String phone) {
		db.resetEncryption(thread_id);
		sendKeyRequest(phone);		
	}
	
	private void receiveNewAESSendNew(String phone, String message) {
		String[] values = db.getKeyInfoPubPriv(thread_id);
		PrivateKey privateKey = KeyConversion.stringToPrivateKeyECC(values[1]);
		PublicKey publicKey = KeyConversion.stringToPublicKeyECC(values[0]);
		String dev = values[2];
		String enc = values[3];
		IEKeySpec keys = new IEKeySpec(privateKey, publicKey);
		
		String decryptedKey = EncryptDecrypt.decryptWithECC(keys, message, 
				SmsUtils.convertBytes(dev), SmsUtils.convertBytes(enc));
		String userKey = KeyConversion.secretKeyToString(KeyGeneration.getNewKeys());
		ContentValues v = new ContentValues();
		v.put("keyCon", decryptedKey);
		v.put("keyUser", userKey);
		v.put("timeIV", Calendar.getInstance().getTimeInMillis());
		updateKeyState(1, 1, 1, 1, v);
		
		String encrypted = EncryptDecrypt.encryptString(userKey,
				KeyConversion.stringToSecretKey(decryptedKey), message);
		
		SmsSender.sendMsg(phone, NEW_KEY + encrypted, NEW_KEY);
		
	}

	private void sendNewAES(String phone) {
		String[] values = db.getKeyInfoPubPriv(thread_id);
		PrivateKey privateKey = KeyConversion.stringToPrivateKeyECC(values[1]);
		PublicKey publicKey = KeyConversion.stringToPublicKeyECC(values[0]);
		String dev = values[2];
		String enc = values[3];
		IEKeySpec keys = new IEKeySpec(privateKey, publicKey);
		
		String userKey = KeyConversion.secretKeyToString(KeyGeneration.getNewKeys());
		ContentValues v = new ContentValues();
		v.put("keyUser", userKey);		
		updateKeyState(1, 1, 1, 0, v);
		
		String encrypted = EncryptDecrypt.encryptWithECC(keys, userKey, 
				SmsUtils.convertBytes(dev), SmsUtils.convertBytes(enc));
		SmsSender.sendMsg(phone, AES_KEY + encrypted, AES_KEY);
	}

	private void sendPendingMessages(String phone) {
		Log.d(TAG, "sendPendingMessages()");
		ArrayList<SmsMessages> pending = db.getPendingMsgs(thread_id);
		String[] keyInfo = db.getKeyInfoConIV(thread_id);
		SecretKey key = KeyConversion.stringToSecretKey(keyInfo[0]);
		String iv = keyInfo[1];
		for(SmsMessages sms : pending){
			String encrypted = EncryptDecrypt.encryptString(sms.message, key, iv);
			SmsSender.sendMsg(phone, ENC_KEY + encrypted, ENC_KEY);
			iv = encrypted;
		}
		db.updateKey("ivVector", thread_id, iv);
	}


	private void receiveContactKey(String phone, String message) {
		Log.d(TAG, "receiveContactKey()");
		String key = message.substring(NEW_KEY_LENGTH);

		String[] keyValues = db.getKeyInfoUserIV(thread_id);
		String decrypted = EncryptDecrypt.decryptString(key, KeyConversion.stringToSecretKey(keyValues[0]), keyValues[1]);

		ContentValues values = new ContentValues();
		values.put("keyCon", decrypted);
		values.put("timeIV", Calendar.getInstance().getTimeInMillis());
		updateKeyState(1, 1, 1, 1, values);
		
			try {
				SystemClock.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sendPendingMessages(phone);
		
	}

	
	private void receiveContactKeySendKey(String phone, String message) {
		Log.d(TAG, "receiveContactKeySendKey()");
		String[] values = getStringValues(message, NEW_KEY);
		String key = values[0];
		String aesKey = KeyConversion.secretKeyToString(KeyGeneration.getNewKeys());

		String[] keyInfo = db.getKeyInfoPubPriv(thread_id);
		PrivateKey privateKey = KeyConversion.stringToPrivateKeyECC(keyInfo[1]);
		PublicKey publicKey = KeyConversion.stringToPublicKeyECC(keyInfo[0]);
		String dev = keyInfo[2];
		String enc = keyInfo[3];
		IEKeySpec keys = new IEKeySpec(privateKey, publicKey);
		String decrypted = EncryptDecrypt.decryptWithECC(keys, key, SmsUtils.convertBytes(dev),
				SmsUtils.convertBytes(enc));

		Calendar c = Calendar.getInstance();
		ContentValues v = new ContentValues();
		v.put("keyCon", decrypted);
		v.put("keyUser", aesKey);
		v.put("ivVector", values[1]);
		v.put("timeIV", c.getTimeInMillis());
		updateKeyState(1, 1, 1, 1, v);

		SecretKey conKey = KeyConversion.stringToSecretKey(decrypted);
		String encrypted = EncryptDecrypt.encryptString(aesKey, conKey, values[1]);
		
		String msg = NEW_KEY + encrypted;
		SmsSender.sendMsg(phone, msg, NEW_KEY);		
	}

	private void receivePublicKeySendKey(String phone, String message) {
		Log.d(TAG, "receivePublicKeySendKey()");
		String[] values = getStringValues(message, NEW_KEY);
		String aesKey = KeyConversion.secretKeyToString(KeyGeneration.getNewKeys());
		String iv = getBytes();

		Calendar c = Calendar.getInstance();
		ContentValues v = new ContentValues();
		v.put("keyPub", values[0]);
		v.put("keyEnc", values[1]);
		v.put("keyUser", aesKey);
		v.put("ivVector", iv);
		v.put("timeIV", c.getTimeInMillis());
		updateKeyState(1, 1, 1, 0, v);
		
		String[] keyInfo = db.getKeyInfoPriv(thread_id);
		PrivateKey privateKey = KeyConversion.stringToPrivateKeyECC(keyInfo[0]);
		String dev = keyInfo[1];
		PublicKey pkey = KeyConversion.stringToPublicKeyECC(values[0]);
		IEKeySpec keys = new IEKeySpec(privateKey, pkey);
		String encrypted = EncryptDecrypt.encryptWithECC(keys, aesKey, SmsUtils.convertBytes(dev), 
				SmsUtils.convertBytes(values[1]));
		
		String msg = NEW_KEY + encrypted + NEW_KEY + iv; 
		SmsSender.sendMsg(phone, msg, NEW_KEY);
	}

	private void receivePublicSendPublicKey(String phone, String message) {
		Log.d(TAG, "receivePublicSendPublicKey()");
		String[] values = getStringValues(message, NEW_KEY);
		KeyPair pair = KeyGeneration.getECCKeys();
		String privateKey = KeyConversion.privateKeyToString(pair.getPrivate());
		String publicKey = KeyConversion.publicKeyToString(pair.getPublic());
		String bytes = getBytes();
		
		Calendar c = Calendar.getInstance();
		ContentValues v = new ContentValues();
		v.put("keyPub", values[0]);
		v.put("keyDev", values[1]);
		v.put("keyPriv", privateKey);
		v.put("keyEnc", bytes);
		v.put("timeMaster", c.getTimeInMillis());
		updateKeyState(1, 1, 0, 0, v);
		
		String msg = NEW_KEY + publicKey + NEW_KEY + bytes;
		SmsSender.sendMsg(phone, msg, NEW_KEY);
	}

	private void sendKeyRequest(String phone) {
		Log.d(TAG, "sendKeyRequest()");
		KeyPair pair = KeyGeneration.getECCKeys();
		String privateKey = KeyConversion.privateKeyToString(pair.getPrivate());
		String publicKey = KeyConversion.publicKeyToString(pair.getPublic());
		String bytes = getBytes();
		
		Calendar c = Calendar.getInstance();
		ContentValues values = new ContentValues();
		values.put("keyPriv", privateKey);
		values.put("keyDev", bytes);
		values.put("timeMaster", c.getTimeInMillis());
		updateKeyState(1, 0, 0, 0, values);
		
		String msg = NEW_KEY + publicKey + NEW_KEY + bytes;
		SmsSender.sendMsg(phone, msg, NEW_KEY);
	}
	
	//-------------------------------------------------------------------------------------------
	//Utility methods
	
	private void updateKeyState(int i, int j, int k, int l, ContentValues values) {
		values.put("key1", i);
		values.put("key2", j);
		values.put("key3", k);
		values.put("key4", l);
		db.updatekeyState(values, thread_id);
	}

	private String[] getStringValues(String message, String header) {
		String[] values = message.split(header);
		int length = values.length;
		return new String[]{values[length - 2], values[length - 1]};
	}

	private String getBytes() {
		SecureRandom random = new SecureRandom();
		byte[] b = new byte[16];
		random.nextBytes(b);
		return Base64.encodeToString(b, Base64.DEFAULT);
	}
	

	private void getThreadId(String phone) {
		Cursor cursor = db.getThreadIdByPhone(phone);
		if(cursor.moveToFirst()){
			thread_id = cursor.getString(0);
		} else{
			cursor = db.getNewThreadId();
			if(cursor.moveToFirst()){
				thread_id = cursor.getString(0);
			}
		}
		if(thread_id == null){
			thread_id = "1";
		}		
	}
	
}
