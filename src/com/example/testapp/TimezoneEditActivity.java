package com.example.testapp;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class TimezoneEditActivity extends Activity implements OnClickListener {

	private static final int NEGATIVE_OFFSET = 12;
	private EditText mTimezoneName;
	private EditText mCityName;
	private NumberPicker mGmtOffset;
	private Button mSave;
	private Timezone mTimezone;
	private static final String[] DISPLAYED_NUMBERS = new String[] { "-12",
			"-11", "-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1",
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
			"13", "14" };
	public static final String EXTRA_TIMEZONE_ID = "EXTRA_TIMEZONE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_timezone);
		mTimezoneName = (EditText) findViewById(R.id.timezone_name);
		mCityName = (EditText) findViewById(R.id.city_name);
		mGmtOffset = (NumberPicker) findViewById(R.id.gmtOffset);
		mGmtOffset.setMinValue(0);
		mGmtOffset.setMaxValue(26);
		mGmtOffset.setDisplayedValues(DISPLAYED_NUMBERS);
		mGmtOffset.setValue(12);
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(this);
		String s = getIntent().getStringExtra(EXTRA_TIMEZONE_ID);
		if (s != null) {
			ParseQuery<Timezone> query = Timezone.getQuery();
			query.whereEqualTo("objectId", s);
			query.findInBackground(new FindCallback<Timezone>() {

				@Override
				public void done(List<Timezone> arg0, ParseException arg1) {
					if (arg1 == null && arg0.size() == 1) {
						mTimezone = arg0.get(0);
						initFields();
					}
				}
			});
		}
	}

	private void initFields() {
		mTimezoneName.setText(mTimezone.getName());
		mCityName.setText(mTimezone.getCity());
		mGmtOffset.setValue(mTimezone.getGmtDiff() + NEGATIVE_OFFSET);
	}

	@Override
	public void onClick(View v) {
		String timezone = mTimezoneName.getText().toString().trim();
		String city = mCityName.getText().toString().trim();
		if (timezone.length() > 0 && city.length() > 0) {
			if (mTimezone != null) {
				updateExistingTimezone(timezone, city);
			} else {
				saveNewTimezone(timezone, city);
			}
		} else {
			Toast.makeText(this, "Timezone or city cannot be empty!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void saveNewTimezone(String timezone, String city) {
		Timezone newTimezone = new Timezone();
		newTimezone.setUser(ParseUser.getCurrentUser());
		newTimezone.setCity(city);
		newTimezone.setName(timezone);
		newTimezone.setGmtDiff(mGmtOffset.getValue() - NEGATIVE_OFFSET);
		newTimezone.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				setResult(RESULT_OK);
				finish();

			}
		});
	}

	private void updateExistingTimezone(String timezone, String city) {
		mTimezone.setUser(ParseUser.getCurrentUser());
		mTimezone.setCity(city);
		mTimezone.setName(timezone);
		mTimezone.setGmtDiff(mGmtOffset.getValue() - NEGATIVE_OFFSET);
		mTimezone.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				setResult(RESULT_OK);
				finish();
			}
		});
	}
}
