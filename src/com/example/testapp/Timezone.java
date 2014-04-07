package com.example.testapp;

import java.io.Serializable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Timezone")
public class Timezone extends ParseObject  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8349969912397083114L;

	public ParseUser getUser() {
		return getParseUser("user");
	}

	public void setUser(ParseUser user) {
		put("user", user);
	}

	public String getName() {
		return getString("name");
	}

	public void setName(String name) {
		put("name", name);
	}

	public String getCity() {
		return getString("city");
	}

	public void setCity(String city) {
		put("city", city);
	}

	public int getGmtDiff() {
		return getInt("gmtDiff");
	}

	public void setGmtDiff(int gmtDiff) {
		put("gmtDiff", gmtDiff);
	}

	public static ParseQuery<Timezone> getQuery() {
		return ParseQuery.getQuery(Timezone.class);
	}
}
