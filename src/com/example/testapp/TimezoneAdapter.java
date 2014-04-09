package com.example.testapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.example.testapp.api.Timezone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class TimezoneAdapter extends ArrayAdapter<Timezone> implements
		Filterable {

	private static final int HOUR_IN_MILLIS = 1000 * 60 * 60;

	private ArrayList<Timezone> mOriginalItems;

	/** The m filtered items. */
	private ArrayList<Timezone> mFilteredItems;

	private int mResourceId;

	private Filter mFilter;

	private SimpleDateFormat mSdf;

	private static class Holder {
		TextView timezoneName;
		TextView cityName;
		TextView gmtOffset;
		TextView time;
	}

	public TimezoneAdapter(Context context, int resource,
			List<Timezone> objects) {
		super(context, resource, objects);
		mOriginalItems = new ArrayList<Timezone>(objects);
		mFilteredItems = new ArrayList<Timezone>(objects);
		mResourceId = resource;
		mSdf = new SimpleDateFormat("HH:mm");
	}

	@Override
	public int getCount() {
		return mFilteredItems.size();
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new Filter() {
				private int lastConstraintLength;

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {
					mFilteredItems = (ArrayList<Timezone>) results.values;
					if (results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults result = new FilterResults();
					result.values = mOriginalItems;
					ArrayList<Timezone> filteredResult = new ArrayList<Timezone>();
					if (constraint == null || constraint.length() == 0) {
						filteredResult.addAll(mOriginalItems);
						result.values = filteredResult;
						result.count = filteredResult.size();
						lastConstraintLength = 0;
						// depend from length of this and previous constraint
						// we can filter currently filtered items or all items
					} else {
						if (constraint.length() > lastConstraintLength) {
							filteredResult = filterItems(constraint, true);
						} else if (constraint.length() < lastConstraintLength) {
							filteredResult = filterItems(constraint, false);
						} else {
							filteredResult = mFilteredItems;
						}
						lastConstraintLength = constraint.length();
						result.values = filteredResult;
						result.count = filteredResult.size();
					}
					return result;
				}

				/**
				 * Perform filtering on adapter items.
				 * 
				 * @param constraint
				 *            search string
				 * @param searchAll
				 *            whether we should search through all items or
				 *            currently filtered set
				 * @return the array list
				 */
				private ArrayList<Timezone> filterItems(
						CharSequence constraint, boolean searchAll) {
					String name;
					ArrayList<Timezone> filtered = new ArrayList<Timezone>();
					ArrayList<Timezone> toBeFiltered;
					String c = constraint.toString().toLowerCase(
							Locale.getDefault());
					if (searchAll) {
						toBeFiltered = new ArrayList<Timezone>(mFilteredItems);
					} else {
						toBeFiltered = new ArrayList<Timezone>(mOriginalItems);
					}
					if ("".equals(constraint)) {
						;
					} else {
						for (Timezone t : toBeFiltered) {
							name = t.getName().toLowerCase(Locale.getDefault());
							if (name != null) {
								if (name.contains(c)) {
									filtered.add(t);
								}
							}
						}

					}
					return filtered;
				}
			};
		}
		return mFilter;
	}

	@Override
	public Timezone getItem(int position) {
		return mFilteredItems.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		Holder h;
		if (v == null) {
			h = new Holder();
			v = LayoutInflater.from(getContext()).inflate(mResourceId, parent,
					false);
			h.timezoneName = (TextView) v.findViewById(R.id.timezone_name);
			h.cityName = (TextView) v.findViewById(R.id.city_name);
			h.time = (TextView) v.findViewById(R.id.time);
			h.gmtOffset = (TextView) v.findViewById(R.id.gmt_offset);
			v.setTag(h);
		} else {
			h = (Holder) v.getTag();
		}
		Timezone t = mFilteredItems.get(position);
		TimeZone tz = TimeZone.getDefault();
		tz.setRawOffset(t.getGmtDiff() * HOUR_IN_MILLIS);
		h.timezoneName.setText(t.getName());
		h.cityName.setText(t.getCity());
		h.gmtOffset.setText(getContext()
				.getString(R.string.gmt, t.getGmtDiff()));
		mSdf.setTimeZone(tz);
		h.time.setText(mSdf.format(new Date()));
		return v;
	}

	@Override
	public void remove(Timezone object) {
		mFilteredItems.remove(object);
		mOriginalItems.remove(object);
	}
	
	

}
