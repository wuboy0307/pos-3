package edu.txstate.pos.remote;

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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return deviceID;
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
	/**
	 * Break the object data down into primitives.  Called by the
	 * Android OS to serialize the data.
	 * 
	 * @param Parcel
	 * @param int - flag to indicate if this is a return object, too
	 */
	public void writeToParcel(Parcel parcel, int arg1) {
		// marshall the data
		parcel.writeString(id);
		parcel.writeString(description);
		parcel.writeString(price);
		parcel.writeString(deviceID);
	}
	
	/**
	 * Don't need this on the client side.
	 * @param src
	 */
	public void readFromParcel(Parcel src) {

	}

}
