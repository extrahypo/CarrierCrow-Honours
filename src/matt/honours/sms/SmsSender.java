package matt.honours.sms;

import java.util.ArrayList;

import android.telephony.SmsManager;
import android.util.Log;

public class SmsSender {

	public static void sendMsg(String phone, String message, String header){
		try{
			SmsManager manager = SmsManager.getDefault();
			if(message.length() >= 160){
				ArrayList<String> parts = new ArrayList<String>();
				Log.d("SmsSender", "multi");
				if(header.equals("")){
					parts = getMsgParts(message, header);
				} else {
					parts = getMsgParts(message.substring(8), header);
				}
				for(String s : parts){
					String msg =  header + s;
					Log.d("SmsSender", msg);
					manager.sendTextMessage(phone, null, msg, null, null);
				}
			} else {
				manager.sendTextMessage(phone, null, message, null, null);
			}			
		} catch (Exception e){
			Log.e("Send Error", e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static ArrayList<String> getMsgParts(String msg, String header) {
		if(msg.length() >= 150){
			int mid = msg.length() / 2;
			String a = msg.substring(0, mid);
			ArrayList<String> parts = getMsgParts(msg.substring(mid), header);
			ArrayList<String> all = new ArrayList<String>();
			all.add(a);
			all.addAll(parts);
			return all;
		}
		ArrayList<String> all = new ArrayList<String>();
		all.add(msg);
		return all;
	}
}
