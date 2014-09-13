package eu.mcft.sumoremote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SetPreferenceActivity extends PrefsAdjustedActivity
{
	 @Override
	 protected void onCreate(Bundle savedInstanceState)
	 {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	 }
}
