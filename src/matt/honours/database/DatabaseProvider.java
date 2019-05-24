package matt.honours.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseProvider extends ContentProvider{

	private DatabaseHelper databaseHelper;
	
	//STRINGS FOR AUTHORITY AND TABLE URI'S 
	private static final String CONTENT 			= "content://";
	private static final String PACKAGE				= "matt.honours.database.DatabaseProvider";
	private static final String AUTHORITY			= CONTENT + PACKAGE;	
	private static final String CONTACTS 			= "contacts";
	
	//CONTACT STRINGS & URI's
	private static final String CONTACTS_TID 		= CONTACTS + "/tid";
	private static final String CONTACTS_PH 		= CONTACTS + "/ph";
	private static final String CONTACTS_SINGLE		= CONTACTS + "/single";
	
	public static final Uri CONTACTS_CONTENT_URI	= Uri.parse(AUTHORITY + "/" + CONTACTS);
	public static final Uri CONTACT_SGL_URI 		= Uri.parse(AUTHORITY + "/" + CONTACTS_SINGLE);
	public static final Uri CONTACT_PH_URI 			= Uri.parse(AUTHORITY + "/" + CONTACTS_PH);
	public static final Uri CONTACT_TID_URI			= Uri.parse(AUTHORITY + "/" + CONTACTS_TID);

	//CONTACT ID'S FOR TABLE EVENTS
	private static final int 	CONTACTS_LIST 		= 0;
	private static final int 	CONTACTS_THREADID	= 1;
	private static final int 	CONTACTS_PHID		= 2;	
	private static final int 	CONTACTS_ID 		= 3;

	//DATABASE STRING NAMES
	private static String contactID 	= "contactID";
	private static String threadID 		= "threadID";
	private static String phone 		= "phone";

	
	private UriMatcher uriMatcher;
	
	@Override
	public boolean onCreate() {
		setUpUriMatcher();
		databaseHelper = DatabaseHelper.getInstance(getContext());
		return databaseHelper != null;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {	
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		switch(uriMatcher.match(uri)){
		//Get all contacts
		case CONTACTS_LIST:
			return getContactList(db);
		//Get Single Contact info, last is to get contact thread id
		case CONTACTS_ID:
			return getSingleContact(db, selectionArgs);
		case CONTACTS_PHID:
			return getSingleContactbyPhone(db, selectionArgs);
		}		
		return null;
	}

	
	private Cursor getSingleContactbyPhone(SQLiteDatabase db, String[] selectionArgs) {
		String select = "SELECT " + contactID + " AS " + BaseColumns._ID + ", * FROM " + CONTACTS +
				" WHERE " + phone + "='" + selectionArgs[0] + "'";
		Cursor cursor = db.rawQuery(select, null);
		cursor.setNotificationUri(getContext().getContentResolver(), CONTACT_PH_URI);
		return cursor;
	}
	private Cursor getSingleContact(SQLiteDatabase db, String[] selectionArgs) {
		String select = "SELECT " + contactID + " AS " + BaseColumns._ID + ", * FROM " + CONTACTS +
				" WHERE " + contactID + "='" + selectionArgs[0] + "'";
		Cursor cursor = db.rawQuery(select, null);
		cursor.setNotificationUri(getContext().getContentResolver(), CONTACT_SGL_URI);
		return cursor;
	}
	private Cursor getContactList(SQLiteDatabase db) {
		String select = "SELECT " + contactID + " AS " + BaseColumns._ID + ", * FROM " + CONTACTS;
		Cursor cursor = db.rawQuery(select, null);
		cursor.setNotificationUri(getContext().getContentResolver(), CONTACTS_CONTENT_URI);
		return cursor;
	}
	
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d("INSERT", uri.toString());
		switch(uriMatcher.match(uri)){
		case CONTACTS_LIST:
			Log.d("INSERT", "CORRECT");
			insertContact(values);
			break;
		default:
			break;
		}
		return null;
	}

	private void insertContact(ContentValues values) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.insert(CONTACTS, null, values);
		databaseHelper.checkContacts();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch(uriMatcher.match(uri)){
		case CONTACTS_LIST:
			deleteContact(uri, selection, selectionArgs);
			break;
		default:
			break;
		}

		return 0;
	}

	private void deleteContact(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String threadId = getContactThreadId(db, whereArgs);
		db.delete(CONTACTS, where, whereArgs);
		if(!threadID.equals("")){
			String newWhere = threadID + "= ?";
			String[] newWhereArgs = new String[] {threadId};
			db.delete("conversations", newWhere, newWhereArgs);
			db.delete("encryption", newWhere, newWhereArgs);
		}	
	}

	private String getContactThreadId(SQLiteDatabase db, String[] whereArgs) {
		Cursor cursor = db.rawQuery("SELECT " + threadID + " FROM " + CONTACTS + " WHERE " +
				contactID + "='" + whereArgs[0] + "'", null);
		if(cursor.moveToFirst()){
			return cursor.getString(0);
		}
		return "";
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch(uriMatcher.match(uri)){
		//Update whole contact
		case CONTACTS_ID:
			return updateContact(values, selection, selectionArgs);
		//update contact thread id
		case CONTACTS_THREADID:
			return updateThreadID(values, selection, selectionArgs);
		}
		return 0;
	}
	
	private int updateThreadID(ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		return db.update(CONTACTS, values, where, whereArgs);	
		
	}

	private int updateContact(ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		return db.update(CONTACTS, values, where, whereArgs);	
	}
	
	private void setUpUriMatcher() {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);	
		
		//Contacts
		uriMatcher.addURI(PACKAGE, CONTACTS, CONTACTS_LIST);
		uriMatcher.addURI(PACKAGE, CONTACTS_SINGLE, CONTACTS_ID);
		uriMatcher.addURI(PACKAGE, CONTACTS_TID, CONTACTS_THREADID);
		uriMatcher.addURI(PACKAGE, CONTACTS_PH, CONTACTS_PHID);
	}
}
