package edu.txstate.pos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import edu.txstate.pos.storage.ConnectionError;
import edu.txstate.pos.storage.InvalidCartException;
import edu.txstate.pos.storage.NoUserFoundException;
import edu.txstate.pos.storage.Storage;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class ResendActivity extends POSActivity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXAMPLE_EMAIL = "you@domain.com";

	/**
	 * Keep track of the resend task to ensure we can cancel it if requested.
	 */
	private ResendReceiptTask mResendTask = null;

	// Values for email at the time of the login attempt.
	private String mEmail;

	// UI references.
	private EditText mEmailView;
	private View mResendFormView;
	private View mResendStatusView;
	private TextView mResendStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_resend);

		// Set up the resend form.
		mEmail = EXAMPLE_EMAIL;
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mResendFormView = findViewById(R.id.resend_form);
		mResendStatusView = findViewById(R.id.spinner);
		mResendStatusMessageView = (TextView) findViewById(R.id.spinner_message);

		findViewById(R.id.resend_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						resendReceipt();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.resend, menu);
		return true;
	}

	/**
	 * Attempts to resend the receipt.
	 * If there are form errors (invalid email etc.), the
	 * errors are presented and no actual resend attempt is made.
	 */
	public void resendReceipt() {
		if (mResendTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();

		boolean error = false;
		View focusView = null;

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			error = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			error = true;
		}

		if (error) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mResendStatusMessageView.setText(R.string.progress_text);
			showProgress(true);
			mResendTask = new ResendReceiptTask();
			mResendTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mResendStatusView.setVisibility(View.VISIBLE);
			mResendStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mResendStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mResendFormView.setVisibility(View.VISIBLE);
			mResendFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mResendFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mResendStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mResendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous task.  In this case, one that makes a network
	 * call to resend a customer a receipt.
	 * 
	 */
	private class ResendReceiptTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			try {
				// Simulate long network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			// Access to POS storage object to do work of resending receipt
			Storage storage = getStorage();
			try {
				storage.resendReceipt();
			} catch (ConnectionError e) {
				Log.e("RESEND_ACTIVITY",e.getMessage());
			} catch (NoUserFoundException e) {
				Log.e("RESEND_ACTIVITY",e.getMessage());
			} catch (InvalidCartException e) {
				Log.e("RESEND_ACTIVITY",e.getMessage());
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mResendTask = null;
			showProgress(false);

			if (success) {
				// finish() will make the activity close, so use it
				// if you want to
				//finish();
			}
		}

		@Override
		protected void onCancelled() {
			mResendTask = null;
			showProgress(false);
		}
	}
}
