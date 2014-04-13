package edu.txstate.pos;

import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.StorageException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import static edu.txstate.pos.UserListFragment.*;

public class AddUserActivity extends POSActivity {
	private TextView mLogin;
	private TextView mPassword;
	private CheckBox mIsAdmin;
	private CheckBox mIsActive;
	private Button mActionButton;
	
	private User mUser;
	private int mode;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		mUser = new User(bundle.getString(EXTRA_FIELD_LOGIN),
				        bundle.getString(EXTRA_FIELD_PIN));
		mUser.setActive(bundle.getBoolean(EXTRA_FIELD_ACTIVE));
		mUser.setAdmin(bundle.getBoolean(EXTRA_FIELD_ADMIN));
		mUser.setId(bundle.getInt(EXTRA_FIELD_ID));
		
		mode = bundle.getInt(EXTRA_FIELD_MODE);
		
		mLogin = (TextView) findViewById(R.id.user_login);
		mPassword = (TextView) findViewById(R.id.user_password);
		mIsAdmin = (CheckBox) findViewById(R.id.user_admin);
		mIsActive = (CheckBox) findViewById(R.id.user_active);
		
		mLogin.setText(mUser.getLogin());
		mPassword.setText(mUser.getPIN());
		mIsAdmin.setChecked(mUser.isAdmin());
		mIsActive.setChecked(mUser.isActive());
		
		mActionButton = (Button) findViewById(R.id.user_action_button);
		
		switch (mode) {
			case EXTRA_MODE_ADD:
				mActionButton.setText("Add");
				break;
			case EXTRA_MODE_EDIT:
				mActionButton.setText("Update");
				break;
		}
		
		mActionButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
					
					}
				});
		
		mLogin.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence c, int start, int before, int count) {
					
					}
					
					public void beforeTextChanged(CharSequence c, int start, int count, int after) {
						// do nothing
					}
					
					public void afterTextChanged(Editable c) {
						// do nothing
					}
				});
		
		mPassword.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence c, int start, int before, int count) {
						
					}
					
					public void beforeTextChanged(CharSequence c, int start, int count, int after) {
						// do nothing
					}
					
					public void afterTextChanged(Editable c) {
						// do nothing
					}
				});
		
	}
	
	public void updateUserData() {
		mUser.setLogin(mLogin.getText().toString());
		mUser.setPIN(mPassword.getText().toString());
		mUser.setAdmin(mIsAdmin.isChecked());
		mUser.setActive(mIsActive.isChecked());
	}
	
	public void updateButton() {
		boolean hasLogin = mLogin.getText().toString() != null
						   && mLogin.getText().toString().length() > 0;
		boolean hasPassword = mPassword.getText().toString() != null
				              && mPassword.getText().toString().length() > 0;
	    if (hasLogin && hasPassword) {
	    	mActionButton.setEnabled(true);
	    } else {
	    	mActionButton.setEnabled(false);
	    }
	}
	
	@Override
	int getContentView() {
		return R.layout.activity_user;
	}

	@Override
	int getMainView() {
		return R.id.user_form;
	}

	@Override
	int getSpinnerView() {
		return R.id.user_spinner;
	}

}
