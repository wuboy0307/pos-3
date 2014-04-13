package edu.txstate.pos;

import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.Storage;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public abstract class POSTask extends AsyncTask<POSModel, String, Integer> {

	private static String LOG_TAG = "POSTask (Parent)";
	
	private String name;
	private Storage storage;
	private POSTaskParent parent;
	
	public POSTask(String name, POSTaskParent parent) {
		this.name = name;
		this.parent = parent;
		this.storage = parent.getStorage();
	}
	
	@Override
	protected Integer doInBackground(POSModel... args) {
		Log.e(LOG_TAG,"DO IN BACKGROUND");
		Integer ret = null;
		try {
			ret = backgroundWork(storage,args);
		} catch (Exception e) {
			ret = Integer.valueOf(Activity.RESULT_CANCELED);
		}
		
		return ret;
	}
	
	@Override
	protected void onPostExecute(final Integer success) {
		Log.e(LOG_TAG,"POST EXECUTE");
		parent.setTaskResult(success.intValue());
		parent.finishCallback(name);
		postWork(storage,success);
	}
	
	@Override
	protected void onCancelled() {
		parent.finishCallback(name);
	}
	
	abstract Integer backgroundWork(Storage storage, POSModel... args);
	abstract void postWork(Storage storage, Integer success);

}
