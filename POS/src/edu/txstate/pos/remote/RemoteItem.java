package edu.txstate.pos.remote;

import edu.txstate.pos.model.Item;
import android.os.Parcel;
import android.os.Parcelable;

public class RemoteItem implements Parcelable {

	private Item item = null;
	private String mDeviceID = null;
	
	public static final Parcelable.Creator<RemoteItem>
		CREATOR = new Parcelable.Creator<RemoteItem>() {
		
		public RemoteItem createFromParcel(Parcel src) {
			return new RemoteItem(src);
		}
		
		public RemoteItem[] newArray(int size) {
			return new RemoteItem[size];
		}
	};
	
	public RemoteItem() {
		
	}
	
	private RemoteItem(Parcel src) {
		readFromParcel(src);
	}
		
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}
	
	public void readFromParcel(Parcel src) {
		// see if device id is this one.
		String id = src.readString();
		String description = src.readString();
		String price = src.readString();
		mDeviceID = src.readString();
		item = new Item(id,description,price);

	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return mDeviceID;
	}

}
