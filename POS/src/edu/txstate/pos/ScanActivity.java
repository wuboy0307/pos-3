package edu.txstate.pos;

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;
import edu.txstate.pos.model.Item;
import edu.txstate.pos.storage.ItemExistsException;
import edu.txstate.pos.storage.NoItemFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.StorageException;


public class ScanActivity extends POSActivity implements OnClickListener {

	private Button scanBtn;
	private TextView formatTxt, contentTxt;
	private TextView storageDescTxt, storagePriceTxt;
	private ScanActivityTask mScanTask = null;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		
		//Make sure we're running on Honeycomb (v. 11) or greater to use the ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			//show the UP button in the ActionBar
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		scanBtn = (Button)findViewById(R.id.scan_button);
		formatTxt = (TextView)findViewById(R.id.scan_format);
		contentTxt = (TextView)findViewById(R.id.scan_content);
		storageDescTxt = (TextView)findViewById(R.id.storage_content_description);
		storagePriceTxt = (TextView)findViewById(R.id.storage_content_price);
		scanBtn.setOnClickListener(this);
	}

	@Override
	int getContentView() {
		return 0;
	}
	
	@Override
	int getMainView() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	int getSpinnerView() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all, menu);
		return true;
	}
	
	public void onClick(View v){
		//respond to clicks
		if(v.getId()==R.id.scan_button){
			//scan
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
			}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//retrieve scan result
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("CONTENT: " + scanContent);
			mScanTask.execute(scanContent);
			}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(),
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	}

	private class ScanActivityTask extends AsyncTask<String, Void, Item> {

		@Override
		protected Item doInBackground(String... content) {
			// Check to see if scan content exists as an Item in POS storage
			Storage storage = getStorage();
			Item checkItem = null;
			Item newItem = new Item(content[0], "test item", "test price");
			try {
				checkItem = storage.getItem(content[0]);
			} catch (NoItemFoundException e) {
				try {
					storage.addItem(newItem);
				} catch (StorageException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				if (checkItem != null) {
					// print out checkItem
					storageDescTxt.setText(checkItem.getDescription());
					storagePriceTxt.setText(checkItem.getPrice());
				}
				else {
					// print out newItem
					storageDescTxt.setText(newItem.getDescription());
					storagePriceTxt.setText(newItem.getPrice());

				}
			}
			
			return null;
		}
		
	}

}
