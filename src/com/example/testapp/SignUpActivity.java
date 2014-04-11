package com.example.testapp;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends RoboActivity implements OnClickListener {

	@InjectView(R.id.signup)
	private Button mSignup;
	@InjectView(R.id.name)
	private EditText mUsername;
	@InjectView(R.id.password)
	private EditText mPassword;
	@InjectView(R.id.repeatPassword)
	private EditText mPasswordRepeat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		initView();
	}

	private void initView() {
		mSignup.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setTitle(getString(R.string.please_wait));
		pd.setMessage(getString(R.string.signing_up));
		String username = mUsername.getText().toString().trim();
		String password = mPassword.getText().toString().trim();
		String repeatPassword = mPasswordRepeat.getText().toString().trim();
		if (username.length() > 0 && password.length() > 0
				&& repeatPassword.length() > 0) {
			if (password.equals(repeatPassword)) {
				pd.show();
				ParseUser user = new ParseUser();
				user.setUsername(username);
				user.setPassword(password);
				user.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException arg0) {
						pd.dismiss();
						if (arg0 != null) {
							Toast.makeText(SignUpActivity.this,
									arg0.getMessage(), Toast.LENGTH_SHORT)
									.show();
						} else {
							Intent intent = new Intent(SignUpActivity.this,
									LoginActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
									| Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
				});
			}
		}
	}
}
