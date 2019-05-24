package matt.honours.adapter;

import matt.honours.R;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactListCursorAdapter extends CursorAdapter {

	public ContactListCursorAdapter(Context context) {
		super(context, null, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, null);
		
		holder.image = (ImageView) view.findViewById(R.id.contact_list_image);
		holder.fname = (TextView) view.findViewById(R.id.contact_list_name);
		holder.lname = (TextView) view.findViewById(R.id.contact_list_lname);
		holder.phone = (TextView) view.findViewById(R.id.contact_list_phone);
		holder.id = (TextView) view.findViewById(R.id.contact_list_id);
		
		holder.fname.setTag(cursor.getColumnIndexOrThrow("firstName"));
		holder.lname.setTag(cursor.getColumnIndexOrThrow("lastName"));
		holder.phone.setTag(cursor.getColumnIndexOrThrow("phone"));
		holder.id.setTag(cursor.getColumnIndexOrThrow("contactID"));
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = (ViewHolder) view.getTag();
		
		holder.fname.setText(cursor.getString((Integer) holder.fname.getTag()));
		holder.phone.setText(cursor.getString((Integer) holder.phone.getTag()));
		holder.lname.setText(cursor.getString((Integer) holder.lname.getTag()));
		holder.id.setText(cursor.getString((Integer) holder.id.getTag()));
		
	}
	
	static class ViewHolder{
		ImageView image;
		TextView id;
		TextView fname;
		TextView lname;
		TextView phone;
	}

}
