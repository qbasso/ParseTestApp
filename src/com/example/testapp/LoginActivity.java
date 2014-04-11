package com.example.testapp;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends RoboActivity implements OnClickListener {

	@InjectView(R.id.login)
	private Button mLogin;
	@InjectView(R.id.signup)
	private Button mSignup;
	@InjectView(R.id.name)
	private EditText mUsername;
	@InjectView(R.id.password)
	private EditText mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ParseUser.getCurrentUser() != null) {
			startTimezoneActivity();
		} else {
			setContentView(R.layout.activity_login);
			initView();
		}
	}

	private void startTimezoneActivity() {
		Intent intent = new Intent(LoginActivity.this, TimezoneActivity.class);
		startActivity(intent);
		finish();
	}

	private void initView() {
		mLogin.setOnClickListener(this);
		mSignup.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.login:
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setTitle(getString(R.string.please_wait));
			pd.setMessage(getString(R.string.logging_in));
			String username = mUsername.getText().toString().trim();
			String password = mPassword.getText().toString().trim();
			if (mLogin.length() > 0 && mPassword.length() > 0) {
				pd.show();
				ParseUser.logInInBackground(username, password,
						new LogInCallback() {

							@Override
							public void done(ParseUser arg0, ParseException arg1) {
								pd.dismiss();
								if (arg1 != null) {
									Toast.makeText(LoginActivity.this,
											getString(R.string.server_error),
											Toast.LENGTH_SHORT).show();
								} else {
									startTimezoneActivity();
								}
							}
						});
			}
			break;
		case R.id.signup:
			startActivity(new Intent(this, SignUpActivity.class));
			break;
		default:
			break;
		}
	}

}
