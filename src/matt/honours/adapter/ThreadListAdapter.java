package matt.honours.adapter;

import java.util.ArrayList;

import matt.honours.Utils.SmsUtils;
import matt.honours.model.SmsMessages;
import matt.honours.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ThreadListAdapter extends BaseAdapter{

	private Activity activity;
    private ArrayList<SmsMessages> data;
    private static LayoutInflater inflater=null;
    int i=0;
    
    public ThreadListAdapter(Activity a, ArrayList<SmsMessages> ls) {
        
        /********** Take passed values **********/
         activity = a;
         data=ls;
      
         /***********  Layout inflator to call external xml layout () ***********/
          inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);      
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
	
	@Override
	public int getViewTypeCount() {
	   return 2; 
	}
	
	@Override
	public int getItemViewType(int position) {
	    return (data.get(position).from == 1) ? 1 : 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		SmsMessages message = data.get(position);
		
		if(convertView == null){
			holder = new ViewHolder();
			if(message.from == 1){
				convertView = inflater.inflate(R.layout.thread_list_item_user, null);
			} else {
				convertView = inflater.inflate(R.layout.thread_list_item, null);
			}
			
			holder.msg = (TextView) convertView.findViewById(R.id.thread_msg);	
			holder.msgID = (TextView) convertView.findViewById(R.id.thread_msg_id);	
			holder.time = (TextView) convertView.findViewById(R.id.thread_time);	
			holder.image = (ImageView) convertView.findViewById(R.id.thread_image);	
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.msg.setText(message.message);
		holder.msgID.setText(message.message_id);
		holder.time.setText(SmsUtils.getDate(message.time));
		
		return convertView;
	}
	
	/********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
         
        public TextView msgID;
        public TextView msg;
        public TextView time;
        public ImageView image;
 
    }

	public void clear() {
		data = new ArrayList<SmsMessages>();
	}

	public void addAll(ArrayList<SmsMessages> msgs) {
		data = msgs;		
	}
	
}
