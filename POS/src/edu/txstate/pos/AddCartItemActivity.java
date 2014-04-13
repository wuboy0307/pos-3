package edu.txstate.pos;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AddCartItemActivity extends POSActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	int getContentView() {
		// TODO Auto-generated method stub
		return R.layout.activity_add_cart_item;
	}

	@Override
	int getMainView() {
		// TODO Auto-generated method stub
		return R.id.cart_add_item_form;
	}

	@Override
	int getSpinnerView() {
		// TODO Auto-generated method stub
		return R.id.cart_item_spinner;
	}

}
