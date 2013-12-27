package eu.mcft.sumoremote;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class PrefsFragment extends PreferenceFragment
{
	 @Override
	 public void onCreate(Bundle savedInstanceState)
	 { 
		 super.onCreate(savedInstanceState);
		 addPreferencesFromResource(R.xml.preferences);
	 }
}
