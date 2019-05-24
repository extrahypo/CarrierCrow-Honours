package matt.honours.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import matt.honours.R;
import matt.honours.Utils.SmsUtils;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationListAdapter extends BaseAdapter{

	private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    
    public ConversationListAdapter(Activity a, ArrayList<HashMap<String, String>> ls){
    	activity = a;
    	data = ls;
    	inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
    }
    
    @Override
	public int getCount() {
		if(data.size()<=0)
            return 1;
        return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void clear() {
		data = new ArrayList<HashMap<String, String>>();
	}

	public void addAll(ArrayList<HashMap<String, String>> msgs) {
		data = msgs;		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		HashMap<String, String> item = data.get(position);
		
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.convo_list_item, null);
			
			holder.image = (ImageView) convertView.findViewById(R.id.convo_list_image);
			holder.id = (TextView) convertView.findViewById(R.id.convo_list_id);
			holder.msg = (TextView) convertView.findViewById(R.id.convo_list_msg);
			holder.name = (TextView) convertView.findViewById(R.id.convo_list_name);
			holder.time = (TextView) convertView.findViewById(R.id.convo_list_time);
			holder.unread = (TextView) convertView.findViewById(R.id.convo_list_unread);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.image.setImageResource(R.drawable.contact);
		holder.id.setText(item.get(activity.getString(R.string.db_threadId)));
		holder.name.setText(item.get(activity.getString(R.string.db_fullname)));
		
		String msg = item.get(activity.getString(R.string.db_msg));
		if(msg != null && msg.length() >= 40){
			msg = msg.substring(0, 40) + "...";
		}
		holder.msg.setText(msg);		
		
		String time = item.get(activity.getString(R.string.db_time));
		if(time != null){
			Calendar c = SmsUtils.getCalendar(time);
			holder.time.setText(SmsUtils.getDate(c));
		}	
		
		String unread = item.get(activity.getString(R.string.db_read));
		if(unread == null || unread.equals("0"))
			holder.unread.setText("");
		else
			holder.unread.setText(unread);
		
		return convertView;
	}

	 public static class ViewHolder{
		 ImageView image;
		 TextView id;
		 TextView name;
		 TextView msg;
		 TextView time;
		 TextView unread;
	 }
}
