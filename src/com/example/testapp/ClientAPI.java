package com.example.testapp;

import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ClientAPI {

	public static void getTimezones(ParseUser user,
			FindCallback<Timezone> callback) {
		ParseQuery<Timezone> query = Timezone.getQuery();
		query.whereEqualTo("user", user);
		query.findInBackground(callback);
	}
}
