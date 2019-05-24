package matt.honours.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import matt.honours.Utils.SmsUtils;
import matt.honours.Utils.SmsUtils.CustomComparator;
import matt.honours.model.SmsMessages;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static final String DATABASE_NAME 	= "honours.db";
	private static final int 	SCHEMA_VERSION 	= 1;
	
	private static String contactDB 	= "contacts";
	private static String convoDB 		= "conversations";
	private static String pendingDB		= "conversationsPending";
	private static String encryptDB 	= "encryption";
	
	private static String contactID 	= "contactID";
	private static String threadID 		= "threadID";
	private static String messageID 	= "messageID";
	private static String firstName 	= "firstName";
	private static String lastName 		= "lastName";
	private static String phone 		= "phone";
	private static String email 		= "email";
	private static String address 		= "address";
	private static String notes 		= "notes";
	private static String message 		= "message";
	private static String time 			= "time";
	private static String fromCon 		= "fromCon";
	private static String read 			= "read";
	private static String fullName 		= "fullName";
	private static String encrypt 		= "encrypt";
	private static String keyCon 		= "keyCon";
	private static String keyUser 		= "keyUser";
	private static String keyPriv 		= "keyPriv";
	private static String keyPub 		= "keyPub";
	private static String key1 			= "key1";
	private static String key2 			= "key2";
	private static String key3 			= "key3";
	private static String key4 			= "key4";
	private static String keyDev		= "keyDev";
	private static String keyEnc		= "keyEnc";
	private static String ivVector		= "ivVector";
	private static String timeMaster	= "timeMaster";
	private static String timeIV		= "timeIV";

	
	private static DatabaseHelper singleton = null;
	
	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}

	synchronized public static DatabaseHelper getInstance(Context context){
		if(singleton == null){
			singleton = new DatabaseHelper(context.getApplicationContext());
		}
		return singleton;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			db.beginTransaction();
			String contactquery = "CREATE TABLE " + contactDB + "(" + contactID + " INTEGER PRIMARY KEY AUTOINCREMENT," + firstName + 
					" TEXT, " + lastName + " TEXT, " + phone + " TEXT, " + email + " TEXT, " + address + 
					" TEXT, " + notes + " TEXT, " + threadID + " INTEGER)";
			
			String convoquery = "CREATE TABLE " + convoDB + "(" + threadID + " INTEGER," + 
			messageID + " INTEGER," + time + " INTEGER, " + message + " TEXT, " + fromCon + " INTEGER, " +
					read + " INTEGER," + phone + " TEXT, PRIMARY KEY(threadID, messageID))";

			String pendingquery = "CREATE TABLE " + pendingDB + "(" + threadID + " INTEGER," +
					messageID + " INTEGER," + message + " TEXT, " + phone + " TEXT, PRIMARY KEY(threadID, messageID))";
			
			String encryptquery = "CREATE TABLE " + encryptDB + "(" + threadID + " INTEGER PRIMARY KEY, " + 
					encrypt + " INTEGER, " + keyCon + " TEXT, " + keyUser + " TEXT, " + keyPriv + " TEXT, " + keyPub + 
					" TEXT, " + key1 + " INTEGER, " + key2 + " INTEGER, "+ key3 + " INTEGER, " +
					key4 + " INTEGER, " + keyDev + " TEXT, " + keyEnc + " TEXT, " + ivVector + " TEXT, " + timeMaster + 
					" INTEGER, " + timeIV + " INTEGER)";
			
			db.execSQL(contactquery);
			db.execSQL(convoquery);
			db.execSQL(pendingquery);
			db.execSQL(encryptquery);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		try{
			db.beginTransaction();
			String query = "DROP TABLE IF EXISTS " + contactDB;
			db.execSQL(query);
			query= "DROP TABLE IF EXISTS " + convoDB;
			db.execSQL(query);
			query= "DROP TABLE IF EXISTS " + encryptDB;
			db.execSQL(query);
			query= "DROP TABLE IF EXISTS " + pendingDB;
			db.execSQL(query);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		onCreate(db);	
	}
	
	public Cursor getContactByPhone(String ph){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT " + contactID + " AS " + BaseColumns._ID + ", * FROM " + contactDB + " WHERE " + 
				phone + "='" + ph + "'";
		return db.rawQuery(sql, null);
	}
	
	public Cursor getContactByThreadId(String id){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT " + contactID + " AS " + BaseColumns._ID + ", * FROM " + contactDB + " WHERE " + 
				threadID + "='" + id + "'";
		return db.rawQuery(sql, null);
	}
	
	public Cursor getThreadIdByPhone(String ph){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT " + threadID + " AS " + BaseColumns._ID + " FROM " + contactDB + " WHERE " + 
				phone + "='" + ph + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return cursor;
		}
		sql = "SELECT " + threadID + " AS " + BaseColumns._ID + " FROM " + convoDB + " WHERE " + 
				phone + "='" + ph + "'";
		return db.rawQuery(sql, null);
	}
	
	public Cursor getPhoneByThreadID(String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT " + phone + " AS " + BaseColumns._ID + " FROM " + contactDB + " WHERE " + 
				threadID + "='" + id + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return cursor;
		}
		sql = "SELECT " + phone + " AS " + BaseColumns._ID + " FROM " + convoDB + " WHERE " + 
				threadID + "='" + id + "'";
		return db.rawQuery(sql, null);
	}
	
	public Cursor getNewThreadId(){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT max(" + threadID + ") AS " + BaseColumns._ID + " FROM " + convoDB;
		return db.rawQuery(sql, null);
	}
	
	public String getNewMessageId(String id){
		Cursor cursor = this.getReadableDatabase().rawQuery("SELECT max(" + messageID + ") FROM " +
				convoDB + " WHERE " + threadID + " ='" + id + "'", null);
		if(cursor.moveToFirst()){
			return Integer.toString(cursor.getInt(0) + 1);
		}
		return "1";
	}
	
	public boolean encryptionExists(String id){
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "SELECT " + threadID + " AS " + BaseColumns._ID + " FROM " + encryptDB;
		return db.rawQuery(sql, null).moveToFirst();
	}
	
	public void insertEncryption(String id) {
		ContentValues values = new ContentValues();
		values.put(threadID, id);
		values.put(encrypt, 0);
		values.put(key1, 0);
		values.put(key2, 0);
		values.put(key3, 0);
		values.put(key4, 0);
		values.put(timeMaster, 0);
		values.put(timeIV, 0);
		Calendar c = Calendar.getInstance();
		values.put(timeMaster, c.getTimeInMillis());
		values.put(timeIV, c.getTimeInMillis());
		this.getWritableDatabase().insert(encryptDB, null, values);
	}
	

	public void resetEncryption(String id) {
		ContentValues values = new ContentValues();
		values.put(key1, 0);
		values.put(key2, 0);
		values.put(key3, 0);
		values.put(key4, 0);
		values.put(keyUser, "");
		values.put(keyPub, "");
		values.put(keyPriv, "");
		values.put(keyCon, "");
		values.put(keyDev, "");
		values.put(keyEnc, "");		
		Calendar c = Calendar.getInstance();
		values.put(timeMaster, c.getTimeInMillis());
		values.put(timeIV, c.getTimeInMillis());
		this.getWritableDatabase().update(encryptDB, values, threadID + " = ?", new String[] {id});
	}

	public void insertConversation(ContentValues values){
		this.getWritableDatabase().insert(convoDB, null, values);		
	}

	public int getEncryption(String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT " + encrypt + " AS " + BaseColumns._ID + " FROM " + encryptDB 
				+ " WHERE " + threadID + "='" + id + "'", null);
		if(cursor.moveToFirst()){
			return cursor.getInt(0);
		}
		return 0;
	}

	public void setEncryption(String id, int e) {
		ContentValues values = new ContentValues();
		values.put(encrypt, e);
		this.getWritableDatabase().update(encryptDB, values, threadID + " = ?", new String[] {id});		
	}

	public void deleteConvo(String id) {
		this.getWritableDatabase().delete(convoDB, threadID + " = ?", new String[]{id});	
		this.getWritableDatabase().delete(encryptDB, threadID + " = ?", new String[]{id});	
	}
	
	public void deleteEncryption(String id){
		this.getWritableDatabase().delete(encryptDB, threadID + " = ?", new String[]{id});
	}
	
	public ArrayList<HashMap<String, String>> getConversationList(){
		ArrayList<HashMap<String, String>> convoList = new ArrayList<HashMap<String, String>>();
		String sqlConvo = "SELECT " + threadID + " AS " + BaseColumns._ID + ", " + message + 
				", " + time + ", " + phone + ", max(" + messageID + ") FROM " + convoDB;
		String sqlContact = "SELECT " + threadID + " AS " + BaseColumns._ID + ", " + firstName + 
				", " + lastName + " FROM " + contactDB + " WHERE " + threadID + " IS NOT NULL";
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<String> ids = new ArrayList<String>();
		
		//DEAL WITH THE CONTACTS
		Cursor contacts = db.rawQuery(sqlContact, null);
		if(contacts.moveToFirst()){
			do{
				HashMap<String, String> convoMap = new HashMap<String, String>();
				Cursor convo = db.rawQuery(sqlConvo + " WHERE " + threadID + "='" + 
						contacts.getString(0) + "'", null);
				if(convo.moveToFirst()){
					convoMap.put(threadID, convo.getString(0));
					convoMap.put(message, convo.getString(1));
					convoMap.put(time, convo.getString(2));
					convoMap.put(fullName, getFullName(contacts.getString(2), contacts.getString(3),
							convo.getString(3)));
					convoMap.put(read, getUnread(contacts.getString(0), db));
					convoList.add(convoMap);
					ids.add(contacts.getString(0));
				}
			} while(contacts.moveToNext());
		}
		
		//DEAL WITH NON CONTACTS
		convoList.addAll(getNonContacts(ids, sqlConvo, db));
		
		//SORT LIST BY TIME
		Collections.sort(convoList, new CustomComparator());
		return convoList;
	}
	
	private ArrayList<HashMap<String, String>> getNonContacts(ArrayList<String> ids, String sqlConvo,
			SQLiteDatabase db) {
		ArrayList<HashMap<String, String>> convoList = new ArrayList<HashMap<String, String>>();
		String sql = sqlConvo;
		if(!ids.isEmpty()){
			String id = ids.toString();
			id.replace("[", "(");
			id.replace("]", ")");
			sql += " WHERE " + threadID + " NOT IN " + id;
		}
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			do{
				HashMap<String, String> convoMap = new HashMap<String, String>();
				if(cursor.getString(0) != null){
				convoMap.put(threadID, cursor.getString(0));
				convoMap.put(message, cursor.getString(1));
				convoMap.put(time, cursor.getString(2));
				convoMap.put(fullName, cursor.getString(3));
				convoMap.put(read, getUnread(cursor.getString(0), db));				
				Log.d("Data", convoMap.toString());
				convoList.add(convoMap);
				}
			} while(cursor.moveToNext());			
		}
		return convoList;
	}

	private String getFullName(String first, String last, String ph) {
		if(!first.equals("") && !last.equals(""))
			return first + " " + last;
		else if(!first.equals(""))
			return first;
		else
			return ph;
	}

	private String getUnread(String id, SQLiteDatabase db){
		String sql = "SELECT count(" + read + ")" + " FROM " + convoDB + " WHERE " + threadID + "='" +
				id + "' AND " + read + "='0'";
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		}
		return "0";
	}
	
	public ArrayList<SmsMessages> getConversation(String id){
		ArrayList<SmsMessages> convoList = new ArrayList<SmsMessages>();
		String sql = "SELECT * FROM " + convoDB + " WHERE " + threadID + "='" + id + 
				"' ORDER BY " + messageID;		
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			do{
				convoList.add(new SmsMessages(cursor.getString(0), cursor.getString(1), cursor.getString(3),
						SmsUtils.getCalendar(cursor.getString(2)), cursor.getInt(4), cursor.getInt(5), cursor.getString(6)));
			} while(cursor.moveToNext());
		}
		return convoList;
	}

	public void checkContacts() {
		String sql = "SELECT " + contactID + ", " + phone + " FROM " + contactDB + " WHERE " + 
				threadID + " IS NULL";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			do{
				Cursor ph = db.rawQuery("SELECT " + threadID + " FROM " + convoDB + " WHERE " + phone +
						"='" + cursor.getString(0) + "'", null);						
				if(ph.moveToFirst()){
					if(ph.getString(0) != null){
						ContentValues values = new ContentValues();
						values.put(threadID, ph.getString(0));
						db.update(contactDB, values, contactID + " = ?", new String[] {cursor.getString(0)});
					}
				}
			} while(cursor.moveToNext());
		}
		
	}
	
	public int updateContactThreadId(String cid, ContentValues values){
		return this.getWritableDatabase().update(contactDB, values, contactID + " = ?", new String[] {cid});
	}
	
	public String getKey(String type, String id){
		String sql = "SELECT " + type + " FROM " + encryptDB + " WHERE " + threadID + "='" + id + "'";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		}
		return null;
	}

	public int updateKey(String field, String id, String key) {
		ContentValues values = new ContentValues();
		values.put(field, key);
		return this.getWritableDatabase().update(encryptDB, values, threadID + " = ?", new String[] {id});
	}
	
	public int updateKeys(String id, ContentValues values){
		return this.getWritableDatabase().update(encryptDB, values, threadID + " = ?", new String[] {id});
	}
	
	public int getKeyState(String id){
		String sql = "SELECT " + encrypt + " , " + key1 + " , " + key2 + " , " + key3 + ", " + key4 + " FROM " +
				encryptDB + " WHERE " + threadID + "='" + id + "'";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) == 0)
				return -1;
			int keys = cursor.getInt(1) + cursor.getInt(2) + cursor.getInt(3) + cursor.getInt(4);
			return keys;
		}
		return -1;		
 	}
	
	public String[] getKeyInfoPubPriv(String id) {
		String sql = "SELECT " + keyPub + ", " + keyPriv + ", " + keyDev + ", " + keyEnc + " FROM " +
				encryptDB + " WHERE " + threadID + "='"  + id + "'";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2),
					cursor.getString(3)};
		}
		return null;
	}
	
	public String[] getKeyInfoPriv(String id){
		String sql = "SELECT " + keyPriv + ", " + keyDev + " FROM " +
				encryptDB + " WHERE " + threadID + "='"  + id + "'";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return new String[] {cursor.getString(0), cursor.getString(1)};
		}
		return null;
	}
	
	public String[] getKeyInfoUserIV(String id) {
		String sql = "SELECT " + keyUser + ", " + ivVector + " FROM " +
				encryptDB + " WHERE " + threadID + "='"  + id + "'";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return new String[] {cursor.getString(0), cursor.getString(1)};
		}
		return null;
	}
	

	public String[] getKeyInfoConIV(String id) {
		String sql = "SELECT " + keyCon + ", " + ivVector + " FROM " +
				encryptDB + " WHERE " + threadID + "='"  + id + "'";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			return new String[] {cursor.getString(0), cursor.getString(1)};
		}
		return null;
	}

	
	public int startKeyExchange(String id) {
		String sql = "SELECT " + encrypt + " , " + key1 + " FROM " +
				encryptDB + " WHERE " + threadID + "='" + id + "'";
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) == 1){
				if(cursor.getInt(1) == 0)
					return 1;
				return 0;
			}
		}
		return -1;
	}
	
	public int updatekeyState(ContentValues values, String id){
		return this.getWritableDatabase().update(encryptDB, values, threadID + " = ?", new String[] {id});
	}
	
	public int updateByteArrays(String id, String field, String value){
		ContentValues values = new ContentValues();
		values.put(field, value);
		return this.getWritableDatabase().update(encryptDB, values, threadID + " = ?", new String[]{id});
	}

	public ArrayList<SmsMessages> getPendingMsgs(String id) {
		ArrayList<SmsMessages> msgs = new ArrayList<SmsMessages>();
		String sql = "SELECT * FROM " + pendingDB + " WHERE " + threadID + "='" + id +
				"' ORDER BY " + messageID;
		Cursor cursor = this.getReadableDatabase().rawQuery(sql, null);
		if(cursor.moveToFirst()){
			do{
				SmsMessages sms = new SmsMessages(cursor.getString(0), null, cursor.getString(2), null, 
						0,0, cursor.getString(3));
				msgs.add(sms);
			} while(cursor.moveToNext());
		}
		return msgs;
	}
	
	public void insertPending(SmsMessages sms) {
		ContentValues values = new ContentValues();
		values.put(threadID, sms.thread_id);
		values.put(messageID, sms.message_id);
		values.put(message, sms.message);
		values.put(phone, sms.phone);
		this.getWritableDatabase().insert(pendingDB, null, values);		
	}

	public void setThreadRead(String id) {
		ContentValues values = new ContentValues();
		values.put(read, 1);
		this.getWritableDatabase().update(convoDB, values, threadID + " = ?", new String[]{id});
		
	}

	public Long[] getTimes(String id) {
		// TODO Auto-generated method stub
		return null;
	}



}
