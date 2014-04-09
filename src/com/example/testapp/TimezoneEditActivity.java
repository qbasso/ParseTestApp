package com.example.testapp;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.testapp.api.Timezone;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class TimezoneEditActivity extends Activity implements OnClickListener {

	private static final int NEGATIVE_GMT_COUNT = 12;
	private EditText mTimezoneName;
	private EditText mCityName;
	private NumberPicker mGmtOffset;
	private Button mSave;
	private Timezone t;
	private static final String[] GMT_TO_DISPLAY = new String[] { "-12", "-11",
			"-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0",
			"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
			"13", "14" };
	public static final String EXTRA_TIMEZONE_ID = "EXTRA_TIMEZONE";

	private SaveCallback mSaveCallback = new SaveCallback() {

		@Override
		public void done(ParseException arg0) {
			if (arg0 != null) {
				Toast.makeText(TimezoneEditActivity.this,
						getString(R.string.server_error), Toast.LENGTH_SHORT)
						.show();
			} else {
				setResult(RESULT_OK);
				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_edit_timezone);
		mTimezoneName = (EditText) findViewById(R.id.timezone_name);
		mCityName = (EditText) findViewById(R.id.city_name);
		mGmtOffset = (NumberPicker) findViewById(R.id.gmtOffset);
		mGmtOffset.setMinValue(0);
		mGmtOffset.setMaxValue(26);
		mGmtOffset.setDisplayedValues(GMT_TO_DISPLAY);
		mGmtOffset.setValue(12);
		mSave = (Button) findViewById(R.id.save);
		mSave.setOnClickListener(this);
		String s = getIntent().getStringExtra(EXTRA_TIMEZONE_ID);
		if (s != null) {
			setProgressBarIndeterminateVisibility(true);
			ParseQuery<Timezone> query = Timezone.getQuery();
			query.whereEqualTo("objectId", s);
			query.findInBackground(new FindCallback<Timezone>() {

				@Override
				public void done(List<Timezone> arg0, ParseException arg1) {
					setProgressBarIndeterminateVisibility(false);
					if (arg1 == null && arg0.size() == 1) {
						t = arg0.get(0);
						initFields();
					}
				}
			});
		}
	}

	private void initFields() {
		mTimezoneName.setText(t.getName());
		mCityName.setText(t.getCity());
		mGmtOffset.setValue(t.getGmtDiff() + NEGATIVE_GMT_COUNT);
	}

	@Override
	public void onClick(View v) {
		String timezone = mTimezoneName.getText().toString().trim();
		String city = mCityName.getText().toString().trim();
		if (timezone.length() > 0 && city.length() > 0) {
			saveOrCreateTimezone(t, timezone, city);
		} else {
			Toast.makeText(this, getString(R.string.timezone_validation_msg),
					Toast.LENGTH_SHORT).show();
		}
	}

	private void saveOrCreateTimezone(Timezone t, String timezone, String city) {
		if (t == null) {
			Timezone newTimezone = new Timezone();
			newTimezone.setUser(ParseUser.getCurrentUser());
			newTimezone.setCity(city);
			newTimezone.setName(timezone);
			newTimezone.setGmtDiff(mGmtOffset.getValue() - NEGATIVE_GMT_COUNT);
			newTimezone.saveInBackground(mSaveCallback);
		} else {
			t.setUser(ParseUser.getCurrentUser());
			t.setCity(city);
			t.setName(timezone);
			t.setGmtDiff(mGmtOffset.getValue() - NEGATIVE_GMT_COUNT);
			t.saveInBackground(mSaveCallback);
		}
	}

}
