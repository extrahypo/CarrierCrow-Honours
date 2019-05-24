package matt.honours.fragments;

import matt.honours.ContactDetailsEditActivity;
import matt.honours.R;
import matt.honours.adapter.ContactListCursorAdapter;
import matt.honours.database.DatabaseProvider;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {

	private static final String TAG = "ContactListFragment";
	
	private ContactListCursorAdapter contact_list_adapter;
	private boolean is_sole_list = false;
	private EditText phone_field = null;
	
	private static final String[]		CONTACTS_PROJECTION	= new String[] { BaseColumns._ID,
		"contactID", "firstName", "lastName", "phone", "email", "address", "notes", "threadID"};

	public ContactListFragment(){}
	
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

		contact_list_adapter = new ContactListCursorAdapter(getActivity());
		getActivity().getLoaderManager().initLoader(0, null, this);
	}
	
	public void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();

		getListView().setAdapter(contact_list_adapter);
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setIsLoading(false);
	}
	
	public void setIsLoading(boolean is_loading) {
		setListShown(!is_loading);
	}
	
	public void setOtherValues(boolean list, EditText text){
		is_sole_list = list;
		phone_field = text;
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//for contact activity
		if(!is_sole_list || phone_field == null){
			TextView contactId = (TextView) v.findViewById(R.id.contact_list_id);		
			String contactIdValue = contactId.getText().toString();
			Log.d("CONTACTID", contactIdValue);
			Intent intent = new Intent(getActivity(), ContactDetailsEditActivity.class);
			intent.putExtra(getString(R.string.db_contactId), contactIdValue);
			startActivity(intent);
		} else {
			TextView phone = (TextView) v.findViewById(R.id.contact_list_phone);
			phone_field.setText(phone.getText());
		}
		
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), DatabaseProvider.CONTACTS_CONTENT_URI, CONTACTS_PROJECTION, "", null, "");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		contact_list_adapter.swapCursor(data);		
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		contact_list_adapter.swapCursor(null);		
		
	}
	
}
