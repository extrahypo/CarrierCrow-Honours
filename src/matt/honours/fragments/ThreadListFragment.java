package matt.honours.fragments;

import java.util.ArrayList;

import matt.honours.ConversationThreadActivity;
import matt.honours.adapter.ThreadListAdapter;
import matt.honours.database.DatabaseHelper;
import matt.honours.model.SmsMessages;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class ThreadListFragment extends ListFragment{

	
	private static final String TAG = "ThreadListFragment";
	private ThreadListAdapter adapter;
	private String threadid = "";
		
	public ThreadListFragment(){}
	
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "onAttach()");
		super.onAttach(activity);		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.d(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
		ConversationThreadActivity a = (ConversationThreadActivity) getActivity();
		threadid = a.thread_id;
		ArrayList<SmsMessages> list = db.getConversation(threadid);
		for(SmsMessages h: list){
			Log.d(TAG, h.toString());
		}
		Log.d(TAG + "size", Integer.toString(list.size()));
		if(!list.isEmpty())
			adapter = new ThreadListAdapter(getActivity(), list);
		
	}
	
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();

		getListView().setAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
		setListShown(true);
	}
	
	public void update(){
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
		adapter.clear();
		adapter.addAll(db.getConversation(threadid));
		adapter.notifyDataSetChanged();
	}

}
