package matt.honours.fragments;

import matt.honours.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ContactsDetailsFragment extends Fragment {
	
	private static final String TAG = "ContactDetailsFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.contact_details, container, false);
    }
	
	
}
