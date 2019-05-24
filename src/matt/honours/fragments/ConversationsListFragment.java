package matt.honours.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import matt.honours.ConversationThreadActivity;
import matt.honours.R;
import matt.honours.adapter.ConversationListAdapter;
import matt.honours.database.DatabaseHelper;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ConversationsListFragment extends ListFragment  {

	private static final String TAG = "ConversationListCursorAdapter";	
	
	private ConversationListAdapter adapter;	
	public ConversationsListFragment(){}
	

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
		ArrayList<HashMap<String, String>> list = db.getConversationList();
	
		if(!list.isEmpty())
			adapter = new ConversationListAdapter(getActivity(), list);
		
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();

		getListView().setAdapter(adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setIsLoading(false);
	}
	
	public void setIsLoading(boolean is_loading) {
		setListShown(!is_loading);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TextView textview = (TextView) v.findViewById(R.id.convo_list_id);
		Intent i = new Intent(getActivity(), ConversationThreadActivity.class);
		i.putExtra(getString(R.string.db_threadId), textview.getText().toString());
		startActivity(i);
	}
	
	public void update(){
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
		ArrayList<HashMap<String, String>> list = db.getConversationList();
		if(adapter != null){			
			adapter.clear();
			if(!list.isEmpty())
				adapter.addAll(list);
			adapter.notifyDataSetChanged();
		} else {			
			if(!list.isEmpty())
				adapter = new ConversationListAdapter(getActivity(), list);
		}
	}
}
