package edu.txstate.pos;

import edu.txstate.pos.model.Cart;
import edu.txstate.pos.model.CartItem;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.storage.NoItemFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCartItemActivity extends POSActivity {

	private static String LOG_TAG = "AddCartItemActivity";
	
	private static Integer ITEM_NOT_FOUND = Integer.valueOf(-2);
	
	private Button mScanButton = null;
	private Button mAddButton = null;
	private EditText mUPC = null;
	private EditText mDescription = null;
	private EditText mPrice = null;
	private EditText mQuantity = null;
	private String mStatusMessage = null;
	
	private String mCurrentUPC = null;
	private long mUPCTime = 0;
	
	private boolean addItem = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mScanButton = (Button) findViewById(R.id.cart_scan_button);
		mAddButton = (Button) findViewById(R.id.cart_add_button);
		mUPC = (EditText) findViewById(R.id.cart_item_id);
		mDescription = (EditText) findViewById(R.id.cart_item_description);
		mPrice = (EditText) findViewById(R.id.cart_item_price);
		mQuantity = (EditText) findViewById(R.id.cart_item_quantity);
		
		mUPC.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				Log.d(LOG_TAG,"On" + c);
				
				//checkInventory();
				//setButtons();
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// do nothing
			}
			
			public void afterTextChanged(Editable c) {
				// do nothing
				Log.d(LOG_TAG,"After" + c);
				mCurrentUPC = c.toString();
				mUPCTime = System.currentTimeMillis();
				checkInventory();
				setButtons();
			}
		});
		
		mDescription.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				setButtons();
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// do nothing
			}
			
			public void afterTextChanged(Editable c) {
				// do nothing
			}
		});
		
		mPrice.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				setButtons();
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// do nothing
			}
			
			public void afterTextChanged(Editable c) {
				// do nothing
			}
		});
		
		mQuantity.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				setButtons();
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// do nothing
			}
			
			public void afterTextChanged(Editable c) {
				// do nothing
			}
		});
		
		mAddButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						addCartItem();
					}
				});
		
		mScanButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
					}
				});
		
		setButtons();
	}
	
	boolean allFieldsFilledIn() {
		boolean ret = false;
		String upc = mUPC.getText().toString();
		String desc = mDescription.getText().toString();
		String price = mPrice.getText().toString();
		String qty = mPrice.getText().toString();
		if (upc != null && price != null 
			&& qty != null && desc != null
			&& upc.trim().length() > 0
			&& desc.trim().length() > 0
			&& price.trim().length() > 0
			&& qty.trim().length() > 0
			) ret = true;
		
		return ret;
	}
	
	private void setButtons() {
		if (allFieldsFilledIn()) {
			mAddButton.setEnabled(true);
		} else {
			mAddButton.setEnabled(false);
		}
	}
	
	private void addCartItem() {
		Item item = new Item(mUPC.getText().toString(),
				             mDescription.getText().toString(),
				             mPrice.getText().toString());
		int qty = Integer.valueOf(mQuantity.getText().toString());
		CartItem cartItem = new CartItem(item,qty);
		executeAsyncTask("AddCartTask", new AddItemTask("AddCartTask",this), true, cartItem);
	}
	
	private void checkInventory() {
		String itemID = mUPC.getText().toString();
		Log.d(LOG_TAG,"Checking Inventory: itemID");
		if (itemID != null) {
			Item item = new Item(itemID,null,null);
			executeAsyncTask("CheckInventory",new CheckInventoryTask("CheckInventory", this), false, item);
			
		}
	}

	@Override
	int getContentView() {
		return R.layout.activity_add_cart_item;
	}

	@Override
	int getMainView() {
		return R.id.cart_add_item_form;
	}

	@Override
	int getSpinnerView() {
		return R.id.cart_item_spinner;
	}
	
	public class AddItemTask extends POSTask<CartItem> {
		
		public AddItemTask(String name, POSTaskParent parent) {
			super(name, parent);
		}

		@Override
		CartItem backgroundWork(Storage storage, POSModel... args) {
			CartItem item = (CartItem) args[0];
			try {
				if (addItem) storage.addItem(item.getItem());
				getCart().addItem(item.getItem(), item.getQuantity());
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				return null;
			}
			return item;
		}

		@Override
		void postWork(Storage storage, CartItem workResult) {
			if (workResult != null) {
				finish();
			}
		}
	}
	
	public class CheckInventoryTask extends POSTask<Item> {
		
		public CheckInventoryTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		protected Item backgroundWork(Storage storage, POSModel... args) {
			Item item = (Item) args[0];
			try {
				item = storage.getItem(item.getId());
			} catch (StorageException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				return null;
			} catch (NoItemFoundException e) {
				return null;
			}
			return item;
		}

		@Override
		void postWork(Storage storage, Item result) {
			if (result != null) {
				mPrice.setText(result.getPrice());
				mDescription.setText(result.getDescription());
				addItem = false;
			} else {
				mPrice.setText(null);
				mDescription.setText(null);
				addItem = true;
			}
		}
	}

}
