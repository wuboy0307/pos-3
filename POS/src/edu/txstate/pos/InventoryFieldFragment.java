package edu.txstate.pos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InventoryFieldFragment extends POSFieldFragment {

	private Button mRefreshButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_inventory_fields, parent, false);
		
		mRefreshButton = (Button) v.findViewById(R.id.inventory_refresh_button);
		
		mRefreshButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						refreshList();
					}
				});
		
		return v;
	}
	
	private void refreshList() {
		((InventoryActivity) parent).refreshList();
	}
	
	@Override
	void netStatusUpdate() {
	}

}
