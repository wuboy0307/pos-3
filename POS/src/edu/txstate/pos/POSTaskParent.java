package edu.txstate.pos;

import edu.txstate.pos.storage.Storage;

interface POSTaskParent {
	void showProgress(final boolean show);
	void finishCallback(String taskName);
	void setTaskResult(int result);
	Storage getStorage();
}
