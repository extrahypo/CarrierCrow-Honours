package matt.honours.model;

import java.util.Calendar;

public class SmsMessages {

	public String thread_id;
	public String message_id;
	public String message;
	public Calendar time;
	public int from;
	public int read;
	public String phone;
	
	public SmsMessages(String tid, String mid, String msg, Calendar t, int f, int r, String ph){
		thread_id = tid;
		message_id = mid;
		message = msg;
		time = t;
		from = f;
		read = r;
		phone = ph;
		
	}
	
	@Override
	public String toString(){
		return "ThreadID=" + thread_id + ", MessageID=" + message_id + ", message=" + message;
	}
}
