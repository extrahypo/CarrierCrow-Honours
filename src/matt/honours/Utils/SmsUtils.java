package matt.honours.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;

import matt.honours.ConversationsActivity;
import matt.honours.R;
import matt.honours.database.DatabaseHelper;
import matt.honours.fragments.ConversationsListFragment;
import matt.honours.fragments.ThreadListFragment;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Base64;

@TargetApi(VERSION_CODES.KITKAT)
public class SmsUtils {

	public static boolean hasKitKat(){
		 return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
	}

	public static SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        if (messages == null) {
            return null;
        }
        if (messages.length == 0) {
            return null;
        }

        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
	}
	
	public static Calendar getCalendar(String time){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(Long.parseLong(time));
		return c;
	}
	
	public static String getDate(Calendar c) {
		String past = getDateDM(c);
		if(past.equals(getDateDM(Calendar.getInstance()))){
			return getDateHM(c);
		}
		return past;
	}
	
	public static boolean compareDateY(Calendar first, Calendar second){
		return getDateY(first) == getDateY(second);
	}
	
	public static boolean compareDateM(Calendar first, Calendar second){
		first.add(Calendar.MONTH, 6);
		return first.getTimeInMillis() <= second.getTimeInMillis();
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String getDateHM(Calendar c){
		return new SimpleDateFormat("HH:mm").format(c.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String getDateDM(Calendar c){
		return new SimpleDateFormat("d MMM").format(c.getTime());
	}
	
	@SuppressLint("SimpleDateFormat")
	private static int getDateY(Calendar c){
		return Integer.parseInt(new SimpleDateFormat("YYYY").format(c.getTime()));
	}
	
	@SuppressLint("SimpleDateFormat")
	private static String getDateM(Calendar c){
		return new SimpleDateFormat("MMM").format(c.getTime());
	}
		
	public static void notify(String phone, String msg, Context context){
		String text = "";
		DatabaseHelper db = DatabaseHelper.getInstance(context);
		Cursor contact = db.getContactByPhone(phone);
		if(contact.moveToFirst()){
			String last = contact.getString(contact.getColumnIndex("lastName"));
			text += contact.getString(contact.getColumnIndex("firstName"));
			if(last != null &&  !last.equals("")){
				text += " " + last + ": ";
			}
		} else {
			text = phone + ": ";
		}

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.blue_evenlope)
		        .setContentTitle(text)
		        .setContentText(msg)
		        .setAutoCancel(true)
		        .setDefaults(Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, ConversationsActivity.class);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ConversationsActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
		
	}
	
	public static class CustomComparator implements Comparator<HashMap<String,String>> {

		@Override
		public int compare(HashMap<String, String> lhs,	HashMap<String, String> rhs) {
			Long t1 = Long.parseLong(lhs.get("time"));
			Long t2 =  Long.parseLong(rhs.get("time"));
			return t2.compareTo(t1);
		}

	 }
	
	public static void updateListFragments(FragmentManager manager){
		if(manager != null){
			ThreadListFragment tmp = (ThreadListFragment) manager.findFragmentByTag("ThreadListFragment");
			if(tmp != null){
				if(tmp.isVisible() && tmp.isResumed() && !tmp.isRemoving()){
					tmp.update();					
				}
				return;
			}
			
			ConversationsListFragment tmp2 = (ConversationsListFragment) 
					manager.findFragmentByTag("ConversationListFragment");
			if(tmp2 != null){
				if(tmp2.isVisible() && tmp2.isResumed() && !tmp2.isRemoving()){
					tmp2.update();
				}
				return;
			}
		}
	}
	
	public static byte[] convertBytes(String text){
		return Base64.decode(text, Base64.DEFAULT);
	}
}
