package matt.honours.adapter;

import java.util.Calendar;

import matt.honours.R;
import matt.honours.Utils.SmsUtils;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThreadListCursorAdapter extends CursorAdapter {

	public ThreadListCursorAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		ViewHolder holder = new ViewHolder();
		View view = null;
		if(cursor.getInt(4) == 1){
			view = LayoutInflater.from(context).inflate(R.layout.thread_list_item, null);
		} else {
			view = LayoutInflater.from(context).inflate(R.layout.thread_list_item_user, null);
		}
		
		holder.image 		= (ImageView) 	view.findViewById(R.id.thread_image);
		holder.messageId	= (TextView) 	view.findViewById(R.id.thread_msg_id);
		holder.msg 			= (TextView) 	view.findViewById(R.id.thread_msg);
		holder.time 		= (TextView)	view.findViewById(R.id.thread_time);
		
		holder.messageId.setTag(cursor.getColumnIndexOrThrow("messageID"));
		holder.msg.setTag(cursor.getColumnIndexOrThrow("message"));
		holder.time.setTag(cursor.getColumnIndexOrThrow("time"));

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		
		holder.image.setImageResource(R.drawable.contact);
		holder.messageId.setText(cursor.getString((Integer) holder.messageId.getTag()));
		holder.msg.setText(cursor.getString((Integer) holder.msg.getTag()));
		Long time = Long.parseLong(cursor.getString((Integer) holder.time.getTag()));
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		holder.msg.setText(SmsUtils.getDate(c));
	}
	
	static class ViewHolder{
		ImageView image;
		TextView messageId;
		TextView msg;
		TextView time;
	}

}
