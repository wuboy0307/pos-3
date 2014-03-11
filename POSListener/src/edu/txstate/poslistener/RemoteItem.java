package edu.txstate.poslistener;

import android.os.Parcel;
import android.os.Parcelable;

public class RemoteItem implements Parcelable {


	private String id = null;
	private String description = null;
	private String price = null;
	private String deviceID = null;
	
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
		
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @param deviceID the deviceID to set
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(id);
		parcel.writeString(description);
		parcel.writeString(price);

	}
	
	public void readFromParcel(Parcel src) {

	}

}
