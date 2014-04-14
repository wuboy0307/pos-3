package edu.txstate.pos;

import java.util.List;
import edu.txstate.pos.model.POSModel;
import edu.txstate.pos.model.User;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;
import edu.txstate.pos.storage.UserExistsException;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import static edu.txstate.pos.UserListFragment.*;

public class AddUserActivity extends POSActivity {
	
	private static final String LOG_TAG = "AddUserActivity";
	
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
		
		mode = bundle.getInt(EXTRA_FIELD_MODE);
		mActionButton = (Button) findViewById(R.id.user_action_button);

		mLogin = (TextView) findViewById(R.id.user_login);
		mPassword = (TextView) findViewById(R.id.user_password);
		mIsAdmin = (CheckBox) findViewById(R.id.user_admin);
		mIsActive = (CheckBox) findViewById(R.id.user_active);
		
		if (mode == EXTRA_MODE_EDIT) {
			mUser = new User(bundle.getString(EXTRA_FIELD_LOGIN),
				        bundle.getString(EXTRA_FIELD_PIN));
			mUser.setActive(bundle.getBoolean(EXTRA_FIELD_ACTIVE));
			mUser.setAdmin(bundle.getBoolean(EXTRA_FIELD_ADMIN));
			mUser.setId(bundle.getInt(EXTRA_FIELD_ID));
			mLogin.setText(mUser.getLogin());
			mPassword.setText(mUser.getPIN());
			mIsAdmin.setChecked(mUser.isAdmin());
			mIsActive.setChecked(mUser.isActive());
			mActionButton.setText("Update");
		} else {
			mUser = new User();
			mActionButton.setText("Add");
			mIsActive.setChecked(true);
		}

		mActionButton.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mode == EXTRA_MODE_ADD) {
							addUser();
						} else if (mode == EXTRA_MODE_EDIT) {
							updateUser();
						}
					}
				});
		
		mLogin.addTextChangedListener(new TextWatcher() {
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
		
		mPassword.addTextChangedListener(new TextWatcher() {
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
		
		updateButton();
		
	}
	
	public void updateUser() {
		updateUserData();
		executeAsyncTask("UpdateUser",
				new UpdateUserTask("UpdateUser",this),
				true,
				mUser);
	}
	
	public void addUser() {
		updateUserData();
		executeAsyncTask("AddUser",
				new AddUserTask("AddUser",this),
				true,
				mUser);
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
	
	public class AddUserTask extends POSTask<User> {
		public AddUserTask(String name, POSTaskParent parent) {
			super(name, parent);
		}

		@Override
		User backgroundWork(Storage storage, POSModel... args) {
			User ret = null;
			User user = (User) args[0];
			try {
				ret = storage.addUser(user);
			} catch (ConnectionError e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			} catch (UserExistsException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			}
			return ret;
		}

		@Override
		void postWork(Storage storage, User workResult) {
			if (workResult != null) {
				List<User> users = 
						((POSApplication) getApplication()).getUsers();
				users.add(workResult);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public class UpdateUserTask extends POSTask<User> {
		public UpdateUserTask(String name, POSTaskParent parent) {
			super(name,parent);
		}

		@Override
		User backgroundWork(Storage storage, POSModel... args) {
			User ret = null;
			User user = (User) args[0];
			try {
				storage.updateUser(user);
				ret = user;
			} catch (ConnectionError e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			} catch (NoUserFoundException e) {
				mStatusMessage = e.getMessage();
				Log.e(LOG_TAG,e.getMessage());
				ret = null;
			}
			
			return ret;
		}

		@Override
		void postWork(Storage storage, User workResult) {
			if (workResult != null) {
				List<User> users = 
						((POSApplication) getApplication()).getUsers();
				for (User u : users) {
					if (u.getId() == workResult.getId()) {
						u.setUser(workResult);
						break;
					}
				}
				finish();
			} else {
				Toast.makeText(getApplicationContext(), 
						"Error: " + mStatusMessage, Toast.LENGTH_LONG).show();
			}
			
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
