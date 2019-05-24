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

public class ConversationListCursorAdapter extends CursorAdapter{

	public ConversationListCursorAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.convo_list_item, null);
		
		ViewHolder holder = new ViewHolder();
		
		holder.image 	= (ImageView) view.findViewById(R.id.convo_list_image);
		holder.threadId = (TextView) view.findViewById(R.id.convo_list_id);
		holder.time 	= (TextView) view.findViewById(R.id.convo_list_time);
		holder.message 	= (TextView) view.findViewById(R.id.convo_list_msg);
		holder.read 	= (TextView) view.findViewById(R.id.convo_list_unread);
		holder.name 	= (TextView) view.findViewById(R.id.convo_list_name);
		
		holder.threadId.setTag(cursor.getColumnIndexOrThrow(context.getString(R.string.db_threadId)));
		holder.message.setTag(cursor.getColumnIndexOrThrow(context.getString(R.string.db_msg)));
		holder.time.setTag(cursor.getColumnIndexOrThrow(context.getString(R.string.db_time)));
		holder.read.setTag(cursor.getColumnIndexOrThrow(context.getString(R.string.db_read)));
		holder.name.setTag(cursor.getColumnIndexOrThrow(context.getString(R.string.db_fname)));

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		ViewHolder holder = (ViewHolder) view.getTag();
		
		//holder.image.setImageResource(R.drawable.contact);
		holder.message.setText(cursor.getString((Integer) holder.message.getTag()));
		holder.threadId.setText(cursor.getString((Integer) holder.threadId.getTag()));
		holder.read.setText(cursor.getString((Integer) holder.read.getTag()));
		
		Long time = Long.parseLong(cursor.getString((Integer) holder.time.getTag()));
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		holder.time.setText(SmsUtils.getDate(c));
		//String fname = cursor.getString((Integer) holder.message.getTag());
		String name = "";
		//if(fname != null && !fname.equals("")){
		//	String lname = cursor.getString((Integer) holder.message.getTag() + 1);
			//if (lname != null && !lname.equals("")){
				//name = fname + " " + lname;
			//} else {
			//	name = fname;
		//	}
		//} else {
			name =  cursor.getString(cursor.getColumnIndexOrThrow("phone"));
		//}
		
		holder.name.setText(name);
		
	}
	
	
	static class ViewHolder{
		ImageView image;
		TextView threadId;
		TextView message;
		TextView time;
		TextView read;
		TextView name;
	}

}
