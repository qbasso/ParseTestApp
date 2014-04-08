package com.example.testapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class TimezoneActivity extends Activity implements OnItemClickListener {

	private static final int REQUEST_EDIT_ADD_TIMEZONE = 10;
	private ListView mListView;
	private TextView mTitle;
	protected List<Timezone> mItems;
	protected ActionMode mActionMode;
	protected TimezoneAdapter mAdapter;

	private TextWatcher mWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (mAdapter != null) {
				mAdapter.getFilter().filter(s.toString());
			}
		}
	};

	private View mActionBarSearchView;
	private EditText mSearchInput;
	private boolean mSearchEnabled = false;
	private Menu mMenu;
	private BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (mListView != null && mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timezones);
		mListView = (ListView) findViewById(R.id.list);
		mTitle = (TextView) findViewById(R.id.title);
		mActionBarSearchView = LayoutInflater.from(this).inflate(
				R.layout.search_view, null);
		mSearchInput = (EditText) mActionBarSearchView
				.findViewById(R.id.search_input);
		mSearchInput.addTextChangedListener(mWatcher);
		setUpListSelectMode();
		reloadList();
	}

	private void setUpListSelectMode() {
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		mListView.setOnItemClickListener(this);
		mListView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			private int selectedCount = 0;
			private Set<Integer> checkedIds = new HashSet<Integer>();

			@Override
			public void onItemCheckedStateChanged(ActionMode mode,
					int position, long id, boolean checked) {
				if (checked) {
					checkedIds.add(position);
					selectedCount++;
				} else {
					checkedIds.remove(position);
					selectedCount--;
				}
				mode.setTitle(String.format("%d selected", selectedCount));
			}

			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.timezone_list_contextual, menu);
				mActionMode = mode;
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_delete:
					List<ParseObject> toDelete = new ArrayList<ParseObject>();
					for (Integer id : checkedIds) {
						Timezone t = mItems.get(id);
						toDelete.add(t);
					}
					ParseObject.deleteAllInBackground(toDelete, null);
					for (ParseObject parseObject : toDelete) {
						mItems.remove(parseObject);
					}
					mAdapter = new TimezoneAdapter(TimezoneActivity.this,
							R.layout.list_item_timezone, mItems);
					mListView.setAdapter(mAdapter);
					showEmptyMessageIfNeeded();
					mode.finish();
					return true;
				default:
					return false;
				}
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				selectedCount = 0;
				checkedIds.clear();
				mListView.clearChoices();
				mListView.requestLayout();
				mActionMode = null;
			}
		});
	}

	protected void enableSearch(boolean enable) {
		ActionBar ab = getActionBar();
		if (enable) {
			ab.setDisplayOptions(ab.getDisplayOptions()
					| ActionBar.DISPLAY_SHOW_CUSTOM);
			ab.setDisplayShowTitleEnabled(false);
			ab.setCustomView(mActionBarSearchView);
			mSearchEnabled = true;
			invalidateOptionsMenu();
		} else {
			ab.setDisplayShowTitleEnabled(true);
			ab.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_CUSTOM);
			ab.setCustomView(null);
			mSearchInput.getText().clear();
			mSearchEnabled = false;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		getMenuInflater().inflate(R.menu.timezone_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_search:
			enableSearch(true);
			break;
		case R.id.action_add_edit:
			startActivityForResult(
					new Intent(this, TimezoneEditActivity.class),
					REQUEST_EDIT_ADD_TIMEZONE);
			break;
		case R.id.action_logout:
			ParseUser.logOut();
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_EDIT_ADD_TIMEZONE) {
			reloadList();
		}
	}

	private void reloadList() {
		ClientAPI.getTimezones(ParseUser.getCurrentUser(),
				new FindCallback<Timezone>() {
					@Override
					public void done(List<Timezone> arg0, ParseException arg1) {
						if (arg1 == null) {
							mItems = arg0;
							showEmptyMessageIfNeeded();
							mAdapter = new TimezoneAdapter(
									TimezoneActivity.this,
									R.layout.list_item_timezone,
									new ArrayList<Timezone>(mItems));
							mListView.setAdapter(mAdapter);
						} else {
							mTitle.setVisibility(View.VISIBLE);
						}
					}
				});
	}

	private void showEmptyMessageIfNeeded() {
		if (mItems.size() == 0) {
			mTitle.setVisibility(View.VISIBLE);
		} else {
			mTitle.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		if (mSearchEnabled) {
			enableSearch(false);
			mAdapter.getFilter().filter("");
			invalidateOptionsMenu();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void invalidateOptionsMenu() {
		mMenu.findItem(R.id.action_add_edit).setVisible(!mSearchEnabled);
		mMenu.findItem(R.id.action_logout).setVisible(!mSearchEnabled);
		mMenu.findItem(R.id.action_search).setVisible(!mSearchEnabled);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mTimeTickReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(mTimeTickReceiver, new IntentFilter(
				Intent.ACTION_TIME_TICK));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this, TimezoneEditActivity.class);
		i.putExtra(TimezoneEditActivity.EXTRA_TIMEZONE_ID, mItems.get(arg2)
				.getObjectId());
		startActivityForResult(i, REQUEST_EDIT_ADD_TIMEZONE);
	}

}
