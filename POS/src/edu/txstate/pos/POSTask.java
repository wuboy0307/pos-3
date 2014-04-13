package edu.txstate.pos;

import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.Storage;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public abstract class POSTask<X> extends AsyncTask<POSModel, String, X> {

	private static String LOG_TAG = "POSTask (Parent)";
	
	private String name;
	private Storage storage;
	private POSTaskParent parent;
	private boolean showProgress;
	
	public POSTask(String name, POSTaskParent parent) {
		this.name = name;
		this.parent = parent;
		this.storage = parent.getStorage();
	}
	
	@Override
	protected X doInBackground(POSModel... args) {
		Log.e(LOG_TAG,"DO IN BACKGROUND");
		X ret = null;
		try {
			ret = backgroundWork(storage,args);
		} catch (Exception e) {
			ret = null;
		}
		
		return ret;
	}
	
	@Override
	protected void onPostExecute(X x) {
		Log.e(LOG_TAG,"POST EXECUTE");
		if (x == null) parent.setTaskResult(Activity.RESULT_CANCELED);
		else parent.setTaskResult(Activity.RESULT_OK);
		parent.finishCallback(name);
		postWork(storage,x);
	}
	
	@Override
	protected void onCancelled() {
		parent.finishCallback(name);
	}
	
	abstract X backgroundWork(Storage storage, POSModel... args);
	abstract void postWork(Storage storage, X workResult);

}
