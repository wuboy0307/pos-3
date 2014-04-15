package edu.txstate.pos;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

public abstract class POSFieldFragment extends Fragment {

	private static String LOG_TAG = "POSFieldFragment";
	
	protected String mStatusMessage;
	POSTaskParent parent = null;
	boolean mNetworkAvailable;
	protected POSTask<Boolean> mPingTask;
	
	protected Storage getStorage() {
		return ((POSApplication) getActivity().getApplication()).getStorage();
	}
	
	protected Cart getCart() throws StorageException {
		return ((POSApplication) getActivity().getApplication()).getCart();
	}
	
	protected POSApplication getPOSApplication() {
		return ((POSApplication) getActivity().getApplication());
	}
	
	protected void finish() {
		getActivity().finish();
	}
	
	protected boolean ping() {
		return ((POSApplication) getActivity().getApplication()).ping();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		parent = (POSTaskParent) activity;
	}
	
	protected void setPing() {
		if (mPingTask != null) return;
		else {
			mPingTask = new PingTask("PingTask",parent);
			mPingTask.execute((POSModel) null);
		}
	}
	
	abstract void netStatusUpdate();
	
	public class PingTask extends POSTask<Boolean> {
		public PingTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		Boolean backgroundWork(Storage storage, POSModel... args) {
			boolean ret = ((POSApplication) ((POSFragmentActivity) parent).getApplication()).ping(); 
			Log.d(LOG_TAG,"background " + ret);
			return ret;
		}

		@Override
		void postWork(Storage storage, Boolean workResult) {
			mNetworkAvailable = workResult;
			((POSApplication) ((POSFragmentActivity) parent).getApplication()).setConnected(mNetworkAvailable);
			netStatusUpdate();
			mPingTask = null;
		}
	}
	
}
