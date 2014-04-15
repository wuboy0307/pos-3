package edu.txstate.pos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import static edu.txstate.pos.CartActivity.*;


public class ManualPayActivity extends POSActivity {

	private static final String LOG_TAG = "ManualPayActivity";
	
	private TextView mCreditCard;
	private TextView mPIN;
	private Button mActionButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mCreditCard = (TextView) findViewById(R.id.manual_card_number);
		mPIN = (TextView) findViewById(R.id.manual_pin);
		mActionButton = (Button) findViewById(R.id.manual_action_button);
		
		mCreditCard.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				// do nothing
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// do nothing
			}
			
			public void afterTextChanged(Editable c) {
				updateButton();
			}
		});
		
		mPIN.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence c, int start, int before, int count) {
				// do nothing
			}
			
			public void beforeTextChanged(CharSequence c, int start, int count, int after) {
				// do nothing
			}
			
			public void afterTextChanged(Editable c) {
				updateButton();
			}
		});
		
		mActionButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						addPayment();
					}
				});
		
		updateButton();
	
	}
	
	public void addPayment() {
		Intent ret = new Intent("edu.txstate.pos.Payment.MANUAL");
		ret.putExtra(EXTRA_FIELD_CARD,mCreditCard.getText().toString());
		ret.putExtra(EXTRA_FIELD_PIN,mPIN.getText().toString());
		setResult(RESULT_OK, ret);
		finish();
	}

	public void updateButton() {
		boolean hasCard = mCreditCard.getText().toString() != null
						   && mCreditCard.getText().toString().length() >= 4;
		boolean hasPIN = mPIN.getText().toString() != null
				              && mPIN.getText().toString().length() >= 4;
	    if (hasCard && hasPIN) {
	    	mActionButton.setEnabled(true);
	    } else {
	    	mActionButton.setEnabled(false);
	    }
	}
	
	@Override
	int getContentView() {
		return R.layout.activity_manual_pay;
	}

	@Override
	int getMainView() {
		return R.id.user_form;
	}

	@Override
	int getSpinnerView() {
		return R.id.user_spinner;
	}

	@Override
	void netStatusUpdate() {
		// TODO Auto-generated method stub
		
	}

}
