package matt.honours.model;

import android.app.FragmentManager;
import android.content.Context;

public class AsyncTaskData {

	public String[] params;
	public Context context;
	public FragmentManager manager;
	
	public AsyncTaskData(String[] p, Context c, FragmentManager fm){
		params = p;
		context = c.getApplicationContext();
		manager = fm;
	}
	
}
