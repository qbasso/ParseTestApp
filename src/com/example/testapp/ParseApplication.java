package com.example.testapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "cONU8ZJRvn7nTKG277mzju8sAYYWkK2nWPJkydBj",
				"jThyZuwNuwie5WH93nbKmOrFdEIur0UKaig4Qoq3");
		ParseObject.registerSubclass(Timezone.class);
	}

}
